import {Button, Col, Container, Form, Modal, Row} from "react-bootstrap";
import React, {Dispatch, useEffect, useRef, useState} from "react";
import {ChatWindow} from "../chat/ChatWindow";
import {ChatMessageIn, ChatMessageOut, StubAttachmentDTO} from "../../classes/Chat";
import ChatAPI from "../../API/Ticketing/chat";
import {useAuthentication} from "../../contexts/Authentication";

interface ModalChatProps {
    show: boolean,
    setShow: Dispatch<boolean>,
    ticket: number,
}

export function ModalChat({ show, setShow, ticket}: ModalChatProps) {

    const [currentText, setCurrentText] = useState("")
    const [files, setFiles] = useState<FileList | undefined>(undefined)
    const [messages, setMessages] = useState<Array<ChatMessageOut>>([])
    const ref = useRef<HTMLInputElement | null>(null)
    const auth = useAuthentication()
    const token = auth.user!.token

    useEffect(() => {
        async function settingUpMessages() {
            const messages = await ChatAPI.getChatMessages(token, ticket) as Array<ChatMessageOut>
            setMessages(messages.sort((n1,n2) => {
                if (n1.id > n2.id) return 1;
                else if (n1.id < n2.id) return -1;
                else return 0;
            }))
        }
        settingUpMessages()
    }, [token, ticket])

    const onSendMessage = async () => {
        setCurrentText("")

        const response = await ChatAPI.postChatMessages(token, ticket,
            new ChatMessageIn(currentText), files)
        if(response) {
            setMessages((e) => [...e, new ChatMessageOut(currentText, 0, auth.user!.email, "", new Set<StubAttachmentDTO>())])
            ref.current?.lastElementChild?.scrollIntoView();
            console.log(ref)
        }
    }

    const onAttach = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFiles(e.target.files!!)
    }

    return <Modal show={show} scrollable={true} fullscreen={"md-down"}>
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
                    <Button variant="secondary" onClick={() => setShow(false)}>
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
