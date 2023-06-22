import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {useAuthentication} from "../../contexts/Authentication";
import Loading from "../Loading";
import {useAlert} from "../../contexts/Alert";
import {BsPlus} from "react-icons/bs";
import {useNavigate} from "react-router-dom";
import {Skill} from "../../classes/Skill";
import SkillAPI from "../../API/Skill/skill";
import SkillCard from "../skill/SkillCard";

function Skills() {
    const navigate = useNavigate()
    const {user} = useAuthentication()
    const alert = useAlert()
    const [skills, setSkills] = useState(Array<Skill>)
    const [loading, setLoading] = useState(true)
    const [dirty, setDirty] = useState(true)
    const token = user!.token
    useEffect(() => {
        async function getSkills() {
            setSkills(await SkillAPI.getAll(token))
            setLoading(false)
        }

        if (dirty) {
            setLoading(true)
            getSkills()
                .catch(err => {
                    alert.getBuilder()
                        .setTitle("Error")
                        .setMessage("Error loading skills. Details: " + err)
                        .setButtonsOk()
                        .show()
                })
                .finally(() => setDirty(false))
        }
    }, [token, dirty])

    return (
        <Container fluid>
            <Row className={"mt-3"}>
                <Col>
                    <h1>Skills</h1>
                </Col>
                <Col className={"d-flex flex-row align-items-center"} xs={1}>
                    <BsPlus size={"2em"} onClick={() => navigate("/skills/add")} role={"button"}/>
                </Col>
            </Row>
            {loading && <Loading/>}
            <Row>
                {
                    !loading && skills.length !== 0 && skills.map(skill =>
                        <Col xs={12} sm={6} md={4} className={"pt-3"} key={skill.name}>
                            <SkillCard skill={skill} forceReload={() => setDirty(true)}/>
                        </Col>
                    )
                }
            </Row>
            {
                !loading && skills.length === 0 &&
                <h1 color="primary" className={"position-absolute top-50 start-50"}>
                    <strong>No skill found</strong>
                </h1>
            }
        </Container>
    )
}

export default Skills