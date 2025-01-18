import {check} from 'k6'
import http from 'k6/http'
import {generateAuthToken} from './auth.js'
import {BASE_URL, getOption} from './config.js'

const faker = require('./faker.min.js')

function generateCreateConversationRequest() {
    return {
        name: faker.lorem.words(3),
        participants: [
            faker.datatype.number({min: 10, max: 100}),
            faker.datatype.number({min: 10, max: 100}),
        ],
    }
}

function testCreateConversation() {
    const body = JSON.stringify(generateCreateConversationRequest())
    const headers = {
        Authorization: generateAuthToken(),
        'Content-Type': 'application/json',
    }

    const res = http.post(`${BASE_URL}/conversations`, body, {headers})
    check(res, {
        'Create conversation: status is 201': (r) => r.status === 201,
        'Create conversation: location header exists': (r) =>
            r.headers['Location'] !== undefined,
    })
}

export default function () {
    testCreateConversation()
}

export let options = getOption(1)
