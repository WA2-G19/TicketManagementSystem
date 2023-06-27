/* /Api/tickets
// /{ticketId}/chat-messages/{chatMessageId} get
// /{ticketId}/chat-messages get
// /{ticketId}/chat-messages post
// /{ticketId}/chat-messages/{chatMessageId}/attachments/{attachmentId} get */

import {ChatMessageIn, ChatMessageOut} from "../../classes/Chat";
import ProblemDetail from "../../classes/ProblemDetail";

const {REACT_APP_SERVER_URL} = process.env;

async function getChatMessage(token: string | undefined, ticketId: number, chatMessageId: number) {

    try {
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
        } else {
            return undefined
        }
    } catch (e) {
        throw e
    }


}

async function getChatMessages(token: string, ticketId: number) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId + "/chat-messages",
            {
                headers: {
                    "Authorization": "Bearer " + token,
                    "accept": "application/json"
                }
            }
        )
        if (response.ok) {
            return response.json()
        } else {
            return undefined
        }
    } catch (e) {
        throw e
    }
}

async function postChatMessages(token: string | undefined, ticketId: number, message: ChatMessageIn, files: FileList | undefined) {

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

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId + "/chat-messages",
            {
                method: "POST",
                headers: {
                    "Authorization": "Bearer " + token
                },
                body: formData
            },
        )
        return response.ok
    } catch (e) {
        throw e
    }

}

async function getAttachmentByChatMessageId(token: string | undefined, ticketId: number, chatMessageId: number, attachmentId: number) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId + "/chat-messages/" + chatMessageId +"/attachments/" + attachmentId,
            {
                headers: {
                    "Authorization": "Bearer " + token
                }
            }
        )
        if(response.ok) {
            await response.blob()
            return response.ok
        } else {
            return undefined
        }
    } catch (e) {
        throw e
    }

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
    getUnreadMessages,
    getAllUnreadMessages
}
export default ChatAPI

