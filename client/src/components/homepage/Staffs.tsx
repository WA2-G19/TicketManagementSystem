import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {Staff} from "../../classes/Profile";
import StaffAPI from "../../API/Profile/staff";
import {useAuthentication} from "../../contexts/Authentication";
import StaffCard from "../staff/StaffCard";
import {Loading} from "../Loading";
import {useAlert} from "../../contexts/Alert";
import {BsPlus} from "react-icons/bs";
import HasRole from "../authentication/HasRole";
import {useNavigate} from "react-router-dom";

function Staffs() {
    const navigate = useNavigate()
    const {user} = useAuthentication()
    const alert = useAlert()
    const [staffs, setStaffs] = useState(Array<Staff>)
    const [loading, setLoading] = useState(true)
    const token = user!.token
    useEffect(() => {
        async function getStaffs() {
            setStaffs(await StaffAPI.getProfilesWithStatistics(token))
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
            <Row className={"mt-3"}>
                <Col>
                    <h1>Staff members</h1>
                </Col>
                <HasRole role={"Manager"}>
                    <Col className={"d-flex flex-row align-items-center"} xs={1}>
                        <BsPlus size={"2em"} onClick={() => navigate("/staffs/add")} role={"button"}/>
                    </Col>
                </HasRole>
            </Row>
            {loading && <Loading/>}
            <Row>
                {
                    !loading && staffs.length !== 0 && staffs.map(staff =>
                        <Col xs={12} sm={6} md={4} className={"pt-3"} key={staff.email}>
                            <StaffCard staff={staff}/>
                        </Col>
                    )
                }
            </Row>
            {
                !loading && staffs.length === 0 &&
                <h1 color="primary" className={"position-absolute top-50 start-50"}>
                    <strong>No staff found</strong>
                </h1>
            }
        </Container>
    )
}

export default Staffs