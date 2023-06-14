import {Button, Col, Container, Modal, Row} from "react-bootstrap";
import React, {useState} from "react";

interface ModalProps<T> {
    show: boolean,
    hide: () => void,
    title: string,
    elements: T[] | undefined,
    render: (arg0: T) => JSX.Element,
    keySelector: (arg0: T) => string | number,
    onComplete: (arg0: T | undefined) => any
}

function ModalDialog<T>(props: ModalProps<T>) {
    const [selected, setSelected] = useState<T | undefined>(undefined)

    const handleClose = () => {
        props.hide()
    }

    const handleComplete = () => {
        props.onComplete(selected)
        handleClose()
    }

    return <Modal show={props.show} onHide={handleClose} scrollable={true} fullscreen={"md-down"}>
        <Modal.Header closeButton>
            <Modal.Title>{props.title}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
            <Container>
                <Row>
                    {
                        props.elements && props.elements.map(e =>
                            <Col className={"pt-2" + (e === selected ? " border-danger border-3" : "")} onClick={() => setSelected(e)} key={props.keySelector(e)}>
                                {props.render(e)}
                            </Col>)
                    }
                </Row>
            </Container>
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