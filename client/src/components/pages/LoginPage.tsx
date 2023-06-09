import Navbar from "../Navbar";
import {Container} from "react-bootstrap";
import LoginForm from "../loginpage/LoginForm";
import {AuthenticationContextProvider} from "../../contexts/Authentication";

function LoginPage() {
    return <AuthenticationContextProvider>
        <>
            <Navbar/>
            <Container><LoginForm/></Container>
        </>
    </AuthenticationContextProvider>
}

export default LoginPage