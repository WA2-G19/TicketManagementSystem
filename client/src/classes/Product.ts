class Product {
    ean: string
    name: string
    brand?: string

    constructor(ean: string, name: string, brand?: string){
        this.ean = ean
        this.name = name
        this.brand = brand
    }

    toJsonObject(): string {
        return JSON.stringify({
            "ean": this.ean,
            "name": this.name,
            "brand": this.brand
        })
    }
}

export default Product