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
    throw await response.json() as ProblemDetail
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
    throw await response.json() as ProblemDetail
}

const ProductAPI = {getAllProducts, getProductByEAN}
export default ProductAPI


