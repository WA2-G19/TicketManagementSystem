import {Col, Row} from "react-bootstrap";
import {Children} from "react";

function SidebarLayout({children}: { children: JSX.Element[] | JSX.Element }): JSX.Element {
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
        <Row className={"vh-100"}>
            <Col className={"col-md-3 d-none d-md-flex bg-light p-0"}>
                {_sidebar}
            </Col>
            <Col className={"col-xs-12 col-md-9 p-0"}>
                {_main}
            </Col>
        </Row>
    )
}

function Sidebar({children}: { children: JSX.Element[] | JSX.Element }): JSX.Element {
    return (
        <>
            {children}
        </>
    )
}

function Main({children}: { children: JSX.Element[] | JSX.Element }): JSX.Element {
    return (
        <>
            {children}
        </>
    )
}

SidebarLayout.Sidebar = Sidebar
SidebarLayout.Main = Main

export default SidebarLayout