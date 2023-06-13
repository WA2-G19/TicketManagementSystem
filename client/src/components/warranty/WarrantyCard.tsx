import {Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import React from "react";
import {Warranty, Duration} from "../../classes/Warranty";
import {parseISO} from 'date-fns';

function WarrantyCard({warranty}: {
    warranty: Warranty
}): JSX.Element {
    const duration = Duration.fromString(warranty.duration)
    const creationTime = parseISO(warranty.creationTimestamp)
    const activationTime = parseISO(warranty.activationTimestamp)
    return <Container className={"border border-3 rounded border-primary"}>
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
                    <Col>{duration.hours}:{duration.minutes}:{duration.seconds}</Col>
                </Col>
            </Row>
        </Row>
    </Container>
}

export default WarrantyCard