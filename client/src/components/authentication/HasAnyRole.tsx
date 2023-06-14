import {useAuthentication} from "../../contexts/Authentication";

function HasAnyRole({
                     children,
                     roles
                 }: {
    children: JSX.Element[] | JSX.Element,
    roles: string[]
}): JSX.Element {
    const auth = useAuthentication()

    if (auth.isLoggedIn && roles.some(role => auth.user!.role.includes(role))) {
        return (
            <>
                {children}
            </>
        )
    }
    return <>{false}</>
}

export default HasAnyRole