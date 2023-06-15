import {useAuthentication} from "../../contexts/Authentication";
import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import ProductAPI from "../../API/Products/products";
import Product from "../../classes/Product";
import ProductCard from "../product/ProductCard";
import {Loading} from "../Loading";
import { useAlert } from "../../contexts/Alert";

function Products(): JSX.Element {
    const {user} = useAuthentication()
    const alert = useAlert()
    const [products, setProducts] = useState(Array<Product>)
    const [loading, setLoading] = useState(true)
    const token = user!.token
    useEffect(() => {
        async function getWarranties() {
            const tmp = await ProductAPI.getAllProducts(token)
            if (tmp) {
                setProducts(tmp)
            } else {
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Error loading tickets. Try again later.")
                    .setButtonsOk()
                    .show()
            }
            setLoading(false)
        }

        getWarranties()
            .catch(err => {
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Error loading tickets. Details: " + err)
                    .setButtonsOk()
                    .show()
            })
    }, [token])

    return (
        <Container fluid>
            {loading && <Loading/>}
            <Row>
                {
                    !loading && products.length !== 0 && products.map(product =>
                        <Col xs={12} sm={6} md={4} className={"pt-3"} key={product.ean}>
                            <ProductCard product={product} />
                        </Col>
                    )
                }
                {
                    !loading && products.length === 0 &&
                    <Typography variant="h5" component="div" color="primary"
                                className={"position-absolute top-50 start-50"}>
                        <strong>No product found</strong>
                    </Typography>
                }
            </Row>
        </Container>)
}

export default Products