const request=axios.create({
    baseURL:"http://localhost:8080/api/",
    headers:{
        token:localStorage.getItem('token')
    }

})