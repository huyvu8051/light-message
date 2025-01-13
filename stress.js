import { check } from 'k6';
import http from 'k6/http';
import { Random } from 'k6';

// Base URL for the API
const BASE_URL = 'http://localhost:8080/api/v1';

// Generate Authorization Token (Mock)
function generateAuthToken() {
    const userId = 1; // Simulating a userId
    return String(userId); // Using userId as the token for simplicity
}

// Generate Feeder Data
function generateFeederData() {
    let data = [];
    for (let i = 0; i < 1000; i++) {
        const conversation = {
            content: 'dick', // Random string as content
            convId: 2020 // Random conversation ID
        };
        data.push(conversation);
    }
    return data;
}

const feederData = generateFeederData();

// Scenario to send messages
export default function () {
    // Randomly pick a conversation
    const conversation = feederData[Math.floor(Math.random() * feederData.length)];

    // Prepare request body
    const body = JSON.stringify({
        convId: conversation.convId,
        content: conversation.content
    });

    // Prepare headers
    const headers = {
        'Authorization': generateAuthToken(),
        'Content-Type': 'application/json'
    };

    // Send POST request
    const res = http.post(`${BASE_URL}/messages`, body, { headers });

    // Check if the response status is 201 (Created)
    check(res, {
        'is status 201': (r) => r.status === 201,
    });
}

// Define options for virtual users and load testing behavior
export let options = {
    stages: [
        { duration: '20s', target: 100 },  // Ramp-up to 100 users in 20 seconds
        { duration: '30s', target: 200 },  // Ramp-up to 200 users in 30 seconds
        { duration: '60s', target: 500 },  // Ramp-up to 500 users in 1 minute
        { duration: '300s', target: 500 }, // Maintain 500 users for 5 minutes
        { duration: '30s', target: 0 },    // Gradually reduce users to 0 in 30 seconds
    ],
};
