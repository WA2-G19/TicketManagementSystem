import {Card, Col, Container, Nav, Row} from "react-bootstrap";
import 'bootstrap/dist/css/bootstrap.min.css'
import {Navbar} from "react-bootstrap";
import {useAuthentication} from "../contexts/Authentication";
import {useNavigate} from "react-router-dom";
import IsAuthenticated from "./authentication/IsAuthenticated";
import IsAnonymous from "./authentication/IsAnonymous";


function MyNavbar() {
    const navigate = useNavigate()
    const auth = useAuthentication()

    const logout = () => {
        auth.logout()
            .then(() => navigate("/"))
    }

    const goToHome = () => {
        navigate("/")
    }

    return <Navbar bg="primary" variant="dark">
        <Row style={{width: "100%"}}>
            <Col>
                <Nav>
                    <Nav.Link onClick={() => goToHome()}>Home</Nav.Link>
                    <IsAuthenticated>
                        <Nav.Link onClick={() => logout()}>Logout</Nav.Link>
                    </IsAuthenticated>
                    <IsAnonymous>
                        <Nav.Link onClick={() => navigate("/login")}>Login</Nav.Link>
                    </IsAnonymous>
                </Nav>
            </Col>
            <Col className={"justify-content-start align-self-center text-center"}>
                <Navbar.Brand href="#home">
                        <svg xmlns="http://www.w3.org/2000/svg" width="50" height="30" fill="currentColor"
                             className="bi bi-ticket-detailed-fill" viewBox="0 0 16 16">
                            <path
                                d="M0 4.5A1.5 1.5 0 0 1 1.5 3h13A1.5 1.5 0 0 1 16 4.5V6a.5.5 0 0 1-.5.5 1.5 1.5 0 0 0 0 3 .5.5 0 0 1 .5.5v1.5a1.5 1.5 0 0 1-1.5 1.5h-13A1.5 1.5 0 0 1 0 11.5V10a.5.5 0 0 1 .5-.5 1.5 1.5 0 1 0 0-3A.5.5 0 0 1 0 6V4.5Zm4 1a.5.5 0 0 0 .5.5h7a.5.5 0 0 0 0-1h-7a.5.5 0 0 0-.5.5Zm0 5a.5.5 0 0 0 .5.5h7a.5.5 0 0 0 0-1h-7a.5.5 0 0 0-.5.5ZM4 8a1 1 0 0 0 1 1h6a1 1 0 1 0 0-2H5a1 1 0 0 0-1 1Z"/>
                        </svg>
                        {' '}Ticket Management System
                </Navbar.Brand>
            </Col>
            <Col style={{textAlign: "end"}} className={"justify-content-center align-self-center"}>
                <Navbar.Brand>
                    Welcome from Group 19!
                </Navbar.Brand>
            </Col>
        </Row>
    </Navbar>
}


export default MyNavbar