import {useAuthentication} from "../../contexts/Authentication";

function HasRole({
                     children,
                     role
                 }: {
    children: JSX.Element[] | JSX.Element,
    role: string[]
}): JSX.Element {
    const auth = useAuthentication()
    if (auth.isLoggedIn() && role.some(role => auth.user!.role.includes(role))) {
        return (
            <>
                {children}
            </>
        )
    }
    return <>{false}</>
}

export default HasRole