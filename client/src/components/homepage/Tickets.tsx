import TicketAPI from "../../API/Ticketing/tickets";
import {TicketOut} from "../../classes/Ticket";
import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import {useAuthentication} from "../../contexts/Authentication";
import TicketCard from "../ticket/TicketCard";

function Tickets() {
    const [tickets, setTickets] = useState(Array<TicketOut>)
    const { user } = useAuthentication()
    const token = user!.token
    useEffect(() => {
        async function getTickets() {
            const tmp = await TicketAPI.getTickets(token) as Array<TicketOut>
            setTickets(tmp)
        }
        getTickets()
            .catch(err => {

            })
    }, [token])

    return (
        <Container>
            <Row>
                {
                    tickets.length > 0 && tickets.map(it =>
                        <Col xs={12} className={"pt-3"}>
                            <TicketCard key={it.id} ticket={it}/>
                        </Col>
                    )
                }
                {
                    tickets.length === 0 &&
                    <Typography variant="h5" component="div" color="primary" className={"position-absolute top-50 start-50"}>
                        <strong>No tickets found</strong>
                    </Typography>
                }
            </Row>
        </Container>
    )
}

export default Tickets