export class Ticket {
    warrantyUUID: string
    description: string

    constructor(warrantyUUID: string, description: string) {
        this.warrantyUUID = warrantyUUID
        this.description = description
    }

    toJSONObject(): string {
        return JSON.stringify({
            "warrantyUUID": this.warrantyUUID,
            "description": this.description
        })
    }
}

export enum PriorityLevelEnum {
    LOW, MEDIUM, HIGH, CRITICAL
}

export enum TicketStatusEnum {
    Open= "Open",
    InProgress = "InProgress",
    Closed = "Closed",
    Resolved = "Resolved",
    Reopened = "Reopened"
}

export class TicketOut {
    id: number
    customerEmail: string
    productEan: string
    description: string
    status: TicketStatusEnum
    warrantyUUID : string
    expertEmail?: string
    priorityLevel?: PriorityLevelEnum
    unreadMessages?: number

    constructor(id: number, customerEmail: string, productEan: string, description: string, status: TicketStatusEnum, warrantyUUID: string, priorityLevel?: PriorityLevelEnum, expertEmail?: string, unreadMessages?: number) {
        this.id = id
        this.customerEmail = customerEmail
        this.productEan = productEan
        this.warrantyUUID = warrantyUUID
        this.description = description
        this.status = status
        this.expertEmail = expertEmail
        this.priorityLevel = priorityLevel
        this.unreadMessages = unreadMessages
    }
}