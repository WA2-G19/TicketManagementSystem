import TicketAPI from "../../API/Ticketing/tickets";
import Ticket, {TicketOut} from "../../classes/Ticket";
import {useEffect, useState} from "react";
import HasRole from "../authentication/HasRole";
import {Button, Container} from "react-bootstrap";
import {useNavigate} from "react-router-dom";

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
    }, [])

    return <Container>
        {tickets.map((it, idx) => <TicketCard key={idx} ticket={it}/>)}
    </Container>

}

interface TicketCardProps {
    ticket: TicketOut | undefined
}

export function TicketCard(props: TicketCardProps): JSX.Element {


    return <div>
        <h2>Ticket ID: {props.ticket?.id}</h2>
        <HasRole role={["Manager", "Expert"]}>
            <p>
                <strong>Customer Email:</strong> {props.ticket?.customerEmail}
            </p>
        </HasRole>
        <p>
            <strong>Description:</strong> {props.ticket?.description}
        </p>
        <p>
            <strong>Expert Email:</strong> {props.ticket?.expertEmail || 'Not assigned yet'}
        </p>
        <p>
            <strong>Priority Level:</strong> {props.ticket?.priorityLevel || 'Not assigned yet'}
        </p>
        <p>
            <strong>Product EAN:</strong> {props.ticket?.productEan}
        </p>
        <p>
            <strong>Status:</strong> {props.ticket?.status}
        </p>
        <p>
            <strong>Warranty UUID:</strong> {props.ticket?.warrantyUUID}
        </p>
    </div>
}