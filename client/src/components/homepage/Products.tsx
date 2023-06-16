import {useAuthentication} from "../../contexts/Authentication";
import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import ProductAPI from "../../API/Products/products";
import Product from "../../classes/Product";
import ProductCard from "../product/ProductCard";
import {Loading} from "../Loading";
import {useAlert} from "../../contexts/Alert";
import {BsPlus} from "react-icons/bs";
import HasRole from "../authentication/HasRole";
import {useNavigate} from "react-router-dom";

function Products(): JSX.Element {
    const navigate = useNavigate()
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
            <Row className={"mt-3"}>
                <Col>
                    <h1>Products</h1>
                </Col>
                <HasRole role={"Manager"}>
                    <Col className={"d-flex flex-row align-items-center"} xs={1}>
                        <BsPlus size={"2em"} onClick={() => navigate("/products/add")} role={"button"}/>
                    </Col>
                </HasRole>
            </Row>
            {loading && <Loading/>}
            <Row>
                {
                    !loading && products.length !== 0 && products.map(product =>
                        <Col xs={12} sm={6} md={4} className={"pt-3 h-100"} key={product.ean}>
                            <ProductCard product={product}/>
                        </Col>
                    )
                }
            </Row>
            {
                !loading && products.length === 0 &&
                <h1 color="primary" className={"position-absolute top-50 start-50"}>
                    <strong>No product found</strong>
                </h1>
            }
        </Container>)
}

export default Products