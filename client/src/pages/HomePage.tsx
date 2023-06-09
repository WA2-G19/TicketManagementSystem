import NavbarLayout from "../components/layout/NavbarLayout";
import HasRole from "../components/authentication/HasRole";

function HomePage() {
    return (
        <NavbarLayout>
            <HasRole role={"Manager"} key={"manager"}>
                <p>Manager page</p>
            </HasRole>
            <HasRole role={"Client"} key={"client"}>
                <p>Client page</p>
            </HasRole>
            <HasRole role={"Expert"} key={"expert"}>
                <p>Expert page</p>
            </HasRole>
            <HasRole role={"Vendor"} key={"vendor"}>
                <p>Vendor page</p>
            </HasRole>
        </NavbarLayout>
    )
}

export default HomePage