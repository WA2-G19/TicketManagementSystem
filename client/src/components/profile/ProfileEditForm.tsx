import {useAuthentication} from "../../contexts/Authentication";
import {Button, Col, Container, Form, Row} from "react-bootstrap";
import {BsArrowLeft} from "react-icons/bs";
import {FormEvent, useEffect, useRef, useState} from "react";
import {Navigate, useNavigate, useParams} from "react-router-dom";
import CustomerAPI from "../../API/Profile/customer";
import ProblemDetail from "../../classes/ProblemDetail";
import {useAlert} from "../../contexts/Alert";
import StaffAPI from "../../API/Profile/staff";
import {Profile, Staff, StaffType} from "../../classes/Profile";
import HasRole from "../authentication/HasRole";
import SkillAPI from "../../API/Skill/skill";
import {Skill} from "../../classes/Skill";

function ProfileEditForm() {
    const {expertEmail} = useParams()
    const navigate = useNavigate()
    const auth = useAuthentication()
    const alert = useAlert()
    const [profile, setProfile] = useState<Profile | Staff | null>(null)
    const [skills, setSkills] = useState(Array<Skill>)

    const addressRef = useRef<HTMLInputElement>(null)
    const skillsRef = useRef<HTMLSelectElement>(null)

    const token = auth.user!.token
    const email = auth.user!.email
    const isClient = auth.user!.role.includes("Client")
    const isManager = auth.user!.role.includes("Manager")

    useEffect(() => {
        async function getSkills() {
            const tmp = await SkillAPI.getAll(token)
            if (tmp) {
                setSkills(tmp)
            }
        }

        if (isManager) {
            getSkills()
                .catch(e => {
                    const builder = alert.getBuilder()
                        .setTitle("Error")
                        .setButtonsOk()
                    if (e instanceof ProblemDetail) {
                        builder.setMessage("Error loading skills. Details: " + e.getDetails())
                    } else {
                        builder.setMessage("Error loading skills. Details: " + e)
                    }
                    builder.show()
                })
        }
    }, [token, isManager])

    useEffect(() => {
        async function getCustomer() {
            setProfile(await CustomerAPI.getProfileByEmail(token, email))
        }

        async function getExpert(email: string) {
            setProfile(await StaffAPI.getProfile(token, email))
        }

        function onError(e: any) {
            const builder = alert.getBuilder()
                .setTitle("Error")
                .setButtonsOk(() => navigate("/"))
            if (e instanceof ProblemDetail) {
                builder.setMessage("Error loading profile. Details: " + e.getDetails())
            } else {
                builder.setMessage("Error loading profile. Details: " + e)
            }
            builder.show()
        }

        if (isClient) {
            getCustomer()
                .catch(onError)
        } else if (isManager && expertEmail !== undefined) {
            getExpert(expertEmail)
                .catch(onError)
        }
    }, [token, email, isClient, isManager, expertEmail])

    if (!profile)
        return <></>

    if (auth.user!.email !== profile.email && !auth.user!.role.includes("Manager")) {
        return (<Navigate to={"/profile"} />)
    }

    async function handleSubmit(e: FormEvent) {
        e.preventDefault()
        if (!profile)
            return
        if (isClient && addressRef.current) {
            try {
                await CustomerAPI.putProfile(auth.user!.token, new Profile(
                    profile.email,
                    profile.name,
                    profile.surname,
                    addressRef.current.value))
                alert.getBuilder()
                    .setTitle("Success")
                    .setMessage("Update completed!")
                    .setButtonsOk(() => navigate("/profile"))
                    .show()
            } catch (e) {
                const builder = alert.getBuilder()
                    .setTitle("Error")
                    .setButtonsOk()
                if (e instanceof ProblemDetail) {
                    builder.setMessage("Error updating user. Details: " + e.getDetails())
                } else {
                    builder.setMessage("Error updating user. Details: " + e)
                }
                builder.show()
            }
        }
        if (isManager && skillsRef.current) {
            try {
                const skills = Array<string>()
                for (let i = 0; i < skillsRef.current.selectedOptions.length;i++) {
                    skills.push(skillsRef.current.selectedOptions[i].value)
                }
                await StaffAPI.putProfile(token, new Staff(
                    profile.email,
                    profile.name,
                    profile.surname,
                    StaffType.Expert,
                    skills
                ))
                alert.getBuilder()
                    .setTitle("Success")
                    .setMessage("Update completed!")
                    .setButtonsOk(() => navigate("/staffs"))
                    .show()
            } catch (e) {
                const builder = alert.getBuilder()
                    .setTitle("Error")
                    .setButtonsOk()
                if (e instanceof ProblemDetail) {
                    builder.setMessage("Error updating user. Details: " + e.getDetails())
                } else {
                    builder.setMessage("Error updating user. Details: " + e)
                }
                builder.show()
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
                    <h1>Edit profile</h1>
                </Col>
            </Row>
            <Row>
                <Col>
                    <Form onSubmit={handleSubmit}>
                        <HasRole role={"Client"}>
                            <Form.FloatingLabel
                                label={"Address"}
                                className={"mb-3"}
                            >
                                <Form.Control required={true} ref={addressRef} defaultValue={(profile as Profile).address} />
                            </Form.FloatingLabel>
                        </HasRole>
                        <HasRole role={"Manager"}>
                            <Form.FloatingLabel
                                label={"Skills"}
                                className={"mb-3"}
                            >
                                <Form.Select required={false} ref={skillsRef} multiple={true} style={{height: "10em"}} defaultValue={(profile as Staff).skills}>
                                    {
                                        skills.map(s =>
                                            <option key={s.name} value={s.name}>{s.name}</option>
                                        )
                                    }
                                </Form.Select>
                            </Form.FloatingLabel>
                        </HasRole>
                        <Button type={"submit"}>
                            Save
                        </Button>
                    </Form>
                </Col>
            </Row>
        </Container>
    )
}

export default ProfileEditForm