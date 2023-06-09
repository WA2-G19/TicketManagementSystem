import {ListGroup, Nav} from "react-bootstrap";
import HasRole from "./authentication/HasRole";
import {Dispatch, useState} from "react";

const clientSideBar: Array<string> = ["Warranty", "Tickets", "Ticket", "Add ticket", "Profile"]
const managerSideBar: Array<string> = ["Stats", "Tickets", "Staff", "Vendor", "Products", "Warranty", "Profile"]
const expertSideBar: Array<string> = ["Tickets", "Ticket", "Profile"]
const vendorSideBar: Array<string> = ["Warrenties", "Add warranty", "Warranty", "Profile"]



function Sidebar() {
    const [active, setActive] = useState("")
    return <>
        <Nav className="col-md-12 d-none d-md-block bg-light sidebar" style={{display: "flex", height: "100vh"}}>
            <div className="sidebar-sticky"></div>
            <HasRole role={"Manager"} key={"manager"}>
                <ListGroup>
                    {managerSideBar.map((e) => {
                        return <ListItem active={active} setActive={setActive} key={e} item={e}/>
                    })}
                </ListGroup>
            </HasRole>
            <HasRole role={"Client"} key={"client"}>
                <ListGroup>
                    {clientSideBar.map((e) => {
                        return <ListItem active={active} setActive={setActive} key={e} item={e}/>
                    })}
                </ListGroup>
            </HasRole>
            <HasRole role={"Expert"} key={"expert"}>
                <ListGroup>
                    {expertSideBar.map((e) => {
                        return <ListItem active={active} setActive={setActive} key={e} item={e}/>
                    })}
                </ListGroup>
            </HasRole>
            <HasRole role={"Vendor"} key={"vendor"}>
                <ListGroup>
                    {vendorSideBar.map((e) => {
                        return <ListItem active={active} setActive={setActive} key={e} item={e}/>
                    })}
                </ListGroup>
            </HasRole>
        </Nav>
    </>
}

interface ListItemProps {
    item: string
    active: string
    setActive: Dispatch<string>
}

function ListItem(props: ListItemProps): JSX.Element {
    return <ListGroup.Item onClick={() => {
        props.setActive(props.item)
    }} active={props.item == props.active}>
        <Nav.Link>{props.item}</Nav.Link>
    </ListGroup.Item>

}

export default Sidebar