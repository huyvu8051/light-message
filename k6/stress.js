import { check } from 'k6';
import http from 'k6/http';
const faker = require('./faker.min.js');

const BASE_URL = __ENV.BASE_URL || "http://localhost:8080/api/v1";
function generateAuthToken() {
    return String(faker.datatype.number({ min: 10, max: 100 }));
}

function generateConversation() {
    return {
        content: faker.lorem.paragraph(),
        convId: faker.datatype.number({ min: 2000, max: 2020 }),
    };
}

export default function () {
    const conversation = generateConversation();
    const body = JSON.stringify(conversation);
    const headers = {
        Authorization: generateAuthToken(),
        'Content-Type': 'application/json',
    };
    const res = http.post(`${BASE_URL}/messages`, body, { headers });
    check(res, {
        'is status 201': (r) => r.status === 201,
    });
}

export let options = {
    stages: [
        {duration: '20s', target: 100},
        {duration: '30s', target: 200},
        {duration: '60s', target: 500},
        {duration: '300s', target: 500},
        {duration: '30s', target: 0},
    ],
}
