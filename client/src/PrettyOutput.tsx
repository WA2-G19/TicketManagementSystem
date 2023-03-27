
import { Col, Row, ListGroupItem, ListGroup, DropdownButton, Container } from "react-bootstrap"
import APIObject from "./classes/APIObject"


function PrettyOutput(props: {
    result: Array<APIObject>
    prototype: APIObject
}){


    return (
    <Container className="flex-grow-1" style={{ height: 200, overflowY: "auto"}}>
        <ListGroup >
        <ObjectRowHeader prototype={props.prototype}/>
        {props.result.map((obj, idx) => <ObjectRow object={obj}/>)}
    </ListGroup>
    </Container>
    )
}



function ObjectRowHeader(props: {
    prototype: APIObject
}){

    return (
        <ListGroupItem>
            <Row style={{fontWeight: 'bold'}}>
                {props.prototype.getKeys().map((key, idx) => <Col>{key}</Col>)}
            </Row>
        </ListGroupItem>
    )
}

function ObjectRow(props: {
    object: APIObject
}){
    
    return (
        <ListGroupItem>
            <Row>
             {props.object.getValues().map((value, idx) => <Col>{value}</Col>)}
            </Row>
        </ListGroupItem>
    )
}


export default PrettyOutput