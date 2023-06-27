import {Button, Col, Form} from "react-bootstrap";
import {Typography} from "@mui/material";
import {TicketOut, TicketStatusEnum} from "../../classes/Ticket";
import {
    ClosedClient,
    InprogressExpert,
    InProgressManager,
    OpenManager,
    ReopenedManager,
    ResolvedClient, ResolvedManager
} from "../../utils/changeStatus";
import React, {useState} from "react";
import {useAlert} from "../../contexts/Alert";
import HasRole from "../authentication/HasRole";
import {useAuthentication} from "../../contexts/Authentication";
import TicketAPI from "../../API/Ticketing/tickets";

export function ChangeStatus({ticketTMP}: {
    ticketTMP: TicketOut
}): JSX.Element {
    const user = useAuthentication().user
    const alert = useAlert()
    const [selectedStatus, setSelectedStatus] = useState<string>("Choose")
    const [ticket, setTicket] = useState<TicketOut>(ticketTMP)

    const handleDropdownChange = (
        event: React.ChangeEvent<HTMLSelectElement>
    ) => {
        const selectedOption = event.target.value;
        setSelectedStatus(selectedOption);
    };

    async function handleConfirm() {
        if (selectedStatus === "Choose") {
            alert.getBuilder().setTitle("Error").setMessage("Choose an option").show()
        } else {
            try {
                if (selectedStatus === TicketStatusEnum[TicketStatusEnum.Reopened]) {
                    await TicketAPI.reopenTicket(user?.token, ticket.id)
                } else if (selectedStatus === TicketStatusEnum[TicketStatusEnum.Resolved]) {
                    await TicketAPI.resolveTicket(user?.token, ticket.id, user?.email)
                } else if (selectedStatus === TicketStatusEnum[TicketStatusEnum.Closed]) {
                    await TicketAPI.closeTicket(user?.token, ticket.id, user?.email)
                }
                setSelectedStatus("Choose")
                const tmp = await TicketAPI.getTicketById(user?.token as string, ticket.id)
                setTicket(tmp)
            } catch (e: any) {
                alert.getBuilder().setTitle("Error").setMessage(e['detail']).show()
            }
        }
    }


    return <>
        <Col>
            <Typography variant="body2" color="primary">
                <strong>Change status (Actual: {ticket.status})</strong>
            </Typography>
            <Form.Select value={selectedStatus} onChange={handleDropdownChange}>
                <option key={"Choose"} value={"Choose"}>Choose</option>
                <HasRole role={"Client"}><>
                    {ticket.status.toString() === TicketStatusEnum[TicketStatusEnum.Resolved] && Object.values(ResolvedClient).map((value) => (
                        <option key={value} value={value}>
                            {value}
                        </option>
                    ))}
                    {ticket.status.toString() === TicketStatusEnum[TicketStatusEnum.Closed] && Object.values(ClosedClient).map((value) => (
                        <option key={value} value={value}>
                            {value}
                        </option>
                    ))}
                </>
                </HasRole>
                <HasRole role={"Manager"}><>
                    {ticket.status.toString() === TicketStatusEnum[TicketStatusEnum.Open] && Object.values(OpenManager).map((value) => (
                        <option key={value} value={value}>
                            {value}
                        </option>
                    ))}
                    {ticket.status.toString() === TicketStatusEnum[TicketStatusEnum.Reopened] && Object.values(ReopenedManager).map((value) => (
                        <option key={value} value={value}>
                            {value}
                        </option>
                    ))}
                    {ticket.status.toString() === TicketStatusEnum[TicketStatusEnum.InProgress] && Object.values(InProgressManager).map((value) => (
                        <option key={value} value={value}>
                            {value}
                        </option>
                    ))}
                    {ticket.status.toString() === TicketStatusEnum[TicketStatusEnum.Resolved] && Object.values(ResolvedManager).map((value) => (
                        <option key={value} value={value}>
                            {value}
                        </option>
                    ))}
                </>
                </HasRole>
                <HasRole role={"Expert"}><>
                    {ticket.status.toString() === TicketStatusEnum[TicketStatusEnum.InProgress] && Object.values(InprogressExpert).map((value) => (
                        <option key={value} value={value}>
                            {value}
                        </option>
                    ))}
                </>
                </HasRole>
            </Form.Select>
            <Button onClick={handleConfirm}>Confirm</Button>
        </Col>
    </>
}