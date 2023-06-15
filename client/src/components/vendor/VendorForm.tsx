import {useNavigate} from "react-router-dom";
import {useAuthentication} from "../../contexts/Authentication";
import {useAlert} from "../../contexts/Alert";
import {FormEvent, useRef} from "react";
import {Button, Col, Container, Form, Row} from "react-bootstrap";
import {BsArrowLeft} from "react-icons/bs";
import VendorAPI from "../../API/Profile/vendor";
import {CredentialVendor, Vendor} from "../../classes/Profile";

function VendorForm(): JSX.Element {
    const navigate = useNavigate()
    const { user } = useAuthentication()
    const alert = useAlert()
    const token = user!.token

    const emailRef = useRef<HTMLInputElement>(null)
    const businessNameRef = useRef<HTMLInputElement>(null)
    const phoneNumberRef = useRef<HTMLInputElement>(null)
    const addressRef = useRef<HTMLInputElement>(null)
    const passwordRef = useRef<HTMLInputElement>(null)
    const confirmPasswordRef = useRef<HTMLInputElement>(null)

    async function handleSubmit(e: FormEvent) {
        e.preventDefault()
        if (emailRef.current && businessNameRef.current && phoneNumberRef.current && phoneNumberRef.current && addressRef.current && passwordRef.current && confirmPasswordRef.current) {
            if (passwordRef.current.value !== confirmPasswordRef.current.value) {
                confirmPasswordRef.current.setCustomValidity("The two passwords are not matching")
                return
            }

            try {
                const response = await VendorAPI.createVendor(token, new CredentialVendor(
                    new Vendor(emailRef.current.value, businessNameRef.current.value, phoneNumberRef.current.value, addressRef.current.value),
                    passwordRef.current.value
                ))
                if (response) {
                    alert.getBuilder()
                        .setTitle("Vendor created")
                        .setMessage("Vendor created successfully!")
                        .setButtonsOk(() => navigate("/vendors"))
                        .show()
                } else {
                    alert.getBuilder()
                        .setTitle("Error")
                        .setMessage("Vendor creation failed. Try again later.")
                        .setButtonsOk()
                        .show()
                }
            } catch (e) {
                console.error(e)
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Vendor creation failed. Details: " + e)
                    .setButtonsOk()
                    .show()
            }
        }
    }

    return (
        <Container fluid>
            <Row>
                <Col className={"d-flex flex-row align-items-center"} xs={1}>
                    <BsArrowLeft size={"2em"} onClick={() => navigate(-1)} role={"button"} />
                </Col>
                <Col>
                    <h1>New Vendor</h1>
                </Col>
            </Row>
            <Row>
                <Col>
                    <Form onSubmit={handleSubmit}>
                        <Form.FloatingLabel
                            label={"Email"}
                            className={"mb-3"}
                        >
                            <Form.Control type={"email"} required={true} ref={emailRef} />
                        </Form.FloatingLabel>
                        <Form.FloatingLabel
                            label={"Business name"}
                            className={"mb-3"}
                        >
                            <Form.Control required={true} ref={businessNameRef} />
                        </Form.FloatingLabel>
                        <Form.FloatingLabel
                            label={"Phone number"}
                            className={"mb-3"}
                        >
                            <Form.Control type={"tel"} required={true} ref={phoneNumberRef} />
                        </Form.FloatingLabel>
                        <Form.FloatingLabel
                            label={"Address"}
                            className={"mb-3"}
                        >
                            <Form.Control required={true} ref={addressRef} />
                        </Form.FloatingLabel>
                        <Form.FloatingLabel
                            label={"Password"}
                            className={"mb-3"}
                        >
                            <Form.Control type={"password"} required={true} ref={passwordRef} />
                        </Form.FloatingLabel>
                        <Form.FloatingLabel
                            label={"Confirm password"}
                            className={"mb-3"}
                        >
                            <Form.Control type={"password"} required={true} ref={confirmPasswordRef} />
                        </Form.FloatingLabel>
                        <Button type={"submit"}>
                            Create vendor
                        </Button>
                    </Form>
                </Col>
            </Row>
        </Container>
    )
}

export default VendorForm