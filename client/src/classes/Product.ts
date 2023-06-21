class Product {
    ean: string
    name: string
    brand?: string

    constructor(ean: string, name: string, brand?: string){
        this.ean = ean
        this.name = name
        this.brand = brand
    }
}

export default Product