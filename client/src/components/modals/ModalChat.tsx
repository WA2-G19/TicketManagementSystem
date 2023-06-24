import {Button, Col, Container, Form, FormControlProps, Modal, Row} from "react-bootstrap";
import ProductCard from "../product/ProductCard";
import React, {Dispatch, useEffect, useRef, useState} from "react";
import Product from "../../classes/Product";
import {ChatWindow} from "../chat /ChatWindow";
import {FormControl} from "@mui/material";
import {ChatMessageIn, ChatMessageOut, StubAttachmentDTO} from "../../classes/Chat";
import ChatAPI from "../../API/Ticketing/chat";
import {useAuthentication} from "../../contexts/Authentication";

interface ModalChatProps {
    show: boolean,
    setShow: Dispatch<boolean>,
    ticket: number,
}

export function ModalChat(props: ModalChatProps) {

    const [currentText, setCurrentText] = useState("")
    const [files, setFiles] = useState<FileList | undefined>(undefined)
    const [messages, setMessages] = useState<Array<ChatMessageOut>>([])
    const ref = useRef<HTMLInputElement | null>(null)
    const auth = useAuthentication()

    useEffect(() => {
        async function settingUpMessages() {
            const messages = await ChatAPI.getChatMessages(auth.user?.token, props.ticket)
            setMessages(messages)
        }
        settingUpMessages()
    }, [])

    const onSendMessage = async () => {
        setCurrentText("")
        const response = await ChatAPI.postChatMessages(auth.user?.token, props.ticket,
            new ChatMessageIn(currentText), files)
        if(response) {
            setMessages((e) => [...e, new ChatMessageOut(currentText, 0, auth.user?.email!!, "", new Set<StubAttachmentDTO>())])
            ref.current?.lastElementChild?.scrollIntoView();
            console.log(ref)
        }
    }

    const onAttach = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFiles(e.target.files!!)
    }

    return <Modal show={props.show} scrollable={true} fullscreen={"md-down"}>
        <Modal.Header>
            <Modal.Title>Ticket Chat</Modal.Title>
        </Modal.Header>
        <Modal.Body>
            <Container>
                <ChatWindow referenceCard = {ref} setCurrentText={setCurrentText} currentText={currentText} messages={messages} setMessages={setMessages}/>
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
                        <Form.Control type="file" name={"file"} onChange={onAttach} multiple/>
                    </Form.Group>
                </Col>
            </Modal.Footer>
        </Row>
    </Modal>
}
