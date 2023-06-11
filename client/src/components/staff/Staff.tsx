import React, {useEffect, useState} from "react";
import {Card, Container} from "react-bootstrap";
import {CardContent, Grid, Typography} from "@mui/material";
import {Staff} from "../../classes/Profile";
import StaffAPI from "../../API/Profile/staff";

interface StaffsProps {
    token: string | undefined
}

export function Staffs(props: StaffsProps) {

    const [staffs, setStaffs] = useState(Array<Staff>)
    useEffect(() => {
        async function getStaffs() {
            const tmp = await StaffAPI.getProfiles(props.token) as Array<Staff>
            setStaffs(tmp)
        }

        getStaffs()
    }, [props.token])

    return <Container>
        {staffs.length > 0 && staffs.map((it, idx) => <StaffCard key={idx} staff={it}/>)}
        {staffs.length === 0 &&
            <Grid container spacing={2}>
                <Grid item xs={12}>
                    <Typography variant="h5" component="div" color="primary">
                        No staff found
                    </Typography>
                </Grid>
            </Grid>}
    </Container>

}

interface StaffCardProps {
    staff: Staff | undefined
}

export function StaffCard(props: StaffCardProps): JSX.Element {

    return <Card>
        <CardContent>
            <Grid container spacing={2}>
                <Grid item xs={24}>
                    <Typography variant="h5" component="div" color="primary">
                        Email: {props.staff?.email}
                    </Typography>
                </Grid>
                <Grid item xs={6}>
                    <Typography variant="body2" color="primary">
                        <strong>Name</strong>
                    </Typography>
                    {props.staff?.name}
                </Grid>
                <Grid item xs={6}>
                    <Typography variant="body2" color="primary">
                        <strong>Surname</strong>
                    </Typography>
                    {props.staff?.surname}
                </Grid>
                <Grid item xs={6}>
                    <Typography variant="body2" color="primary">
                        <strong>Skills</strong>
                    </Typography>
                    {props.staff?.skills.map((it, idx) => <Grid>{it}</Grid>)}
                </Grid>
            </Grid>
        </CardContent>
    </Card>
}