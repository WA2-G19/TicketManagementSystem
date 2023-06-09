import {useAuthentication} from "../../contexts/Authentication";

function IsAnonymous({ children }:
     {
         children: JSX.Element[] | JSX.Element
     }): JSX.Element | null {
    const auth = useAuthentication()
    if (!auth.isLoggedIn()) {
        return (
            <>
                {children}
            </>
        )
    }
    return null
}

export default IsAnonymous