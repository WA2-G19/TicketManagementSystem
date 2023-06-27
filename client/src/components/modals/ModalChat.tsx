import {Button, Col, Container, Form, Modal, Row} from "react-bootstrap";
import React, {useEffect, useRef, useState} from "react";
import {ChatWindow} from "../chat/ChatWindow";
import {ChatMessageIn, ChatMessageOut} from "../../classes/Chat";
import ChatAPI from "../../API/Ticketing/chat";
import {useAuthentication} from "../../contexts/Authentication";
import {useLocation, useNavigate} from "react-router-dom";
import TicketCard from "../ticket/TicketCard";



export function ModalChat() {

    const [currentText, setCurrentText] = useState("")
    const [files, setFiles] = useState<FileList | undefined>(undefined)
    const [messages, setMessages] = useState<Array<ChatMessageOut>>([])
    const ref = useRef<HTMLInputElement | null>(null)
    const refFiles = useRef<HTMLInputElement | null>(null)
    const auth = useAuthentication()
    const token = auth.user!.token
    const navigate = useNavigate()
    const {state} = useLocation()

    useEffect(() => {
        async function settingUpMessages() {
            const messages = await ChatAPI.getChatMessages(token, state.ticket.id) as Array<ChatMessageOut>
            setMessages(messages)
        }

        settingUpMessages()
    }, [token, state.ticket])

    useEffect(() => {
        const interval = setInterval(async () => {
            const messages = await ChatAPI.getChatMessages(token, state.ticket.id) as Array<ChatMessageOut>
            setMessages(messages)
            console.log("Updated....")
        }, 1000)

        return () => {
            clearInterval(interval)
        }
    }, [])

    const goBack = () => {
        navigate(-1)
    }


    const onSendMessage = async () => {
        setCurrentText("")
        const response = await ChatAPI.postChatMessages(token, state.ticket.id,
            new ChatMessageIn(currentText), files)
    }

    const onAttach = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFiles(e.target.files!!)
    }

    return <>
        <TicketCard ticket={state.ticket} chatopen={false}/>
        <Container>
            <Modal.Title>Chat</Modal.Title>
        </Container>
        <Modal.Body>
            <Container>
                <ChatWindow referenceCard={ref} setCurrentText={setCurrentText} currentText={currentText}
                            messages={messages} setMessages={setMessages}/>
            </Container>
        </Modal.Body>
        <Row>
            <Modal.Footer>
                <Col xs={2}>
                    <Button variant="secondary" onClick={() => onSendMessage()}>
                        Send
                    </Button>
                </Col>
                <Col xs={2}>
                    <Button variant="secondary" onClick={() => goBack()}>
                        Close
                    </Button>
                </Col>
                <Col xs={7} className={"p-3"}>
                    <Form.Group controlId="formFile">
                        <Form.Control ref={refFiles} type="file" name={"file"} onChange={onAttach} multiple/>
                    </Form.Group>
                </Col>
            </Modal.Footer>
        </Row>
    </>

}
