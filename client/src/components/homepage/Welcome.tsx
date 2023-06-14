import {useAuthentication} from "../../contexts/Authentication";
import {Navigate} from "react-router-dom";

function Welcome() {
    const auth = useAuthentication()
    if (!auth.isLoggedIn)
        return (<Navigate to={"/login"} />)
    return (
        <div className={"h-100 d-flex align-items-center justify-content-center"}>
            <h1>Welcome {auth.user!.name}!</h1>
        </div>
    )
}

export default Welcome