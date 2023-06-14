import {useAuthentication} from "../../contexts/Authentication";
import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import {WarrantyOut} from "../../classes/Warranty";
import WarrantyAPI from "../../API/Warranty/warranty";
import WarrantyCard from "../warranty/WarrantyCard";
import {Loading} from "../Loading";
import {BsPlus} from "react-icons/bs";
import {useNavigate} from "react-router-dom";

function Warranties(): JSX.Element {
    const navigate = useNavigate()
    const {user} = useAuthentication()
    const [warranties, setWarranties] = useState(Array<WarrantyOut>)
    const [loading, setLoading]= useState(true)
    const token = user!.token
    useEffect(() => {
        async function getWarranties() {
            const tmp = await WarrantyAPI.getAllWarranty(token) as Array<WarrantyOut>
            setWarranties(tmp)
            setLoading(false)
        }

        getWarranties()
            .catch(err => {

            })
    }, [token])

    return (
        <Container fluid>
            <Row className={"mt-3"}>
                <Col>
                    <h1>Warranties</h1>
                </Col>
                <Col className={"d-flex flex-row align-items-center"} xs={1}>
                    <BsPlus size={"2em"} onClick={() => navigate("/warranties/add")} role={"button"} />
                </Col>
            </Row>
            {loading && <Loading/>}
            <Row>
                {
                    !loading && warranties.length !== 0 && warranties.map((warranty, idx) =>
                        <Col xs={12} sm={6} md={4} className={"pt-3"} key={idx}>
                            <WarrantyCard warranty={warranty} key={idx}/>
                        </Col>
                    )
                }
                {
                    !loading && warranties.length === 0 &&
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