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
        const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/", {
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

async function putTicket(token: string | undefined, ticket: Ticket | undefined) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                'Content-Type': 'application/json'
            },
            body: ticket?.toJSONObject()
        })
        return response.ok
    } catch (e) {
        throw e
    }

}

const TicketAPI = {getTickets, getTicketById, postTicket, putTicket}
export default TicketAPI