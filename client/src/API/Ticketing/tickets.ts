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
    throw await response.json() as ProblemDetail
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
    throw await response.json() as ProblemDetail
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
        throw await response.json() as ProblemDetail
    }
    return true
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
        throw await response.json() as ProblemDetail
    }
    return true
}

async function closeTicket(token: string, ticketId: number, managerEmail: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            ticketId: ticketId,
            status: "Closed",
            by: managerEmail,
            timestamp: new Date(Date.now()).toISOString()
        })
    })
    if (!response.ok) {
        throw await response.json() as ProblemDetail
    }
    return true
}

async function resolveTicket(token: string, ticketId: number, managerEmail: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId, {
        method: "PUT",
        headers: {
            "Authorization": "Bearer " + token,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            ticketId: ticketId,
            status: "Resolved",
            by: managerEmail,
            timestamp: new Date(Date.now()).toISOString()
        })
    })
    if (!response.ok) {
        throw await response.json() as ProblemDetail
    }
    return true
}

async function reopenTicket(token: string, ticketId: number) {
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
        throw await response.json() as ProblemDetail
    }
    return true
}

const TicketAPI = {getTickets, getTicketsWithUnreadMessages, getTicketById, postTicket, startProgressTicket, closeTicket, resolveTicket, reopenTicket}
export default TicketAPI