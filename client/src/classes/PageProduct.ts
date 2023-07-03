import Product from "./Product"


class PageProduct {
    products: Array<Product>
    totalPages: number

    constructor(products: Array<Product>, totalPages: number){
        this.products = products
        this.totalPages = totalPages
        
    }

    toJsonObject(): string {
        return JSON.stringify({
            "products": this.products,
            "totalPages": this.totalPages
        })
    }
}

export default PageProduct