import {Container, Form, Card, Button} from 'react-bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import React, {useState} from 'react';
import API from "./API/api"


function LoginForm() {
    const [email, setEmail] = useState("client@test.it")
    const [pwd, setPwd] = useState("password")

    const onSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        try {
            const user = await API.login(email, pwd)
            console.log(user)
            if (user === null) {
                return
            }
        } catch (e) {
            console.log(e)
        }
    }

    return (
        <>
            <Container fluid className="flex-grow-1 justify-content-center d-flex align-items-center">
                <Card border="primary" style={{padding: '4rem'}} >
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
                        <Button variant="primary" type="submit">
                            Login
                        </Button>
                    </Form>
                </Card>
            </Container>
        </>
    )
}

export default LoginForm