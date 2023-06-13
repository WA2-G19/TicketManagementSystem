import {Staff} from "../../classes/Profile";
import {Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import React from "react";

function StaffCard({ staff }: {
    staff: Staff
}): JSX.Element {
    return <Container className={"border border-3 rounded border-primary"}>
        <Row className={"ps-3 mt-3"}>
            <Typography variant="h5" component="div" color="primary">
                {staff.name + " " + staff.surname}
            </Typography>
        </Row>
        <Row className={"p-3"}>
            <Row>
                <Col>
                    <Col>
                        <Typography variant="body2" color="primary">
                            <strong>Email</strong>
                        </Typography>
                    </Col>
                    <Col>{staff.email}</Col>
                </Col>
                <Col>
                    <Col>
                        <Typography variant="body2" color="primary">
                            <strong>Skills</strong>
                        </Typography>
                    </Col>
                    {staff.skills.length !== 0 ? staff?.skills.map((it,idx) => <Col key={idx}>{it}</Col>) :
                        <Col>No Skills</Col>}
                </Col>
            </Row>
            {
                staff.ticketClosed !== undefined &&
                <Row className={"pt-2"}>
                    <Col>
                        <Typography display={"inline"} variant="body1" color="primary">
                            <strong>Ticket Closed:</strong>
                        </Typography>
                        {" " + staff.ticketClosed}
                    </Col>
                </Row>
            }
            {
                staff.avgTime !== undefined &&
                <Row>
                    <Col>
                        <Typography display={"inline"} variant="body1" color="primary">
                            <strong>Average Time:</strong>
                        </Typography>
                        {" " + staff.avgTime}
                    </Col>
                </Row>
            }
        </Row>
    </Container>
}

export default StaffCard