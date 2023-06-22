import {Container, Form, Card, Button, Row, Col} from 'react-bootstrap';
import React, {useRef} from 'react';
import {Link, useNavigate} from "react-router-dom";
import {useAlert} from "../../contexts/Alert";
import {useAuthentication} from "../../contexts/Authentication";

function LoginForm() {
    const alert = useAlert()
    const auth = useAuthentication()
    const emailRef = useRef<HTMLInputElement>(null)
    const passwordRef = useRef<HTMLInputElement>(null)
    const navigate = useNavigate();
    const onSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        if (emailRef.current && passwordRef.current) {
            try {
                if (!/^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(emailRef.current.value)) {
                    alert.getBuilder()
                        .setTitle("Error in email")
                        .setMessage("Email format is incorrect")
                        .setButtonsOk()
                        .show()
                    return
                }
                await auth.login({username: emailRef.current.value, password: passwordRef.current.value})
                navigate("/")
            } catch (e) {
                alert.getBuilder()
                    .setTitle("Error in login")
                    .setMessage("Email or password incorrect")
                    .setButtonsOk()
                    .show()
            }
        }
    }

    return (
        <Container fluid className='p-4'>
            <Row>
                <Col md='6' className='text-center text-md-start d-flex flex-column justify-content-center'>

                    <h1 className="my-5 display-3 fw-bold ls-tight px-3">
                        The best TMS <br/>
                        <span className="text-primary"> for your products</span>
                    </h1>

                    <p className='px-3' style={{color: 'hsl(217, 10%, 50.8%)'}}>
                        Lorem ipsum dolor sit amet consectetur adipisicing elit.
                        Eveniet, itaque accusantium odio, soluta, corrupti aliquam
                        quibusdam tempora at cupiditate quis eum maiores libero
                        veritatis? Dicta facilis sint aliquid ipsum atque?
                    </p>

                </Col>

                <Col md='6'>

                    <Card className='my-5'>
                        <Card.Body className='p-5'>
                            <Row>
                                <Form onSubmit={onSubmit}>
                                    <Form.FloatingLabel
                                        label={"Email"}
                                        className={"mb-3"}
                                    >
                                        <Form.Control
                                            type={"email"}
                                            placeholder={"email@address.dom"}
                                            ref={emailRef}
                                            defaultValue={"client@test.it"}
                                            required={true} />
                                    </Form.FloatingLabel>
                                    <Form.FloatingLabel
                                        label={"Password"}
                                        className={"mb-3"}
                                    >
                                        <Form.Control
                                            type={"password"}
                                            placeholder={"password"}
                                            ref={passwordRef}
                                            defaultValue={"password"}
                                            required={true} />
                                    </Form.FloatingLabel>
                                    <Row>
                                        <Col>
                                            <Button variant="primary" type="submit">
                                                Login
                                            </Button>
                                        </Col>
                                        <Col className="d-flex align-items-center justify-content-center" md={8}>
                                            <p className="small">
                                                Don't have an account yet?
                                                <Link to={"/signup"}>
                                                    <span className={"text-primary"} style={{textDecoration: 'underline'}}>
                                                        Sign Up
                                                    </span>
                                                </Link>
                                            </p>
                                        </Col>
                                    </Row>
                                </Form>
                            </Row>
                        </Card.Body>
                    </Card>

                </Col>

            </Row>
        </Container>
    )
}

export default LoginForm