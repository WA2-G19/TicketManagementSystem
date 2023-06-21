import {Profile, CredentialCustomer} from "../../classes/Profile"
import ProblemDetail from "../../classes/ProblemDetail";

const { REACT_APP_SERVER_URL } = process.env;

async function getProfileByEmail(token: string, email: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/profiles/" + email,
        {
            headers: {
                "Authorization": "Bearer " + token
            }
        })
    if (response.ok) {
        return await response.json() as Profile
    }
    throw await response.json() as ProblemDetail
}


async function getProfiles(token: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/profiles",
        {
            headers: {
                "Authorization": "Bearer " + token
            }
        })
    if (response.ok) {
        return await response.json() as Array<Profile>
    }
    throw await response.json() as ProblemDetail
}


async function postProfile(token: string, profile: Profile) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/profiles", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            "Authorization": "Bearer " + token
        },
        body: profile.toJsonObject()
    })
    if (!response.ok) {
        throw await response.json() as ProblemDetail
    }
    return true
}

async function putProfile(token: string, profile: Profile) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/profiles/" + profile.email, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            "Authorization": "Bearer " + token
        },
        body: profile.toJsonObject()
    })
    if (!response.ok) {
        throw await response.json() as ProblemDetail
    }
    return true
}

async function signup(credentials: CredentialCustomer) {
    const response = await fetch( REACT_APP_SERVER_URL + "/API/signup", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: credentials.toJsonObject()
    })
    return response.status
}

const CustomerAPI = {getProfileByEmail, postProfile, putProfile, getProfiles, signup}
export default CustomerAPI