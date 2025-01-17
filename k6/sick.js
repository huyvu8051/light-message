const faker =  require( './faker.min.js')

 async function sick() {
    /*console.log(faker)
    faker.then(e=>{
        console.log( e)
    })*/

    const a = await faker
     console.log(faker.datatype.number({ min: 10, max: 100 }))

}

sick()