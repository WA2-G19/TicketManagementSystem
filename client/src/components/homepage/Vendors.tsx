import React, {useEffect, useState} from "react";
import {Button, Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import {Staff, Vendor} from "../../classes/Profile";
import VendorAPI from "../../API/Profile/vendor";
import {useAuthentication} from "../../contexts/Authentication";

function Vendors() {
    const { user } = useAuthentication()
    const [vendors, setVendors] = useState(Array<Array<Vendor>>)
    useEffect(() => {
        async function getVendors() {
            const tmp = await VendorAPI.getVendors(user!.token) as Array<Vendor>
            setVendors(divideArray(tmp, 3))
        }

        getVendors()
            .catch(err => {

            })
    }, [user!.token])

    function divideArray<T>(array: T[], chunkSize: number): T[][] {
        const result: T[][] = [];
        while (array.length > 0) {
            result.push(array.splice(0, chunkSize));
        }
        return result;
    }

    return <Container fluid>
        {vendors.length !== 0 && vendors.map((vendorSubArray, idx) => {
            return <Row key={idx} className={"pt-3"}>
                {vendorSubArray.map(vendor => {
                    return <Col key={idx} md={4} style={{height: "100%"}}><VendorCard vendor={vendor} key={vendor.email} /></Col>
                })}
            </Row>
        })}
        {vendors.length === 0 &&
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