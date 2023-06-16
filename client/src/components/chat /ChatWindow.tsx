import {Container, Row} from "react-bootstrap";
import {Card, TextField} from "@mui/material";
import React, {Dispatch, useState} from "react";
import {ChatReceiverItem} from "./ChatReceiverItem";
import {ChatSenderItem} from "./ChatSenderItem";

interface ChatWindowProps {

    setCurrentText: Dispatch<string>,
    currentText: string
}

export function ChatWindow(props: ChatWindowProps) {


    return <Container fluid>
        <Row>
            <Card style={{height: "50vh"}}>
                <ChatReceiverItem/>
                <ChatSenderItem/>
            </Card>
        </Row>
        <Row className={"pt-3"}>
            <TextField value={props.currentText} onChange={(e) => props.setCurrentText(e.target.value)} placeholder={"Start to chatting..."}/>
        </Row>
    </Container>
}