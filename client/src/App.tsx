import './App.css';
import Sidebar from './Sidebar';
import Navbar from './Navbar';
import { Container, Row, Col } from 'react-bootstrap';
import ProductAPI from './ProductAPI';
function App() {
  return (
     <Container fluid className =" d-flex flex-column m-0 p-0 min-vh-100">
      <Navbar/>
      <Container fluid className="flex-grow-1">
          <Row className = "h-100"> 
          <Sidebar/>
          <Col  xs = {10} className="p-2">
                {<ProductAPI/>}
          </Col>
          </Row>
      </Container>
    </Container>
    
  );
}

export default App;
