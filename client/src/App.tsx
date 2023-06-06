import './App.css';
import Sidebar from './Sidebar';
import Navbar from './components/Navbar';
import {Container, Row, Col} from 'react-bootstrap';
// import ProductAPI from './ProductAPI';
import ProductAPI from './API/Products/products'
import ProfilesAPI from './components/ProfilesAPI';
import {useEffect, useState} from "react";
import LoginForm from "./components/authentications/LoginForm";
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {RegistrationForm} from "./components/authentications/RegistrationForm";

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


    return (
        <Container fluid className=" vh-100 d-flex flex-column m-0 p-0 min-vh-100">
            <Navbar/>
            <Container fluid className="flex-grow-1 pt-lg-5">
                <BrowserRouter>
                    <Routes>
                        <Route path={"/"} element={<LoginForm/>}/>
                        <Route path={"/signup"} element={<RegistrationForm error={error} setError={setError}/>}/>
                    </Routes>
                </BrowserRouter>
            </Container>
        </Container>

    );
}

export default App;
