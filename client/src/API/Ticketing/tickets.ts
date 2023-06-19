import {Ticket, TicketOut} from "../../classes/Ticket";


const {REACT_APP_SERVER_URL} = process.env;


async function getTickets(token: string | undefined) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets", {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token,
                "accept": "application/json"
            }
        })
        if(response.ok) {
            return await response.json() as Array<TicketOut>
        } else {
            return undefined
        }
    } catch (e) {
        console.log(e)
        throw e
    }

}


async function getTicketById(token: string | undefined, ticketId: number) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId, {
            method: "GET",
            headers: {
                "Authorization": "Bearer " + token,
                "accept": "application/json"
            }
        })
        if(response.ok) {
            return await response.json() as TicketOut
        } else {
            return undefined
        }
    } catch (e) {
        throw e
    }

}



async function postTicket(token: string, ticket: Ticket) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                'Content-Type': 'application/json'
            },
            body: ticket.toJSONObject()
        })
        return response.ok
    } catch (e) {
        throw e
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
    return response.ok
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
    return response.ok
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
    return response.ok
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
    return response.ok
}

const TicketAPI = {getTickets, getTicketById, postTicket, startProgressTicket, closeTicket, resolveTicket, reopenTicket}
export default TicketAPI