import {Button, Col, Container, Form, Modal, Row} from "react-bootstrap";
import React, {useEffect, useRef, useState} from "react";
import {ChatWindow} from "../chat/ChatWindow";
import {ChatMessageIn, ChatMessageOut, StubAttachmentDTO} from "../../classes/Chat";
import ChatAPI from "../../API/Ticketing/chat";
import {useAuthentication} from "../../contexts/Authentication";
import {useLocation, useNavigate} from "react-router-dom";
import {Ticket} from "../../classes/Ticket";
import TicketCard from "../ticket/TicketCard";

interface ModalChatProps {
    ticket?: Ticket,
}

export function ModalChat({ticket}: ModalChatProps) {

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
            setMessages(messages.sort((n1, n2) => {
                if (n1.id > n2.id) return 1;
                else if (n1.id < n2.id) return -1;
                else return 0;
            }))
        }

        settingUpMessages()
    }, [token, ticket])

    const timeout = setTimeout(async () => {
        const messages = await ChatAPI.getChatMessages(token, state.ticket) as Array<ChatMessageOut>
        setMessages(messages.sort((n1, n2) => {
            if (n1.id > n2.id) return 1;
            else if (n1.id < n2.id) return -1;
            else return 0;
        }))
        console.log("Updated....")
    }, 5 * 1000)

    const goBack = () => {
        clearTimeout(timeout)
        navigate(-1)
    }


    const onSendMessage = async () => {
        setCurrentText("")
        const response = await ChatAPI.postChatMessages(token, state.ticket,
            new ChatMessageIn(currentText), files)
        if (response) {
            setMessages((e) => [...e, new ChatMessageOut(currentText, 0, auth.user!.email, "", new Set<StubAttachmentDTO>())])
            ref.current?.lastElementChild?.scrollIntoView();
        }
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
