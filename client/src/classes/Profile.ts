export class Profile {
    name: string
    surname: string
    email: string
    address: string

    constructor(email: string, name: string, surname: string, address: string) {
        this.name = name
        this.surname = surname
        this.email = email
        this.address = address
    }

    toJsonObject(): string {

        return JSON.stringify({
            email: this.email,
            name: this.name,
            surname: this.surname,
            address: this.address
        })
    }
}

export class CredentialCustomer {
    profile: Profile
    password: string

    constructor(profile: Profile, password: string) {
        this.profile = profile
        this.password = password
    }

    toJsonObject(): string {
        return JSON.stringify({
            customerDTO: this.profile,
            password: this.password
        })
    }
}

export enum StaffType {
    Manager,
    Expert
}

export class Staff {
    name: string
    surname: string
    email: string
    type: StaffType
    skills: Array<string>
    avgTime?: number
    ticketsClosed?: number
    ticketsInProgress?: number

    constructor(email: string, name: string, surname: string, type: StaffType, skills: Array<string>, avgTime?: number, ticketsClosed?: number, ticketsInProgress?: number) {
        this.name = name
        this.surname = surname
        this.email = email
        this.type = type
        this.skills = skills
        this.avgTime = avgTime
        this.ticketsClosed = ticketsClosed
        this.ticketsInProgress = ticketsInProgress
    }
}

export class CredentialStaff {
    staff: Staff
    password: string

    constructor(staff: Staff, password: string) {
        this.staff = staff
        this.password = password
    }

    toJsonObject(): string {
        return JSON.stringify({
            staffDTO: {
                ...this.staff,
                avgTime: undefined,
                ticketsClosed: undefined,
                ticketsInProgress: undefined
            },
            password: this.password
        })
    }
}

export class Vendor {
    email: string
    businessName: string
    phoneNumber: string
    address: string

    constructor(email: string, businessName: string, phoneNumber: string, address: string) {
        this.email = email
        this.businessName = businessName
        this.phoneNumber = phoneNumber
        this.address = address
    }
}

export class CredentialVendor {
    vendor: Vendor
    password: string

    constructor(vendor: Vendor, password: string) {
        this.vendor = vendor
        this.password = password
    }

    toJsonObject(): string {
        return JSON.stringify({
            profile: this.vendor,
            password: this.password
        })
    }
}