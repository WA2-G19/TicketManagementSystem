import Navbar from "../Navbar";
import {Container} from "react-bootstrap";
import {AuthenticationContextProvider} from "../../contexts/Authentication";
import RegistrationForm from "../signuppage/RegistrationForm";

function SignupPage() {
    return <AuthenticationContextProvider>
        <>
            <Navbar/>
            <Container><RegistrationForm/></Container>
        </>
    </AuthenticationContextProvider>
}

export default SignupPage