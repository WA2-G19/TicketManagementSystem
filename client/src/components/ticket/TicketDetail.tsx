import React, {FormEvent, useEffect, useRef, useState} from "react";
import {ChatMessageIn, ChatMessageOut} from "../../classes/Chat";
import {useAuthentication} from "../../contexts/Authentication";
import {Navigate, useNavigate, useParams} from "react-router-dom";
import ChatAPI from "../../API/Ticketing/chat";
import TicketCard from "./TicketCard";
import {Button, Col, Container, Form, InputGroup, Row} from "react-bootstrap";
import {useAlert} from "../../contexts/Alert";
import ProblemDetail from "../../classes/ProblemDetail";
import {BsArrowLeft, BsFileArrowDown} from "react-icons/bs";
import {TicketOut} from "../../classes/Ticket";
import TicketAPI from "../../API/Ticketing/tickets";
import "./chat.css"

function TicketDetail() {
    const [messages, setMessages] = useState(Array<ChatMessageOut>)
    const [ticket, setTicket] = useState<TicketOut | null>(null)
    const messageRef = useRef<HTMLInputElement>(null)
    const attachmentsRef = useRef<HTMLInputElement>(null)
    const auth = useAuthentication()
    const alert = useAlert()
    const token = auth.user!.token
    const navigate = useNavigate()
    const { ticketId: ticketIdString } = useParams()
    const ticketId = parseInt(ticketIdString || "-1")

    useEffect(() => {
        const interval = setInterval(() => {
            if (ticketId !== -1) {
                ChatAPI.getChatMessages(token, ticketId)
                    .then(messages => setMessages(messages))
                    .catch(err => {
                        const builder = alert.getBuilder()
                            .setTitle("Error")
                            .setButtonsOk()
                        if (err instanceof ProblemDetail) {
                            builder.setMessage("Error loading chat messages. Details: " + err.getDetails())
                        } else {
                            builder.setMessage("Error loading chat messages. Details: " + err)
                        }
                        builder.show()
                    })
            }
        }, 1000)

        return () => {
            clearInterval(interval)
        }
    }, [token, ticketId])

    useEffect(() => {
        async function getTicket() {
            setTicket(await TicketAPI.getTicketById(token, ticketId))
        }

        if (ticketId !== -1) {
            getTicket()
                .catch(err => {
                    const builder = alert.getBuilder()
                        .setTitle("Error")
                        .setButtonsOk()
                    if (err instanceof ProblemDetail) {
                        builder.setMessage("Error loading ticket. Details: " + err.getDetails())
                    } else {
                        builder.setMessage("Error loading ticket. Details: " + err)
                    }
                    builder.show()
                })
        }
    }, [token, ticketId])

    async function handleSubmit(e: FormEvent) {
        e.preventDefault()
        if (messageRef.current && attachmentsRef.current) {
            try {
                await ChatAPI.postChatMessages(token, ticketId, new ChatMessageIn(messageRef.current.value), attachmentsRef.current.files);
                (e.target as HTMLFormElement).reset()
            } catch (err) {
                const builder = alert.getBuilder()
                    .setTitle("Error")
                    .setButtonsOk()
                if (err instanceof ProblemDetail) {
                    builder.setMessage("Error sending message. Details: " + err.getDetails())
                } else {
                    builder.setMessage("Error sending message. Details: " + err)
                }
                builder.show()
            }
        }
    }

    async function downloadAttachment(name: string, url: string) {
        const response = await ChatAPI.getAttachmentByUrl(token, url)
        const link = document.createElement("a")
        link.href = URL.createObjectURL(response)
        link.setAttribute("download", name)
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
    }

    function stringToColor(s: string) {
        let hash = 0;
        for (let i = 0; i < s.length; i++) {
            hash = s.charCodeAt(i) + ((hash << 5) - hash);
        }
        const c =(hash & 0x00FFFFFF)
            .toString(16)
            .toUpperCase();

        return "#" + "00000".substring(0, 6 - c.length) + c;
    }

    if (!ticketIdString || isNaN(parseInt(ticketIdString))) {
        return (<Navigate to={"/tickets"} />)
    }

    return (
        <Container fluid className={"h-100 d-flex flex-column"}>
            <Row className={"mt-3"}>
                <Col className={"d-flex flex-row align-items-center"} xs={1}>
                    <BsArrowLeft size={"2em"} onClick={() => navigate(-1)} role={"button"} />
                </Col>
                <Col>
                    <h1>Ticket details</h1>
                </Col>
            </Row>
            {
                ticket &&
                <Row className={"mb-3"}>
                    <Col>
                        <TicketCard ticket={ticket} openDetails={false} />
                    </Col>
                </Row>
            }
            <Row className={"flex-grow-1 mb-3"}>
                <Col className={"d-flex flex-column"}>
                    <Container className={"chat"}>
                        {
                            messages.map(message => <Row key={message.id} className={"chat-message " + (message.authorEmail === auth.user!.email ? "from-me" : "from-them")}>
                                <Col>
                                    {
                                        message.authorEmail !== auth.user!.email &&
                                        <Row>
                                            <Col style={{color: stringToColor(message.authorEmail)}}>
                                                {message.authorEmail}
                                            </Col>
                                        </Row>
                                    }
                                    <Row>
                                        <Col>
                                            {message.body}
                                        </Col>
                                    </Row>
                                    {
                                        message.stubAttachments.length > 0 &&
                                        message.stubAttachments.map(a =>
                                            <Row key={a.url} role={"button"} onClick={() => downloadAttachment(a.name, a.url)}>
                                                <Col>
                                                    <BsFileArrowDown /> {a.name}
                                                </Col>
                                            </Row>)
                                    }
                                </Col>
                            </Row>)
                        }
                    </Container>
                </Col>
            </Row>
            <Row className={"mb-3"}>
                <Col>
                    <Form onSubmit={handleSubmit}>
                        <Row className={"mb-3"}>
                            <Col >
                                <InputGroup>
                                <Form.FloatingLabel label={"Message"}>
                                    <Form.Control required={true} ref={messageRef} />
                                </Form.FloatingLabel>
                                <Button type={"submit"}>
                                            Send
                                        </Button>
                                </InputGroup>
                                
                            </Col>
                           
                        </Row>
                        <Row>
                            <Col>
                                <Row>
                                    
                                    <Col>
                                        <Form.Control ref={attachmentsRef} type={"file"} required={false} multiple={true} />
                                    </Col>
                                </Row>
                            </Col>
                        </Row>
                    </Form>
                </Col>
            </Row>
        </Container>
    )
}

export default TicketDetail