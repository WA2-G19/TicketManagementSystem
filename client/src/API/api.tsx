async function login(username: string, password: string) {

    const user = {
        "username": username,
        "password": password
    }
    const response = await fetch("http://localhost:8080/API/login", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(user)
    })
    if(response.ok) {
        return await response.text()
    } else {
        throw response.statusText
    }
}

const API = {login}
export default API