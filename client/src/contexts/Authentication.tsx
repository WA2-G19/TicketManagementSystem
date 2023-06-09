import {createContext, useContext, useState} from "react"
import jwt_decode from 'jwt-decode';

interface Authentication {
    user: any | null
    isLoggedIn(): boolean
}

const AuthenticationContext = createContext<Authentication | null>(null)

function AuthenticationContextProvider({ children }: {
    children: [JSX.Element] | JSX.Element
}) {
    const [authentication, _] = useState( new class implements Authentication {
        user: any | null
        isLoggedIn(): boolean {
            return this.user !== null
        }

        constructor() {
            const token = localStorage.getItem("token")
            if (token === null) {
                this.user = null
            } else {
                try {
                    this.user = jwt_decode(token)
                } catch (e) {
                    console.error(e)
                    this.user = null
                    localStorage.removeItem("token")
                }
            }
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