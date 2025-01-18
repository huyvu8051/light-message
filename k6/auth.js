const faker = require('./faker.min.js')

function generateAuthToken() {
    return String(faker.datatype.number({min: 10, max: 100}))
}

export {
    generateAuthToken
}