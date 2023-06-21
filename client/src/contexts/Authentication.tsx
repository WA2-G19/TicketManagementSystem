import {createContext, useContext, useState} from "react"
import jwt_decode from 'jwt-decode';
import ProfileAPI from '../API/Profile/profile'

interface Credentials {
    username: string
    password: string
}

interface Authentication {
    readonly user: User | null
    readonly isLoggedIn: boolean
    login(credentials: Credentials): Promise<void>
    logout(): Promise<void>
}

interface User {
    email: string
    name: string
    role: string[]
    token: string
    exp: number
}

const AuthenticationContext = createContext<Authentication | null>(null)

function AuthenticationContextProvider({ children }: {
    children: JSX.Element[] | JSX.Element
}) {
    const [user, setUser] = useState<User | null>((() => {
        const token = localStorage.getItem("jwt")
        if (token === null)
            return null
        try {
            const user = jwt_decode<User>(token)
            if (Date.now() >= user.exp * 1000) {
                localStorage.removeItem("jwt")
                return null
            }
            user.token = token
            return user
        } catch (e) {
            localStorage.removeItem("jwt")
            console.error(e)
            return null
        }
    })())

    async function login(credentials: Credentials): Promise<void> {
        if (user !== null)
            throw new Error("you are already logged in as another user, please log out first")
        const token = await ProfileAPI.login({
            username: credentials.username,
            password: credentials.password
        })
        try {
            const user = jwt_decode<User>(token)
            user.token = token
            localStorage.setItem("jwt", token)
            setUser(user)
        } catch (e) {
            console.error(e)
        }
    }

    async function logout(): Promise<void> {
        if (user === null)
            throw new Error("you are not logged in, please log in first")
        localStorage.removeItem("jwt")
        setUser(null)
    }

    return (
        <AuthenticationContext.Provider value={{
            user,
            get isLoggedIn() {
                return user !== null
            },
            login,
            logout
        }}>
            {children}
        </AuthenticationContext.Provider>);
}

function useAuthentication() {
    const c = useContext(AuthenticationContext)
    if (!c) {
        throw new Error("useAuthentication has to be used inside <AuthenticationContextProvider>")
    }
    return c
}

export {
    AuthenticationContextProvider,
    useAuthentication
}