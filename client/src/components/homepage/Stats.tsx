import React from "react";
import {Col, Container, Row} from "react-bootstrap";
import {StatCard} from "../stats/StatCard";

function Stats() {

    return (
        <Container>
            <Row>
                <Col xs={2}>
                    <StatCard
                        url={"/d-solo/dLsDQIUnzb/spring-boot-observability?orgId=1&refresh=5s&panelId=4"}
                        height={"200px"}
                        width={"200px"}
                    />
                </Col>
                <Col>
                    <StatCard
                        url={"/d-solo/dLsDQIUnzb/spring-boot-observability?orgId=1&refresh=5s&panelId=16"}
                        height={"200px"}
                        width={"1000px"}
                    />
                </Col>
            </Row>
            <Row>
                <StatCard
                    url={"/d-solo/dLsDQIUnzb/spring-boot-observability?orgId=1&refresh=5s&panelId=18"}
                    height={"200px"}
                    width={"1000px"}
                />
            </Row>
            <Row>
                 <StatCard
                     url={"/d-solo/dLsDQIUnzb/spring-boot-observability?orgId=1&refresh=5s&panelId=8"}
                     height={"200px"}
                     width={"1000px"}/>

            </Row>
            <Row>
                 <StatCard
                     url={"/d-solo/dLsDQIUnzb/spring-boot-observability?orgId=1&refresh=5s&panelId=23"}
                     height={"200px"}
                     width={"1000px"}/>
            </Row>
            <Row>
                 <StatCard
                     url={"/d-solo/dLsDQIUnzb/spring-boot-observability?orgId=1&refresh=5s&panelId=12"}
                     height={"200px"}
                     width={"1000px"}/>
            </Row>
            <Row>
                 <StatCard
                     url={"/d-solo/dLsDQIUnzb/spring-boot-observability?orgId=1&refresh=5s&panelId=20"}
                     height={"200px"}
                     width={"1000px"}/>
            </Row>
            <Row>
                 <StatCard
                     url={"/d-solo/dLsDQIUnzb/spring-boot-observability?orgId=1&refresh=5s&panelId=6"}
                     height={"200px"}
                     width={"1000px"}/>
            </Row>
            <Row>
                 <StatCard
                     url={"/d-solo/dLsDQIUnzb/spring-boot-observability?orgId=1&refresh=5s&panelId=14"}
                     height={"200px"}
                     width={"1000px"}/>
            </Row>
            <Row>
                 <StatCard
                     url={"/d-solo/szVLMe97z/logs-traces-metrics?orgId=1&panelId=4"}
                     height={"200px"}
                     width={"1000px"}/>
            </Row>
               </Container>
    )
}

export default Stats