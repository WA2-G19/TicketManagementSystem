import APIObject from "./APIObject"
import Any = jasmine.Any;


class Ticket extends APIObject {
    id: number
    customerEmail: string
    productEan: string
    description: string

    constructor(id: number, customerEmail: string, productEan: string, description: string) {
        super()
        this.id = id
        this.customerEmail = customerEmail
        this.productEan = productEan
        this.description = description
    }

    toJSONObject(): string {

        const ticketMap = {
            "id": this.id,
            "customerEmail": this.customerEmail,
            "productEan": this.productEan,
            "description": this.description
        }

        return JSON.stringify(ticketMap)
    }

}

export enum PriorityLevelEnum {
    LOW, MEDIUM, HIGH, CRITICAL
}

export enum TicketStatusEnum {
    Open,
    InProgress,
    Closed,
    Resolved,
    Reopened
}

export class TicketOut extends Ticket {

    expertEmail: string | undefined

    priorityLevel: PriorityLevelEnum | undefined

    status: TicketStatusEnum

    warrantyUUID : string

    constructor(id: number, customerEmail: string, productEan: string, description: string, status: TicketStatusEnum, priorityLevel: PriorityLevelEnum | undefined, expertEmail: string | undefined, warrantyUUID: string) {
        super(id, customerEmail, productEan, description);
        this.status = status
        this.expertEmail = expertEmail
        this.priorityLevel = priorityLevel
        this.warrantyUUID = warrantyUUID
    }

    toJSONObject(): string {

        const ticketOutMap = {
            "id": this.id,
            "customerEmail": this.customerEmail,
            "productEan": this.productEan,
            "description": this.description,
            "status": this.status.valueOf(),
            "expertEmail": this.expertEmail,
            "priorityLevel": this.priorityLevel
        }

        return JSON.stringify(ticketOutMap)
    }

}

export default Ticket;