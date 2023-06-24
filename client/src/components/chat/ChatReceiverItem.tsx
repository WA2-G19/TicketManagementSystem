import {Row} from "react-bootstrap";
import React from "react";

interface ChatReceiverItemProps {
    message: string,
    sender: string
}

export function ChatReceiverItem(props: ChatReceiverItemProps): JSX.Element {
    return <Row className={"p-3 border border-3 flex-row text-center"}>
        <Row style={{textAlign: "center", background: "darkred"}}>
            {props.sender}
        </Row>
        <Row className={"pt-2"}>
            {props.message}
        </Row>
    </Row>
}