import {CredentialStaff, Staff} from "../../classes/Profile";

const {REACT_APP_SERVER_URL} = process.env;

async function getProfiles(token: string | undefined) {
    try {

        const response = await fetch(REACT_APP_SERVER_URL + "/API/staff/profiles", {
            headers: {
                "Authorization": "Bearer " + token,
                "Accept": "Application/Json"
            }
        })
        if (response.ok) {
            return await response.json() as Array<Staff>
        } else {
            return undefined
        }

    } catch (e) {
        throw e
    }
}

async function getProfile(token: string, email: string) {
    try {

        const response = await fetch(REACT_APP_SERVER_URL + "/API/staff/" + email, {
            headers: {
                "Authorization": "Bearer " + token,
                "Accept": "Application/Json"
            }
        })
        if (response.ok) {
            return await response.json() as Staff
        } else {
            return undefined
        }

    } catch (e) {
        throw e
    }
}

async function createExpert(token: string, credentials: CredentialStaff) {
    try {

        const response = await fetch(REACT_APP_SERVER_URL + "/API/staff/createExpert", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Accept": "Application/Json",
                "Content": "Application/Json"
            },
            body: credentials.toJsonObject()
        })
        return response.ok

    } catch (e) {
        throw e
    }
}

const StaffAPI = {getProfile, createExpert, getProfiles}
export default StaffAPI