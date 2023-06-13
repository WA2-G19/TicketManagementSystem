import {useAuthentication} from "../../contexts/Authentication";
import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import {Warranty} from "../../classes/Warranty";
import WarrantyAPI from "../../API/Warranty/warranty";
import WarrantyCard from "../warranty/WarrantyCard";

function Warranties(): JSX.Element {
    const {user} = useAuthentication()
    const [warranties, setWarranties] = useState(Array<Warranty>)
    useEffect(() => {
        async function getWarranties() {
            const tmp = await WarrantyAPI.getAllWarranty(user!.token) as Array<Warranty>
            setWarranties(tmp)
        }

        getWarranties()
            .catch(err => {

            })
    }, [user!.token])

    return (
        <Container fluid>
            <Row>
                {
                    warranties.length !== 0 && warranties.map((warranty, idx) =>
                        <Col xs={12} sm={6} md={4} className={"pt-3"} key={idx}>
                            <WarrantyCard warranty={warranty} key={idx}/>
                        </Col>
                    )
                }
                {
                    warranties.length === 0 &&
                    <Typography variant="h5" component="div" color="primary"
                                className={"position-absolute top-50 start-50"}>
                        <strong>No warranties found</strong>
                    </Typography>
                }
            </Row>
        </Container>
    )
}

export default Warranties