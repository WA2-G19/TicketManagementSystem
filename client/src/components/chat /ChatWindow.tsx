import {Container, Row} from "react-bootstrap";
import {Card, CardContent, List, TextField} from "@mui/material";
import React, {Dispatch, useRef, useState} from "react";
import {ChatReceiverItem} from "./ChatReceiverItem";
import {ChatSenderItem} from "./ChatSenderItem";
import {ChatMessageOut} from "../../classes/Chat";
import {useAuthentication} from "../../contexts/Authentication";

interface ChatWindowProps {
    setCurrentText: Dispatch<string>,
    currentText: string,
    messages: Array<ChatMessageOut>,
    setMessages: Dispatch<Array<ChatMessageOut>>
}

export function ChatWindow(props: ChatWindowProps) {

    const auth = useAuthentication()

    return <Container fluid>
        <Row>
            <Card style={{height: "50vh"}} sx={{overflow: 'auto'}} >
                {
                    props.messages.map((e, idx) => {
                        if (e.authorEmail == auth.user?.email) {
                            return <ChatSenderItem message={e.body} sender={e.authorEmail}/>
                        } else {
                            return <ChatReceiverItem message={e.body} sender={e.authorEmail}/>
                        }
                    })
                }
            </Card>
        </Row>
        <Row className={"pt-3"}>
            <TextField value={props.currentText} onChange={(e) => props.setCurrentText(e.target.value)}
                       placeholder={"Start to chatting..."}/>
        </Row>
    </Container>
}