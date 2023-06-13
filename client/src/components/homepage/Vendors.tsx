import React, {useEffect, useState} from "react";
import {Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import {Vendor} from "../../classes/Profile";
import VendorAPI from "../../API/Profile/vendor";
import {useAuthentication} from "../../contexts/Authentication";
import {Loading} from "../Loading";

function Vendors() {
    const { user } = useAuthentication()
    const [vendors, setVendors] = useState(Array<Vendor>)
    const [loading, setLoading] = useState(true)
    useEffect(() => {
        async function getVendors() {
            const tmp = await VendorAPI.getVendors(user!.token) as Array<Vendor>
            setVendors(tmp)
            setLoading(false)
        }

        getVendors()
            .catch(err => {

            })
    }, [user!.token])

    return <Container fluid>
        {loading && <Loading/>}
        {
            !loading && vendors.length !== 0 && vendors.map(vendor =>
                <Col xs={12} sm={6} md={4} className={"pt-3"}>
                    <VendorCard vendor={vendor} key={vendor.email}/>
                </Col>
            )
        }
        {
            !loading && vendors.length === 0 &&
            <Typography variant="h5" component="div" color="primary" className={"position-absolute top-50 start-50"}>
                <strong>No vendors found</strong>
            </Typography>
        }
    </Container>


}

function VendorCard({ vendor }: {
    vendor: Vendor
}): JSX.Element {

    return <Container className={"border border-3 rounded border-primary"}>
        <Row className={"ps-3 mt-3"}>
            <Typography variant="h5" component="div" color="primary">
                {vendor.businessName}
            </Typography>
        </Row>
        <Row className={"p-3"}>
            <Row>
                <Col>
                    <Col>
                        <Typography variant="body2" color="primary">
                            <strong>Email</strong>
                        </Typography>
                    </Col>
                    <Col>{vendor.email}</Col>
                </Col>
                <Col>
                    <Col>
                        <Typography variant="body2" color="primary">
                            <strong>Phone</strong>
                        </Typography>
                    </Col>
                    <Col>{vendor.phoneNumber}</Col>
                </Col>
                <Col>
                    <Col>
                        <Typography variant="body2" color="primary">
                            <strong>Address</strong>
                        </Typography>
                    </Col>
                    <Col>{vendor.address}</Col>
                </Col>
            </Row>
        </Row>
    </Container>
}

export default Vendors