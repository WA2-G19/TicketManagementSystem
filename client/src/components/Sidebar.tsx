import {ListGroup, Nav} from "react-bootstrap";
import HasRole from "./authentication/HasRole";
import {Dispatch, useState} from "react";
import {ClientSideBar, ExpertSideBar, ManagerSideBar, VendorSideBar} from "../utils/pageSwitch";

interface SidebarProps{
    active: string;
    setActive: Dispatch<string>
}


function Sidebar(props: SidebarProps) {
    return <>
        <Nav className="col-md-12 d-none d-md-block bg-light sidebar" style={{display: "flex", height: "100vh"}}>
            <div className="sidebar-sticky"></div>
            <HasRole role={["Manager"]} key={"manager"}>
                <ListGroup>
                    {Object.keys(ManagerSideBar).filter(item => isNaN(Number(item))).map((e) => {
                        return <ListItem active={props.active} setActive={props.setActive} key={e} item={e}/>
                    })}
                </ListGroup>
            </HasRole>
            <HasRole role={["Client"]} key={"client"}>
                <ListGroup>
                    {Object.keys(ClientSideBar).filter(item => isNaN(Number(item))).map((e) => {
                        return <ListItem active={props.active} setActive={props.setActive} key={e} item={e}/>
                    })}
                </ListGroup>
            </HasRole>
            <HasRole role={["Expert"]} key={"expert"}>
                <ListGroup>
                    {Object.keys(ExpertSideBar).filter(item => isNaN(Number(item))).map((e) => {
                        return <ListItem active={props.active} setActive={props.setActive} key={e} item={e}/>
                    })}
                </ListGroup>
            </HasRole>
            <HasRole role={["Vendor"]} key={"vendor"}>
                <ListGroup>
                    {Object.keys(VendorSideBar).filter(item => isNaN(Number(item))).map((e) => {
                        return <ListItem active={props.active} setActive={props.setActive} key={e} item={e}/>
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