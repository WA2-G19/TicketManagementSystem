import TicketAPI from "../../API/Ticketing/tickets";
import {TicketOut, TicketStatusEnum} from "../../classes/Ticket";
import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {useAuthentication} from "../../contexts/Authentication";
import TicketCard from "../ticket/TicketCard";
import Loading from "../Loading";
import {useAlert} from "../../contexts/Alert";
import ModalAssignTicket from "../modals/ModalAssignTicket";

function Tickets() {
    const [tickets, setTickets] = useState(Array<TicketOut>)
    const [selectedTicket, setSelectedTicket] = useState<TicketOut | undefined>(undefined)
    const {user} = useAuthentication()
    const alert = useAlert()
    const token = user!.token
    const isManager = user!.role.includes("Manager")
    const [loading, setLoading] = useState(true)
    useEffect(() => {
        async function getTickets() {
            setTickets(await TicketAPI.getTicketsWithUnreadMessages(token))
            setLoading(false)
        }

        getTickets()
            .catch(err => {
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Error loading tickets. Details: " + err)
                    .setButtonsOk()
                    .show()
            })
    }, [token])

    return (
        <Container fluid>
            <Row className={"mt-3"}>
                <Col>
                    <h1>Tickets</h1>
                </Col>

            </Row>
            {loading && <Loading/>}
            <Row>
                {
                    !loading && tickets.length > 0 && tickets.map(ticket =>
                        <Col xs={12} className={"pt-3"} key={ticket.id}>
                            <TicketCard ticket={ticket}
                                        setSelected={isManager && (ticket.status === TicketStatusEnum.Open || ticket.status === TicketStatusEnum.Reopened) ? () => setSelectedTicket(ticket) : undefined}/>
                        </Col>
                    )
                }
            </Row>
            {
                !loading && tickets.length === 0 &&
                <h1 color="primary" className={"position-absolute top-50 start-50"}>
                    <strong>No tickets found</strong>
                </h1>
            }
            {
                isManager &&
                <ModalAssignTicket
                    ticket={selectedTicket}
                    hide={() => setSelectedTicket(undefined)}
                />
            }

        </Container>
    )
}

export default Tickets