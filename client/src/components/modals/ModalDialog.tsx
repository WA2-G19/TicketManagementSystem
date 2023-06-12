import {Button, Container, ListGroup, ListGroupItem, Modal} from "react-bootstrap";
import React, {Dispatch, useState} from "react";

interface ModalProps<T> {
    show: boolean,
    setShow: Dispatch<boolean>,
    elements: T[] | undefined,
    render: (arg0: T) => JSX.Element,
    onComplete: (arg0: T | undefined) => any
}

function ModalDialog<T>(props: ModalProps<T>) {
    const [selected, setSelected] = useState<T | undefined>(undefined)

    const handleClose = () => {
        props.setShow(false)
    }

    const handleComplete = () => {
        props.onComplete(selected)
        handleClose()
    }

    return <Modal show={props.show} onHide={handleClose} scrollable={true} fullscreen={"md-down"}>
        <Modal.Header closeButton>
            <Modal.Title>Assign Ticket</Modal.Title>
        </Modal.Header>
        <Modal.Body>
            <ListGroup>
                {
                    props.elements && props.elements.map(e =>
                        <ListGroup.Item className={"pt-2"} active={e === selected} onClick={() => setSelected(e)}>
                            {props.render(e)}
                        </ListGroup.Item>)
                }
            </ListGroup>
        </Modal.Body>
        <Modal.Footer>
            <Button variant="secondary" onClick={handleClose}>
                Close
            </Button>
            <Button variant="primary" onClick={handleComplete}>
                Save Changes
            </Button>
        </Modal.Footer>
    </Modal>
}

export default ModalDialog