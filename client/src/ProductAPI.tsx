import { Container, Button, Form, Row, Col, Card } from "react-bootstrap"
import Response from "./Response"
import Product from "./classes/Product"
import API from "./API/api"
import  { useEffect, useState } from "react"



function ProductAPI(){

    const prototype = new Product("","","")
    const [statusCode, setStatusCode] = useState<number>(0) 
    const [bodyPretty, setBodyPretty] = useState<Object[]>([])
    const [bodyRaw, setBodyRaw] = useState<string>("")
    const products: Product[] = bodyPretty.map((p: any, idx) => new Product(p.ean, p.name, p.brand))
    

    return (<>
        <Container className="vh-100 d-flex flex-column ">
        <Row className="mb-3">
            <Col >
                <GetByIdCard setBodyPretty={setBodyPretty} setBodyRaw={setBodyRaw} setStatusCode={setStatusCode}/>
            </Col>
            <Col>
                <PostProductCard/>
            </Col>
        </Row>
        <Row className="h-100">
            <Response prototype={prototype} responseBodyPretty={products} responseBodyRaw={bodyRaw} responseStatusCode={statusCode}/>
        </Row>
        
        </Container>
    </>)
}


function GetByIdCard(props: {
    setBodyPretty: React.Dispatch<React.SetStateAction<Object[]>>,
    setBodyRaw: React.Dispatch<React.SetStateAction<string>>,
    setStatusCode: React.Dispatch<React.SetStateAction<number>>,

}){
    const [ean, setEan] = useState("")
    const handleGetAll = async (event: React.MouseEvent) => {
        event.preventDefault();
        try{
            const response = await API.getAllProducts()
            const jsonResponse = await response.json()
            props.setBodyPretty(jsonResponse)
            props.setStatusCode(response.status)
            props.setBodyRaw(JSON.stringify(jsonResponse, null, 2))

        } catch(e){
            console.log(e)
        }
    }

    const handleGetByID = async (event: React.MouseEvent) => {
        event.preventDefault();
        try{
            const response = await API.getProductByEAN(ean)
            const jsonResponse = await response.json()
            props.setBodyPretty([jsonResponse])
            props.setStatusCode(response.status)
            props.setBodyRaw(JSON.stringify(jsonResponse, null, 2))

        } catch(e){
            console.log(e)
        }
    }

    return (
    <Card className="h-100">
        <Card.Body className="h-100">
        <Card.Title>
            GET
        </Card.Title>
            <Form>
            <Form.Group className="mb-3" controlId="getEan">
                <Form.Label>EAN</Form.Label>
                <Form.Control  value={ean} onChange={(e) => setEan(e.target.value)} placeholder="The product EAN you want to search" />                
            
            </Form.Group>
            <Row>
                <div>
                <Button variant="primary" onClick={(e) => handleGetAll(e)}>
                    Get all
                </Button> {' '}{' '}
                <Button  variant="primary" onClick={(e) => handleGetByID(e)}>
                    Get by EAN
                </Button>
                </div>   
            </Row>
            </Form>
        </Card.Body>
    </Card>)
}




function PostProductCard(){
    return (
    <Card className="h-100">
        <Card.Body className="h-100 ">
        <Card.Title>
            POST
        </Card.Title>
        <Form>
        <Form.Group className="mb-3" controlId="postEan">
            <Form.Label>EAN</Form.Label>
            <Form.Control  placeholder="" />                
        </Form.Group>
        <Form.Group className="mb-3" controlId="postEan">
            <Form.Label>Name</Form.Label>
            <Form.Control  placeholder="" />                
        </Form.Group>
        <Form.Group className="mb-3" controlId="postEan">
            <Form.Label>Brand</Form.Label>
            <Form.Control  placeholder="" />                
        </Form.Group>
        <Button  variant="primary" type="submit">
            Post Product
        </Button>
        </Form>
    </Card.Body>
    </Card>
    )
}









export default ProductAPI