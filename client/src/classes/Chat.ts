import APIObject from "./APIObject";

export class ChatMessageIn extends APIObject {

    body: string

    constructor(body: string){
        super()
        this.body = body
    }

    toJsonObject(): string {

        return JSON.stringify({
            body: this.body
        })
    }
}

export class ChatMessageOut extends APIObject {

    id: number
    body: string
    authorEmail: string
    stubAttachments: Set<StubAttachmentDTO>
    timestamp: string

    constructor(body: string, id: number, authorEmail: string, timestamp: string, stubAttachments: Set<StubAttachmentDTO>){
        super()
        this.body = body
        this.id = id
        this.timestamp = timestamp
        this.authorEmail = authorEmail
        this.stubAttachments = stubAttachments
    }

}

export class StubAttachmentDTO extends APIObject {

    name: string
    contentType: string
    lenght: number
    url: string
    timestamp: string

    constructor(name: string, contentType: string, length: number, timestamp: string, url: string){
        super()
        this.name = name
        this.contentType = contentType
        this.timestamp = timestamp
        this.lenght = length
        this.url = url
    }

}