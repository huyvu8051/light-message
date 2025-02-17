const faker = require('./faker.min.js')
faker.setLocale("vi");
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080'
const getOption = (ratio) => {
    return {
        stages: [
            {duration: '30s', target: 1 * ratio},
            {duration: '1m', target: 3 * ratio},
            {duration: '10m', target: 3 * ratio},
            {duration: '30s', target: 1 * ratio},
        ],
    }
}

export {
    BASE_URL,
    getOption
}
