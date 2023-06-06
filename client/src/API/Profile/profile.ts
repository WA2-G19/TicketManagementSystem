import {Login} from "../../classes/Login";

const { REACT_APP_SERVER_URL } = process.env;

async function login(loginDTO: Login) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/login", {
            method: "POST",
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginDTO)
        })
        if (response.ok) {
            return await response.text()
        }
    } catch (e) {
        throw e
    }

}

const ProfileAPI = {login}
export default ProfileAPI