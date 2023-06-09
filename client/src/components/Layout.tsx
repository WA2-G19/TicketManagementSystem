import {Container, Form, Card, Button, FormText, Row, Col, ListGroup} from 'react-bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import React, {createContext, useContext, useEffect, useState} from 'react';
import {Link, Navigate, useNavigate} from "react-router-dom";
import {AuthenticationContextProvider, useAuthentication} from "../contexts/Authentication";

function Layout() {

    return (
        <>
            <Container fluid={"xxl"} className={"bg-primary"} style={{height: "100%"}}>
                <Row>
                    <Col className={"bg-primary"}>
                        <ListGroup>
                            <ListGroup.Item>
                                Create Ticket
                            </ListGroup.Item>
                            <ListGroup.Item>
                                Dashboard
                            </ListGroup.Item>
                            <ListGroup.Item>
                                Tickets
                            </ListGroup.Item>
                            <ListGroup.Item>
                                History
                            </ListGroup.Item>
                            <ListGroup.Item>
                                Personal Notebook
                            </ListGroup.Item>
                        </ListGroup>
                    </Col>
                    <Col></Col>
                </Row>
            </Container>
        </>
    )
}

export default Layout