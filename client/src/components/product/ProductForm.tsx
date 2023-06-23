import {useNavigate} from "react-router-dom";
import {useAuthentication} from "../../contexts/Authentication";
import {useAlert} from "../../contexts/Alert";
import {FormEvent, useRef} from "react";
import {Button, Col, Container, Form, Row} from "react-bootstrap";
import {BsArrowLeft} from "react-icons/bs";
import ProductAPI from "../../API/Products/products";
import Product from "../../classes/Product";
import ProblemDetail from "../../classes/ProblemDetail";

function ProductForm(): JSX.Element {
    const navigate = useNavigate()
    const { user } = useAuthentication()
    const alert = useAlert()
    const token = user!.token

    const eanRef = useRef<HTMLInputElement>(null)
    const nameRef = useRef<HTMLInputElement>(null)
    const brandRef = useRef<HTMLInputElement>(null)

    async function handleSubmit(e: FormEvent) {
        e.preventDefault()
        if (eanRef.current && nameRef.current && brandRef.current) {
            try {
                await ProductAPI.postProduct(token, new Product(eanRef.current.value, nameRef.current.value, brandRef.current.value))
                alert.getBuilder()
                    .setTitle("Product created")
                    .setMessage("Product created successfully!")
                    .setButtonsOk(() => navigate("/products"))
                    .show()
            } catch (e) {
                console.error(e)
                if (e instanceof ProblemDetail) {
                    alert.getBuilder()
                        .setTitle("Error")
                        .setMessage("Product creation failed. Details: " + e.getDetails("<br/>"))
                        .setButtonsOk()
                        .show()
                } else {
                    alert.getBuilder()
                        .setTitle("Error")
                        .setMessage("Product creation failed. Details: " + e)
                        .setButtonsOk()
                        .show()
                }
            }
        }
    }

    return (
        <Container fluid>
            <Row className={"mt-3"}>
                <Col className={"d-flex flex-row align-items-center"} xs={1}>
                    <BsArrowLeft size={"2em"} onClick={() => navigate(-1)} role={"button"} />
                </Col>
                <Col>
                    <h1>New Product</h1>
                </Col>
            </Row>
            <Row>
                <Col>
                    <Form onSubmit={handleSubmit}>
                        <Form.FloatingLabel
                            label={"EAN"}
                            className={"mb-3"}
                        >
                            <Form.Control required={true} ref={eanRef} pattern={"^[0-9]{13}$"} onInput={(e) => {
                                const value = e.currentTarget.value
                                const checkDigit = parseInt(value[value.length - 1])
                                let sumEven = 0
                                let sumOdd = 0
                                for (let i = 0;i < value.length - 1;i++) {
                                    if (i % 2 === 0)
                                        sumEven += parseInt(value[i])
                                    else
                                        sumOdd += parseInt(value[i])
                                }
                                let check = (sumEven * 3 + sumOdd) % 10
                                if (check !== 0)
                                    check = 10 - check
                                if (checkDigit !== check) {
                                    e.currentTarget.setCustomValidity("This is not a valid EAN-13")
                                } else {
                                    e.currentTarget.setCustomValidity("")
                                }
                            }} />
                        </Form.FloatingLabel>
                        <Form.FloatingLabel
                            label={"Name"}
                            className={"mb-3"}
                        >
                            <Form.Control required={true} ref={nameRef} />
                        </Form.FloatingLabel>
                        <Form.FloatingLabel
                            label={"Brand"}
                            className={"mb-3"}
                        >
                            <Form.Control required={true} ref={brandRef} />
                        </Form.FloatingLabel>
                        <Button type={"submit"}>
                            Create product
                        </Button>
                    </Form>
                </Col>
            </Row>
        </Container>
    )
}

export default ProductForm