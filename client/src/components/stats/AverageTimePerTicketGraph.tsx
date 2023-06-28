import {Staff} from "../../classes/Profile";
import {Col, Container, Row} from "react-bootstrap";
import {Chart} from "react-google-charts";
import React from "react";

function AverageTimePerTicketGraph({
    experts
}: {
    experts: Staff[]
}) {

    const data = [
        ["Expert", "Average Time Per Ticket"],
        ...experts.map(e => [`${e.name} ${e.surname}`, e.avgTime!])
    ]

    return (
        <Container fluid className={"border border-3 rounded border-primary h-100"}>
            <Row>
                <Col>
                    <Chart
                        chartType="Bar"
                        data={data}
                        options={{
                            title: "Average time per ticket",
                            is3D: true
                        }}
                    />
                </Col>
            </Row>
        </Container>
    )
}

export default AverageTimePerTicketGraph