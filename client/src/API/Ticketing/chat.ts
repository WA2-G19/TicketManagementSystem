import {ChatMessageIn, ChatMessageOut} from "../../classes/Chat";
import ProblemDetail from "../../classes/ProblemDetail";

const {REACT_APP_SERVER_URL} = process.env;

async function getChatMessage(token: string, ticketId: number, chatMessageId: number) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId + "/chat-messages/" + chatMessageId,
        {
            headers: {
                "Authorization": "Bearer " + token,
                "accept": "application/json"
            }
        }
    )
    if (response.ok) {
        return await response.json() as ChatMessageOut
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function getChatMessages(token: string, ticketId: number) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId + "/chat-messages",
        {
            headers: {
                "Authorization": "Bearer " + token,
                "accept": "application/json"
            }
        }
    )
    if (response.ok) {
        return await response.json() as Array<ChatMessageOut>
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function postChatMessages(token: string, ticketId: number, message: ChatMessageIn, files: FileList | null) {

    const formData = new FormData();
    formData.append(
        "message",
        new Blob([message.toJsonObject()], {
            type: "application/json",
        })
    );
    if (files) {
        Array.from(files).forEach(file => {
                formData.append("files", file, file.name)
            }
        );
    }
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId + "/chat-messages",
        {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token
            },
            body: formData
        },
    )
    if (!response.ok) {
        throw ProblemDetail.fromJSON(await response.json())
    }
}

async function getAttachmentByChatMessageId(token: string, ticketId: number, chatMessageId: number, attachmentId: number) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId + "/chat-messages/" + chatMessageId +"/attachments/" + attachmentId,
        {
            headers: {
                "Authorization": "Bearer " + token
            }
        }
    )
    if (!response.ok) {
        throw ProblemDetail.fromJSON(await response.json())
    }
    return await response.blob()
}

async function getAttachmentByUrl(token: string, url: string) {
    const response = await fetch(REACT_APP_SERVER_URL + url,
        {
            headers: {
                "Authorization": "Bearer " + token
            }
        }
    )
    if (!response.ok) {
        throw ProblemDetail.fromJSON(await response.json())
    }
    return await response.blob()
}

async function getUnreadMessages(token: string, ticketId: number) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId + "/chat-messages/unread", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    if (response.ok) {
        return parseInt(await response.text())
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function getAllUnreadMessages(token: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/chat-messages/unread", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token
        }
    })
    if (response.ok) {
        return await response.json() as { [k: string]: number }
    }
    throw ProblemDetail.fromJSON(await response.json())
}

const ChatAPI = {
    getChatMessage,
    getChatMessages,
    postChatMessages,
    getAttachmentByChatMessageId,
    getAttachmentByUrl,
    getUnreadMessages,
    getAllUnreadMessages
}
export default ChatAPI