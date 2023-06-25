import {useAuthentication} from "../../contexts/Authentication";
import LoginPage from "../../pages/LoginPage";

function IsAuthenticated({
    children,
    alt
 }:
     {
         children: JSX.Element[] | JSX.Element,
         alt?: JSX.Element
     }): JSX.Element {
    const auth = useAuthentication()
    if (auth.isLoggedIn) {
        return (
            <>
                {children}
            </>
        )
    } 
    else if(alt !== null){
        return <>{alt}</>
    } 
     
    return <>{false}</>
}

export default IsAuthenticated