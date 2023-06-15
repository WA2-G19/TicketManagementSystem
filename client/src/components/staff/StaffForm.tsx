import {useNavigate} from "react-router-dom";
import {useAuthentication} from "../../contexts/Authentication";
import {useAlert} from "../../contexts/Alert";
import {FormEvent, useEffect, useRef, useState} from "react";
import {Button, Col, Container, Form, Row} from "react-bootstrap";
import {BsArrowLeft} from "react-icons/bs";
import {CredentialStaff, Staff, StaffType} from "../../classes/Profile";
import StaffAPI from "../../API/Profile/staff";

function StaffForm(): JSX.Element {
    const navigate = useNavigate()
    const { user } = useAuthentication()
    const alert = useAlert()
    const [skills, setSkills] = useState(Array<string>)
    const token = user!.token

    const emailRef = useRef<HTMLInputElement>(null)
    const nameRef = useRef<HTMLInputElement>(null)
    const surnameRef = useRef<HTMLInputElement>(null)
    const typeRef = useRef<HTMLSelectElement>(null)
    const skillsRef = useRef<HTMLSelectElement>(null)
    const passwordRef = useRef<HTMLInputElement>(null)
    const confirmPasswordRef = useRef<HTMLInputElement>(null)

    useEffect(() => {
        async function getSkills() {
            //TODO: Get skills API
        }

        getSkills()
            .catch(err => {
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Error loading skills. Details: " + err)
                    .setButtonsOk()
                    .show()
            })
    })

    async function handleSubmit(e: FormEvent) {
        e.preventDefault()
        if (emailRef.current && nameRef.current && surnameRef.current && typeRef.current && skillsRef.current && passwordRef.current && confirmPasswordRef.current) {
            try {
                const skills = Array<string>()
                for (let i = 0; i < skillsRef.current.selectedOptions.length;i++) {
                    skills.push(skillsRef.current.selectedOptions[i].value)
                }
                const response = await StaffAPI.createExpert(token, new CredentialStaff(
                    new Staff(emailRef.current.value, nameRef.current.value, surnameRef.current.value, parseInt(typeRef.current.value), skills),
                    passwordRef.current.value
                ))
                if (response) {
                    alert.getBuilder()
                        .setTitle("Staff member created")
                        .setMessage("Staff member created successfully!")
                        .setButtonsOk(() => navigate("/staffs"))
                        .show()
                } else {
                    alert.getBuilder()
                        .setTitle("Error")
                        .setMessage("Staff member creation failed. Try again later.")
                        .setButtonsOk()
                        .show()
                }
            } catch (e) {
                console.error(e)
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Staff member creation failed. Details: " + e)
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
                    <h1>New staff member</h1>
                </Col>
            </Row>
            <Row>
                <Col>
                    <Form onSubmit={handleSubmit}>
                        <Form.FloatingLabel
                            label={"Email"}
                            className={"mb-3"}
                        >
                            <Form.Control type={"email"} required={true} ref={emailRef} />
                        </Form.FloatingLabel>
                        <Form.FloatingLabel
                            label={"Name"}
                            className={"mb-3"}
                        >
                            <Form.Control required={true} ref={nameRef} />
                        </Form.FloatingLabel>
                        <Form.FloatingLabel
                            label={"Surname"}
                            className={"mb-3"}
                        >
                            <Form.Control required={true} ref={surnameRef} />
                        </Form.FloatingLabel>
                        <Form.FloatingLabel
                            label={"Type"}
                            className={"mb-3"}
                        >
                            <Form.Select required={true} ref={typeRef}>
                                <option value={StaffType.Expert}>Expert</option>
                                <option value={StaffType.Manager}>Manager</option>
                            </Form.Select>
                        </Form.FloatingLabel>
                        <Form.FloatingLabel
                            label={"Skills"}
                            className={"mb-3"}
                        >
                            <Form.Select required={false} ref={skillsRef} multiple={true}>
                                {
                                    skills.map(s =>
                                        <option key={s} value={s}>{s}</option>
                                    )
                                }
                            </Form.Select>
                        </Form.FloatingLabel>
                        <Form.FloatingLabel
                            label={"Password"}
                            className={"mb-3"}
                        >
                            <Form.Control type={"password"} required={true} ref={passwordRef} />
                        </Form.FloatingLabel>
                        <Form.FloatingLabel
                            label={"Confirm password"}
                            className={"mb-3"}
                        >
                            <Form.Control type={"password"} required={true} ref={confirmPasswordRef} onInput={() => {
                                if (passwordRef.current && confirmPasswordRef.current) {
                                    if (passwordRef.current.value !== confirmPasswordRef.current.value) {
                                        console.log(passwordRef.current.value, confirmPasswordRef.current.value)
                                        confirmPasswordRef.current.setCustomValidity("The two passwords are not matching")
                                    } else {
                                        confirmPasswordRef.current.setCustomValidity("")
                                    }
                                }
                            }} />
                        </Form.FloatingLabel>
                        <Button type={"submit"}>
                            Create vendor
                        </Button>
                    </Form>
                </Col>
            </Row>
        </Container>
    )
}

export default StaffForm