import {ListGroup, Nav} from "react-bootstrap";
import HasRole from "./authentication/HasRole";
import {useLocation, useNavigate} from "react-router-dom";

interface SidebarLink {
    path: string
    display: string
}

const clientSideBar: Array<SidebarLink> = [{
    path: "/warranties",
    display: "Warranties"
}, {
    path: "/tickets",
    display: "Tickets"
}, {
    path: "/profile",
    display: "Profile"
}]
const managerSideBar: Array<SidebarLink> = [{
    path: "/stats",
    display: "Stats"
}, {
    path: "/tickets",
    display: "Tickets"
}, {
    path: "/staffs",
    display: "Staff Members"
}, {
    path: "/vendors",
    display: "Vendors"
}, {
    path: "/products",
    display: "Products"
}, {
    path: "/warranties",
    display: "Warranties"
}, {
    path: "/skills",
    display: "Skills"
}, {
    path: "/profile",
    display: "Profile"
}]
const expertSideBar: Array<SidebarLink> = [{
    path: "/tickets",
    display: "Tickets"
}, {
    path: "/profile",
    display: "Profile"
}]
const vendorSideBar: Array<SidebarLink> = [{
    path: "/warranties",
    display: "Warranties"
}, {
    path: "/profile",
    display: "Profile"
}]

function Sidebar() {
    return (
        <Nav className={"w-100"}>
            <ListGroup variant={"flush"} className={"w-100"}>
                <HasRole role={"Manager"} key={"manager"}>
                    {
                        managerSideBar.map((e) => <ListItem key={e.path} item={e}/>)
                    }
                </HasRole>
                <HasRole role={"Client"} key={"client"}>
                    {
                        clientSideBar.map((e) => <ListItem key={e.path} item={e}/>)
                    }
                </HasRole>
                <HasRole role={"Expert"} key={"expert"}>
                    {
                        expertSideBar.map((e) => <ListItem key={e.path} item={e}/>)
                    }
                </HasRole>
                <HasRole role={"Vendor"} key={"vendor"}>
                    {
                        vendorSideBar.map((e) => <ListItem key={e.path} item={e}/>)
                    }
                </HasRole>
            </ListGroup>
    </Nav>
    )
}

function ListItem({ item }: {
    item: SidebarLink
}): JSX.Element {
    const navigate = useNavigate()
    const { pathname } = useLocation()
    const active = item.path === pathname
    return <ListGroup.Item onClick={() => {
        navigate(item.path)
    }} active={active} action={true} className={!active ? "bg-transparent" : ""}>
        <Nav.Link className={"link-dark"}>{item.display}</Nav.Link>
    </ListGroup.Item>

}

export default Sidebar