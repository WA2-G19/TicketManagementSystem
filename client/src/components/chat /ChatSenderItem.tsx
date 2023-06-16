import {Col, Row} from "react-bootstrap";
import React from "react";


interface ChatSenderItemProps {

}

export function ChatSenderItem(props: ChatSenderItemProps): JSX.Element {
    return <Row className={"p-3 border border-3"}>
        <Col xs = {2} className={"border border-3"}>
                Me
        </Col>
        <Col>
            Hello from Sender
        </Col>
    </Row>
}