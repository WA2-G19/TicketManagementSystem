import Card from 'react-bootstrap/Card';
import React from "react";
import {Row} from "react-bootstrap";

interface StatCardProp {
    url: string,
    height: string,
    width: string
}

const {REACT_APP_GRAFANA_URL} = process.env;

export function StatCard(props: StatCardProp) {

    return <Row >
        <iframe src={REACT_APP_GRAFANA_URL + props.url} style={{width: props.width, height: props.height}}></iframe>
    </Row>
}