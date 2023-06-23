import React, {FormEvent, useRef} from 'react';
import {Profile, CredentialCustomer} from "../../classes/Profile";
import {Button, Card, Col, Container, Form, Row} from "react-bootstrap";
import CustomerAPI from "../../API/Profile/customer";
import {useAlert} from "../../contexts/Alert";
import HttpStatusCode from "../../utils/httpStatusCode";
import ProblemDetail from "../../classes/ProblemDetail";
import {useNavigate} from "react-router-dom";

function RegistrationForm() {
    const navigate = useNavigate()
    const alert = useAlert()
    const nameRef = useRef<HTMLInputElement>(null)
    const surnameRef = useRef<HTMLInputElement>(null)
    const emailRef = useRef<HTMLInputElement>(null)
    const addressRef = useRef<HTMLInputElement>(null)
    const passwordRef = useRef<HTMLInputElement>(null)

    async function handleSubmit(e: FormEvent) {
        e.preventDefault();
        if (nameRef.current && surnameRef.current && emailRef.current && addressRef.current && passwordRef.current) {
            if (!/^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(emailRef.current.value)) {
                alert.getBuilder()
                    .setTitle("Error in email")
                    .setMessage("Email format is incorrect")
                    .setButtonsOk()
                    .show()
                return
            }
            try {
                await CustomerAPI.signup(new CredentialCustomer(new Profile(
                    emailRef.current.value,
                    nameRef.current.value,
                    surnameRef.current.value,
                    addressRef.current.value
                ), passwordRef.current.value))
            } catch (e) {
                if (e instanceof ProblemDetail) {
                    if (e.status === HttpStatusCode.CREATED) {
                        alert.getBuilder()
                            .setTitle("Registration succeeded")
                            .setMessage("Registration is completed, but before you can log in you have to verify your email.")
                            .setButtonsOk(() => navigate("/login"))
                            .show()
                    } else if (e.status === HttpStatusCode.CONFLICT) {
                        alert.getBuilder()
                            .setTitle("Conflict")
                            .setMessage(JSON.stringify(e.detail))
                            .setButtonsOk()
                            .show()
                    } else {
                        alert.getBuilder()
                            .setTitle("Error")
                            .setMessage("Unexpected error")
                            .setButtonsOk()
                            .show()
                    }
                } else {
                    alert.getBuilder()
                        .setTitle("Error")
                        .setMessage("Unexpected error")
                        .setButtonsOk()
                        .show()
                }
            }
        }
    }

    return (<Container fluid className='p-4'>
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
                    <Card>
                        <Card.Body className='p-5'>
                            <Form onSubmit={handleSubmit}>
                                <Container>
                                    <Row className={"mt-3"}>
                                        <Col>
                                            <Form.FloatingLabel
                                                label={"Name"}
                                            >
                                                <Form.Control required={true}
                                                              pattern="[A-Za-z]+"
                                                              ref={nameRef}/>
                                            </Form.FloatingLabel>
                                        </Col>
                                        <Col>
                                            <Form.FloatingLabel
                                                label={"Surname"}
                                            >
                                                <Form.Control required={true}
                                                              pattern={"[A-Za-z]+"}
                                                              ref={surnameRef}/>
                                            </Form.FloatingLabel>
                                        </Col>
                                    </Row>
                                    <Row className={"mt-3"}>
                                        <Col>
                                            <Form.FloatingLabel
                                                label={"Email"}
                                            >
                                                <Form.Control type={"email"}
                                                              required={true}
                                                              ref={emailRef}/>
                                            </Form.FloatingLabel>
                                        </Col>
                                        <Col>
                                            <Form.FloatingLabel
                                                label={"Address"}
                                            >
                                                <Form.Control required={true}
                                                              ref={addressRef}/>
                                            </Form.FloatingLabel>
                                        </Col>
                                    </Row>
                                    <Row className={"mt-3"}>
                                        <Col>
                                            <Form.FloatingLabel
                                                label={"Password"}
                                            >
                                                <Form.Control type={"password"}
                                                              required={true}
                                                              minLength={6}
                                                              ref={passwordRef}/>
                                            </Form.FloatingLabel>
                                        </Col>
                                        <Col>
                                            <Form.FloatingLabel
                                                label={"Confirm password"}
                                            >
                                                <Form.Control type={"password"}
                                                              required={true}
                                                              minLength={6}
                                                              onInput={(e) => {
                                                    if (passwordRef.current) {
                                                        if (passwordRef.current.value !== e.currentTarget.value) {
                                                            e.currentTarget.setCustomValidity("The two passwords are not matching")
                                                        } else {
                                                            e.currentTarget.setCustomValidity("")
                                                        }
                                                    }
                                                }} />
                                            </Form.FloatingLabel>
                                        </Col>
                                    </Row>
                                    <Button type="submit" className={"mt-3"}>Sign Up</Button>
                                </Container>
                            </Form>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>

    );
}

export default RegistrationForm;