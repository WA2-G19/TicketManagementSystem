import {Profile, CredentialCustomer} from "../../classes/Profile"
import dotenv from "dotenv"

const { REACT_APP_SERVER_URL } = process.env;


async function getProfileByEmail(token: string, email: string) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/profiles/" + email,
            {
                headers: {
                    "Authorization": "Bearer " + token
                }
            })
        if (response.ok)
            return await response.json() as Profile
        else
            return undefined

    } catch (e) {
        throw e
    }
}


async function getProfiles(token: string) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/profiles",
            {
                headers: {
                    "Authorization": "Bearer " + token
                }
            })
        if (response.ok)
            return await response.json() as Array<Profile>
        else
            return undefined

    } catch (e) {
        throw e
    }
}


async function postProfile(token: string, profile: Profile) {
    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/profiles", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                "Authorization": "Bearer " + token
            },
            body: profile.toJsonObject()
        })
        return response.ok
    } catch (e) {
        throw e
    }
}

async function putProfile(token: string, profile: Profile) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/profiles/" + profile.email, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json',
                "Authorization": "Bearer " + token
            },
            body: profile.toJsonObject()
        })
        return response.ok
    } catch (e) {
        throw e
    }

}


async function signup(credentials: CredentialCustomer) {

    try {
        const response = await fetch( REACT_APP_SERVER_URL + "/API/signup", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: credentials.toJsonObject()
        })
        return response.ok
    } catch (e) {
        throw e
    }

}

const CustomerAPI = {getProfileByEmail, postProfile, putProfile, getProfiles, signup}
export default CustomerAPI