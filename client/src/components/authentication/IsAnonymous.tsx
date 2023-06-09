import {useAuthentication} from "../../contexts/Authentication";

function IsAnonymous({ children }:
     {
         children: JSX.Element[] | JSX.Element
     }): JSX.Element {
    const auth = useAuthentication()
    if (!auth.isLoggedIn) {
        return (
            <>
                {children}
            </>
        )
    }
    return <>{false}</>
}

export default IsAnonymous