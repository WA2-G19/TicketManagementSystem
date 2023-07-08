export class WarrantyIn {
    productEan: string
    duration: string

    constructor(productEan: string, duration: string) {
        this.productEan = productEan
        this.duration = duration
        Period.fromString(duration)
    }

    toJSONObject(): string {
        return JSON.stringify({
            "productEan": this.productEan,
            "duration": this.duration
        })
    }
}

export class WarrantyOut {
    id: number
    productEan: string
    vendorEmail: string
    customerEmail: string
    creationTimestamp: string
    activationTimestamp: string
    duration: string

    constructor(id: number, productEan: string, vendorEmail: string, customerEmail: string, creationTimestamp: string, activationTimestamp: string, duration: string) {
        this.id = id
        this.productEan = productEan
        this.vendorEmail = vendorEmail
        this.customerEmail = customerEmail
        this.creationTimestamp = creationTimestamp
        this.activationTimestamp = activationTimestamp
        this.duration = duration
    }
}

export class Period {
    days: number
    months: number
    years: number

    constructor(years: number = 0, months: number = 0, days: number = 0) {
        this.years = years
        this.months = months
        this.days = days
    }

    addToDate(date: Date): Date {
        const d = new Date(date)
        const years = Math.floor(this.years)
        const months = Math.floor(this.months + this.years - years)
        const days = Math.floor(this.days + this.months - months)
        d.setFullYear(d.getFullYear() + years, d.getMonth() + months, d.getDate() + days)
        return d
    }

    toFormattedString(): string {
        const pieces = [
            this.years > 0 ? `${this.years} Y` : "",
            this.months > 0 ? `${this.months} M` : "",
            this.days > 0 ? `${this.days} D` : ""
        ]

        return pieces.filter(p => p !== "").join(" ")
    }

    toString(): string {
        const datePieces = [
            this.years > 0 ? `${this.years}Y` : "",
            this.months > 0 ? `${this.months}M` : "",
            this.days > 0 ? `${this.days}D` : "",
        ].filter(p => p !== "")
        return `P${datePieces.join("")}`
    }

    static fromString(durationString: string): Period {
        const integer = /[1-9][0-9]*|0/
        const validation = new RegExp("P(?<years>" + integer.source + ")Y)?((?<months>" + integer.source + ")M)?((?<days>" + integer.source + ")D")
        const match = durationString.match(validation)
        if (!match || !match.groups) {
            throw new Error('Invalid duration string');
        }

        return new Period(
            parseFloat(match.groups["years"]) | 0,
            parseInt(match.groups["months"]) | 0,
            parseInt(match.groups["days"]) | 0,
        )
    }
}