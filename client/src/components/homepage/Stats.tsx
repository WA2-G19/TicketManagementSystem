import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {StatCard} from "../stats/StatCard";
import {Staff} from "../../classes/Profile";
import StaffAPI from "../../API/Profile/staff";
import {useAuthentication} from "../../contexts/Authentication";

function Stats() {

    const [expert, setExpert] = useState<Array<Staff>>()
    const auth = useAuthentication()

    useEffect(() => {
        async function getExperts() {
            const response = await StaffAPI.getProfilesWithStatistics(auth.user?.token!)
            setExpert(response)
        }
        getExperts()
    }, [])

    return (
        <Container>
            {
                expert?.map((e, idx) => {
                    return <Row className = {"p-5"} key = {idx}><StatCard expert={e}/></Row>
                })
            }
        </Container>
    )
}

export default Stats