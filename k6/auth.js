const faker = require('./faker.min.js')

function generateAuthToken() {
    return String(faker.datatype.number({min: 1, max: 20}))
}

export {
    generateAuthToken
}