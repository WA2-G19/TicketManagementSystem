import React, {useEffect, useState} from "react";
import {Button, Col, Container, Row} from "react-bootstrap";
import {Typography} from "@mui/material";
import {Staff, Vendor} from "../../classes/Profile";
import TicketAPI from "../../API/Ticketing/tickets";
import {useAuthentication} from "../../contexts/Authentication";
import VendorAPI from "../../API/Profile/vendor";

interface StaffsProps {
    token: string | undefined
}

export function Vendors(props: StaffsProps) {

    const [vendors, setVendors] = useState(Array<Array<Vendor>>)
    useEffect(() => {
        async function getVendors() {
            const tmp = await VendorAPI.getVendors(props.token) as Array<Vendor>
            setVendors(divideArray(tmp, 3))
        }

        getVendors()
    }, [props.token])

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
                {vendorSubArray.map((vendor, idx) => {
                    return <Col md={4} style={{height: "100%"}}>Ciaooo</Col>
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

interface StaffCardProps {
    staff: Staff | undefined,
    assign?: boolean
}

export function StaffCard(props: StaffCardProps): JSX.Element {

    const auth = useAuthentication()

    const assignTicket = async () => {
        await TicketAPI.putTicket(auth.user?.token, undefined)
    }

    return <Container className={"border border-3 rounded border-primary"}>
        <Row className={"ps-3 mt-3"}>
            <Typography variant="h5" component="div" color="primary">
                {props.staff?.name + " " + props.staff?.surname}
            </Typography>
        </Row>
        <Row className={"p-3"}>
            <Row>
                <Col>
                    <Col><Typography variant="body2" color="primary">
                        <strong>Email</strong>
                    </Typography></Col>
                    <Col>{props.staff?.email}</Col>
                </Col>
                <Col>
                    <Col>
                        <Typography variant="body2" color="primary">
                            <strong>Skills</strong>
                        </Typography>
                    </Col>
                    {props.staff?.skills.length !== 0 ? props.staff?.skills.map((it, idx) => <Col key={idx}>{it}</Col>) :
                        <Col>No Skills</Col>}
                </Col>
            </Row>
            <Row className={"pt-2"}>
                <Col><Typography display={"inline"} variant="body1" color="primary">
                    <strong>Ticket Closed:</strong>
                </Typography>
                    {" " + props.staff?.ticketClosed}
                </Col>
            </Row>
            <Row>
                <Col><Typography display={"inline"} variant="body1" color="primary">
                    <strong>Average Time:</strong>
                </Typography>
                    {" " + props.staff?.avgTime}
                </Col>
            </Row>
        </Row>
        {props.assign ? <Row className={"p-3"}>
            <Col>
                <Button onClick={() => assignTicket()}>Assign</Button>
            </Col>
        </Row> : <></>
        }
    </Container>
}