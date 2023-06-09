import Navbar from "../Navbar";
import {Col, Container, Row} from "react-bootstrap";
import {Children} from "react";

function SidebarLayout({ children }: { children: JSX.Element[] | JSX.Element}): JSX.Element {
    let _sidebar = null, _main = null;

    Children.forEach(children, child => {
        if (child.type === Sidebar) {
            _sidebar = child
        }
        if (child.type === Main) {
            _main = child
        }
    })

    return (
        <>
            <Navbar/>
            <Container fluid>
                <Row>
                    <Col className={"d-none d-md-block col-md-3 bg-light"}>
                        {_sidebar}
                    </Col>
                    <Col className={"col-sm-12 col-md-9"}>
                        {_main}
                    </Col>
                </Row>
            </Container>
        </>
    )
}

function Sidebar({ children }: { children: JSX.Element[] | JSX.Element}): JSX.Element {
    return (
        <>
            {children}
        </>
    )
}

function Main({ children }: { children: JSX.Element[] | JSX.Element}): JSX.Element {
    return (
        <>
            {children}
        </>
    )
}

SidebarLayout.Sidebar = Sidebar
SidebarLayout.Main = Main

export default SidebarLayout