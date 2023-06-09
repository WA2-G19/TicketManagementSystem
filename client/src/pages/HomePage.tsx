import HasRole from "../components/authentication/HasRole";
import SidebarLayout from "../components/layout/SidebarLayout";
import Sidebar from "../components/Sidebar";

interface HomePageProps {
    select: string
}

function HomePage(props: HomePageProps) {
    return <><SidebarLayout.Main>
        <HasRole role={"Manager"} key={"manager"}>
            <p>Manager page</p>
        </HasRole>
        <HasRole role={"Client"} key={"client"}>
            <> <p>Client page {props.select}</p></>
        </HasRole>
        <HasRole role={"Expert"} key={"expert"}>
            <p>Expert page</p>
        </HasRole>
        <HasRole role={"Vendor"} key={"vendor"}>
            <p>Vendor page</p>
        </HasRole>
            </SidebarLayout.Main>
          <SidebarLayout.Sidebar>
              <Sidebar />
          </SidebarLayout.Sidebar>
        </>
}

export default HomePage