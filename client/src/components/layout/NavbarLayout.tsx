import Navbar from "../Navbar";
import {Container} from "react-bootstrap";

function NavbarLayout({ children }: { children: JSX.Element[] | JSX.Element}): JSX.Element {
    return (
        <>
            <Navbar/>
            <Container fluid>
                {children}
            </Container>
        </>
    )
}

export default NavbarLayout