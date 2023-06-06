import {Container, Form, Card, Button, FormText, Row, Col} from 'react-bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import React, {useState} from 'react';
import API from "../../API/api"
import {Link, Navigate} from "react-router-dom";

function LoginForm() {

    const [email, setEmail] = useState("client@test.it")
    const [pwd, setPwd] = useState("password")

    const onSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        try {
            const token = await API.login(email, pwd)
            localStorage.setItem("jwt", token)
            if (!token) {
                return
            }
            console.log(token)
        } catch (e) {
            console.log(e)
        }
    }

    return (
        <>
            <Container fluid className="flex-grow-1 justify-content-center d-flex align-items-center p-lg-5">
                <Card border="primary" style={{padding: '4rem'}}>
                    <Form onSubmit={(e: React.FormEvent) => onSubmit(e)}>
                        <Form.Group className="mb-2" controlId="formGroupEmail">
                            <Form.Label>Email address</Form.Label>
                            <Form.Control type="email" placeholder="email@address.dom" value={email}
                                          onChange={(e) => setEmail(e.target.value)}/>
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="formGroupPassword">
                            <Form.Label>Password</Form.Label>
                            <Form.Control type="password" placeholder="Password" value={pwd}
                                          onChange={(e) => setPwd(e.target.value)}/>
                        </Form.Group>
                        <Row>
                            <Col>
                                <Button variant="primary" type="submit">
                                    Login
                                </Button>
                            </Col>
                            <Col className="d-flex align-items-center justify-content-center" md={8}>
                                <p className="small">Don't have an account yet? <Link to={"/signup"}><span
                                    className={"text-primary"} style={{textDecoration: 'underline'}}
                                >Sign Up</span></Link></p>
                            </Col>
                        </Row>
                    </Form>
                </Card>
            </Container>
        </>
    )
}

export default LoginForm