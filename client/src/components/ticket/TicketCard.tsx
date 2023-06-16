import {TicketOut} from "../../classes/Ticket";
import React from "react";
import {Button, Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import HasAnyRole from "../authentication/HasAnyRole";

function TicketCard({ticket, setSelected}: {
    ticket: TicketOut,
    setSelected?: (() => void)
}): JSX.Element {
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
            <Col>
                <Typography variant="body2" color="primary">
                    <strong>Status</strong>
                </Typography>
                {ticket.status}
            </Col>
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
                {ticket.productEan}
            </Col>
        </Row>
        <Row className={"pt-3"}>
            <Col md={2}><Button>Open chat</Button></Col>
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