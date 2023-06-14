import {Badge, Button, Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import React from "react";
import {WarrantyOut, Duration} from "../../classes/Warranty";
import {addDays, addHours, addMinutes, addMonths, addSeconds, addWeeks, addYears, parseISO} from 'date-fns';
import HasRole from "../authentication/HasRole";
import {useNavigate} from "react-router-dom";

function WarrantyCard({warranty}: {
    warranty: WarrantyOut
}): JSX.Element {
    const navigate = useNavigate()
    const duration = Duration.fromString(warranty.duration)
    const creationTime = parseISO(warranty.creationTimestamp)
    const activationTime = parseISO(warranty.activationTimestamp)

    function isExpired() {
        let expiredDate = activationTime
        if(duration.days != 0) {
            expiredDate = addDays(expiredDate, duration.hours)
        }
        if(duration.months != 0) {
            expiredDate = addMonths(expiredDate, duration.hours)
        }
        if(duration.years != 0) {
            expiredDate = addYears(expiredDate, duration.hours)
        }
        if(duration.weeks != 0) {
            expiredDate = addWeeks(expiredDate, duration.hours)
        }
        if(duration.hours != 0) {
            expiredDate = addHours(expiredDate, duration.hours)
        }
        if(duration.minutes != 0) {
            expiredDate = addMinutes(expiredDate, duration.hours)
        }
        if(duration.seconds != 0) {
            expiredDate = addSeconds(expiredDate, duration.hours)
        }
        return new Date(Date.now()) < expiredDate
    }

    return <Container className={"border border-3 rounded border-primary"}>
        <Row className={"pt-3 ms-1"} style={{display: "flex", justifyContent: "left"}}>
            <Col>
                {!isExpired() ? <h4><Badge bg={"danger"}>Expired</Badge></h4> : <h4><Badge bg={"success"}>Valid</Badge></h4>}
            </Col>
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
                    <Col>{warranty.productEan}</Col>
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
                    <Col>{creationTime.getDate()}/{creationTime.getMonth()}/{creationTime.getFullYear()}</Col>
                    <Col>{creationTime.getHours()}:{creationTime.getMinutes()}:{creationTime.getSeconds()}</Col>
                </Col>
                <Col>
                    <Col>
                        <Typography variant="body2" color="primary">
                            <strong>Activation time</strong>
                        </Typography>
                    </Col>
                    <Col>{activationTime.getDate()}/{activationTime.getMonth()}/{activationTime.getFullYear()}</Col>
                    <Col>{activationTime.getHours()}:{activationTime.getMinutes()}:{activationTime.getSeconds()}</Col>
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
                {isExpired() ? <Col>
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
}

export default WarrantyCard