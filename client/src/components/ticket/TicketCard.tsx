import {TicketOut} from "../../classes/Ticket";
import React, {useState} from "react";
import {Button, Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import HasAnyRole from "../authentication/HasAnyRole";
import {useAuthentication} from "../../contexts/Authentication";
import {useAlert} from "../../contexts/Alert";
import Product from "../../classes/Product";
import ProductAPI from "../../API/Products/products";
import ProductCard from "../product/ProductCard";
import {BsInfoCircle} from "react-icons/bs";
import {useNavigate} from "react-router-dom";
import {ChangeStatus} from "../chat/ChangeStatus";

function TicketCard({ticket, setSelected, chatopen}: {
    ticket: TicketOut,
    setSelected?: (() => void),
    chatopen: boolean
}): JSX.Element {
    const auth = useAuthentication()
    const alert = useAlert()
    const token = auth.user!.token
    const [productInfo, setProductInfo] = useState<Product | null>(null)
    const navigate = useNavigate()

    async function seeDetailsProduct() {
        const product = productInfo || await ProductAPI.getProductByEAN(token, ticket.productEan)
        if (productInfo === null)
            setProductInfo(product)
        alert.getBuilder()
            .setTitle("Product details")
            .setMessage(<Container>
                <ProductCard product={product}/>
            </Container>)
            .setButtonsOk()
            .show()
    }

    return <Container className={"border border-3 rounded border-primary p-3"}>
        <Row className={"ps-3"}>
            <Typography variant="h5" component="div" color="primary">
                ID {ticket.id}
            </Typography>
        </Row>
        <Row className={"pt-3"}>
            <HasAnyRole roles={["Expert", "Manager"]}>
                <Col>
                    <Typography variant="body2" color="primary">
                        <strong>Customer Email</strong>
                    </Typography>
                    {ticket.customerEmail}
                </Col>
                <Col>
                    <Typography variant="body2" color="primary">
                        <strong>Priority Level</strong>
                    </Typography>
                    {ticket.priorityLevel || 'Not assigned yet'}
                </Col>
            </HasAnyRole>

        </Row>
        <Row className={"pt-3"}>
            <Col>
                <Typography variant="body2" color="primary">
                    <strong>Description</strong>
                </Typography>
                {ticket.description}
            </Col>
            {chatopen && <Col>
                <Typography variant="body2" color="primary">
                    <strong>Status</strong>
                </Typography>
                {ticket.status}
            </Col>}
            {!chatopen && <ChangeStatus ticketTMP={ticket}/>}
        </Row>
        <Row className={"pt-3"}>
            <Col>
                <Typography variant="body2" color="primary">
                    <strong>Warranty UUID</strong>
                </Typography>
                {ticket.warrantyUUID}
            </Col>
            <Col>
                <Typography variant="body2" color="primary">
                    <strong>Expert Email</strong>
                </Typography>
                {ticket.expertEmail || 'Not assigned yet'}
            </Col>
        </Row>
        <Row className={"pt-3"}>
            <Col>
                <Typography variant="body2" color="primary">
                    <strong>EAN</strong>
                </Typography>
                {ticket.productEan}&nbsp;<BsInfoCircle role={"button"} className={"align-top"} size={"0.7em"}
                                                       onClick={seeDetailsProduct}/>
            </Col>
        </Row>
        <Row className={"pt-3"}>
            {chatopen && <Col md={2}>
                <Button onClick={() => navigate("/chat", {
                    state: {
                        ticket: ticket
                    }
                })}>Open chat</Button>
            </Col>}
            {
                setSelected !== undefined &&
                <Col>
                    <Button onClick={() => setSelected()}>Assign Ticket</Button>
                </Col>
            }
        </Row>
    </Container>
}

export default TicketCard