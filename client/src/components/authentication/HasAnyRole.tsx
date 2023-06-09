import {useAuthentication} from "../../contexts/Authentication";

function HasAnyRole({
                     children,
                     roles
                 }: {
    children: JSX.Element[] | JSX.Element,
    roles: string[]
}): JSX.Element | null {
    const auth = useAuthentication()

    if (auth.isLoggedIn() && roles.some(role => auth.user!.role.includes(role))) {
        return (
            <>
                {children}
            </>
        )
    }
    return null
}

export default HasAnyRole