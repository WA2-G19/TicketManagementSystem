import {Container, Form, Card, Button, Row, Col} from 'react-bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import React, {Dispatch, SetStateAction, useState} from 'react';
import API from "../../API/api"
import {Link, useNavigate} from "react-router-dom";
import {useAlert} from "../../contexts/Alert";
import {useAuthentication} from "../../contexts/Authentication";

interface LoginProps {
    useRefresh: Dispatch<SetStateAction<number>>
}

function LoginForm() {
    const alert = useAlert()
    const auth = useAuthentication()
    const [email, setEmail] = useState("client@test.it")
    const [pwd, setPwd] = useState("password")
    const navigate = useNavigate();
    const validator = require("validator")
    const onSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        try {
            if (!validator.isEmail(email)) {
                alert.getBuilder().setTitle("Error in email").setMessage("Email format is uncorrect").show()
                return
            }
            await auth.login({username: email, password: pwd})
            navigate("/")
        } catch (e) {
            alert.getBuilder().setTitle("Error in login").setMessage("Email or password uncorrect").show()
        }
    }

    return (
        <>
            <Container fluid className="flex-grow-1 justify-content-center d-flex align-items-center p-lg-5">
                <Card border="primary" style={{padding: '4rem'}}>
                    <Form onSubmit={(e: React.FormEvent) => onSubmit(e)}>
                        <Form.Group className="mb-2" controlId="formGroupEmail">
                            <Form.Label>Email address</Form.Label>
                            <Form.Control type="email" placeholder="email@address.dom" value={email} required={true}
                                          onChange={(e) => setEmail(e.target.value)}/>
                        </Form.Group>
                        <Form.Group className="mb-3" controlId="formGroupPassword">
                            <Form.Label>Password</Form.Label>
                            <Form.Control type="password" placeholder="Password" value={pwd} required={true}
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