import {useAuthentication} from "../../contexts/Authentication";
import React, {useEffect, useState} from "react";
import {Button, Col, Container, Row} from "react-bootstrap";
import {WarrantyOut} from "../../classes/Warranty";
import WarrantyAPI from "../../API/Warranty/warranty";
import WarrantyCard from "../warranty/WarrantyCard";
import Loading from "../Loading";
import {BsPlus} from "react-icons/bs";
import {useNavigate} from "react-router-dom";
import HasAnyRole from "../authentication/HasAnyRole";
import {useAlert} from "../../contexts/Alert";
import ProblemDetail from "../../classes/ProblemDetail";

function Warranties(): JSX.Element {
    const navigate = useNavigate()
    const alert = useAlert()
    const {user} = useAuthentication()
    const [warranties, setWarranties] = useState(Array<WarrantyOut>)
    const [loading, setLoading] = useState(true)
    const [now, setNow] = useState(() => new Date(Date.now()))
    const token = user!.token

    useEffect(() => {
        const interval = setInterval(() => {
            setNow(new Date(Date.now()))
        }, 1000)

        return () => {
            clearInterval(interval)
        }
    }, [])

    useEffect(() => {
        async function getWarranties() {
            setWarranties(await WarrantyAPI.getAllWarranty(token))
            setLoading(false)
        }

        getWarranties()
            .catch(err => {
                const builder = alert.getBuilder()
                    .setTitle("Error")
                    .setButtonsOk()
                if (err instanceof ProblemDetail) {
                    builder.setMessage("Error loading warranties. Details: " + err.getDetails())
                } else {
                    builder.setMessage("Error loading warranties. Details: " + err)
                }
                builder.show()
            })
    }, [token])

    const deleteWarranty = (warrantyId: number) => {
        let ws = warranties
        ws = ws.filter(w => w.id !== warrantyId)
        setWarranties(ws)
    }

    return (
        <Container fluid>
            <Row className={"mt-3"}>
                <Col>
                    <h1>Warranties</h1>
                </Col>
                <HasAnyRole roles={["Client", "Vendor"]}>
                    <Col className={"d-flex flex-row align-items-center"} xs={2}>
                        <Button onClick={() => navigate("/warranties/add")}>
                            {user?.role[0] === "Client" ?  "Activate Warranty":"Create Warranty"} 
                            <BsPlus size={"2em"}  role={"button"}/>
                        </Button>
                    </Col>
                </HasAnyRole>
            </Row>
            {loading && <Loading/>}
            <Row>
                {
                    !loading && warranties.length !== 0 && warranties.map(warranty =>
                        <Col xs={12} sm={6} md={4} className={"pt-3 d-flex flex-column"} key={warranty.id}>
                            <WarrantyCard warranty={warranty} now={now} remove = {deleteWarranty}/>
                        </Col>
                    )
                }
            </Row>
            {
                !loading && warranties.length === 0 &&
                <h1 color="primary" className={"position-absolute top-50 start-50"}>
                    <strong>No warranties found</strong>
                </h1>
            }
        </Container>
    )
}

export default Warranties