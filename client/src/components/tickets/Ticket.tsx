import TicketAPI from "../../API/Ticketing/tickets";
import {TicketOut} from "../../classes/Ticket";
import React, {useEffect, useState} from "react";
import HasRole from "../authentication/HasRole";
import {Button, Card, Container} from "react-bootstrap";
import {CardContent, Grid, Typography} from "@mui/material";

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
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <Typography variant="h5" component="div" color="primary">
                        No ticket found
                    </Typography>
                </Grid>
            </Grid>}
    </Container>

}

interface TicketCardProps {
    ticket: TicketOut | undefined
}

export function TicketCard(props: TicketCardProps): JSX.Element {

    return <Card>
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
            <Button>Apri chat</Button>
        </CardContent>
    </Card>
}