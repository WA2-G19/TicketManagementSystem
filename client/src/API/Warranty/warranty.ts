import {Warranty} from "../../classes/Warranty";

const {REACT_APP_SERVER_URL} = process.env;

async function getAllWarranty(token: string | undefined) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/warranty", {
            headers: {
                "Authorization": "Bearer " + token
            }
        })
        if (response.ok) {
            return await response.json() as Array<Warranty>
        } else {
            return undefined
        }
    } catch (e) {
        throw e
    }

}

async function getProductByID(token: string, id: string) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/products/" + id, {
            headers: {
                "Authorization": "Bearer " + token
            }
        })
        if (response.ok) {
            return await response.json() as Warranty
        } else {
            return undefined
        }
    } catch (e) {
        throw e
    }

}

const WarrantyAPI = {getAllWarranty, getProductByID}
export default WarrantyAPI