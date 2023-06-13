import {useAuthentication} from "../../contexts/Authentication";
import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import ProductAPI from "../../API/Products/products";
import Product from "../../classes/Product";
import ProductCard from "../product/ProductCard";

function Products(): JSX.Element {
    const {user} = useAuthentication()
    const [products, setProducts] = useState(Array<Product>)
    useEffect(() => {
        async function getWarranties() {
            const tmp = await ProductAPI.getAllProducts(user!.token) as Array<Product>
            setProducts(tmp)
        }

        getWarranties()
            .catch(err => {

            })
    }, [user!.token])

    return (
        <Container fluid>
            <Row>
                {
                    products.length !== 0 && products.map((product, idx) =>
                        <Col xs={12} sm={6} md={4} className={"pt-3"} key={idx}>
                            <ProductCard product={product} key={idx}/>
                        </Col>
                    )
                }
                {
                    products.length === 0 &&
                    <Typography variant="h5" component="div" color="primary"
                                className={"position-absolute top-50 start-50"}>
                        <strong>No product found</strong>
                    </Typography>
                }
            </Row>
        </Container>
    )
}

export default Products