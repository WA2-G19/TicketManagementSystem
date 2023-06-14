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
    days: number
    weeks: number
    months: number
    years: number

    constructor(years: number = 0, months: number = 0, weeks: number = 0, days: number = 0, hours: number = 0, minutes: number = 0, seconds: number = 0) {
        this.years = years
        this.months = months
        this.weeks = weeks
        this.days = days
        this.hours = hours
        this.minutes = minutes
        this.seconds = seconds
    }

    toString(): string {
        const pieces = [
            this.years > 0 ? `${this.years} Y` : "",
            this.months > 0 ? `${this.months} M` : "",
            this.weeks > 0 ? `${this.weeks} W` : "",
            this.days > 0 ? `${this.days} D` : "",
            this.hours > 0 ? `${this.hours} H` : "",
            this.minutes > 0 ? `${this.minutes} M` : "",
            this.seconds > 0 ? `${this.seconds} S` : "",
        ]

        return pieces.filter(p => p !== "").join(" ")
    }

    static fromString(durationString: string): Duration {
        const integer = /[1-9][0-9]*|0/
        const float = new RegExp("(" + integer.source + ")([.,][0-9]+)?")
        const validation = new RegExp("P((?<years>" + float.source + ")Y)?((?<months>" + integer.source + ")M)?((?<days>" + integer.source + ")D)?(T((?<hours>" + integer.source + ")H)?((?<minutes>" + integer.source + ")M)?((?<seconds>" + float.source + ")S)?)?|P(?<weeks>" + integer.source + ")W")
        const match = durationString.match(validation)
        if (!match || !match.groups) {
            throw new Error('Invalid duration string');
        }

        return new Duration(
            parseFloat(match.groups["years"]) | 0,
            parseInt(match.groups["months"]) | 0,
            parseInt(match.groups["weeks"]) | 0,
            parseInt(match.groups["days"]) | 0,
            parseInt(match.groups["hours"]) | 0,
            parseInt(match.groups["minutes"]) | 0,
            parseFloat(match.groups["seconds"]) | 0
        )
    }
}