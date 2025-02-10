import {check} from 'k6'
import http from 'k6/http'
import {generateAuthToken} from './auth.js'
import {BASE_URL, getOption} from './config.js'

const faker = require('./faker.min.js')

function generateMessage() {
    return {
        convId: faker.datatype.number({min: 1, max: 20}),
        content: faker.lorem.sentence(),
    }
}

function testSendMessage() {
    const body = JSON.stringify(generateMessage())
    const headers = {
        Cookie: `Authorization=${generateAuthToken()}`,
        'Content-Type': 'application/json',
    }

    const res = http.post(`${BASE_URL}/api/v1/messages`, body, {headers})
    check(res, {
        'Send message: status is 201': (r) => r.status === 201,
        'Send message: location header exists': (r) =>
            r.headers['Location'] !== undefined,
    })
}

export default function () {
    testSendMessage()
}

export let options = getOption(3)
