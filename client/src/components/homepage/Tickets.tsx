import TicketAPI from "../../API/Ticketing/tickets";
import {TicketOut} from "../../classes/Ticket";
import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import {useAuthentication} from "../../contexts/Authentication";
import TicketCard from "../ticket/TicketCard";
import StaffAPI from "../../API/Profile/staff";
import {Staff} from "../../classes/Profile";
import StaffCard from "../staff/StaffCard";
import ModalDialog from "../modals/ModalDialog";

function Tickets() {
    const [tickets, setTickets] = useState(Array<TicketOut>)
    const [selectedTicket, setSelectedTicket] = useState<TicketOut | null>(null)
    const [experts, setExperts] = useState(Array<Staff>)
    const { user} = useAuthentication()
    const token = user!.token
    const isManager = user!.role.includes("Manager")
    useEffect(() => {
        async function getTickets() {
            const tmp = await TicketAPI.getTickets(token) as Array<TicketOut>
            setTickets(tmp)
        }
        getTickets()
            .catch(err => {

            })
    }, [token])

    useEffect(() => {
        if (isManager) {
            StaffAPI.getProfilesWithStatistics(token)
                .then(experts => {
                    if (experts !== undefined) {
                        setExperts(experts)
                    }
                })
        }
    }, [token, isManager])

    async function onAssigned(profile: Staff | undefined) {
        //TODO: Assign ticket
        console.log("Assigning ticket to ")
        console.log(profile)
    }

    return (
        <Container>
            <Row>
                {
                    tickets.length > 0 && tickets.map(it =>
                        <Col xs={12} className={"pt-3"} key={it.id}>
                            <TicketCard ticket={it} setSelected={isManager ? () => setSelectedTicket(it) : undefined}/>
                        </Col>
                    )
                }
                {
                    tickets.length === 0 &&
                    <Typography variant="h5" component="div" color="primary" className={"position-absolute top-50 start-50"}>
                        <strong>No tickets found</strong>
                    </Typography>
                }
            </Row>
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