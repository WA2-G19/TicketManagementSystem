import {useAuthentication} from "../../contexts/Authentication";

function HasRole({
    children,
    role
}: {
    children: JSX.Element[] | JSX.Element,
    role: string
}): JSX.Element | null {
    const auth = useAuthentication()

    if (auth.isLoggedIn() && auth.user!.role.includes(role)) {
        return (
            <>
                {children}
            </>
        )
    }
    return null
}

export default HasRole