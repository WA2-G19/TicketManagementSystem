import React, {useEffect, useState} from "react";
import {Col, Container, Row, Button} from "react-bootstrap";
import {Staff} from "../../classes/Profile";
import StaffAPI from "../../API/Profile/staff";
import {useAuthentication} from "../../contexts/Authentication";
import StaffCard from "../staff/StaffCard";
import Loading from "../Loading";
import {useAlert} from "../../contexts/Alert";
import {BsPlus} from "react-icons/bs";
import HasRole from "../authentication/HasRole";
import {useNavigate} from "react-router-dom";
import ProblemDetail from "../../classes/ProblemDetail";

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
                const builder = alert.getBuilder()
                    .setTitle("Error")
                    .setButtonsOk()
                if (err instanceof ProblemDetail) {
                    builder.setMessage("Error loading staff members. Details: " + err.getDetails())
                } else {
                    builder.setMessage("Error loading members. Details: " + err)
                }
                builder.show()
            })
    }, [token])

    return (
        <Container fluid>
            <Row className={"mt-3"}>
                <Col>
                    <h1>Staff members</h1>
                </Col>
                <HasRole role={"Manager"}>
                    <Col className={"d-flex flex-row align-items-center"} xs={2}>
                        <Button  onClick={() => navigate("/staffs/add")}> 
                            Add expert <BsPlus size={"2em"} role={"button"}/>
                        </Button>
                    </Col>
                </HasRole>
            </Row>
            {loading && <Loading/>}
            <Row>
                {
                    !loading && staffs.length !== 0 && staffs.map(staff =>
                        <Col xs={12} sm={6} md={4} className={"pt-3 d-flex flex-column"} key={staff.email}>
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