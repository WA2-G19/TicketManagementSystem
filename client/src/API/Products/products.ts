import Product from "../../classes/Product";
import PageProduct from "../../classes/PageProduct";
import ProblemDetail from "../../classes/ProblemDetail";

const { REACT_APP_SERVER_URL } = process.env;

async function getAllProducts(token: string, page: number) {
    const response = await fetch(REACT_APP_SERVER_URL + `/API/products?page=${page}&size=9`, {
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    if (response.ok) {
        return await response.json() as PageProduct
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


