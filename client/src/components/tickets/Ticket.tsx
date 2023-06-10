import {useAuthentication} from "../../contexts/Authentication";
import TicketAPI from "../../API/Ticketing/tickets";
import Ticket from "../../classes/Ticket";
import {useEffect, useState} from "react";
import ProductAPI from "../../API/Products/products";
import Product from "../../classes/Product";

interface TicketsProps {
    token: string | undefined
}

function Tickets(props: TicketsProps) {

    //   const [tickets, setTickets] = useState(
    useEffect(() => {
        async function getTickets() {
            const tickets = await TicketAPI.getTicketById(props.token,0)
            console.log(tickets)
        }
        getTickets()
    }, [])

    // return (
    //     <Row>
    //         <Col xs={12} md={3}>
    //             <h5>{user?.name} (Nome Cognome)</h5>
    //             <p>{user?.email} (Email)</p>
    //         </Col>
    //     </Row>
    // );
    return <></>

};

interface TicketCardProps{
    ticket: Ticket
}

function TicketCard(props: TicketCardProps){
    return <></>
}

export default Tickets;