const faker = require('./faker.min.js')
faker.setLocale("vi");
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/api/v1'
const getOption = (ratio) => {
    return {
        stages: [
            {duration: '3m', target: 25 * ratio},
            /*{duration: '3m', target: 200 * ratio},
            {duration: '30s', target: 10 * ratio},*/
        ],
    }
}

export {
    BASE_URL,
    getOption
}