import {Container, Button, Form, Row, Col, Card} from "react-bootstrap"
import Response from "./Response"
import {Profile} from "../classes/Profile"
import API from "../API/api"
import React, {useState} from "react"


function ProfilesAPI() {

    const prototype = new Profile("", "", "", "")
    const [statusCode, setStatusCode] = useState<number>(0)
    const [bodyPretty, setBodyPretty] = useState<Object[]>([])
    const [bodyRaw, setBodyRaw] = useState<string>("")
    const profile: Profile[] = bodyPretty.map((p: any) => new Profile(p.email, p.name, p.surname, p.address))
    return (<>
        <Container className="h-100 d-flex flex-column align-items-stretch dashboard-page">
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
            <Row className="">
                <Response prototype={prototype} responseBodyPretty={profile} responseBodyRaw={bodyRaw}
                          responseStatusCode={new Number(statusCode)}/>
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


    const handleGetByID = async (event: React.FormEvent) => {
        event.preventDefault();
        const response = await API.getProfileByEmail(email)
        let decodedResponse = null
        try {
            decodedResponse= await response.json()
            console.log(decodedResponse)
            props.setBodyPretty([decodedResponse])
            props.setBodyRaw(JSON.stringify(decodedResponse, null, 2))

        } catch(e){
            console.log(e)
            decodedResponse = await response.text()
            props.setBodyRaw(decodedResponse)
            
        } finally {
            props.setStatusCode(response.status)
        }
    }

    return (
        <Card className="h-100">
            <Card.Body className="h-100">
                <Card.Title>
                    GET
                </Card.Title>
                <Form onSubmit={handleGetByID}>
                    <Form.Group className="mb-3" controlId="getEan">
                        <Form.Label>Email</Form.Label>
                        <Form.Control value={email} onChange={(e) => setEmail(e.target.value)}
                                      placeholder="The profile email you want to search"/>

                    </Form.Group>
                    <Row>
                        <div>
                            <Button variant="primary" type="submit" disabled={email.length === 0}>
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
    const [address, setAddress] = useState<string>("")

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        const p = new Profile(email, name, surname, address)
        const response = await API.postProfile(p)
        const decodedResponse = await response.text()
        console.log(decodedResponse.length)
        if(decodedResponse.length === 0){
            props.setBodyRaw("")
           
        } else {

            props.setBodyRaw(JSON.stringify(JSON.parse(decodedResponse), null, 2))
        }
        props.setBodyPretty([])
        props.setStatusCode(response.status)
    }

    return (
        <Card className="h-100">
            <Card.Body className="h-100 ">
                <Card.Title>
                    POST
                </Card.Title>
                <Form onSubmit={handleSubmit}>
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
                    <Form.Group className="mb-3" controlId="postProfile">
                        <Form.Label>Address</Form.Label>
                        <Form.Control value={address} onChange={(e) => setAddress(e.target.value)} placeholder=""/>
                    </Form.Group>
                    <Button variant="primary" type="submit">
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
    const [address, setAddress] = useState<string>("")

    const handleSubmit = async (event: React.FormEvent) => {
        event.preventDefault();
        const p = new Profile(email, name, surname, address)
        const response = await API.putProfile(p)
        const decodedResponse = await response.text()
        console.log(decodedResponse.length)
        if(decodedResponse.length === 0){
            props.setBodyRaw("")
           
        } else {

            props.setBodyRaw(JSON.stringify(JSON.parse(decodedResponse), null, 2))
        }
        props.setBodyPretty([])
        props.setStatusCode(response.status)
    }

    return (
        <Card className="h-100">
            <Card.Body className="h-100 ">
                <Card.Title>
                    PUT
                </Card.Title>
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3" controlId="putProfile">
                        <Form.Label>Email</Form.Label>
                        <Form.Control value={email} onChange={(e) => setEmail(e.target.value)} placeholder=""/>
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="putProfile">
                        <Form.Label>Name</Form.Label>
                        <Form.Control value={name} onChange={(e) => setName(e.target.value)} placeholder=""/>
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="putProfile">
                        <Form.Label>Surname</Form.Label>
                        <Form.Control value={surname} onChange={(e) => setSurname(e.target.value)} placeholder=""/>
                    </Form.Group>
                    <Form.Group className="mb-3" controlId="putProfile">
                        <Form.Label>Address</Form.Label>
                        <Form.Control value={address} onChange={(e) => setAddress(e.target.value)} placeholder=""/>
                    </Form.Group>
                    <Button variant="primary" type="submit">
                        Put Profile
                    </Button>
                </Form>
            </Card.Body>
        </Card>
    )
}


export default ProfilesAPI