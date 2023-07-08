import {Badge, Button, Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import React, {useState} from "react";
import {WarrantyOut, Period} from "../../classes/Warranty";
import HasRole from "../authentication/HasRole";
import {useNavigate} from "react-router-dom";
import ProductAPI from "../../API/Products/products";
import {useAuthentication} from "../../contexts/Authentication";
import Product from "../../classes/Product";
import {BsInfoCircle} from "react-icons/bs";
import ProductCard from "../product/ProductCard";
import {useAlert} from "../../contexts/Alert";
import WarrantyAPI from "../../API/Warranty/warranty";
import ProblemDetail from "../../classes/ProblemDetail";

function WarrantyCard({ warranty, now = new Date(Date.now()), remove }: {
    warranty: WarrantyOut,
    now?: Date,
    remove: (arg0 : number) => void
}): JSX.Element {
    const navigate = useNavigate()
    const duration = Period.fromString(warranty.duration)
    const creationTime = new Date(warranty.creationTimestamp)
    const activationTime = new Date(warranty.activationTimestamp)
    const isExpired = duration.addToDate(creationTime) < now
    const auth = useAuthentication()
    const alert = useAlert()
    const token = auth.user!.token
    const [productInfo, setProductInfo] = useState<Product | null>(null)

    async function seeDetailsProduct() {
        const product = productInfo || await ProductAPI.getProductByEAN(token, warranty.productEan)
        if (productInfo === null)
            setProductInfo(product)
        alert.getBuilder()
            .setTitle("Product details")
            .setMessage(<Container>
                <ProductCard product={product} />
            </Container>)
            .setButtonsOk()
            .show()
    }

    async function deleteWarranty(){
        try {
            await WarrantyAPI.deleteWarranty(token, warranty.id)
            remove(warranty.id)
        } catch (e) {
            const builder = alert.getBuilder()
                .setTitle("Error")
                .setButtonsOk()
            if (e instanceof ProblemDetail) {
                builder.setMessage("Error deleting warranty. Details: " + e.getDetails("<br/>"))
            } else {
                builder.setMessage("Error deleting warranty. Details: " + e)
            }
            builder.show()
        }
    }

    return (
        <Container className={"border border-3 rounded border-primary h-100"}>
            <Row className={"pt-3 ms-1 d-flex justify-content-start"}>
                <Col>
                    {
                        isExpired ?
                            <h4><Badge bg={"danger"}>Expired</Badge></h4>
                        :
                            <h4><Badge bg={"success"}>Valid</Badge></h4>
                    }
                </Col>
                { warranty.activationTimestamp === null ?
                    <Col>
                        <Button variant="danger" onClick={deleteWarranty}>Delete</Button>
                    </Col>: <></>
                }

            </Row>
            <Row className={"ps-3 mt-3"}>
                <Typography variant="h5" component="div" color="primary">
                    <strong>ID</strong>
                </Typography>
                <Col>{warranty.id}</Col>
            </Row>
            <Row className={"p-3"}>
                <Row>
                    <Col>
                        <Col>
                            <Typography variant="body2" color="primary">
                                <strong>Product EAN</strong>
                            </Typography>
                        </Col>
                        <Col>
                            {warranty.productEan}&nbsp;<BsInfoCircle role={"button"} className={"align-top"} size={"0.7em"} onClick={seeDetailsProduct} />
                        </Col>
                    </Col>
                    <Col>
                        <Col>
                            <Typography variant="body2" color="primary">
                                <strong>Vendor Email</strong>
                            </Typography>
                        </Col>
                        <Col>{warranty.vendorEmail}</Col>
                    </Col>
                    <Col>
                        <Col>
                            <Typography variant="body2" color="primary">
                                <strong>Customer Email</strong>
                            </Typography>
                        </Col>
                        <Col>{warranty.customerEmail}</Col>
                    </Col>
                </Row>
            </Row>
            <Row className={"p-3"}>
                <Row>
                    <Col>
                        <Col>
                            <Typography variant="body2" color="primary">
                                <strong>Creation time</strong>
                            </Typography>
                        </Col>
                        <Col>{creationTime.toLocaleDateString()}</Col>
                    </Col>
                    <Col>
                        <Col>
                            <Typography variant="body2" color="primary">
                                <strong>Activation time</strong>
                            </Typography>
                        </Col>
                        {
                            warranty.activationTimestamp !== null &&
                                <Col>{activationTime.toLocaleDateString()}</Col>
                        }
                    </Col>
                    <Col>
                        <Col>
                            <Typography variant="body2" color="primary">
                                <strong>Duration</strong>
                            </Typography>
                        </Col>
                        <Col>{duration.toFormattedString()}</Col>
                    </Col>
                </Row>
            </Row>
            <HasRole role={"Client"}>
                <Row className={"p-3"}>
                    {!isExpired ? <Col>
                        <Button variant={"primary"} onClick={() => navigate("/tickets/add", {
                            state: {
                                warranty: warranty
                            }
                        })}>
                            Open ticket
                        </Button>
                    </Col> : <></>}
                </Row>
            </HasRole>
        </Container>
    )
}

export default WarrantyCard