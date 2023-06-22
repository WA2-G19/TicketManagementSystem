import {CredentialVendor, Vendor} from "../../classes/Profile";
import ProblemDetail from "../../classes/ProblemDetail";

const {REACT_APP_SERVER_URL} = process.env;

async function getVendors(token: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/vendor/profiles", {
        headers: {
            "Authorization": "Bearer " + token,
            "Accept": "Application/Json"
        }
    })
    if (response.ok) {
        return await response.json() as Array<Vendor>
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function getVendor(token: string, email: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/vendor/" + email, {
        headers: {
            "Authorization": "Bearer " + token,
            "Accept": "Application/Json"
        }
    })
    if (response.ok) {
        return await response.json() as Vendor
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function createVendor(token: string, credentials: CredentialVendor) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/vendor/", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token,
            "Accept": "Application/Json",
            "Content": "Application/Json"
        },
        body: credentials.toJsonObject()
    })
    if (!response.ok) {
        throw ProblemDetail.fromJSON(await response.json())
    }
}

const VendorAPI = {getVendors, getVendor, createVendor}
export default VendorAPI