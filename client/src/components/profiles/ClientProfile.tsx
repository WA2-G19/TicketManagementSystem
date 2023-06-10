import {Col, Row} from "react-bootstrap";
import {useAuthentication} from "../../contexts/Authentication";


const ClientProfile = () => {
    const auth = useAuthentication()
    const user = auth.user
    return (
        <Row>
            <Col xs={12} md={3}>
                <h5>{user?.name} (Nome Cognome)</h5>
                <p>{user?.email} (Email)</p>
            </Col>
        </Row>
    );
};

export default ClientProfile;