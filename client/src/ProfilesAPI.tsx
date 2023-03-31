import {Container, Button, Form, Row, Col, Card} from "react-bootstrap"
import Response from "./Response"
import Profile from "./classes/Profile"
import API from "./API/api"
import React, {useState} from "react"


function ProfilesAPI() {

    const prototype = new Profile("", "", "")
    const [statusCode, setStatusCode] = useState<number>(0)
    const [bodyPretty, setBodyPretty] = useState<Object[]>([])
    const [bodyRaw, setBodyRaw] = useState<string>("")
    const profile: Profile[] = bodyPretty.map((p: any) => new Profile(p.name, p.surname, p.email))

    return (<>
        <Container className="vh-100 d-flex flex-column ">
            <Row className="mb-3">
                <Col>
                    <GetByIdCard setBodyPretty={setBodyPretty} setBodyRaw={setBodyRaw} setStatusCode={setStatusCode}/>
                </Col>
                <Col>
                    <PostProfileCard setBodyPretty={setBodyPretty} setBodyRaw={setBodyRaw}
                                     setStatusCode={setStatusCode}/>
                </Col>
                <Col>
                    <PutProfileCard setBodyPretty={setBodyPretty} setBodyRaw={setBodyRaw}
                                    setStatusCode={setStatusCode}/>
                </Col>
            </Row>
            <Row className="h-100">
                <Response prototype={prototype} responseBodyPretty={profile} responseBodyRaw={bodyRaw}
                          responseStatusCode={statusCode}/>
            </Row>

        </Container>
    </>)
}


function GetByIdCard(props: {
    setBodyPretty: React.Dispatch<React.SetStateAction<Object[]>>,
    setBodyRaw: React.Dispatch<React.SetStateAction<string>>,
    setStatusCode: React.Dispatch<React.SetStateAction<number>>,

}) {
    const [email, setEmail] = useState("")


    const handleGetByID = async (event: React.MouseEvent) => {
        event.preventDefault();
        try {
            const response = await API.getProfileByEmail(email)
            const jsonResponse = await response.json()
            props.setBodyPretty([jsonResponse])
            props.setStatusCode(response.status)
            props.setBodyRaw(JSON.stringify(jsonResponse, null, 2))

        } catch (e) {
            console.log(e)
        }
    }

    return (
        <Card className="h-100">
            <Card.Body className="h-100">
                <Card.Title>
                    GET
                </Card.Title>
                <Form>
                    <Form.Group className="mb-3" controlId="getEan">
                        <Form.Label>EMAIL</Form.Label>
                        <Form.Control value={email} onChange={(e) => setEmail(e.target.value)}
                                      placeholder="The profile email you want to search"/>

                    </Form.Group>
                    <Row>
                        <div>
                            <Button variant="primary" onClick={(e) => handleGetByID(e)}>
                                Get by Email
                            </Button>
                        </div>
                    </Row>
                </Form>
            </Card.Body>
        </Card>)
}


function PostProfileCard(props: {
    setBodyPretty: React.Dispatch<React.SetStateAction<Object[]>>,
    setBodyRaw: React.Dispatch<React.SetStateAction<string>>,
    setStatusCode: React.Dispatch<React.SetStateAction<number>>,

}) {
    const [email, setEmail] = useState<string>("")
    const [name, setName] = useState<string>("")
    const [surname, setSurname] = useState<string>("")

    const handleSubmit = async (event: React.MouseEvent) => {
        event.preventDefault();
            const p = new Profile(email, name, surname)
            const response = await API.postProfile(p)
            if (response.ok) {
                props.setBodyPretty([p])
                props.setBodyRaw(JSON.stringify(p, null, 2))
            } else {
                props.setBodyPretty([response])
                props.setBodyRaw(JSON.stringify(response, null, 2))
            }
            props.setStatusCode(response.status)
    }

    return (
        <Card className="h-100">
            <Card.Body className="h-100 ">
                <Card.Title>
                    POST
                </Card.Title>
                <Form>
                    <Form.Group className="mb-3" controlId="postProfile">
                        <Form.Label>Email</Form.Label>
                        <Form.Control value={email} onChange={(e) => setEmail(e.target.value)} placeholder=""/>
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="postProfile">
                        <Form.Label>Name</Form.Label>
                        <Form.Control value={name} onChange={(e) => setName(e.target.value)} placeholder=""/>
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="postProfile">
                        <Form.Label>Surname</Form.Label>
                        <Form.Control value={surname} onChange={(e) => setSurname(e.target.value)} placeholder=""/>
                    </Form.Group>
                    <Button variant="primary" onClick={(e) => handleSubmit(e)}>
                        Post Profile
                    </Button>
                </Form>
            </Card.Body>
        </Card>
    )
}

function PutProfileCard(props: {
    setBodyPretty: React.Dispatch<React.SetStateAction<Object[]>>,
    setBodyRaw: React.Dispatch<React.SetStateAction<string>>,
    setStatusCode: React.Dispatch<React.SetStateAction<number>>,

}) {
    const [email, setEmail] = useState<string>("")
    const [name, setName] = useState<string>("")
    const [surname, setSurname] = useState<string>("")

    const handleSubmit = async (event: React.MouseEvent) => {
        event.preventDefault();
            const p = new Profile(email, name, surname)
            const response = await API.putProfile(p)
            if (response.ok) {
                props.setBodyPretty([p])
                props.setBodyRaw(JSON.stringify(p, null, 2))
            } else {
                props.setBodyPretty([response])
                props.setBodyRaw(JSON.stringify(response, null, 2))
            }
            props.setStatusCode(response.status)
    }

    return (
        <Card className="h-100">
            <Card.Body className="h-100 ">
                <Card.Title>
                    PUT
                </Card.Title>
                <Form>
                    <Form.Group className="mb-3" controlId="postProfile">
                        <Form.Label>Email</Form.Label>
                        <Form.Control value={email} onChange={(e) => setEmail(e.target.value)} placeholder=""/>
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="postProfile">
                        <Form.Label>Name</Form.Label>
                        <Form.Control value={name} onChange={(e) => setName(e.target.value)} placeholder=""/>
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="postProfile">
                        <Form.Label>Surname</Form.Label>
                        <Form.Control value={surname} onChange={(e) => setSurname(e.target.value)} placeholder=""/>
                    </Form.Group>
                    <Button variant="primary" onClick={(e) => handleSubmit(e)}>
                        Post Profile
                    </Button>
                </Form>
            </Card.Body>
        </Card>
    )
}


export default ProfilesAPI