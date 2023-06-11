import HasRole from "../components/authentication/HasRole";
import SidebarLayout from "../components/layout/SidebarLayout";
import Sidebar from "../components/Sidebar";
import {useState} from "react";
import {ClientSideBar, ExpertSideBar, ManagerSideBar, VendorSideBar} from "../utils/pageSwitch";
import ClientProfile from "../components/profiles/ClientProfile";
import {Tickets} from "../components/tickets/Ticket";
import {useAuthentication} from "../contexts/Authentication";
import ExpertProfile from "../components/profiles/ExpertProfile";
import ManagerProfile from "../components/profiles/ManagerProfile";
import VendorProfile from "../components/profiles/VendorProfile";

function HomePage() {
    const auth = useAuthentication()
    const token = auth.user?.token
    const [active, setActive] = useState("Profile")
    return <SidebarLayout>
        <SidebarLayout.Sidebar>
            <Sidebar active={active} setActive={setActive}/>
        </SidebarLayout.Sidebar>
        <SidebarLayout.Main>
            <HasRole role={["Manager"]} key={"manager"}>
                {switchManager(active, token)}
            </HasRole>
            <HasRole role={["Client"]} key={"client"}>
                {switchClient(active, token)}
            </HasRole>
            <HasRole role={["Expert"]} key={"expert"}>
                {switchExpert(active, token)}
            </HasRole>
            <HasRole role={["Vendor"]} key={"vendor"}>
                {switchVendor(active, token)}
            </HasRole>
        </SidebarLayout.Main>
    </SidebarLayout>
}

function switchClient(active: string, token: string | undefined): JSX.Element {
    switch (active) {
        case ClientSideBar[ClientSideBar.Profile]:
            return <ClientProfile/>
        case ClientSideBar[ClientSideBar.Tickets]:
            return <Tickets token={token}/>
        default:
            return <></>
    }
}

function switchExpert(active: string, token: string | undefined): JSX.Element {
    switch (active) {
        case ExpertSideBar[ExpertSideBar.Profile]:
            return <ManagerProfile/>
        case ExpertSideBar[ExpertSideBar.Tickets]:
            return <Tickets token={token}/>
        default:
            return <></>
    }
}

function switchManager(active: string, token: string | undefined): JSX.Element {
    switch (active) {
        case ManagerSideBar[ManagerSideBar.Profile]:
            return <ExpertProfile/>
        case ManagerSideBar[ManagerSideBar.Tickets]:
            return <Tickets token={token}/>
        default:
            return <></>
    }
}

function switchVendor(active: string, token: string | undefined): JSX.Element {
    switch (active) {
        case VendorSideBar[VendorSideBar.Profile]:
            return <VendorProfile/>
        default:
            return <></>
    }
}


export default HomePage