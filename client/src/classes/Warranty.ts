import APIObject from "./APIObject";

export class Warranty extends APIObject {
    id: number
    productEan: string
    vendorEmail: string
    customerEmail: string
    creationTimestamp: string
    activationTimestamp: string
    duration: string

    constructor(id: number, productEan: string, vendorEmail: string, customerEmail: string, creationTimestamp: string, activationTimestamp: string, duration: string) {
        super()
        this.id = id
        this.productEan = productEan
        this.vendorEmail = vendorEmail
        this.customerEmail = customerEmail
        this.creationTimestamp = creationTimestamp
        this.activationTimestamp = activationTimestamp
        this.duration = duration
    }

    toJSONObject(): string {

        const warrantyMap = {
            "id": this.id,
            "productEan": this.productEan,
            "vendorEmail": this.vendorEmail,
            "customerEmail": this.customerEmail,
            "creationTimestamp": this.creationTimestamp,
            "activationTimestamp": this.activationTimestamp,
            "duration": this.duration
        }

        return JSON.stringify(warrantyMap)
    }
}

export class Duration {
    minutes: number
    seconds: number
    hours: number

    constructor(hours: number, minutes: number, seconds: number) {
        this.hours = hours
        this.minutes = minutes
        this.seconds = seconds
    }

    static fromString(durationString: string): Duration {
        const regexH = /PT(\d+)H(\d+)M(\d+)S/
        const regexM = /PT(\d+)M(\d+)S/
        const matchesH = durationString.match(regexH)
        const matchesM = durationString.match(regexM)

        if (matchesH && matchesH.length === 4) {
            const hours = parseInt(matchesH[1], 10)
            const minutes = parseInt(matchesH[2], 10)
            const seconds = parseInt(matchesH[3], 10)
            return new Duration(hours, minutes, seconds);
        }

        if (matchesM && matchesM.length === 3) {
            const minutes = parseInt(matchesM[1], 10);
            const seconds = parseInt(matchesM[2], 10);
            return new Duration(0, minutes, seconds);
        }

        throw new Error('Invalid duration string');
    }
}