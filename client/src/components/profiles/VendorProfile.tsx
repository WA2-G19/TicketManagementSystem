import {useAuthentication} from "../../contexts/Authentication";
import {Card, CardContent, Grid, Typography} from "@mui/material";
import React from "react";


const VendorProfile = () => {
    const auth = useAuthentication()
    const user = auth.user
    return <Card>
        <CardContent>
            <Grid container spacing={2}>
                <Grid item xs={12} sm={3}>
                    <Typography variant="h5" component="div" color="primary">
                        Name:
                    </Typography>
                    {user?.name}
                </Grid>
                <Grid item xs={12} sm={3}>
                    <Typography variant="h5" component="div" color="primary">
                        Email:
                    </Typography>
                    {user?.email}
                </Grid>
            </Grid>
        </CardContent>
    </Card>
};

export default VendorProfile;