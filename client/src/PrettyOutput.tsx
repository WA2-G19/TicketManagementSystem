
import { Col, Row, ListGroupItem, ListGroup, DropdownButton, Container } from "react-bootstrap"
import APIObject from "./classes/APIObject"
import { prototype } from "events"
import { DataGrid, GridRowsProp, GridColDef } from '@mui/x-data-grid';

function PrettyOutput(props: {
    result: Array<APIObject>
    prototype: APIObject
}){

    const columns: GridColDef[]  = props.prototype.getKeys().map(key =>
        { 
            return { field: key, headerName: key, flex: 1 }
        }
    )
    
    const rows: GridRowsProp = props.result.map((row,idx) => {
        const castedRow: any = row as any
        castedRow.id = idx
        return castedRow
    })
    
     
    return (
    <Container className="" style={{height: 56*8}}>
              <DataGrid rows={rows} columns={columns}    disableColumnSelector scrollbarSize={25}
/>
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