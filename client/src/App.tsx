import './App.css';
import Sidebar from './Sidebar';
import Navbar from './components/Navbar';
import {Container, Row, Col, Button} from 'react-bootstrap';
// import ProductAPI from './ProductAPI';
import ProductAPI from './API/Products/products'
import ProfilesAPI from './components/ProfilesAPI';
import {createContext, useEffect, useState} from "react";
import LoginForm from "./components/loginpage/LoginForm";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {RegistrationForm} from "./components/signuppage/RegistrationForm";
import {AuthenticationContextProvider, useAuthentication} from "./contexts/Authentication";
import Layout from "./components/Layout";
import HomePage from "./components/pages/HomePage";
import LoginPage from "./components/pages/LoginPage";
import ClientPage from "./components/pages/ClientPage";

function App() {

    const [filter, setFilter] = useState<string>("Product");
    const [error, setError] = useState("")

    // useEffect(()=>{
    //     const checkAuth = async () => {
    //         try {
    //             console.log(await ProductAPI.getAllProducts(""))
    //         } catch(e) {
    //         }
    //     }
    //     checkAuth();
    // },[])
    const [refresh, useRefresh] = useState(1);


    return (
        <Container fluid className=" vh-100 p-0 min-vh-100">
            <BrowserRouter>
                <Routes>
                    <Route path={"/"} element={<HomePage/>}/>
                    <Route path={"/login"} element={<LoginPage/>}/>
                    <Route path={"/signup"} element={<RegistrationForm error={error} setError={setError}/>}/>
                    <Route path={"/client"} element={<ClientPage/>}/>
                </Routes>
            </BrowserRouter>
        </Container>
    );
}

export default App;
