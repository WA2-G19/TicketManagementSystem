import {Button, Col} from "react-bootstrap";
import {Typography} from "@mui/material";
import {TicketOut, TicketStatusEnum} from "../../classes/Ticket";
import {
    ClosedClient,
    InProgressClient,
    InprogressExpert,
    InProgressManager,
    OpenManager,
    ReopenedClient,
    ReopenedManager,
    ResolvedClient
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
            if (user?.role.includes("Client")) {
                if (ticket.status.toString() == TicketStatusEnum[TicketStatusEnum.Closed] && selectedStatus == ClosedClient.REOPENED) {
                    await TicketAPI.reopenTicket(user?.token, ticket.id)
                }
                if ((ticket.status.toString() == TicketStatusEnum[TicketStatusEnum.Resolved]
                        || ticket.status.toString() == TicketStatusEnum[TicketStatusEnum.Reopened]
                        || ticket.status.toString() == TicketStatusEnum[TicketStatusEnum.InProgress])
                    && (selectedStatus == ResolvedClient.CLOSED
                        || selectedStatus == ReopenedClient.CLOSED
                        || selectedStatus == InProgressClient.CLOSED)) {
                    await TicketAPI.closeTicket(user?.token, ticket.id, user?.email)
                }
            }
            if (user?.role.includes("Expert") && ticket.status.toString() == TicketStatusEnum[TicketStatusEnum.InProgress] && selectedStatus == InprogressExpert.RESOLVED) {
                await TicketAPI.resolveTicket(user?.token, ticket.id, user?.email)
            }
            if (user?.role.includes("Manager")) {
                if (ticket.status.toString() === TicketStatusEnum[TicketStatusEnum.Open] && selectedStatus === OpenManager.RESOLVED) {
                    await TicketAPI.resolveTicket(user?.token, ticket.id, user?.email)
                }
                if (ticket.status.toString() === TicketStatusEnum[TicketStatusEnum.Reopened] && selectedStatus === ReopenedManager.CLOSED) {
                    await TicketAPI.closeTicket(user?.token, ticket.id, user?.email)
                }
                if (ticket.status.toString() === TicketStatusEnum[TicketStatusEnum.InProgress] && selectedStatus === InProgressManager.RESOLVED) {
                    await TicketAPI.resolveTicket(user?.token, ticket.id, user?.email)
                }
            }
        }
        setSelectedStatus("Choose")
        const tmp = await TicketAPI.getTicketById(user?.token as string, ticket.id)
        setTicket(tmp)
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