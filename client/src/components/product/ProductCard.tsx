import {Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import React from "react";
import Product from "../../classes/Product";

function ProductCard({product}: {
    product: Product | undefined
}): JSX.Element {
    return <Container className={"border border-3 rounded border-primary"}>
        <Row className={"ps-3 mt-3"}>
            <Typography variant="h5" component="div" color="primary">
                <strong>EAN</strong>
            </Typography>
            <Col>{product?.ean}</Col>
        </Row>
        <Row className={"p-3"}>
            <Row>
                <Col>
                    <Col>
                        <Typography variant="body2" color="primary">
                            <strong>Brand</strong>
                        </Typography>
                    </Col>
                    <Col>{product?.brand}</Col>
                </Col>
                <Col>
                    <Col>
                        <Typography variant="body2" color="primary">
                            <strong>Name</strong>
                        </Typography>
                    </Col>
                    <Col>{product?.name}</Col>
                </Col>
            </Row>
        </Row>
    </Container>
}

export default ProductCard