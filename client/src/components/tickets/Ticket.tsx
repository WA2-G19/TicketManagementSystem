import {useAuthentication} from "../../contexts/Authentication";
import TicketAPI from "../../API/Ticketing/tickets";
import Ticket from "../../classes/Ticket";
import React, {useEffect, useState} from "react";
import ProductAPI from "../../API/Products/products";
import Product from "../../classes/Product";
import {Col, Row} from "react-bootstrap";

interface TicketsProps {
    token: string | undefined
}

function Tickets(props: TicketsProps) {

    const auth = useAuthentication()

    useEffect(() => {
        async function getTickets() {

            const tickets = await TicketAPI.getTickets(localStorage.getItem("jwt") as string)
            console.log(tickets)
        }
        getTickets()
    }, [])

    return (
        <></>
    );
}

interface TicketCardProps{
    ticket: Ticket
}

function TicketCard(props: TicketCardProps){

    return <></>
}

export default Tickets;