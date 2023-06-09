import './App.css';
import {Container} from 'react-bootstrap';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {AuthenticationContextProvider} from "./contexts/Authentication";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import {AlertContextProvider} from "./contexts/Alert";
import SignupPage from "./pages/SignupPage";
import SidebarLayout from "./components/layout/SidebarLayout";
import Sidebar from "./components/Sidebar";
import NavbarLayout from "./components/layout/NavbarLayout";
import {useState} from "react";

function App() {
    const [select,useSelect] = useState("")
    return (
        <AlertContextProvider>
            <AuthenticationContextProvider>
                <Container fluid className="vh-100 p-0 min-vh-100">
                    <BrowserRouter>
                        <NavbarLayout>
                            <Routes>
                                <Route path={"/"} element={<></>}/>
                                <Route path={"/home"} element={
                                    <SidebarLayout><SidebarLayout.Main><HomePage select={select}/></SidebarLayout.Main><SidebarLayout.Sidebar><Sidebar useSelect={useSelect}/></SidebarLayout.Sidebar></SidebarLayout>}/>
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
