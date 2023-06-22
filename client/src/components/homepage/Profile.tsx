import {useAuthentication} from "../../contexts/Authentication";
import {Navigate} from "react-router-dom";
import React from "react";
import {Col, Container, Row, Card} from "react-bootstrap";
import {Typography} from "@mui/material";

function Profile() {
    const auth = useAuthentication()
    console.log(auth.user!)
    if (!auth.isLoggedIn)
        return (<Navigate to={"/login"} />)
    return (
        <Container>
            <Row>
                <Col>
                    <Card className={"mt-3"}>
                        <Card.Body>
                            <Container>
                                <Row>
                                    <Col xs={12} sm={4}>
                                        <Typography variant="h5" component="div" color="primary">
                                            Name:
                                        </Typography>
                                        {auth.user!.given_name}
                                    </Col>
                                    <Col xs={12} sm={4}>
                                        <Typography variant="h5" component="div" color="primary">
                                            Surname:
                                        </Typography>
                                        {auth.user!.family_name}
                                    </Col>
                                    <Col xs={12} sm={4}>
                                        <Typography variant="h5" component="div" color="primary">
                                            Email:
                                        </Typography>
                                        {auth.user!.email}
                                    </Col>
                                </Row>
                            </Container>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    )
}

export default Profile