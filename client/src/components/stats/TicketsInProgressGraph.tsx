import {Staff} from "../../classes/Profile";
import {Col, Container, Row} from "react-bootstrap";
import {Chart} from "react-google-charts";
import React from "react";

function TicketsInProgressGraph({
    experts
}: {
    experts: Staff[]
}) {

    const data = [
        ["Expert", "Tickets in progress"],
        ...experts.map(e => [`${e.name} ${e.surname}`, e.ticketsInProgress!])
    ]

    return (
        <Container fluid className={"border border-3 rounded border-primary h-100"}>
            <Row>
                <Col>
                    <Chart
                        chartType="Bar"
                        data={data}
                        options={{
                            title: "Tickets in progress",
                            is3D: true
                        }}
                    />
                </Col>
            </Row>
        </Container>
    )
}

export default TicketsInProgressGraph