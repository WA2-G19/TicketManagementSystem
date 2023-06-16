import {Col, Row} from "react-bootstrap";
import React from "react";
import {Divider} from "@mui/material";


interface ChatReceiverItemProps {

}

export function ChatReceiverItem(props: ChatReceiverItemProps): JSX.Element {
    return <Row className={"p-3 border border-3"}>
        <Col xs={10}>
            Hello from Receiver
        </Col>
        <Col xs = {2} className={"border border-3"}>
            Expert
        </Col>

    </Row>
}