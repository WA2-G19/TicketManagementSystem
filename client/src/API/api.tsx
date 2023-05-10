import Profile from "../classes/Profile"

async function getAllProducts(): Promise<Response> {
    return await fetch("/API/products")
}

async function getProductByEAN(ean: string): Promise<Response> {
    return await fetch("/API/products/" + ean)

}

async function getProfileByEmail(email: string): Promise<Response> {
    return await fetch("/API/profiles/" + email)
}

async function postProfile(profile: Profile): Promise<Response> {
    return await fetch("/API/profiles", {
        method: 'POST',
        headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
        body: JSON.stringify({
            email: profile.email,
            name: profile.name,
            surname: profile.surname,
            address: profile.address
        })
    })
}

async function putProfile(profile: Profile): Promise<Response> {
    return await fetch("/API/profiles/" + profile.email, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
        body: JSON.stringify({
            email: profile.email,
            name: profile.name,
            surname: profile.surname,
            address: profile.address
        })
    })
}

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

const API = {getAllProducts, getProductByEAN, getProfileByEmail, postProfile, putProfile, login}
export default API