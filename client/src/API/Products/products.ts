import Product from "../../classes/Product";

const { REACT_APP_SERVER_URL } = process.env;

async function getAllProducts(token: string) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/products", {
            headers: {
                "Authorization": "Bearer " + token
            }
        })
        if(response.ok) {
            return await response.json() as Array<Product>
        } else {
           return undefined
        }
    } catch (e) {
        throw e
    }

}

async function getProductByEAN(token: string, ean: string) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/products/" + ean, {
            headers: {
                "Authorization": "Bearer " + token
            }
        })
        if(response.ok) {
            return await response.json() as Product
        } else {
            return undefined
        }
    } catch (e) {
        throw e
    }

}

const ProductAPI = {getAllProducts, getProductByEAN}
export default ProductAPI


