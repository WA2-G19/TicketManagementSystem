import TicketAPI from "../../API/Ticketing/tickets";
import {TicketOut} from "../../classes/Ticket";
import React, {useEffect, useState} from "react";
import HasRole from "../authentication/HasRole";
import {Button, Card, Col, Container, Row} from "react-bootstrap";
import {CardContent, Grid, Typography} from "@mui/material";
import ModalDialog from "../modals/ModalDialog";
import {useAuthentication} from "../../contexts/Authentication";
import {Staff} from "../../classes/Profile";
import StaffAPI from "../../API/Profile/staff";
import StatsAPI from "../../API/Ticketing/statuses";
import HasAnyRole from "../authentication/HasAnyRole";
import StaffCard from "../staff/StaffCard";

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
            {tickets.length > 0 && tickets.map(it => <TicketCard key={it.id} ticket={it}/>)}
            {tickets.length === 0 &&
                <Typography variant="h5" component="div" color="primary" className={"position-absolute top-50 start-50"}>
                    <strong>No tickets found</strong>
                </Typography>
            }
        </Container>
    )
}

function TicketCard({ ticket }: {
    ticket: TicketOut
}): JSX.Element {
    const [show, setShow] = useState(false)
    const [experts, setExperts] = useState<Array<Staff> | undefined>()
    const { user } = useAuthentication()
    const token = user!.token
    const isManager = user!.role.includes("Manager")

    useEffect(() => {
        async function getExperts() {
            const tmp = await StaffAPI.getProfiles(token) as Array<Staff>
            const mappedStaffStats = await Promise.all(tmp.map(async (staff) => {
                staff.avgTime = await StatsAPI.getAverageTimedByExpert(token, staff.email)
                staff.ticketClosed = await StatsAPI.getTicketClosedByExpert(token, staff.email)
                return staff
            }))
            setExperts(mappedStaffStats)
        }

        if (isManager) {
            getExperts()
                .catch(err => {

                })
        }
    }, [token, isManager])

    async function onAssigned(profile: Staff | undefined) {
        //TODO: Assign Ticket
    }

    return <Container className={"pt-3"}>
        <Card>
            <CardContent>
                <Grid container spacing={2}>
                    <Grid item xs={24}>
                        <Typography variant="h5" component="div" color="primary">
                            Ticket ID: {ticket.id}
                        </Typography>
                    </Grid>
                    <HasAnyRole roles={["Expert", "Manager"]}>
                        <Grid item xs={6}>
                            <Typography variant="body2" color="primary">
                                <strong>Customer Email:</strong>
                            </Typography>
                            {ticket.customerEmail}
                        </Grid>
                    </HasAnyRole>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="primary">
                            <strong>Product EAN:</strong>
                        </Typography>
                        {ticket.productEan}
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="primary">
                            <strong>Description:</strong>
                        </Typography>
                        {ticket.description}
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="primary">
                            <strong>Status:</strong>
                        </Typography>
                        {ticket.status}
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="primary">
                            <strong>Expert Email:</strong>
                        </Typography>
                        {ticket.expertEmail || 'Not assigned yet'}
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="primary">
                            <strong>Priority Level:</strong>
                        </Typography>
                        {ticket.priorityLevel || 'Not assigned yet'}
                    </Grid>
                    <Grid item xs={6}>
                        <Typography variant="body2" color="primary">
                            <strong>Warranty UUID:</strong>
                        </Typography>
                        {ticket.warrantyUUID}
                    </Grid>
                </Grid>
                <Row className={"pt-3"}>
                    <Col md={2}><Button>Open chat</Button></Col>
                    <HasRole role={"Manager"}>
                        <Col>
                            <Button onClick={() => setShow(true)}>Assign Ticket</Button>
                            <ModalDialog show={show}
                                         setShow={setShow}
                                         elements={experts}
                                         onComplete={onAssigned}
                                         render={(e) => <StaffCard key={e.email} staff={e}/>}
                            />
                        </Col>
                    </HasRole>
                </Row>
            </CardContent>
        </Card>
    </Container>
}

export default Tickets