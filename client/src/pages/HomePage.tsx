import HasRole from "../components/authentication/HasRole";
import SidebarLayout from "../components/layout/SidebarLayout";
import Sidebar from "../components/Sidebar";
import IsAuthenticated from "../components/authentication/IsAuthenticated";
import {Route, Routes} from "react-router-dom";


function HomePage(): JSX.Element {
    return (
        <IsAuthenticated>
            <SidebarLayout>
                <SidebarLayout.Main>
                    <HasRole role={"Manager"} key={"manager"}>
                        <p>Manager page</p>
                        <Routes>
                            <Route path={"/stats"} element={<p>Stats</p>} />
                            <Route path={"/tickets"} element={<p>Tickets</p>} />
                            <Route path={"/staff"} element={<p>Staff</p>} />
                            <Route path={"/vendors"} element={<p>Vendors</p>} />
                            <Route path={"/products"} element={<p>Products</p>} />
                            <Route path={"/warranties"} element={<p>Warranties</p>} />
                        </Routes>
                    </HasRole>
                    <HasRole role={"Client"} key={"client"}>
                        <p>Client page</p>
                        <Routes>
                            <Route path={"/warranties"} element={<p>Warranties</p>} />
                            <Route path={"/tickets"} element={<p>Tickets</p>} />
                        </Routes>
                    </HasRole>
                    <HasRole role={"Expert"} key={"expert"}>
                        <p>Expert page</p>
                        <Routes>
                            <Route path={"/tickets"} element={<p>Tickets</p>} />
                        </Routes>
                    </HasRole>
                    <HasRole role={"Vendor"} key={"vendor"}>
                        <p>Vendor page</p>
                        <Routes>
                            <Route path={"/warranties"} element={<p>Warranties</p>} />
                        </Routes>
                    </HasRole>
                    <IsAuthenticated>
                        <Routes>
                            <Route path={"/profile"} element={<p>Profile</p>} />
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