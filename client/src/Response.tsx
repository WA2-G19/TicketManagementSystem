import { Card, InputGroup, Button, Container } from "react-bootstrap"
import APIObject from "./classes/APIObject"
import PrettyOutput from "./PrettyOutput"
import { useEffect, useState } from "react"
import { HttpStatusEnum } from "./utils/httpMapping"

function Response(props: {
    prototype: APIObject,
    responseBodyPretty: APIObject[],
    responseBodyRaw: string,
    responseStatusCode: Number,
}){

    
    const [pretty, setPretty] = useState(true)
    useEffect(() => {
        if (props.responseStatusCode.valueOf() < 400)
        setPretty(false)
    }, [props.responseStatusCode])
    return(
        <Card className="">
            <Card.Body className="">
                <Card.Title>
                    Response
                </Card.Title>
                <Card.Subtitle className="mb-2 text-muted">Status Code: {props.responseStatusCode.valueOf()}{' '}-{' '}
                {HttpStatusEnum.get(props.responseStatusCode.valueOf())?.name}
                </Card.Subtitle>
                <InputGroup size="sm" className="mb-2">
                    <Button variant={pretty? "primary": "outline-primary"} onClick={() => setPretty(true)}>Pretty</Button>
                    <Button variant={!pretty? "primary": "outline-primary"} onClick={() => setPretty(false)}>Raw</Button>
                </InputGroup>
           
                {pretty?<PrettyOutput prototype={props.prototype} result={props.responseBodyPretty}/>: <ResultBodyRaw responseBodyRaw={props.responseBodyRaw}/>}
                
            </Card.Body>
        </Card>
    )
}


function ResultBodyRaw(props: {
    responseBodyRaw: string
}){
    return (
        <Container className="flex-grow-1" style={{ height: 200, overflowY: "auto"}}>
            <pre>{props.responseBodyRaw}</pre>
        </Container>
    )
}

export default Response