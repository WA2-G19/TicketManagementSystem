import {Button, Col, Container, Form, Row} from "react-bootstrap";
import {BsArrowLeft} from "react-icons/bs";
import {useNavigate} from "react-router-dom";
import {FormEvent, useEffect, useRef, useState} from "react";
import Product from "../../classes/Product";
import ProductAPI from "../../API/Products/products";
import {useAuthentication} from "../../contexts/Authentication";
import {useAlert} from "../../contexts/Alert";
import WarrantyAPI from "../../API/Warranty/warranty";
import {Duration, WarrantyIn} from "../../classes/Warranty";
import HasRole from "../authentication/HasRole";

function WarrantyForm(): JSX.Element {
    const navigate = useNavigate()
    const { user } = useAuthentication()
    const alert = useAlert()
    const [products, setProducts] = useState(Array<Product>)
    const token = user!.token
    const isVendor = user!.role.includes("Vendor")
    const isClient = user!.role.includes("Client")

    const warrantyIdRef = useRef<HTMLInputElement>(null)
    const productRef = useRef<HTMLSelectElement>(null)
    const yearsRef = useRef<HTMLInputElement>(null)
    const monthsRef = useRef<HTMLInputElement>(null)
    const daysRef = useRef<HTMLInputElement>(null)
    const hoursRef = useRef<HTMLInputElement>(null)
    const minutesRef = useRef<HTMLInputElement>(null)
    const secondsRef = useRef<HTMLInputElement>(null)

    useEffect(() => {
        async function getProducts() {
            const tmp = await ProductAPI.getAllProducts(token)
            if (tmp !== undefined) {
                setProducts(tmp)
            } else {
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Error loading products. Try again later.")
                    .setButtonsOk()
                    .show()
            }
        }

        if (isVendor) {
            getProducts()
                .catch(err => {
                    alert.getBuilder()
                        .setTitle("Error")
                        .setMessage("Error loading products. Details: " + err)
                        .setButtonsOk()
                        .show()
                })
        }
    }, [token, isVendor])

    async function handleSubmit(e: FormEvent) {
        e.preventDefault()
        if (isVendor && productRef.current && yearsRef.current && monthsRef.current && daysRef.current && hoursRef.current && minutesRef.current && secondsRef.current) {
            try {
                const response = await WarrantyAPI.postWarranty(token, new WarrantyIn(
                    productRef.current.value,
                    new Duration(
                        parseFloat(yearsRef.current.value),
                        parseInt(monthsRef.current.value),
                        0,
                        parseInt(daysRef.current.value),
                        parseInt(hoursRef.current.value),
                        parseInt(minutesRef.current.value),
                        parseFloat(secondsRef.current.value),
                    ).toString()
                ))
                if (response) {
                    alert.getBuilder()
                        .setTitle("Warranty created")
                        .setMessage(<>
                            <p>Warranty created successfully! The code is shown below.</p>
                            <h3>{response.id}</h3>
                        </>)
                        .setButtonsOk()
                        .show()
                } else {
                    alert.getBuilder()
                        .setTitle("Error")
                        .setMessage("Error creating warranty. Try again later.")
                        .setButtonsOk()
                        .show()
                }
            } catch (e) {
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Error creating warranty. Details: " + e)
                    .setButtonsOk()
                    .show()
            }
        } else if (isClient && warrantyIdRef.current) {
            try {
                const response = await WarrantyAPI.activateWarranty(token, warrantyIdRef.current.value)
                if (response) {
                    alert.getBuilder()
                        .setTitle("Warranty activated")
                        .setMessage("Warranty activated successfully! You can see it in the list of warranties.")
                        .setButtonsOk(() => navigate("/warranties"))
                        .show()
                } else {
                    alert.getBuilder()
                        .setTitle("Error")
                        .setMessage("Error activating warranty. Try again later.")
                        .setButtonsOk()
                        .show()
                }
            } catch (e) {
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Error activating warranty. Details: " + e)
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
                    <h1>New Warranty</h1>
                </Col>
            </Row>
            <Row>
                <Col>
                    <Form onSubmit={handleSubmit}>
                        <HasRole role={"Vendor"}>
                            <Row>
                                <Col>
                                    <Form.FloatingLabel
                                        label={"Product"}
                                        className={"mb-3"}
                                    >
                                        <Form.Select required={true} ref={productRef}>
                                            {
                                                products.map(p =>
                                                    <option key={p.ean} value={p.ean}>
                                                        {
                                                            p.brand &&
                                                            `(${p.brand})`
                                                        }
                                                        {p.name}
                                                    </option>
                                                )
                                            }
                                        </Form.Select>
                                    </Form.FloatingLabel>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <Form.FloatingLabel
                                        label={"Years"}
                                        className={"mb-3"}
                                    >
                                        <Form.Control type={"number"} step={"0.01"} min={"0"} ref={yearsRef} defaultValue={0} />
                                    </Form.FloatingLabel>
                                </Col>
                                <Col>
                                    <Form.FloatingLabel
                                        label={"Months"}
                                        className={"mb-3"}
                                    >
                                        <Form.Control type={"number"} step={"1"} min={"0"} ref={monthsRef} defaultValue={0} />
                                    </Form.FloatingLabel>
                                </Col>
                                <Col>
                                    <Form.FloatingLabel
                                        label={"Days"}
                                        className={"mb-3"}
                                    >
                                        <Form.Control type={"number"} step={"1"} min={"0"} ref={daysRef} defaultValue={0} />
                                    </Form.FloatingLabel>
                                </Col>
                                <Col>
                                    <Form.FloatingLabel
                                        label={"Hours"}
                                        className={"mb-3"}
                                    >
                                        <Form.Control type={"number"} step={"1"} min={"0"} ref={hoursRef} defaultValue={0} />
                                    </Form.FloatingLabel>
                                </Col>
                                <Col>
                                    <Form.FloatingLabel
                                        label={"Minutes"}
                                        className={"mb-3"}
                                    >
                                        <Form.Control type={"number"} step={"1"} min={"0"} ref={minutesRef} defaultValue={0} />
                                    </Form.FloatingLabel>
                                </Col>
                                <Col>
                                    <Form.FloatingLabel
                                        label={"Seconds"}
                                        className={"mb-3"}
                                    >
                                        <Form.Control type={"number"} step={"0.01"} min={"0"} ref={secondsRef} defaultValue={0} />
                                    </Form.FloatingLabel>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <Button type={"submit"}>
                                        Create warranty
                                    </Button>
                                </Col>
                            </Row>
                        </HasRole>
                        <HasRole role={"Client"}>
                            <Row>
                                <Col>
                                    <Form.FloatingLabel
                                        label={"Warranty ID"}
                                        className={"mb-3"}
                                    >
                                        <Form.Control required={true} ref={warrantyIdRef}/>
                                    </Form.FloatingLabel>
                                </Col>
                            </Row>
                            <Row>
                                <Col>
                                    <Button type={"submit"}>
                                        Activate warranty
                                    </Button>
                                </Col>
                            </Row>
                        </HasRole>
                    </Form>
                </Col>
            </Row>
        </Container>
    )
}

export default WarrantyForm