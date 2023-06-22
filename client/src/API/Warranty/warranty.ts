import {WarrantyOut, WarrantyIn} from "../../classes/Warranty";
import ProblemDetail from "../../classes/ProblemDetail";

const {REACT_APP_SERVER_URL} = process.env;

async function getAllWarranty(token: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/warranty", {
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    if (response.ok) {
        return await response.json() as Array<WarrantyOut>
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function getWarrantyByID(token: string, id: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/warranty/" + id, {
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    if (response.ok) {
        return await response.json() as WarrantyOut
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function postWarranty(token: string, warranty: WarrantyIn) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/warranty", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        },
        body: warranty.toJSONObject()
    })
    if (response.ok) {
        return await response.json() as WarrantyOut
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function activateWarranty(token: string, warrantyId: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/warranty/" + encodeURIComponent(warrantyId) + "/activate", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    if (response.ok) {
        return await response.json() as WarrantyOut
    }
    throw ProblemDetail.fromJSON(await response.json())
}

const WarrantyAPI = {getAllWarranty, getWarrantyByID, postWarranty, activateWarranty}
export default WarrantyAPI