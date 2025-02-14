import ws from "k6/ws";
import http from "k6/http";
import { sleep } from "k6";
import {getOption} from './config.js'
const faker = require('./faker.min.js')

const socketIoPath = "localhost:8081/socket.io/?EIO=4&transport=";


export default function () {
    const headers = {
        "Cookie": `Authorization=${faker.datatype.number({min: 1, max: 20})}`
    };

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
                    /*const messageSent = formatMessage("chatMessage", JSON.stringify({
                        username: "testUser",
                        message: "Hello from k6 with cookies!",
                    }));*/

                    const messageSent = '42/chat,["chatMessage","chung ta cua tuong lai"]'
                    console.log(`Message sent : ${messageSent}`)
                    socket.send(messageSent); // Send test message
                    /*sleep(5);
                    socket.send('41/chat'); // Disconnect from chat namespace
                    sleep(1);
                    socket.close();*/
                }
            });

            socket.on("error", (error) => {
                console.error(error);
            });

            socket.on('close', () => {
                console.log('closing')
            })
        });
    }
}

export let options = getOption(3)
