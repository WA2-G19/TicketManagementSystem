import Navbar from "../Navbar";
import {Container} from "react-bootstrap";
import Layout from "../Layout";
import {AuthenticationContextProvider} from "../../contexts/Authentication";

function ClientPage() {
    return <AuthenticationContextProvider>
        <>
            <Navbar/>
            <Container><Layout/></Container>
        </>
    </AuthenticationContextProvider>

}

export default ClientPage