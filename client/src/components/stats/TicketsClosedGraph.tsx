import {Staff} from "../../classes/Profile";
import {Col, Container, Row} from "react-bootstrap";
import {Chart} from "react-google-charts";
import React from "react";

function TicketsClosedGraph({
    experts
}: {
    experts: Staff[]
}) {

    const data = [
        ["Expert", "Tickets closed"],
        ...experts.map(e => [`${e.name} ${e.surname}`, e.ticketsClosed!])
    ]

    return (
        <Container fluid className={"border border-3 rounded border-primary h-100"}>
            <Row>
                <Col>
                    <Chart
                        chartType="Bar"
                        data={data}
                        options={{
                            title: "Tickets closed",
                            is3D: true
                        }}
                    />
                </Col>
            </Row>
        </Container>
    )
}

export default TicketsClosedGraph