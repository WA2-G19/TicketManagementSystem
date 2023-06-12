import React, {useEffect, useState} from "react";
import {Button, Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import {Staff} from "../../classes/Profile";
import StaffAPI from "../../API/Profile/staff";
import {useAuthentication} from "../../contexts/Authentication";
import StatsAPI from "../../API/Ticketing/statuses";
import StaffCard from "../staff/StaffCard";

function Staffs() {
    const { user } = useAuthentication()
    const [staffs, setStaffs] = useState(Array<Staff>)
    useEffect(() => {
        async function getStaffs() {
            const tmp = await StaffAPI.getProfiles(user!.token) as Array<Staff>
            const mappedStaffStats = await Promise.all(tmp.map(async (staff) => {
                staff.avgTime = await StatsAPI.getAverageTimedByExpert(user!.token, staff.email)
                staff.ticketClosed = await StatsAPI.getTicketClosedByExpert(user!.token, staff.email)
                return staff
            }))
            setStaffs(mappedStaffStats)
        }

        getStaffs()
            .catch(err => {

            })
    }, [user!.token])

    return (
        <Container fluid>
            <Row>
                {
                    staffs.length !== 0 && staffs.map(staff =>
                        <Col xs={12} sm={6} md={4} lg={3}>
                            <StaffCard staff={staff} key={staff.email}/>
                        </Col>
                    )
                }
                {staffs.length === 0 &&
                    <Typography variant="h5" component="div" color="primary" className={"position-absolute top-50 start-50"}>
                        <strong>No staff found</strong>
                    </Typography>
                }
            </Row>
        </Container>
    )
}

export default Staffs