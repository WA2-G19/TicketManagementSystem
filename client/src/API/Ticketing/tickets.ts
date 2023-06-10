import Ticket from "../../classes/Ticket"
import TicketOut from "../../classes/Ticket"
import TicketStatusEnum from "../../classes/Ticket"

const {REACT_APP_SERVER_URL} = process.env;


async function getTickets(token: string | undefined) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/all", {
            headers: {
                "Authorization": "Bearer " + token
            }
        })
        if(response.ok) {
            return await response.json() as Array<TicketOut>
        } else {
            return undefined
        }
    } catch (e) {
        throw e
    }

}


async function getTicketById(token: string | undefined, ticketId: number) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId, {
            headers: {
                "Authorization": "Bearer " + token
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

async function putTicket(token: string, status: TicketStatusEnum) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                'Content-Type': 'application/json'
            },
            body: status.toJSONObject()
        })
        return response.ok
    } catch (e) {
        throw e
    }

}

const TicketAPI = {getTickets, getTicketById, postTicket, putTicket}
export default TicketAPI