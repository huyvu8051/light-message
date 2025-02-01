import {check} from 'k6'
import http from 'k6/http'
import {generateAuthToken} from './auth.js'
import {BASE_URL, getOption} from './config.js'

function testGetConversations() {
    const headers = {
        Authorization: generateAuthToken(),
    }

    const res = http.get(`${BASE_URL}/api/v1/conversations`, {headers})
    check(res, {
        'Get conversations: status is 200': (r) => r.status === 200,
        'Get conversations: response contains data': (r) =>
            JSON.parse(r.body).length > 0,
    })
}

export default function () {
    testGetConversations()
}

export let options = getOption(2)


