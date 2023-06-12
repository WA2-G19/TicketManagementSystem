import {Button, Container, ListGroup, ListGroupItem, Modal} from "react-bootstrap";
import React, {Dispatch} from "react";
import {StaffCard} from "../staff/Staff";
import {Staff} from "../../classes/Profile";
import {List} from "@mui/material";

interface ModalProps<T> {
    show: boolean,
    setShow: Dispatch<boolean>,
    elements: T[] | undefined
}

export function ModalDialog<T>(props: ModalProps<T>) {

    const handleClose = () => {
        props.setShow(false)
    }

    return <Modal show={props.show} onHide={handleClose} scrollable={true} fullscreen={"md-down"}>
        <Modal.Header closeButton>
            <Modal.Title>Assign Ticket</Modal.Title>
        </Modal.Header>
        <Modal.Body>
            <Container>
                <div data-bs-spy="scroll" data-bs-target="#navbar-example2" data-bs-offset="0"
                     className="scrollspy-example" >
                    {props.elements ? props.elements?.map((e, idx) => <ListGroupItem className={"pt-2"}>
                        <StaffCard key={idx} staff={e as Staff} assign={true}/>
                    </ListGroupItem>) : <></>}
                </div>
            </Container>
        </Modal.Body>
        <Modal.Footer>
            <Button variant="secondary" onClick={handleClose}>
                Close
            </Button>
            <Button variant="primary" onClick={handleClose}>
                Save Changes
            </Button>
        </Modal.Footer>
    </Modal>
}
