export class WarrantyIn {
    productEan: string
    duration: string

    constructor(productEan: string, duration: string) {
        this.productEan = productEan
        this.duration = duration
        Duration.fromString(duration)
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

export class Duration {
    seconds: number
    minutes: number
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

    addToDate(date: Date): Date {
        const d = new Date(date)
        const years = Math.floor(this.years)
        const months = Math.floor(this.months + this.years - years)
        const days = Math.floor(this.days + this.months - months)
        d.setFullYear(d.getFullYear() + years, d.getMonth() + months, d.getDate() + days)
        const hours = Math.floor(this.hours + this.days - days)
        const minutes = Math.floor(this.minutes + this.hours - hours)
        const seconds = Math.floor(this.seconds + this.minutes - minutes)
        const milliSeconds = Math.floor((this.seconds - seconds) * 1000)
        d.setHours(d.getHours() + hours, d.getMinutes() + minutes, d.getSeconds() + seconds, d.getMilliseconds() + milliSeconds)
        return d
    }

    toFormattedString(): string {
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

    toString(): string {
        const datePieces = [
            this.years > 0 ? `${this.years}Y` : "",
            this.months > 0 ? `${this.months}M` : "",
            this.days > 0 ? `${this.days}D` : "",
        ].filter(p => p !== "")

        const weekPiece = this.weeks > 0 ? `${this.weeks}W` : ""

        const timePieces = [
            this.hours > 0 ? `${this.hours}H` : "",
            this.minutes > 0 ? `${this.minutes}M` : "",
            this.seconds > 0 ? `${this.seconds}S` : "",
        ].filter(p => p !== "")

        if (weekPiece !== "" && (datePieces.length > 0 || timePieces.length > 0)) {
            throw new Error('Invalid duration string');
        }

        if (weekPiece !== "") {
            return `P${weekPiece}`
        }
        return `P${datePieces.join("")}${timePieces.length > 0 ? `T${timePieces.join("")}` : ""}`
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