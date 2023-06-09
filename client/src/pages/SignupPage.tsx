import Navbar from "../components/Navbar";
import {Container} from "react-bootstrap";
import {AuthenticationContextProvider} from "../contexts/Authentication";
import RegistrationForm from "../components/signuppage/RegistrationForm";

function SignupPage() {
    return <AuthenticationContextProvider>
        <>
            <Navbar/>
            <Container><RegistrationForm/></Container>
        </>
    </AuthenticationContextProvider>
}

export default SignupPage