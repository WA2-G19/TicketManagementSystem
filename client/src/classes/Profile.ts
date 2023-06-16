import APIObject from "./APIObject"


export class Profile extends APIObject {
    name: string
    surname: string
    email: string
    address: string

    constructor(email: string, name: string, surname: string, address: string) {
        super()
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

export class CredentialCustomer extends APIObject {

    profile: Profile
    password: string

    constructor(profile: Profile, password: string) {
        super()
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

export class Staff extends APIObject {
    name: string
    surname: string
    email: string
    type: StaffType
    skills: Array<string>
    avgTime: number | undefined
    ticketClosed: number | undefined

    constructor(email: string, name: string, surname: string, type: StaffType, skills: Array<string>) {
        super()
        this.name = name
        this.surname = surname
        this.email = email
        this.type = type
        this.skills = skills
    }

    toJsonObject(): string {
        return JSON.stringify({
            email: this.email,
            name: this.name,
            surname: this.surname,
            type: this.type,
            skills: this.skills
        })
    }
}

export class CredentialStaff extends APIObject {

    staff: Staff
    password: string


    constructor(staff: Staff, password: string) {
        super()
        this.staff = staff
        this.password = password
    }

    toJsonObject(): string {

        return JSON.stringify({
            staffDTO: this.staff,
            password: this.password
        })
    }

}

export class Vendor extends APIObject {

    email: string
    businessName: string
    phoneNumber: string
    address: string

    constructor(email: string, businessName: string, phoneNumber: string, address: string) {
        super()
        this.email = email
        this.businessName = businessName
        this.phoneNumber = phoneNumber
        this.address = address
    }

    toJsonObject(): string {
        return JSON.stringify({
            email: this.email,
            businessName: this.businessName,
            phoneNumber: this.phoneNumber,
            address: this.address,
        })
    }
}

export class CredentialVendor extends APIObject {

    vendor: Vendor
    password: string


    constructor(vendor: Vendor, password: string) {
        super()
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