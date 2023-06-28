import {useAuthentication} from "../../contexts/Authentication";
import {Navigate, useNavigate} from "react-router-dom";
import React, {useEffect, useState} from "react";
import {Col, Container, Row, Card, Button} from "react-bootstrap";
import {Typography} from "@mui/material";
import {Profile as ProfileType, Staff, Vendor} from "../../classes/Profile";
import CustomerAPI from "../../API/Profile/customer";
import StaffAPI from "../../API/Profile/staff";
import ProblemDetail from "../../classes/ProblemDetail";
import {useAlert} from "../../contexts/Alert";
import HasRole from "../authentication/HasRole";
import VendorAPI from "../../API/Profile/vendor";
import HasAnyRole from "../authentication/HasAnyRole";

function Profile() {
    const navigate = useNavigate()
    const auth = useAuthentication()
    const alert = useAlert()
    const [profile, setProfile] = useState<ProfileType | Staff | Vendor | null>(null)

    const token = auth.user!.token
    const email = auth.user!.email
    const isClient = auth.user!.role.includes("Client")
    const isExpert = auth.user!.role.includes("Expert")
    const isVendor = auth.user!.role.includes("Vendor")
    const isManager = auth.user!.role.includes("Manager")

    useEffect(() => {
        async function getCustomer() {
            setProfile(await CustomerAPI.getProfileByEmail(token, email))
        }

        async function getStaff() {
            setProfile(await StaffAPI.getProfile(token, email))
        }

        async function getVendor() {
            setProfile(await VendorAPI.getVendor(token, email))
        }

        function onError(e: any) {
            const builder = alert.getBuilder()
                .setTitle("Error")
                .setButtonsOk(() => navigate("/"))
            if (e instanceof ProblemDetail) {
                builder.setMessage("Error loading profile. Details: " + e.getDetails())
            } else {
                builder.setMessage("Error loading profile. Details: " + e)
            }
            builder.show()
        }

        if (isClient) {
            getCustomer()
                .catch(onError)
        } else if (isExpert || isManager) {
            getStaff()
                .catch(onError)
        } else if (isVendor) {
            getVendor()
                .catch(onError)
        }
    }, [token, email, isClient, isExpert, isVendor, isManager])

    if (!auth.isLoggedIn)
        return (<Navigate to={"/login"} />)

    return (
        <Container>
            <Row>
                <Col>
                    {
                        profile !== null &&
                        <Card className={"mt-3"}>
                            <Card.Body>
                                <Container>
                                    <Row>
                                        <HasAnyRole roles={["Client", "Expert", "Manager"]}>
                                            <Col xs={12} sm={4}>
                                                <Typography variant="h5" component="div" color="primary">
                                                    Name:
                                                </Typography>
                                                {(profile as ProfileType).name}
                                            </Col>
                                            <Col xs={12} sm={4}>
                                                <Typography variant="h5" component="div" color="primary">
                                                    Surname:
                                                </Typography>
                                                {(profile as ProfileType).surname}
                                            </Col>
                                        </HasAnyRole>
                                        <HasRole role={"Vendor"}>
                                            <Col xs={12} sm={4}>
                                                <Typography variant="h5" component="div" color="primary">
                                                    Business name:
                                                </Typography>
                                                {(profile as Vendor).businessName}
                                            </Col>
                                            <Col xs={12} sm={4}>
                                                <Typography variant="h5" component="div" color="primary">
                                                    Phone number:
                                                </Typography>
                                                {(profile as Vendor).phoneNumber}
                                            </Col>
                                        </HasRole>
                                        <Col xs={12} sm={4}>
                                            <Typography variant="h5" component="div" color="primary">
                                                Email:
                                            </Typography>
                                            {profile.email}
                                        </Col>
                                    </Row>
                                    <HasRole role={"Client"}>
                                        <Row>
                                            <Col xs={12} sm={4}>
                                                <Typography variant="h5" component="div" color="primary">
                                                    Address:
                                                </Typography>
                                                {(profile as ProfileType).address}
                                            </Col>
                                        </Row>
                                    </HasRole>
                                    <HasRole role={"Expert"}>
                                        <Row>
                                            <Col xs={12} sm={4}>
                                                <Typography variant="h5" component="div" color="primary">
                                                    Skills:
                                                </Typography>
                                                {!(profile as Staff).skills || (profile as Staff).skills.length === 0 ? "No skill" : (profile as Staff).skills.join("\n")}
                                            </Col>
                                        </Row>
                                    </HasRole>
                                </Container>
                            </Card.Body>
                            <HasRole role={"Client"}>
                                <Card.Footer>
                                    <Row>
                                        <Col className={"d-flex flex-row-reverse"}>
                                            <Button onClick={() => navigate("/profile/edit")}>
                                                Edit
                                            </Button>
                                        </Col>
                                    </Row>
                                </Card.Footer>
                            </HasRole>
                        </Card>
                    }
                </Col>
            </Row>
        </Container>
    )
}

export default Profile