import APIObject from "./APIObject"

export class Ticket {
    warrantyUUID: string
    description: string

    constructor(warrantyUUID: string, description: string) {
        this.warrantyUUID = warrantyUUID
        this.description = description
    }

    toJSONObject(): string {

        const ticketMap = {
            "warrantyUUID": this.warrantyUUID,
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

export class TicketOut extends APIObject {
    id: number
    customerEmail: string
    productEan: string
    description: string

    expertEmail: string | undefined

    priorityLevel: PriorityLevelEnum | undefined

    status: TicketStatusEnum

    warrantyUUID : string

    unreadMessages?: number

    constructor(id: number, customerEmail: string, productEan: string, description: string, status: TicketStatusEnum, priorityLevel: PriorityLevelEnum | undefined, expertEmail: string | undefined, warrantyUUID: string, unreadMessages?: number) {
        super()
        this.id = id
        this.customerEmail = customerEmail
        this.productEan = productEan
        this.description = description
        this.status = status
        this.expertEmail = expertEmail
        this.priorityLevel = priorityLevel
        this.warrantyUUID = warrantyUUID
        this.unreadMessages = unreadMessages
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