import { Container } from "react-bootstrap"
import APIObject from "./classes/APIObject"
import { DataGrid, GridRowsProp, GridColDef } from '@mui/x-data-grid';

function PrettyOutput(props: {
    result: Array<APIObject>
    prototype: APIObject
}){
    console.log(props.result)

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
        <DataGrid rows={rows} columns={columns} disableColumnSelector scrollbarSize={25} />
    </Container>
    )
}





export default PrettyOutput