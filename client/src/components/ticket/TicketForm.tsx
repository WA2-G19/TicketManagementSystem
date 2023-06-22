import {Navigate, useLocation, useNavigate} from "react-router-dom";
import {Button, Col, Container, Form, Row} from "react-bootstrap";
import {FormEvent, useRef} from "react";
import TicketAPI from "../../API/Ticketing/tickets";
import {useAuthentication} from "../../contexts/Authentication";
import {Ticket} from "../../classes/Ticket";
import {useAlert} from "../../contexts/Alert";
import {BsArrowLeft} from "react-icons/bs";

function TicketForm(): JSX.Element {
    const navigate = useNavigate()
    const { user } = useAuthentication()
    const alert = useAlert()
    const { state } = useLocation()
    const descriptionRef = useRef<HTMLTextAreaElement>(null)

    if (!state.warranty) {
        return (
            <Navigate to={"/warranties"} />
        )
    }
    
    async function handleSubmit(e: FormEvent) {
        e.preventDefault()
        if (descriptionRef.current) {
            try {
                await TicketAPI.postTicket(user!.token, new Ticket(state.warranty.id, descriptionRef.current.value))
                alert.getBuilder()
                    .setTitle("Ticket created")
                    .setMessage("Ticket created successfully!")
                    .setButtonsOk(() => navigate("/tickets"))
                    .show()
            } catch (e) {
                console.error(e)
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Ticket creation failed. Details: " + e)
                    .setButtonsOk()
                    .show()
            }
        }
    }
    
    return (
        <Container fluid>
            <Row className={"mt-3"}>
                <Col className={"d-flex flex-row align-items-center"} xs={1}>
                    <BsArrowLeft size={"2em"} onClick={() => navigate(-1)} role={"button"} />
                </Col>
                <Col>
                    <h1>New Ticket</h1>
                </Col>
            </Row>
            <Row>
                <Col>
                    <Form onSubmit={handleSubmit}>
                        <Form.FloatingLabel
                            label={"Warranty ID"}
                            className={"mb-3"}
                        >
                            <Form.Control value={state.warranty.id} disabled={true} required={true} />
                        </Form.FloatingLabel>
                        <Form.FloatingLabel
                            label={"Description"}
                            className={"mb-3"}
                        >
                            <Form.Control as={"textarea"} required={true} ref={descriptionRef} />
                        </Form.FloatingLabel>
                        <Button type={"submit"}>
                            Create ticket
                        </Button>
                    </Form>
                </Col>
            </Row>
        </Container>
    )
}

export default TicketForm