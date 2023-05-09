import './App.css';
import Sidebar from './Sidebar';
import Navbar from './Navbar';
import {Container, Row, Col} from 'react-bootstrap';
import ProductAPI from './ProductAPI';
import ProfilesAPI from './ProfilesAPI';
import {useState} from "react";
import LoginForm from "./LoginForm";

function App() {
    const [filter, setFilter] = useState<string>("Product");

    return (
        <Container fluid className=" vh-100 d-flex flex-column m-0 p-0 min-vh-100">
            {/*<Navbar/>*/}
            {/*<Container fluid className="flex-grow-1">*/}
            {/*    <Row className="h-100">*/}
            {/*        <Sidebar setFilter={setFilter} filter={filter}/>*/}
            {/*        <Col xs={10} className="p-2">*/}
            {/*            {filter=="Profile" ? <ProfilesAPI/> : <></>}*/}
            {/*            {filter=="Product" ? <ProductAPI/> : <></>}*/}
            {/*        </Col>*/}
            {/*    </Row>*/}
            {/*</Container>*/}
            <LoginForm/>
        </Container>

    );
}

export default App;
