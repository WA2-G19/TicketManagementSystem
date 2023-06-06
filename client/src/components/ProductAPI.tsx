import {Container, Button, Form, Row, Col, Card} from "react-bootstrap"
import Response from "./Response"
import Product from "../classes/Product"
import API from "../API/api"
import React, {useState} from "react"


function ProductAPI() {

    const prototype = new Product("", "", "")
    const [statusCode, setStatusCode] = useState<number>(0)
    const [bodyPretty, setBodyPretty] = useState<Object[]>([])
    const [bodyRaw, setBodyRaw] = useState<string>("")
    const products: Product[] = bodyPretty.map((p: any) => new Product(p.ean, p.name, p.brand))


    return (<>
        <Container className="h-100 d-flex flex-column align-items-stretch dashboard-page">
            <Row className="mb-3">
                <Col>
                    <GetByIdCard setBodyPretty={setBodyPretty} setBodyRaw={setBodyRaw} setStatusCode={setStatusCode}/>
                </Col>

            </Row>
            <Row className="">
                <Response prototype={prototype} responseBodyPretty={products} responseBodyRaw={bodyRaw}
                          responseStatusCode={new Number(statusCode)}/>
            </Row>

        </Container>
    </>)
}


function GetByIdCard(props: {
    setBodyPretty: React.Dispatch<React.SetStateAction<Object[]>>,
    setBodyRaw: React.Dispatch<React.SetStateAction<string>>,
    setStatusCode: React.Dispatch<React.SetStateAction<number>>,

}) {
    const [ean, setEan] = useState("")
    const handleGetAll = async (event: React.MouseEvent) => {
        event.preventDefault();
        try {
            const response = await API.getAllProducts()
            let decodedResponse = null
            try {
                decodedResponse = await response.json()
                props.setBodyPretty(decodedResponse)
                props.setBodyRaw(JSON.stringify(decodedResponse, null, 2))


            } catch {
                decodedResponse = await response.text()
            } finally {

                props.setStatusCode(response.status)
            }

        } catch (e) {

        }
    }

    const handleGetByID = async (event: React.MouseEvent) => {
        event.preventDefault();
        try {
            const response = await API.getProductByEAN(ean)
            console.log(response)
            const jsonResponse = await response.json()
            props.setBodyPretty([jsonResponse])
            props.setStatusCode(response.status)
            props.setBodyRaw(JSON.stringify(jsonResponse, null, 2))

        } catch (e) {
            console.log(e)
        }
    }

    return (
        <Card className="h-100">
            <Card.Body className="h-100">
                <Card.Title>
                    GET
                </Card.Title>
                <Form onSubmit={(e) => {
                    e.preventDefault()
                }}>
                    <Form.Group className="mb-3" controlId="getEan">
                        <Form.Label>EAN</Form.Label>
                        <Form.Control value={ean} onChange={(e) => setEan(e.target.value)}
                                      placeholder="The product EAN you want to search"/>

                    </Form.Group>
                    <Row>
                        <div>
                            <Button variant="primary" onClick={(e) => handleGetAll(e)}>
                                Get all
                            </Button> {' '}{' '}
                            <Button variant="primary" disabled={ean.length === 0} onClick={(e) => handleGetByID(e)}>
                                Get by EAN
                            </Button>
                        </div>
                    </Row>
                </Form>
            </Card.Body>
        </Card>)
}


export default ProductAPI