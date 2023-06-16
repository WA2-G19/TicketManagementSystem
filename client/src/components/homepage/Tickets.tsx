import TicketAPI from "../../API/Ticketing/tickets";
import {TicketOut} from "../../classes/Ticket";
import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {useAuthentication} from "../../contexts/Authentication";
import TicketCard from "../ticket/TicketCard";
import StaffAPI from "../../API/Profile/staff";
import {Staff} from "../../classes/Profile";
import StaffCard from "../staff/StaffCard";
import ModalDialog from "../modals/ModalDialog";
import {Loading} from "../Loading";
import {useAlert} from "../../contexts/Alert";

function Tickets() {
    const [tickets, setTickets] = useState(Array<TicketOut>)
    const [selectedTicket, setSelectedTicket] = useState<TicketOut | null>(null)
    const [experts, setExperts] = useState(Array<Staff>)
    const {user} = useAuthentication()
    const alert = useAlert()
    const token = user!.token
    const isManager = user!.role.includes("Manager")
    const [loading, setLoading] = useState(true)
    useEffect(() => {
        async function getTickets() {
            const tmp = await TicketAPI.getTickets(token)
            if (tmp) {
                setTickets(tmp)
            } else {
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Error loading tickets. Try again later.")
                    .setButtonsOk()
                    .show()
            }
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

    useEffect(() => {
        if (isManager) {
            StaffAPI.getProfilesWithStatistics(token)
                .then(experts => {
                    if (experts) {
                        setExperts(experts)
                    } else {
                        alert.getBuilder()
                            .setTitle("Error")
                            .setMessage("Error loading experts. Try again later.")
                            .setButtonsOk()
                            .show()
                    }
                })
                .catch(err => {
                    alert.getBuilder()
                        .setTitle("Error")
                        .setMessage("Error loading experts. Details: " + err)
                        .setButtonsOk()
                        .show()
                })
        }
    }, [token, isManager])

    async function onAssigned(profile: Staff | undefined) {
        //TODO: Assign ticket
        console.log("Assigning ticket to ")
        console.log(profile)
    }

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
                                        setSelected={isManager ? () => setSelectedTicket(ticket) : undefined}/>
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
            <ModalDialog
                title={"Assign ticket"}
                show={selectedTicket !== null}
                hide={() => setSelectedTicket(null)}
                elements={experts}
                onComplete={onAssigned}
                keySelector={(e) => e.email}
                render={(e) => <StaffCard staff={e}/>}
            />
        </Container>
    )
}

export default Tickets