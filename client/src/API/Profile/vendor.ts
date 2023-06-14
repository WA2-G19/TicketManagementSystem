import {CredentialStaff, CredentialVendor, Staff, Vendor} from "../../classes/Profile";

const {REACT_APP_SERVER_URL} = process.env;

async function getVendors(token: string | undefined) {
    try {

        const response = await fetch(REACT_APP_SERVER_URL + "/API/vendor/profiles", {
            headers: {
                "Authorization": "Bearer " + token,
                "Accept": "Application/Json"
            }
        })
        if (response.ok) {
            return await response.json() as Array<Vendor>
        } else {
            return undefined
        }

    } catch (e) {
        throw e
    }
}

async function getVendor(token: string, email: string) {
    try {

        const response = await fetch(REACT_APP_SERVER_URL + "/API/vendor/" + email, {
            headers: {
                "Authorization": "Bearer " + token,
                "Accept": "Application/Json"
            }
        })
        if (response.ok) {
            return await response.json() as Vendor
        } else {
            return undefined
        }

    } catch (e) {
        throw e
    }
}

async function createVendor(token: string, credentials: CredentialVendor) {
    try {

        const response = await fetch(REACT_APP_SERVER_URL + "/API/vendor/", {
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

const VendorAPI = {getVendors, getVendor, createVendor}
export default VendorAPI