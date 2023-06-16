import {Button, Col, Container, Form, Modal, Row} from "react-bootstrap";
import ProductCard from "../product/ProductCard";
import React, {Dispatch, useState} from "react";
import Product from "../../classes/Product";
import {ChatWindow} from "../chat /ChatWindow";
import {FormControl} from "@mui/material";

interface ModalChatProps {
    show: boolean,
    setShow: Dispatch<boolean>
}

export function ModalChat(props: ModalChatProps) {

    const [currentText, setCurrentText] = useState("")
    const [file, setFile] = useState(undefined)

    const onSendMessage = () => {
        console.log(currentText)
        setCurrentText("")
    }

    const onAttach = () => {

    }

    return <Modal show={props.show} scrollable={true} fullscreen={"md-down"}>
        <Modal.Header>
            <Modal.Title>Ticket Chat</Modal.Title>
        </Modal.Header>
        <Modal.Body>
            <Container>
                <ChatWindow setCurrentText={setCurrentText} currentText={currentText}/>
            </Container>
        </Modal.Body>
        <Row>
            <Modal.Footer>
                <Col xs = {2}>
                    <Button variant="secondary" onClick={() => onSendMessage()}>
                        Send
                    </Button>
                </Col>
                <Col xs ={2}>
                    <Button variant="secondary" onClick={() => props.setShow(false)}>
                        Close
                    </Button>
                </Col>
                <Col xs ={7} className={"p-3"}>
                    <Form.Group controlId="formFile">
                        <Form.Control type="file"/>
                    </Form.Group>
                </Col>
            </Modal.Footer>
        </Row>
    </Modal>
}
