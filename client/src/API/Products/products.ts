import Product from "../../classes/Product";
import ProblemDetail from "../../classes/ProblemDetail";

const { REACT_APP_SERVER_URL } = process.env;

async function getAllProducts(token: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/products", {
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    if(response.ok) {
        return await response.json() as Array<Product>
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function getProductByEAN(token: string, ean: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/products/" + ean, {
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    if(response.ok) {
        return await response.json() as Product
    }
    throw ProblemDetail.fromJSON(await response.json())
}

const ProductAPI = {getAllProducts, getProductByEAN}
export default ProductAPI


