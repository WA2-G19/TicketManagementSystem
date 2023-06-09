import Navbar from "../components/Navbar";
import {Container} from "react-bootstrap";
import LoginForm from "../components/loginpage/LoginForm";
import {AuthenticationContextProvider} from "../contexts/Authentication";

function LoginPage() {
    return <AuthenticationContextProvider>
        <>
            <Navbar/>
            <Container><LoginForm/></Container>
        </>
    </AuthenticationContextProvider>
}

export default LoginPage