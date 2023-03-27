
import APIObject from "./APIObject"



class Product extends APIObject {
    ean: string
    name: string
    brand?: string

    constructor(ean: string, name: string, brand?: string){
        super()
        this.ean = ean
        this.name = name
        this.brand = brand
    }

    

}

export default Product