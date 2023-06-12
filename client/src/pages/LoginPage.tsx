import LoginForm from "../components/loginpage/LoginForm";
import {Navigate} from "react-router-dom";
import React from "react";
import {useAuthentication} from "../contexts/Authentication";

function LoginPage(): JSX.Element {
    const auth = useAuthentication()
    if (auth.isLoggedIn)
        return (<Navigate to={"/"} />)
    return (
        <LoginForm/>
    )
}

export default LoginPage