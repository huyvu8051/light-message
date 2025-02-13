import ws from "k6/ws";
import http from "k6/http";
import { sleep } from "k6";

const socketIoPath = "localhost:8081/socket.io/?EIO=4&transport=";

const formatMessage = (...args) => {
    return `42${JSON.stringify(args)}`;
}

// Define headers with cookies
const headers = {
    "Cookie": "Idea-4a7216e0=c551f3eb-c58e-4214-8b0a-e952086fc946; Authorization=2"
};

export default function () {
    // Step 1: Initial HTTP Polling Request with Cookie
    const received = http.get(`http://${socketIoPath}polling`, { headers });
    const { sid, upgrades, pingInterval, pingTimeout, maxPayload } = JSON.parse(received.body.substring(1));

    if (upgrades.includes("websocket")) {
        ws.connect(`ws://${socketIoPath}websocket&sid=${sid}`, { headers }, (socket) => {
            socket.on("open", () => {
                socket.send('2probe'); // Send probe ping
            });

            socket.on("message", (message) => {
                console.log(`Received: ${message}`);

                // Step 2: Handle WebSocket upgrade
                if (message === "3probe") {
                    socket.send('5');  // Confirm upgrade
                    socket.send(`40/chat`); // Connect to "chat" namespace
                }

                // Step 3: Namespace connection established
                if (message.startsWith("40/chat")) {
                    const messageSent = formatMessage("chatMessage", JSON.stringify({
                        username: "testUser",
                        message: "Hello from k6 with cookies!",
                    }));
                    console.log(`Message sent : ${messageSent}`)
                    socket.send(messageSent); // Send test message
                    sleep(5);
                    socket.send('41/chat'); // Disconnect from chat namespace
                    sleep(1);
                    socket.close();
                }
            });

            socket.on("error", (error) => {
                console.error(error);
            });
        });
    }
}
