import {Badge, Button, Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import React, {useEffect} from "react";
import {WarrantyOut, Duration} from "../../classes/Warranty";
import HasRole from "../authentication/HasRole";
import {useNavigate} from "react-router-dom";

function WarrantyCard({ warranty, now = new Date(Date.now()) }: {
    warranty: WarrantyOut,
    now?: Date
}): JSX.Element {
    const navigate = useNavigate()
    const duration = Duration.fromString(warranty.duration)
    const creationTime = new Date(warranty.creationTimestamp)
    const activationTime = new Date(warranty.activationTimestamp)
    const isExpired = duration.addToDate(creationTime) < now

    useEffect(() => {
        console.log("Creation", creationTime.toISOString(), "Expiration", duration.addToDate(creationTime).toISOString())
    }, [])

    return <Container className={"border border-3 rounded border-primary"}>
        <Row className={"pt-3 ms-1"} style={{display: "flex", justifyContent: "left"}}>
            <Col>
                {
                    isExpired ?
                        <h4><Badge bg={"danger"}>Expired</Badge></h4>
                    :
                        <h4><Badge bg={"success"}>Valid</Badge></h4>
                }
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
}

export default WarrantyCard