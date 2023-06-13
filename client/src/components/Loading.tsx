import {Spinner} from "react-bootstrap";
import React from "react";

export function Loading(): JSX.Element {
    return <div className="text-center" style={{
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        height: "100vh"
    }}>
        <Spinner animation="border" variant="primary" style={{width: "15rem", height: "15rem"}}/>
    </div>
}
