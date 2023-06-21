import {Login} from "../../classes/Login";
import ProblemDetail from "../../classes/ProblemDetail";

const { REACT_APP_SERVER_URL } = process.env;

async function login(loginDTO: Login) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/login", {
        method: "POST",
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(loginDTO)
    })
    if (!response.ok) {
        throw await response.json() as ProblemDetail
    }
    return await response.text()
}

const ProfileAPI = {login}
export default ProfileAPI