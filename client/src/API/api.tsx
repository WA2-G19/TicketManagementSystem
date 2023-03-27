




async function getAllProducts(): Promise<Response>{
    
    const response = await fetch("products")
    return response

}

async function getProductByEAN(ean: string): Promise<Response>{
 
    const response = await fetch("products/" + ean )
    return response
    
}


const API = {getAllProducts, getProductByEAN}
export default API
