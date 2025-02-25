import ws from "k6/ws";
import http from "k6/http";
import { sleep } from "k6";
import { getOption } from './config.js';
const faker = require('./faker.min.js');

const socketIoPath = "localhost:8081/socket.io/?EIO=4&transport=";

export default function () {
    const headers = {
        "Cookie": `Authorization=${faker.datatype.number({ min: 1, max: 20 })}`
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
                console.log(`Received: ${message}`.substring(0, 70));

                // Step 2: Handle WebSocket upgrade
                if (message === "3probe") {
                    socket.send('5');  // Confirm upgrade
                    socket.send(`40`); // Connect to the default namespace "/"
                }

                // Step 3: Namespace connection established
                if (message.startsWith("40")) {
                    const messageSent = '42["message","chung ta cua tuong lai"]'; // Default namespace
                    console.log(`Message sent : ${messageSent.substring(0, 10)}`);
                    socket.send(messageSent); // Send test message
                }
            });

            socket.on("error", (error) => {
                console.error(error);
            });

            socket.on('close', () => {
                console.log('closing');
            });
        });
    }
}
const ratio = parseInt(__ENV.CCU_FACTOR) || 10
export let options = {
    stages: [
        /*{ duration: '5s', target: 2 * ratio},
        { duration: '5s', target: 5 * ratio},
        { duration: '5s', target: 10  * ratio},
        { duration: '10m', target: 10  * ratio},
        { duration: '30s', target: 5  * ratio},
        { duration: '30s', target: 2  * ratio},*/
        { duration: '10s', target: 1 * ratio},
        { duration: '1h', target: 1 * ratio},
    ],
};
