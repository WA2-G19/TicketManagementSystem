import {Container, Form, Card, Button, Row, Col} from 'react-bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import React, {useState} from 'react';
import {Link, useNavigate} from "react-router-dom";
import {useAlert} from "../../contexts/Alert";
import {useAuthentication} from "../../contexts/Authentication";
import {
    MDBBtn,
    MDBContainer,
    MDBRow,
    MDBCol,
    MDBIcon,
    MDBInput
}
    from 'mdb-react-ui-kit';

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
                alert.getBuilder().setTitle("Error in email").setMessage("Email format is incorrect").show()
                return
            }
            await auth.login({username: email, password: pwd})
            navigate("/")
        } catch (e) {
            alert.getBuilder().setTitle("Error in login").setMessage("Email or password incorrect").show()
        }
    }

    return (
        <>
            {/*<Container fluid className="flex-grow-1 justify-content-center d-flex align-items-center p-lg-5">*/}
            {/*    <Card border="primary" style={{padding: '4rem'}}>*/}
            {/*        <Form onSubmit={(e: React.FormEvent) => onSubmit(e)}>*/}
            {/*            <Form.Group className="mb-2" controlId="formGroupEmail">*/}
            {/*                <Form.Label>Email address</Form.Label>*/}
            {/*                <Form.Control type="email" placeholder="email@address.dom" value={email} required={true}*/}
            {/*                              onChange={(e) => setEmail(e.target.value)}/>*/}
            {/*            </Form.Group>*/}
            {/*            <Form.Group className="mb-3" controlId="formGroupPassword">*/}
            {/*                <Form.Label>Password</Form.Label>*/}
            {/*                <Form.Control type="password" placeholder="Password" value={pwd} required={true}*/}
            {/*                              onChange={(e) => setPwd(e.target.value)}/>*/}
            {/*            </Form.Group>*/}
            {/*            <Row>*/}
            {/*                <Col>*/}
            {/*                    <Button variant="primary" type="submit">*/}
            {/*                        Login*/}
            {/*                    </Button>*/}
            {/*                </Col>*/}
            {/*                <Col className="d-flex align-items-center justify-content-center" md={8}>*/}
            {/*                    <p className="small">Don't have an account yet? <Link to={"/signup"}><span*/}
            {/*                        className={"text-primary"} style={{textDecoration: 'underline'}}*/}
            {/*                    >Sign Up</span></Link></p>*/}
            {/*                </Col>*/}
            {/*            </Row>*/}
            {/*        </Form>*/}
            {/*    </Card>*/}
            {/*</Container>*/}
            <MDBContainer fluid>
                <MDBRow>

                    <MDBCol sm='6'>

                        <div className='d-flex flex-row ps-5 pt-5'>
                            <MDBIcon fas icon="crow fa-3x me-3" style={{color: '#709085'}}/>
                            <span className="h1 fw-bold mb-0">Logo</span>
                        </div>

                        <div className='d-flex flex-column justify-content-center h-custom-2 w-75 pt-4'>

                            <h3 className="fw-normal mb-3 ps-5 pb-3" style={{letterSpacing: '1px'}}>Log in</h3>

                            <MDBInput wrapperClass='mb-4 mx-5 w-100' label='Email address' id='formControlLg'
                                      type='email' size="lg"/>
                            <MDBInput wrapperClass='mb-4 mx-5 w-100' label='Password' id='formControlLg' type='password'
                                      size="lg"/>

                            <MDBBtn className="mb-4 px-5 mx-5 w-100" color='info' size='lg'>Login</MDBBtn>
                            <p className="small mb-5 pb-lg-3 ms-5"><a className="text-muted" href="#!">Forgot
                                password?</a></p>
                            <p className='ms-5'>Don't have an account? <a href="#!" className="link-info">Register
                                here</a></p>

                        </div>

                    </MDBCol>

                    <MDBCol sm='6' className='d-none d-sm-block px-0'>
                        <img src="https://mdbcdn.b-cdn.net/img/Photos/new-templates/bootstrap-login-form/img3.webp"
                             alt="Login image" className="w-100" style={{objectFit: 'cover', objectPosition: 'left'}}/>
                    </MDBCol>

                </MDBRow>

            </MDBContainer>
        </>
    )
}

export default LoginForm