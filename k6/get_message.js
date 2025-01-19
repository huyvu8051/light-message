import {check} from 'k6'
import http from 'k6/http'
import {generateAuthToken} from './auth.js'
import {BASE_URL, getOption} from './config.js'

const faker = require('./faker.min.js')

// Stress test for fetching messages
function testGetMessages() {
    const convId = faker.datatype.number({min: 2000, max: 2019}) // Simulate conversation ID
    const headers = {
        Authorization: generateAuthToken(),
    }

    const res = http.get(`${BASE_URL}/messages/${convId}`, {headers})
    check(res, {
        'Get messages: status is 200': (r) => r.status === 200,
    })
}

export default function () {
    testGetMessages()
}

export let options = getOption(7)

