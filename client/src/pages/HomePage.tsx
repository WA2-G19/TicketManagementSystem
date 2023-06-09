import Navbar from "../components/Navbar";
import {Container} from "react-bootstrap";
import {AuthenticationContextProvider} from "../contexts/Authentication";
import LoginPage from "./LoginPage";

function HomePage() {
    return <AuthenticationContextProvider>
        <>
            <Navbar/>
            <Container></Container>
        </>
    </AuthenticationContextProvider>
}

export default HomePage