import React, {useEffect, useState} from "react";
import {Container, Row} from "react-bootstrap";
import {StatCard} from "../stats/StatCard";
import {Staff} from "../../classes/Profile";
import StaffAPI from "../../API/Profile/staff";
import {useAuthentication} from "../../contexts/Authentication";
import {useAlert} from "../../contexts/Alert";
import ProblemDetail from "../../classes/ProblemDetail";

function Stats() {

    const [expert, setExpert] = useState<Array<Staff>>()
    const auth = useAuthentication()
    const alert = useAlert()
    const token = auth.user!.token

    useEffect(() => {
        async function getExperts() {
            const response = await StaffAPI.getProfilesWithStatistics(token)
            setExpert(response)
        }
        getExperts()
            .catch(err => {
                const builder = alert.getBuilder()
                    .setTitle("Error")
                    .setButtonsOk()
                if (err instanceof ProblemDetail) {
                    builder.setMessage("Error loading experts. Details: " + err.getDetails())
                } else {
                    builder.setMessage("Error loading experts. Details: " + err)
                }
                builder.show()
            })
    }, [token])

    return (
        <Container>
            {
                expert?.map((e, idx) => {
                    return <Row className = {"p-5"} key = {idx}><StatCard expert={e}/></Row>
                })
            }
        </Container>
    )
}

export default Stats