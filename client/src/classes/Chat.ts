export class ChatMessageIn {

    body: string

    constructor(body: string){
        this.body = body
    }

    toJsonObject(): string {

        return JSON.stringify({
            body: this.body
        })
    }
}

export class ChatMessageOut {

    id: number
    body: string
    authorEmail: string
    stubAttachments: Array<StubAttachmentDTO>
    timestamp: string

    constructor(body: string, id: number, authorEmail: string, timestamp: string, stubAttachments: Array<StubAttachmentDTO>){
        this.body = body
        this.id = id
        this.timestamp = timestamp
        this.authorEmail = authorEmail
        this.stubAttachments = stubAttachments
    }

}

export class StubAttachmentDTO {

    name: string
    contentType: string
    length: number
    url: string
    timestamp: string

    constructor(name: string, contentType: string, length: number, timestamp: string, url: string){
        this.name = name
        this.contentType = contentType
        this.timestamp = timestamp
        this.length = length
        this.url = url
    }

}