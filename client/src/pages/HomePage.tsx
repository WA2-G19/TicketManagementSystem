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
import Stats from "../components/homepage/Stats";
import WarrantyForm from "../components/warranty/WarrantyForm";
import VendorForm from "../components/vendor/VendorForm";
import StaffForm from "../components/staff/StaffForm";
import Skills from "../components/homepage/Skills";
import SkillForm from "../components/skill/SkillForm";
import ProductForm from "../components/product/ProductForm";
import LoginPage from "./LoginPage";
import {ModalChat} from "../components/modals/ModalChat";

function HomePage() {
    return (
        <IsAuthenticated alt={<LoginPage/>}>
            <SidebarLayout>
                <SidebarLayout.Main>
                    <HasRole role={"Manager"} key={"manager"}>
                        <Routes>
                            <Route path={"/stats"} element={<Stats/>} />
                            <Route path={"/tickets"} element={<Tickets />} />
                            <Route path={"/staffs"} element={<Staffs />} />
                            <Route path={"/staffs/add"} element={<StaffForm />} />
                            <Route path={"/vendors"} element={<Vendors />} />
                            <Route path={"/vendors/add"} element={<VendorForm />} />
                            <Route path={"/products"} element={<Products />} />
                            <Route path={"/products/add"} element={<ProductForm />} />
                            <Route path={"/warranties"} element={<Warranties />} />
                            <Route path={"/skills"} element={<Skills />} />
                            <Route path={"/skills/add"} element={<SkillForm />} />
                        </Routes>
                    </HasRole>
                    <HasRole role={"Client"} key={"client"}>
                        <Routes>
                            <Route path={"/warranties"} element={<Warranties />} />
                            <Route path={"/warranties/add"} element={<WarrantyForm />} />
                            <Route path={"/tickets"} element={<Tickets />} />
                            <Route path={"/tickets/add"} element={<TicketForm />} />
                            <Route path={"/chat"} element={<ModalChat />} />
                        </Routes>
                    </HasRole>
                    <HasRole role={"Expert"} key={"expert"}>
                        <Routes>
                            <Route path={"/tickets"} element={<Tickets />} />
                            <Route path={"/chat"} element={<ModalChat />} />
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