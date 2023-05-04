import {Col, Button} from 'react-bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import React from "react";

let tables = ["Product", "Profile"]

function Sidebar(props: {
    setFilter: React.Dispatch<React.SetStateAction<string>>,
    filter: string
}) {

    return (
        
        <Col className="col-2 p-2 bg-secondary bg-opacity-25  ">
            <div className="list-group" id="filters">
                {
                    tables.map((value: string, idx) => {
                        let classes = "list-group-item list-group-item-action " + ((value === props.filter)? "active": "")
                        console.log(classes)
                        return (<Button key={idx} type="button" className={classes}  onClick={(e) => {
                            e.preventDefault()
                            props.setFilter(value)
                        }}> {value}</Button>)
                    })
                }
            </div>
        </Col>
    );
}


export default Sidebar;

