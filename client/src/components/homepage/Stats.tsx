import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {Staff} from "../../classes/Profile";
import StaffAPI from "../../API/Profile/staff";
import {useAuthentication} from "../../contexts/Authentication";
import {useAlert} from "../../contexts/Alert";
import ProblemDetail from "../../classes/ProblemDetail";
import AverageTimePerTicketGraph from "../stats/AverageTimePerTicketGraph";
import TicketsClosedGraph from "../stats/TicketsClosedGraph";
import TicketsInProgressGraph from "../stats/TicketsInProgressGraph";
import Loading from "../Loading";

function Stats() {
    const [experts, setExperts] = useState(Array<Staff>)
    const auth = useAuthentication()
    const alert = useAlert()
    const token = auth.user!.token

    useEffect(() => {
        async function getExperts() {
            const response = await StaffAPI.getProfilesWithStatistics(token)
            setExperts(response)
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

    if (experts.length === 0) {
        return <Loading />
    }

    return (
        <Container fluid>
            <Row className={"mt-3"}>
                <Col>
                    <h1>Statistics</h1>
                </Col>
            </Row>
            <Row className={"mt-3"}>
                <Col>
                    <AverageTimePerTicketGraph experts={experts} />
                </Col>
            </Row>
            <Row className={"mt-3"}>
                <Col>
                    <TicketsClosedGraph experts={experts} />
                </Col>
            </Row>
            <Row className={"mt-3"}>
                <Col>
                    <TicketsInProgressGraph experts={experts} />
                </Col>
            </Row>
        </Container>
    )
}

export default Stats