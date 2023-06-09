import './App.css';
import {Container} from 'react-bootstrap';
import {BrowserRouter, Route, Routes} from "react-router-dom";
import {AuthenticationContextProvider} from "./contexts/Authentication";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import {AlertContextProvider} from "./contexts/Alert";
import SignupPage from "./pages/SignupPage";

function App() {


    return (
        <AlertContextProvider>
            <AuthenticationContextProvider>
                <Container fluid className=" vh-100 p-0 min-vh-100">
                    <BrowserRouter>
                        <Routes>
                            <Route path={"/"} element={<HomePage/>}/>
                            <Route path={"/login"} element={<LoginPage/>}/>
                            <Route path={"/signup"} element={<SignupPage/>}/>
                        </Routes>
                    </BrowserRouter>
                </Container>
            </AuthenticationContextProvider>
        </AlertContextProvider>
    );
}

export default App;
