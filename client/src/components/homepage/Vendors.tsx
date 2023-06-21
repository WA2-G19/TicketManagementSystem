import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {Vendor} from "../../classes/Profile";
import VendorAPI from "../../API/Profile/vendor";
import {useAuthentication} from "../../contexts/Authentication";
import {Loading} from "../Loading";
import {useAlert} from "../../contexts/Alert";
import VendorCard from "../vendor/VendorCard";
import {BsPlus} from "react-icons/bs";
import HasRole from "../authentication/HasRole";
import {useNavigate} from "react-router-dom";

function Vendors() {
    const navigate = useNavigate()
    const {user} = useAuthentication()
    const alert = useAlert()
    const [vendors, setVendors] = useState(Array<Vendor>)
    const [loading, setLoading] = useState(true)
    const token = user!.token
    useEffect(() => {
        async function getVendors() {
            setVendors(await VendorAPI.getVendors(token))
            setLoading(false)
        }

        getVendors()
            .catch(err => {
                alert.getBuilder()
                    .setTitle("Error")
                    .setMessage("Error loading vendors. Details: " + err)
                    .setButtonsOk()
                    .show()
            })
    }, [token])

    return (
        <Container fluid>
            <Row className={"mt-3"}>
                <Col>
                    <h1>Vendors</h1>
                </Col>
                <HasRole role={"Manager"}>
                    <Col className={"d-flex flex-row align-items-center"} xs={1}>
                        <BsPlus size={"2em"} onClick={() => navigate("/vendors/add")} role={"button"}/>
                    </Col>
                </HasRole>
            </Row>
            {loading && <Loading/>}
            <Row>
                {
                    !loading && vendors.length !== 0 && vendors.map(vendor =>
                        <Col xs={12} sm={6} md={4} className={"pt-3"} key={vendor.email}>
                            <VendorCard vendor={vendor}/>
                        </Col>
                    )
                }
            </Row>
            {
                !loading && vendors.length === 0 &&
                <h1 color="primary" className={"position-absolute top-50 start-50"}>
                    <strong>No vendors found</strong>
                </h1>
            }
        </Container>
    )
}

export default Vendors