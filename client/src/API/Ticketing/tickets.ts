import {Ticket, TicketOut} from "../../classes/Ticket";
import ProblemDetail from "../../classes/ProblemDetail";
import ChatAPI from "./chat";

const {REACT_APP_SERVER_URL} = process.env;

async function getTickets(token: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token,
            "accept": "application/json"
        }
    })
    if(response.ok) {
        return await response.json() as Array<TicketOut>
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function getTicketsWithUnreadMessages(token: string) {
    const [tickets, messages] = await Promise.all([getTickets(token), ChatAPI.getAllUnreadMessages(token)])
    return tickets.map(t => {
        t.unreadMessages = messages[`${t.id}`]
        return t
    })
}

async function getTicketById(token: string, ticketId: number) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId, {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token,
            "accept": "application/json"
        }
    })
    if(response.ok) {
        return await response.json() as TicketOut
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function postTicket(token: string, ticket: Ticket) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        },
        body: ticket.toJSONObject()
    })
    if (!response.ok) {
        throw ProblemDetail.fromJSON(await response.json())
    }
}

async function startProgressTicket(token: string, ticketId: number, expertEmail: string, managerEmail: string, priorityLevel: number) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            ticketId: ticketId,
            status: "InProgress",
            expert: expertEmail,
            by: managerEmail,
            priorityLevel: priorityLevel,
            timestamp: new Date(Date.now()).toISOString()
        })
    })
    if (!response.ok) {
        throw ProblemDetail.fromJSON(await response.json())
    }
}

async function closeTicket(token: string | undefined, ticketId: number, email: string | undefined) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            ticketId: ticketId,
            status: "Closed",
            by: email,
            timestamp: new Date(Date.now()).toISOString()
        })
    })
    if (!response.ok) {
        throw ProblemDetail.fromJSON(await response.json())
    }
}

async function resolveTicket(token: string | undefined, ticketId: number, email: string | undefined) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            ticketId: ticketId,
            status: "Resolved",
            by: email,
            timestamp: new Date(Date.now()).toISOString()
        })
    })
    if (!response.ok) {
        throw ProblemDetail.fromJSON(await response.json())
    }
}

async function reopenTicket(token: string | undefined, ticketId: number) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            ticketId: ticketId,
            status: "Reopened",
            timestamp: new Date(Date.now()).toISOString()
        })
    })
    if (!response.ok) {
        throw ProblemDetail.fromJSON(await response.json())
    }
}

const TicketAPI = {getTickets, getTicketsWithUnreadMessages, getTicketById, postTicket, startProgressTicket, closeTicket, resolveTicket, reopenTicket}
export default TicketAPI