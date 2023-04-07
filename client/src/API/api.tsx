
async function getAllProducts(): Promise<Response>{
    
    const response = await fetch("/API/products")
    return response

}

async function getProductByEAN(ean: string): Promise<Response>{
    const response = await fetch("/API/products/" + ean )
    return response
    
}




const API = {getAllProducts, getProductByEAN}
export default API
