import HasRole from "../components/authentication/HasRole";
import SidebarLayout from "../components/layout/SidebarLayout";
import Sidebar from "../components/Sidebar";
import Tickets from "../components/homepage/Tickets";
import Staffs from "../components/homepage/Staffs";
import Vendors from "../components/homepage/Vendors";
import Products from "../components/homepage/Products";
import Warranties from "../components/homepage/Warranties";
import {Route, Routes} from "react-router-dom";
import IsAuthenticated from "../components/authentication/IsAuthenticated";
import Profile from "../components/homepage/Profile";
import Welcome from "../components/homepage/Welcome";
import TicketForm from "../components/ticket/TicketForm";
import Stats from "../components/stats/Stats";
import WarrantyForm from "../components/warranty/WarrantyForm";

function HomePage() {
    return (
        <IsAuthenticated>
            <SidebarLayout>
                <SidebarLayout.Main>
                    <HasRole role={"Manager"} key={"manager"}>
                        <Routes>
                            <Route path={"/stats"} element={<p><Stats/></p>} />
                            <Route path={"/tickets"} element={<Tickets />} />
                            <Route path={"/staff"} element={<Staffs />} />
                            <Route path={"/vendors"} element={<Vendors />} />
                            <Route path={"/products"} element={<Products />} />
                            <Route path={"/warranties"} element={<Warranties />} />
                        </Routes>
                    </HasRole>
                    <HasRole role={"Client"} key={"client"}>
                        <Routes>
                            <Route path={"/warranties"} element={<Warranties />} />
                            <Route path={"/tickets"} element={<Tickets />} />
                            <Route path={"/tickets/add"} element={<TicketForm />} />
                        </Routes>
                    </HasRole>
                    <HasRole role={"Expert"} key={"expert"}>
                        <Routes>
                            <Route path={"/tickets"} element={<Tickets />} />
                        </Routes>
                    </HasRole>
                    <HasRole role={"Vendor"} key={"vendor"}>
                        <Routes>
                            <Route path={"/warranties"} element={<Warranties />} />
                            <Route path={"/warranties/add"} element={<WarrantyForm />} />
                        </Routes>
                    </HasRole>
                    <IsAuthenticated>
                        <Routes>
                            <Route path={"/profile"} element={<Profile />} />
                            <Route path={"/"} element={<Welcome />} />
                        </Routes>
                    </IsAuthenticated>
                </SidebarLayout.Main>
                <SidebarLayout.Sidebar>
                    <Sidebar />
                </SidebarLayout.Sidebar>
            </SidebarLayout>
        </IsAuthenticated>
    )
}

export default HomePage