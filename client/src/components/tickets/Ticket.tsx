import TicketAPI from "../../API/Ticketing/tickets";
import {TicketOut} from "../../classes/Ticket";
import React, {useEffect, useState} from "react";
import HasRole from "../authentication/HasRole";
import {Button, Card, Col, Container, Row} from "react-bootstrap";
import {CardContent, Grid, Typography} from "@mui/material";
import {ModalDialog} from "../modals/ModalDialog";
import {useAuthentication} from "../../contexts/Authentication";
import {Staff} from "../../classes/Profile";
import StaffAPI from "../../API/Profile/staff";
import StatsAPI from "../../API/Ticketing/statuses";

interface TicketsProps {
    token: string | undefined
}

export function Tickets(props: TicketsProps) {

    const [tickets, setTickets] = useState(Array<TicketOut>)
    useEffect(() => {
        async function getTickets() {
            const tmp = await TicketAPI.getTickets(props.token) as Array<TicketOut>
            setTickets(tmp)
        }
        getTickets()
    }, [props.token])

    return <Container>
        {tickets.length > 0 && tickets.map((it, idx) => <TicketCard key={idx} ticket={it}/>)}
        {tickets.length === 0 &&
            <Typography variant="h5" component="div" color="primary" className={"position-absolute top-50 start-50"}>
                <strong>No tickets found</strong>
            </Typography>
        }
    </Container>

}

interface TicketCardProps {
    ticket: TicketOut | undefined
}

export function TicketCard(props: TicketCardProps): JSX.Element {

    const [show, setShow] = useState(false)
    const [experts, setExperts] = useState<Array<Staff> | undefined>()
    const auth = useAuthentication()

    useEffect(() => {
        async function getExperts() {
            const tmp = await StaffAPI.getProfiles(auth.user?.token) as Array<Staff>
            const mappedStaffStats = await Promise.all(tmp.map(async (staff) => {
                staff.avgTime = await StatsAPI.getAverageTimedByExpert(auth.user?.token, staff.email)
                staff.ticketClosed = await StatsAPI.getTicketClosedByExpert(auth.user?.token, staff.email)
                return staff
            }))

            setExperts(mappedStaffStats)
        }

        if (auth.user?.role[0] == "Manager") {
            getExperts()
        }
    }, [])

    return <Container className={"pt-3"}>
        <Card>
            <CardContent>
                <Grid container spacing={2}>
                    <Grid item xs={24}>
                        <Typography variant="h5" component="div" color="primary">
                            Ticket ID: {props.ticket?.id}
                        </Typography>
                    </Grid>
                    <HasRole role={["Expert", "Manager"]}>
                        <Grid item xs={6}>
                            <Typography variant="body2" color="primary">
                                <strong>Customer Email:</strong>
                            </Typography>
                            {props.ticket?.customerEmail}
                        </Grid>
                    </HasRole>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="primary">
                            <strong>Product EAN:</strong>
                        </Typography>
                        {props.ticket?.productEan}
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="primary">
                            <strong>Description:</strong>
                        </Typography>
                        {props.ticket?.description}
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="primary">
                            <strong>Status:</strong>
                        </Typography>
                        {props.ticket?.status}
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="primary">
                            <strong>Expert Email:</strong>
                        </Typography>
                        {props.ticket?.expertEmail || 'Not assigned yet'}
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="primary">
                            <strong>Priority Level:</strong>
                        </Typography>
                        {props.ticket?.priorityLevel || 'Not assigned yet'}
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="primary">
                            <strong>Warranty UUID:</strong>
                        </Typography>
                        {props.ticket?.warrantyUUID}
                    </Grid>
                </Grid>
                <Row className={"pt-3"}>
                    <Col md={2}><Button>Open chat</Button></Col>
                    <HasRole role={["Manager"]}>
                        <Col>
                            <Button onClick={() => setShow(true)}>Assign Ticket</Button>
                            <ModalDialog show={show} setShow={setShow} elements={experts}/>
                        </Col>
                    </HasRole>
                </Row>
            </CardContent>
        </Card>
    </Container>
}