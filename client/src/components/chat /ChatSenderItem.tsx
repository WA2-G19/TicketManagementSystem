import {Col, Row} from "react-bootstrap";
import React from "react";


interface ChatSenderItemProps {
    message: string,
    sender: string
}

export function ChatSenderItem(props: ChatSenderItemProps): JSX.Element {
    return <Row className={"p-3 border border-3"}>
        <Row>
            {props.sender}
        </Row>
        <Row className={"pt-2"}>
            {props.message}
        </Row>

    </Row>
}