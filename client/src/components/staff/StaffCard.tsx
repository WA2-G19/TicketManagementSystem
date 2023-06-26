import {Staff} from "../../classes/Profile";
import {Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import React from "react";
import {BsPencilSquare} from "react-icons/bs";
import {useNavigate} from "react-router-dom";
import HasRole from "../authentication/HasRole";

function StaffCard({ staff }: {
    staff: Staff
}): JSX.Element {
    const navigate = useNavigate()
    return <Container className={"border border-3 rounded border-primary"}>
        <Row className={"ps-3 mt-3"}>
            <Col className={"d-flex flex-row justify-content-between"}>
                <Typography variant="h5" component="div" color="primary">
                    {staff.name + " " + staff.surname}
                </Typography>
                <HasRole role={"Manager"}>
                    <BsPencilSquare role={"button"} onClick={() => navigate("/staffs/edit/" + staff.email)} title={"Edit"} />
                </HasRole>
            </Col>
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
                staff.ticketsClosed !== undefined &&
                <Row className={"pt-2"}>
                    <Col>
                        <Typography display={"inline"} variant="body1" color="primary">
                            <strong>Ticket Closed:</strong>
                        </Typography>
                        {" " + staff.ticketsClosed}
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