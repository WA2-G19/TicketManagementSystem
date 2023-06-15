import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import {Staff} from "../../classes/Profile";
import StaffAPI from "../../API/Profile/staff";
import {useAuthentication} from "../../contexts/Authentication";
import StaffCard from "../staff/StaffCard";
import {Loading} from "../Loading";
import {useAlert} from "../../contexts/Alert";

function Staffs() {
    const { user } = useAuthentication()
    const alert = useAlert()
    const [staffs, setStaffs] = useState(Array<Staff>)
    const [loading, setLoading] = useState(true)
    const token = user!.token
    useEffect(() => {
        async function getStaffs() {
            const tmp = await StaffAPI.getProfilesWithStatistics(token)
            if (tmp) {
                setStaffs(tmp)
            } else {
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Error loading staff members. Try again later.")
                    .setButtonsOk()
                    .show()
            }
            setLoading(false)
        }

        getStaffs()
            .catch(err => {
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Error loading staff members. Details: " + err)
                    .setButtonsOk()
                    .show()
            })
    }, [token])

    return (
        <Container fluid>
            {loading && <Loading/>}
            <Row>
                {
                    !loading && staffs.length !== 0 && staffs.map(staff =>
                        <Col xs={12} sm={6} md={4} className={"pt-3"} key={staff.email}>
                            <StaffCard staff={staff} />
                        </Col>
                    )
                }
                {
                    !loading && staffs.length === 0 &&
                    <Typography variant="h5" component="div" color="primary" className={"position-absolute top-50 start-50"}>
                        <strong>No staff found</strong>
                    </Typography>
                }
            </Row>
        </Container>
    )
}

export default Staffs