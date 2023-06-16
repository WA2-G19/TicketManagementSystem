/* /API/tickets
// /{ticketId}/chat-messages/{chatMessageId} get
// /{ticketId}/chat-messages get
// /{ticketId}/chat-messages post
// /{ticketId}/chat-messages/{chatMessageId}/attachments/{attachmentId} get */

const { REACT_APP_SERVER_URL } = process.env;

async function getChatMessage(token: string | undefined, ticketId: number, chatMessageId: number) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/"+ ticketId +"/chat-messages/" + chatMessageId,
            {
                headers: {
                    "Authorization": "Bearer " + token,
                    "accept": "application/json"
                }
            }
        )
        if(response.ok) {
            return response.json()
        } else {
            return undefined
        }
    } catch (e) {
        throw e
    }
}

async function getChatMessages(token: string | undefined, ticketId: number) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL  + "/API/tickets/"+ ticketId +"/chat-messages",
            {
                headers: {
                    "Authorization": "Bearer " + token,
                    "accept": "application/json"
                }
            }
        )
        if(response.ok) {
            return response.json()
        } else {
            return undefined
        }
    } catch (e) {
        throw e
    }

}

async function postChatMessages(token: string | undefined, ticketId: number) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/tickets/" + ticketId + "/chat-messages",
            {
                method: "POST",
                headers: {
                    "Authorization": "Bearer " + token,
                    "accept": "application/json"
                }
            }
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
                    "Authorization": "Bearer " + token,
                    "accept": "application/json"
                }
            }
        )
        if(response.ok) {
            return response.json()
        } else {
            return undefined
        }
    } catch (e) {
        throw e
    }

}

const ChatAPI = { getChatMessage, getChatMessages, postChatMessages, getAttachmentByChatMessageId }
export default ChatAPI

