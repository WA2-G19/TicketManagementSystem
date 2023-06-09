import {createContext, useContext, useState} from "react"
import jwt_decode from 'jwt-decode';
import API from "../API/api";

interface Credentials {
    username: string
    password: string
}

interface Authentication {
    readonly user: any | null
    isLoggedIn(): boolean
    login(credentials: Credentials): Promise<void>
    logout(): Promise<void>
}

const AuthenticationContext = createContext<Authentication | null>(null)

function AuthenticationContextProvider({ children }: {
    children: JSX.Element[] | JSX.Element
}) {
    const [authentication, _] = useState( new class implements Authentication {
        get user(): any | null {
            const token = localStorage.getItem("jwt")
            if (token === null) {
                return null
            }
            try {
                return jwt_decode(token)
            } catch (e) {
                console.error(e)
                localStorage.removeItem("jwt")
                return null
            }
        }

        isLoggedIn(): boolean {
            return this.user !== null
        }

        async login(credentials: Credentials): Promise<void> {
            if (this.isLoggedIn())
                throw new Error("you are already logged in as another user, please log out first")
            localStorage.setItem("jwt", await API.login(credentials.username, credentials.password))
        }

        async logout(): Promise<void> {
            if (!this.isLoggedIn())
                throw new Error("you are not logged in, please log in first")
            localStorage.removeItem("jwt")
        }
    });
    return (
        <AuthenticationContext.Provider value={authentication}>
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