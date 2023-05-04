import APIObject from "./APIObject"


class Profile extends APIObject {
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


}

export default Profile