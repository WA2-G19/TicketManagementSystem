import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import React, {FormEvent, useEffect, useRef, useState} from "react";
import {TicketOut} from "../../classes/Ticket";
import {Staff} from "../../classes/Profile";
import StaffAPI from "../../API/Profile/staff";
import {useAlert} from "../../contexts/Alert";
import {useAuthentication} from "../../contexts/Authentication";
import StaffCard from "../staff/StaffCard";
import TicketAPI from "../../API/Ticketing/tickets";
import {useNavigate} from "react-router-dom";

interface ModalAssignTicketProps {
    ticket?: TicketOut,
    hide: () => void
}

function ModalAssignTicket({ ticket, hide }: ModalAssignTicketProps) {
    const navigate = useNavigate()
    const alert = useAlert()
    const { user } = useAuthentication()
    const [experts, setExperts] = useState(Array<Staff>)
    const [selected, setSelected] = useState<string | undefined>()
    const priorityLevelRef = useRef<HTMLSelectElement>(null)
    const token = user!.token

    useEffect(() => {
        async function getExperts() {
            const tmp = await StaffAPI.getProfilesWithStatistics(token)
            if (tmp) {
                setExperts(tmp)
            } else {
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Error loading staff members. Try again later.")
                    .setButtonsOk()
                    .show()
            }
        }

        getExperts()
            .catch(err => {
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Error loading staff members. Details: " + err)
                    .setButtonsOk()
                    .show()
            })
    }, [token])

    async function handleSubmit(e: FormEvent) {
        e.preventDefault()
        if (selected !== undefined && priorityLevelRef.current) {
            const response = await TicketAPI.startProgressTicket(token, ticket!.id!, selected, user!.email, parseInt(priorityLevelRef.current.value))
            if (response) {
                alert.getBuilder()
                    .setTitle("Assigned ticket")
                    .setMessage("Ticket assigned correctly!")
                    .setButtonsOk(() => navigate(-1))
                    .show()
            } else {
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Error assigning ticket. Try again later.")
                    .setButtonsOk()
                    .show()
            }
        }
    }

    return <Modal show={ticket !== undefined}  scrollable={true} fullscreen={"md-down"}>
        <Modal.Header>
            <Modal.Title>Assign ticket</Modal.Title>
        </Modal.Header>
        <Modal.Body>
            <Form id={"assignTicketForm"} onSubmit={handleSubmit}>
                <Form.FloatingLabel
                    label={"Expert"}
                >
                    <Form.Select
                        required={true}
                        value={selected}
                        onChange={e => setSelected(e.target.value)}
                    >
                        {
                            experts.map(e =>
                                <option key={e.email} value={e.email}>{e.surname} {e.name}</option>
                            )
                        }
                    </Form.Select>
                </Form.FloatingLabel>
                {
                    selected !== undefined ?
                        <Row className={"mt-3"}>
                            <Col>
                                <StaffCard staff={experts.find(e => e.email === selected)!} />
                            </Col>
                        </Row>
                    :
                        <p>No expert selected</p>
                }
                <Form.FloatingLabel
                    label={"Priority level"}
                    className={"mt-3"}
                >
                    <Form.Select
                        ref={priorityLevelRef}
                        required={true}
                    >
                        <option value={0}>LOW</option>
                        <option value={1}>MEDIUM</option>
                        <option value={2}>HIGH</option>
                        <option value={3}>CRITICAL</option>
                    </Form.Select>
                </Form.FloatingLabel>
            </Form>
        </Modal.Body>
        <Modal.Footer>
            <Button type={"submit"} form={"assignTicketForm"}>
                Assign
            </Button>
            <Button variant="secondary" onClick={hide}>
                Close
            </Button>
        </Modal.Footer>
    </Modal>
}

export default ModalAssignTicket