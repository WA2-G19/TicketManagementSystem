import {Col, Container, Row} from "react-bootstrap";
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
        <>
            <Row>
                <Col xs={5} id="sidebar-wrapper">
                    {_sidebar}
                </Col>
                <Col xs={10} id="page-content-wrapper">
                    {_main}
                </Col>
            </Row>
        </>
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