import Profile from "../classes/Profile"

async function getAllProducts(): Promise<Response>{

    const response = await fetch("/API/products")
    return response

}

async function getProductByEAN(ean: string): Promise<Response>{
    const response = await fetch("/API/products/" + ean )
    return response

}

async function getProfileByEmail(email: string): Promise<Response> {

    const response = await fetch("/API/profiles/" + email)
    return response

}

async function postProfile(profile: Profile): Promise<Response> {
    const response = await fetch("/API/profiles", {
        method: 'POST',
        headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
        body: JSON.stringify({email: profile.email, name: profile.name, surname: profile.surname})
    })
    console.log(response)
    return response
}

async function putProfile(profile: Profile): Promise<Response> {
    const response = await fetch("/API/profiles/" + profile.email, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json', 'Accept': 'application/json'},
        body: JSON.stringify({email: profile.email, name: profile.name, surname: profile.surname})
    })

    return response
}

const API = {getAllProducts, getProductByEAN, getProfileByEmail, postProfile, putProfile}
export default API