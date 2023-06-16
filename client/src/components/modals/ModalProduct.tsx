import {Button, Container, Modal} from "react-bootstrap";
import ProductCard from "../product/ProductCard";
import React, {Dispatch} from "react";
import Product from "../../classes/Product";

interface ModalProductProps {
    show: boolean,
    setShow: Dispatch<boolean>,
    product: Product | undefined
}

export function ModalProduct(props: ModalProductProps) {

    return <Modal show={props.show}  scrollable={true} fullscreen={"md-down"}>
        <Modal.Header>
            <Modal.Title>Product Details</Modal.Title>
        </Modal.Header>
        <Modal.Body>
            <Container>
                <ProductCard product={props.product}/>
            </Container>
        </Modal.Body>
        <Modal.Footer>
            <Button variant="secondary" onClick={() => props.setShow(false)}>
                Close
            </Button>
        </Modal.Footer>
    </Modal>
}
