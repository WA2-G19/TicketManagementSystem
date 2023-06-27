import {Button, Col, Form, Modal, Row} from "react-bootstrap";
import React, {FormEvent, useEffect, useRef, useState} from "react";
import {TicketOut} from "../../classes/Ticket";
import {Staff} from "../../classes/Profile";
import StaffAPI from "../../API/Profile/staff";
import {useAlert} from "../../contexts/Alert";
import {useAuthentication} from "../../contexts/Authentication";
import StaffCard from "../staff/StaffCard";
import TicketAPI from "../../API/Ticketing/tickets";

interface ModalAssignTicketProps {
    ticket?: TicketOut,
    hide: () => void
}

function ModalAssignTicket({ ticket, hide }: ModalAssignTicketProps) {
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
                if (tmp.length > 0)
                    setSelected(tmp[0].email)
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
            await TicketAPI.startProgressTicket(token, ticket!.id!, selected, user!.email, parseInt(priorityLevelRef.current.value))
            alert.getBuilder()
                .setTitle("Assigned ticket")
                .setMessage("Ticket assigned correctly!")
                .setButtonsOk(hide)
                .show()
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