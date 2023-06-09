import HasRole from "../components/authentication/HasRole";

interface HomePageProps {
    select: string
}

function HomePage(props: HomePageProps) {
    return <>
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
    </>
}

export default HomePage