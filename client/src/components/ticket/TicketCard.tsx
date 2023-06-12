import {TicketOut} from "../../classes/Ticket";
import React, {useEffect, useState} from "react";
import {Staff} from "../../classes/Profile";
import {useAuthentication} from "../../contexts/Authentication";
import StaffAPI from "../../API/Profile/staff";
import StatsAPI from "../../API/Ticketing/statuses";
import {Button, Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import HasAnyRole from "../authentication/HasAnyRole";
import HasRole from "../authentication/HasRole";
import ModalDialog from "../modals/ModalDialog";
import StaffCard from "../staff/StaffCard";

function TicketCard({ ticket }: {
    ticket: TicketOut
}): JSX.Element {
    const [show, setShow] = useState(false)
    const [experts, setExperts] = useState<Array<Staff> | undefined>()
    const { user } = useAuthentication()
    const token = user!.token
    const isManager = user!.role.includes("Manager")

    useEffect(() => {
        async function getExperts() {
            const tmp = await StaffAPI.getProfiles(token) as Array<Staff>
            const mappedStaffStats = await Promise.all(tmp.map(async (staff) => {
                staff.avgTime = await StatsAPI.getAverageTimedByExpert(token, staff.email)
                staff.ticketClosed = await StatsAPI.getTicketClosedByExpert(token, staff.email)
                return staff
            }))
            setExperts(mappedStaffStats)
        }

        if (isManager) {
            getExperts()
                .catch(err => {

                })
        }
    }, [token, isManager])

    async function onAssigned(profile: Staff | undefined) {
        //TODO: Assign Ticket
    }

    return <Container className={"border border-3 rounded border-primary p-3"}>
        <Row className={"ps-3"}>
            <Typography variant="h5" component="div" color="primary">
                Ticket ID: {ticket.id}
            </Typography>
        </Row>
        <Row className={"pt-3"}>
            <HasAnyRole roles={["Expert", "Manager"]}>
                <Col>
                    <Typography variant="body2" color="primary">
                        <strong>Customer Email:</strong>
                    </Typography>
                    {ticket.customerEmail}
                </Col>
            </HasAnyRole>
            <Col>
                <Typography variant="body2" color="primary">
                    <strong>Product EAN:</strong>
                </Typography>
                {ticket.productEan}
            </Col>
        </Row>
        <Row className={"pt-3"}>
            <Col>
                <Typography variant="body2" color="primary">
                    <strong>Description:</strong>
                </Typography>
                {ticket.description}
            </Col>
            <Col>
                <Typography variant="body2" color="primary">
                    <strong>Status:</strong>
                </Typography>
                {ticket.status}
            </Col>
        </Row>
        <Row className={"pt-3"}>
            <Col>
                <Typography variant="body2" color="primary">
                    <strong>Expert Email:</strong>
                </Typography>
                {ticket.expertEmail || 'Not assigned yet'}
            </Col>
            <Col>
                <Typography variant="body2" color="primary">
                    <strong>Priority Level:</strong>
                </Typography>
                {ticket.priorityLevel || 'Not assigned yet'}
            </Col>
        </Row>
        <Row className={"pt-3"}>
            <Col>
                <Typography variant="body2" color="primary">
                    <strong>Warranty UUID:</strong>
                </Typography>
                {ticket.warrantyUUID}
            </Col>
        </Row>
        <Row className={"pt-3"}>
            <Col md={2}><Button>Open chat</Button></Col>
            <HasRole role={"Manager"}>
                <Col>
                    <Button onClick={() => setShow(true)}>Assign Ticket</Button>
                    <ModalDialog show={show}
                                 setShow={setShow}
                                 elements={experts}
                                 onComplete={onAssigned}
                                 render={(e) => <StaffCard key={e.email} staff={e}/>}
                    />
                </Col>
            </HasRole>
        </Row>
    </Container>
}

export default TicketCard