import {check} from 'k6'
import http from 'k6/http'
import {BASE_URL} from './config.js'


function testSendMessage() {
    const headers = {
        Cookie: `Authorization=1`,
        'Content-Type': 'application/json',
    }

    const res = http.get(`${BASE_URL}/test`, {headers})
    check(res, {
        'peak test: status is 200': (r) => r.status === 200,
    })
}

export default function () {
    testSendMessage()
}

const ratio = 1
export let options = {
    stages: [
        {duration: '1s', target: 10 * ratio},
        {duration: '5s', target: 30 * ratio},
        {duration: '10m', target: 30 * ratio},
        {duration: '30s', target: 10 * ratio},
    ],
}
