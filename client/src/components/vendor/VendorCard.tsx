import {Vendor} from "../../classes/Profile";
import {Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import React from "react";

function VendorCard({ vendor }: {
    vendor: Vendor
}): JSX.Element {

    return <Container className={"border border-3 rounded border-primary h-100"}>
        <Row className={"ps-3 mt-3"}>
            <Typography variant="h5" component="div" color="primary">
                {vendor.businessName}
            </Typography>
        </Row>
        <Row className={"p-3"}>
            <Row>
                <Col>
                    <Col>
                        <Typography variant="body2" color="primary">
                            <strong>Email</strong>
                        </Typography>
                    </Col>
                    <Col>{vendor.email}</Col>
                </Col>
                <Col>
                    <Col>
                        <Typography variant="body2" color="primary">
                            <strong>Phone</strong>
                        </Typography>
                    </Col>
                    <Col>{vendor.phoneNumber}</Col>
                </Col>
                <Col>
                    <Col>
                        <Typography variant="body2" color="primary">
                            <strong>Address</strong>
                        </Typography>
                    </Col>
                    <Col>{vendor.address}</Col>
                </Col>
            </Row>
        </Row>
    </Container>
}

export default VendorCard