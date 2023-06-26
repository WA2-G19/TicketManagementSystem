import {Button, Col} from "react-bootstrap";
import {Typography} from "@mui/material";
import {TicketOut, TicketStatusEnum} from "../../classes/Ticket";
import {
    ClosedClient, InProgressClient, InprogressExpert,
    InProgressManager,
    OpenManager, ReopenedClient,
    ReopenedManager,
    ResolvedClient
} from "../../utils/changeStatus";
import React, {useState} from "react";
import {useAlert} from "../../contexts/Alert";
import HasRole from "../authentication/HasRole";

export function ChangeStatus({ticket}: {
    ticket: TicketOut
}): JSX.Element {
    const alert = useAlert()
    const [selectedStatus, setSelectedStatus] = useState<string>("Choose");

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

        }
    }

    return <>
        <Col>
            <Typography variant="body2" color="primary">
                <strong>Change status</strong>
            </Typography>
            <select value={selectedStatus} onChange={handleDropdownChange}>
                <option key={"Choose"} value={"Choose"}>Choose</option>
                <HasRole role={"Client"}><>
                    {ticket.status.toString() === TicketStatusEnum[TicketStatusEnum.InProgress] && Object.values(InProgressClient).map((value) => (
                        <option key={value} value={value}>
                            {value}
                        </option>
                    ))}
                    {ticket.status.toString() === TicketStatusEnum[TicketStatusEnum.Reopened] && Object.values(ReopenedClient).map((value) => (
                        <option key={value} value={value}>
                            {value}
                        </option>
                    ))}
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
            </select>
            <Button style={{padding: "6px 8px", fontSize: "10px"}} onClick={handleConfirm}>Confirm</Button>
        </Col>
    </>
}