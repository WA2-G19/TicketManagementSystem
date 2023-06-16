import {useNavigate} from "react-router-dom";
import {useAuthentication} from "../../contexts/Authentication";
import {useAlert} from "../../contexts/Alert";
import {FormEvent, useRef} from "react";
import {Button, Col, Container, Form, Row} from "react-bootstrap";
import {BsArrowLeft} from "react-icons/bs";
import SkillAPI from "../../API/Skill/skill";
import {Skill} from "../../classes/Skill";

function SkillForm(): JSX.Element {
    const navigate = useNavigate()
    const { user } = useAuthentication()
    const alert = useAlert()
    const token = user!.token

    const nameRef = useRef<HTMLInputElement>(null)

    async function handleSubmit(e: FormEvent) {
        e.preventDefault()
        if (nameRef.current) {
            try {
                const response = await SkillAPI.insertSkill(token, new Skill(nameRef.current.value))
                if (response) {
                    alert.getBuilder()
                        .setTitle("Skill created")
                        .setMessage("Skill created successfully!")
                        .setButtonsOk(() => navigate("/skills"))
                        .show()
                } else {
                    alert.getBuilder()
                        .setTitle("Error")
                        .setMessage("Skill creation failed. Try again later.")
                        .setButtonsOk()
                        .show()
                }
            } catch (e) {
                console.error(e)
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Skill creation failed. Details: " + e)
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
                    <h1>New skill</h1>
                </Col>
            </Row>
            <Row>
                <Col>
                    <Form onSubmit={handleSubmit}>
                        <Form.FloatingLabel
                            label={"Name"}
                            className={"mb-3"}
                        >
                            <Form.Control required={true} ref={nameRef} />
                        </Form.FloatingLabel>
                        <Button type={"submit"}>
                            Create skill
                        </Button>
                    </Form>
                </Col>
            </Row>
        </Container>
    )
}

export default SkillForm