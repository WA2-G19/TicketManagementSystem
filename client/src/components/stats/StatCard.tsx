import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {Staff} from "../../classes/Profile";
import {Typography} from "@mui/material";
import {Chart} from "react-google-charts";
import TicketAPI from "../../API/Ticketing/tickets";
import {useAuthentication} from "../../contexts/Authentication";
import {TicketOut, TicketStatusEnum} from "../../classes/Ticket";

interface StatCardProp {
    expert: Staff
}

const optionsAverageTime = {
    title: "Average Time to close",
    is3D: true,
};

const optionsClosedTickets = {
    title: "Closed Tickets",
    is3D: true,
};

const optionsInProgressTickets = {
    title: "In Progress Tickets",
    is3D: true,
};


export function StatCard(props: StatCardProp) {
    const [tickets, setTickets] = useState<Array<TicketOut>>()
    const auth = useAuthentication()
    const token = auth.user!.token

    useEffect(() => {
        async function getTickets() {
            const tickets = await TicketAPI.getTickets(token)
            setTickets(tickets)
        }
        getTickets()
    }, [token])

    const dataAvgTime = [
        ["Label", "Value"],
        [props.expert.name, props.expert.avgTime]
    ];

    const dataClosedTickets = [
        ["Ticket", "Closed"],
        ["Others", tickets ? tickets.filter(e => e.status === TicketStatusEnum.Closed).length : 0],
        [props.expert.name, props.expert.ticketsClosed]
    ];

    const dataInProgressTicket = [
        ["Ticket", "Progress"],
        ["Others", tickets ? tickets.filter(e => e.status === TicketStatusEnum.InProgress).length : 0],
        [props.expert.name, props.expert.ticketsInProgress]
    ];

    return tickets ? <Container className={"border border-3 rounded border-primary"}>
        <Row className={"ps-3 mt-3"}>
            <Col className={"d-flex flex-row justify-content-between"}>
                <Typography variant="h5" component="div" color="primary">
                    {props.expert.name + " " + props.expert.surname}
                </Typography>
            </Col>
        </Row>
        <Row className={"p-3"}>
            <Col xs={4}>
                <Chart
                    chartType="Gauge"
                    data={dataAvgTime}
                    options={optionsAverageTime}
                />
            </Col>
            <Col xs={4}>
                <Chart
                    chartType="Bar"
                    data={dataClosedTickets}
                    options={optionsClosedTickets}
                />
            </Col>
            <Col xs={4}>
                <Chart
                    chartType="Bar"
                    data={dataInProgressTicket}
                    options={optionsInProgressTickets}
                />
            </Col>
        </Row>
    </Container> : <></>
}