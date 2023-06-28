import {Skill} from "../../classes/Skill";
import {Button, Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import React from "react";
import {useAlert} from "../../contexts/Alert";
import SkillAPI from "../../API/Skill/skill";
import {useAuthentication} from "../../contexts/Authentication";

function SkillCard({ skill, forceReload }: {
    skill: Skill,
    forceReload: () => void
}) {
    const alert = useAlert()
    const { user } = useAuthentication()
    const token = user!.token

    function deleteConfirmation() {
        alert.getBuilder()
            .setTitle("Delete")
            .setMessage(<p>Are you sure you want to delete the <strong>{skill.name}</strong> skill?</p>)
            .setButtonsYesNo(() => {
                SkillAPI.deleteSkill(token, new Skill(skill.name))
                    .then(res => {
                        if (res) {
                            alert.getBuilder()
                                .setTitle("Success")
                                .setMessage("Successfully deleted!")
                                .setButtonsOk(() => forceReload())
                                .show()
                        } else {
                            alert.getBuilder()
                                .setTitle("Error")
                                .setMessage("Error deleting skill. Try again later.")
                                .setButtonsOk()
                                .show()
                        }
                    })
                    .catch(err => {
                        alert.getBuilder()
                            .setTitle("Error")
                            .setMessage("Error deleting skill. Details: " + err)
                            .setButtonsOk()
                            .show()
                    })
            })
            .show()
    }

    return (
        <Container className={"border border-3 rounded border-primary p-2 h-100"}>
            <Row>
                <Typography variant="h5" component="div" color="primary">
                    {skill.name}
                </Typography>
            </Row>
            <Row>
                <Col className={"d-flex flex-row-reverse"}>
                    <Button variant={"danger"} className={"btn-sm"} onClick={deleteConfirmation}>
                        Delete
                    </Button>
                </Col>
            </Row>
        </Container>
    )
}

export default SkillCard