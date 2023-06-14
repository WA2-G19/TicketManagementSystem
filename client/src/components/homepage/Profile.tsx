import {useAuthentication} from "../../contexts/Authentication";
import IsAuthenticated from "../authentication/IsAuthenticated";
import IsAnonymous from "../authentication/IsAnonymous";
import {Navigate} from "react-router-dom";
import {Card, CardContent, Grid, Typography} from "@mui/material";
import React from "react";
import {Col, Row} from "react-bootstrap";

function Profile() {
    const auth = useAuthentication()

    if (!auth.isLoggedIn)
        return (<Navigate to={"/login"} />)
    return (
        <Row>
            <Col>
                <Card>
                    <CardContent>
                        <Grid container spacing={2}>
                            <Grid item xs={12} sm={3}>
                                <Typography variant="h5" component="div" color="primary">
                                    Name:
                                </Typography>
                                {auth.user!.name}
                            </Grid>
                            <Grid item xs={12} sm={3}>
                                <Typography variant="h5" component="div" color="primary">
                                    Email:
                                </Typography>
                                {auth.user!.email}
                            </Grid>
                        </Grid>
                    </CardContent>
                </Card>
            </Col>
        </Row>
    )
}

export default Profile