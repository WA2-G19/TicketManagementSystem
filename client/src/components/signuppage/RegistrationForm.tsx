import React, {useState, ChangeEvent, FormEvent} from 'react';
import {Profile, CredentialCustomer} from "../../classes/Profile";
import {Button, Card, Col, Container, Form, Row, Toast, ToastContainer} from "react-bootstrap";
import CustomerAPI from "../../API/Profile/customer";
import {useAlert} from "../../contexts/Alert";
import HttpStatusCode from "../../utils/httpStatusCode";


function RegistrationForm() {
    const alert = useAlert()

    const [showToast, setShowToast] = useState(false)
    const [formData, setFormData] = useState(
        {
            "name": "",
            "surname": "",
            "address": "",
            "email": "",
            "password": "",
            "confirmPassword": "",
        }
    );
    const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
        setFormData({...formData, [e.target.name]: e.target.value})
    };


    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        const validator = require("validator")
        if (!validator.isEmail(formData.email)) {
            alert.getBuilder().setTitle("Error in email").setMessage("Email format is uncorrect").show()
            return
        }
        if (formData.password != formData.confirmPassword) {
            alert.getBuilder().setTitle("Error in signup").setMessage("Confirm password is different from password").show()
            return
        }
        const profile = new Profile(
            formData.email,
            formData.name,
            formData.surname,
            formData.address
        )
        const credentials = new CredentialCustomer(profile, formData.password)
        const response = await CustomerAPI.signup(credentials)
        if (response == HttpStatusCode.CREATED) {
            setFormData({
                "name": "",
                "surname": "",
                "address": "",
                "email": "",
                "password": "",
                "confirmPassword": "",
            });
            setShowToast(true)
        } else if (response == HttpStatusCode.CONFLICT) {
            alert.getBuilder().setTitle("Conflict").setMessage("User with this email already exist").setButtonsOk(undefined).show()
        } else {
            alert.getBuilder().setTitle("Error").setMessage("Unexpected error").show()
        }
    };

    return (
        <Container fluid className="flex-grow-1 justify-content-center d-flex align-items-center p-lg-3">
            <Card border="primary" style={{padding: '3rem'}}>
                {/*error ? <Alert variant={"danger"} onClose={() => setRegistrationError("")} dismissible>{error}</Alert> : <></>*/}
                <h1>Sign Up</h1>
                <Form onSubmit={handleSubmit}>
                    <Row className={"mt-3"}>
                        <Col>
                            <Form.Group>
                                <Form.Label>Name</Form.Label>
                                <Form.Control type="text" name="name" value={formData.name} required pattern="[A-Za-z]+"
                                              onChange={handleChange}/>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group>
                                <Form.Label>Surname</Form.Label>
                                <Form.Control type="text" name="surname" value={formData.surname} required
                                              pattern="[A-Za-z]+"
                                              onChange={handleChange}/>
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row className={"mt-3"}>
                        <Col>
                            <Form.Group>
                                <Form.Label>Email</Form.Label>
                                <Form.Control type="email" name="email" value={formData.email} required
                                              onChange={handleChange}/>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group>
                                <Form.Label>Address</Form.Label>
                                <Form.Control type="address" name="address" value={formData.address} required
                                              onChange={handleChange}/>
                            </Form.Group>
                        </Col>
                    </Row>
                    <Row className={"mt-3"}>
                        <Col>
                            <Form.Group>
                                <Form.Label>Password</Form.Label>
                                <Form.Control type="password" name="password" value={formData.password} required
                                              minLength={6}
                                              onChange={handleChange}/>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group>
                                <Form.Label>Confirm Password</Form.Label>
                                <Form.Control type="password" name="confirmPassword" value={formData.confirmPassword}
                                              required
                                              minLength={6}
                                              onChange={handleChange}/>
                            </Form.Group>
                        </Col>
                    </Row>
                    <Button type="submit" className={"mt-3"}>Sign Up</Button>
                </Form>
            </Card>
            <ToastContainer position={"top-end"} className="p-5">
                <Toast show={showToast} onClose={() => setShowToast(false)} delay={3000} autohide
                       className={"bg-success"}>
                    <Toast.Header>
                        <strong className="me-auto">Email Verification</strong>
                    </Toast.Header>
                    <Toast.Body>Registration is completed but before you have to verify your email</Toast.Body>
                </Toast>
            </ToastContainer>
        </Container>

    );
}

export default RegistrationForm;