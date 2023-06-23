import Product from "../../classes/Product";
import ProblemDetail from "../../classes/ProblemDetail";

const { REACT_APP_SERVER_URL } = process.env;

async function getAllProducts(token: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/products", {
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    if (response.ok) {
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
    if (response.ok) {
        return await response.json() as Product
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function postProduct(token: string, product: Product) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/products", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json"
        },
        body: product.toJsonObject()
    })
    if (!response.ok) {
        throw ProblemDetail.fromJSON(await response.json())
    }
}

const ProductAPI = {getAllProducts, getProductByEAN, postProduct}
export default ProductAPI


