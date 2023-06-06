import APIObject from "./APIObject";

export class Login extends APIObject {
    username: string
    password: string

    constructor(username: string, password: string){
        super()
        this.username = username
        this.password = password
    }

}