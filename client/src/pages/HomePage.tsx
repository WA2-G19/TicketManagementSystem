import HasRole from "../components/authentication/HasRole";
import SidebarLayout from "../components/layout/SidebarLayout";
import Sidebar from "../components/Sidebar";
import {useState} from "react";
import {ClientSideBar} from "../utils/pageSwitch";
import ClientProfile from "../components/profiles/ClientProfile";
import Tickets from "../components/tickets/Ticket";
import {useAuthentication} from "../contexts/Authentication";

function HomePage() {
    const auth = useAuthentication()
    const token = auth.user?.token
    const [active, setActive] = useState("")

    return <SidebarLayout>
        <SidebarLayout.Sidebar>
            <Sidebar active={active} setActive={setActive}/>
        </SidebarLayout.Sidebar>
        <SidebarLayout.Main>
            <HasRole role={"Manager"} key={"manager"}>
                <p>Manager page</p>
            </HasRole>
            <HasRole role={"Client"} key={"client"}>
                {switchClient(active, token)}
            </HasRole>
            <HasRole role={"Expert"} key={"expert"}>
                <p>Expert page</p>
            </HasRole>
            <HasRole role={"Vendor"} key={"vendor"}>
                <p>Vendor page</p>
            </HasRole>
        </SidebarLayout.Main>
    </SidebarLayout>
}

function switchClient(active: string,token: string | undefined): JSX.Element {
    switch (active) {
        case ClientSideBar[ClientSideBar.Profile]:
            return <ClientProfile/>
        case ClientSideBar[ClientSideBar.Tickets]:
            return <Tickets token ={token}/>
        default:
            return <></>
    }
}

export default HomePage