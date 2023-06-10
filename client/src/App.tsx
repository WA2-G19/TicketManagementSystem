import './App.css';
import {Container} from 'react-bootstrap';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {AuthenticationContextProvider} from "./contexts/Authentication";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import {AlertContextProvider} from "./contexts/Alert";
import SignupPage from "./pages/SignupPage";
import NavbarLayout from "./components/layout/NavbarLayout";
import {useState} from "react";

function App() {
    return (
        <AlertContextProvider>
            <AuthenticationContextProvider>
                <Container fluid className="p-0">
                    <BrowserRouter>
                        <NavbarLayout>
                            <Routes>
                                <Route path={"/"} element={<></>}/>
                                <Route path={"/home"} element={<HomePage/>}/>
                                <Route path={"/login"} element={<LoginPage/>}/>
                                <Route path={"/signup"} element={<SignupPage/>}/>
                            </Routes>
                        </NavbarLayout>
                    </BrowserRouter>
                </Container>
            </AuthenticationContextProvider>
        </AlertContextProvider>
    );
}

export default App;
