import APIObject from "./APIObject"


class Profile extends APIObject {
    name: string
    surname: string
    email: string

    constructor(email: string, name: string, surname: string) {
        super()
        this.name = name
        this.surname = surname
        this.email = email
    }


}

export default Profile