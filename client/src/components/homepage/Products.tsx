import {useAuthentication} from "../../contexts/Authentication";
import React, {useEffect, useState} from "react";
import {Button, Col, Container, Row} from "react-bootstrap";
import ProductAPI from "../../API/Products/products";
import ProductCard from "../product/ProductCard";
import Loading from "../Loading";
import {useAlert} from "../../contexts/Alert";
import {BsPlus} from "react-icons/bs";
import HasRole from "../authentication/HasRole";
import {useNavigate} from "react-router-dom";
import ProblemDetail from "../../classes/ProblemDetail";
import Pagination from '@mui/material/Pagination';
import Stack from '@mui/material/Stack';
import PageProduct from "../../classes/PageProduct";

function Products(): JSX.Element {
    const navigate = useNavigate()
    const {user} = useAuthentication()
    const alert = useAlert()
    const [pageProduct, setPageProduct] = useState(new PageProduct(Array.of(), 1))
    const [loading, setLoading] = useState(true)
    
    const token = user!.token

    const getPage = async (page: number) =>{
        setLoading(true)
        setPageProduct((await ProductAPI.getAllProducts(token, page)))
        setLoading(false)

    }

    useEffect(() => {
        async function getWarranties() {
            setPageProduct(await ProductAPI.getAllProducts(token, 0))
            setLoading(false)
        }

        getWarranties()
            .catch(err => {
                const builder = alert.getBuilder()
                    .setTitle("Error")
                    .setButtonsOk()
                if (err instanceof ProblemDetail) {
                    builder.setMessage("Error loading tickets. Details: " + err.getDetails())
                } else {
                    builder.setMessage("Error loading tickets. Details: " + err)
                }
                builder.show()
            })
    }, [token])

    return (
        <Container fluid>
            <Row className={"mt-3"}>
                <Col>
                    <h1>Products</h1>
                </Col>
                <HasRole role={"Manager"}>
                    <Col className={"d-flex flex-row align-items-center"} xs={2}>
                        <Button onClick={() => navigate("/products/add")}> Add Product
                            <BsPlus size={"2em"}  role={"button"}/>
                        </Button>
                    </Col>
                </HasRole>
            </Row>
            <Stack spacing={2} alignItems="center">
                <Pagination count={pageProduct.totalPages} variant="outlined" shape="rounded" size="large" onChange={
                    (_, page) =>  getPage(page)
                }

                />
            </Stack>
            {loading && <Loading/>}
            <Row>
                {
                    !loading && pageProduct.products.length !== 0 && pageProduct.products.map(product =>
                        <Col xs={12} sm={6} md={4} className={"pt-3 d-flex flex-column"} key={product.ean}>
                            <ProductCard product={product}/>
                        </Col>
                    )
                }
            </Row>
            {
                !loading && pageProduct.products.length === 0 &&
                <h1 color="primary" className={"position-absolute top-50 start-50"}>
                    <strong>No product found</strong>
                </h1>
            }
        </Container>)
}

export default Products