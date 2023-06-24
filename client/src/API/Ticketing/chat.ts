/* /Api/tickets
// /{ticketId}/chat-messages/{chatMessageId} get
// /{ticketId}/chat-messages get
// /{ticketId}/chat-messages post
// /{ticketId}/chat-messages/{chatMessageId}/attachments/{attachmentId} get */

import {ChatMessageIn, ChatMessageOut, StubAttachmentDTO} from "../../classes/Chat";
import ProblemDetail from "../../classes/ProblemDetail";

const { REACT_APP_SERVER_URL } = process.env;

async function getChatMessage(token: string | undefined, ticketId: number, chatMessageId: number) {

    // try {
    //     const response = await fetch(REACT_APP_SERVER_URL + "/Api/tickets/"+ ticketId +"/chat-messages/" + chatMessageId,
    //         {
    //             headers: {
    //                 "Authorization": "Bearer " + token,
    //                 "accept": "application/json"
    //             }
    //         }
    //     )
    //     if(response.ok) {
    //         return response.json()
    //     } else {
    //         return undefined
    //     }
    // } catch (e) {
    //     throw e
    // }


}

async function getChatMessages(token: string | undefined, ticketId: number) {

    // try {
    //     const response = await fetch(REACT_APP_SERVER_URL  + "/Api/tickets/"+ ticketId +"/chat-messages",
    //         {
    //             headers: {
    //                 "Authorization": "Bearer " + token,
    //                 "accept": "application/json"
    //             }
    //         }
    //     )
    //     if(response.ok) {
    //         return response.json()
    //     } else {
    //         return undefined
    //     }
    // } catch (e) {
    //     throw e
    // }
    const mockChatMessagesForTicket = [
        new ChatMessageOut("Ciao", 0, "expert@test.it", "", new Set<StubAttachmentDTO>()),
        new ChatMessageOut("Ciao", 1, "client@test.it", "", new Set<StubAttachmentDTO>()),
        new ChatMessageOut("Ciao", 2, "expert@test.it", "", new Set<StubAttachmentDTO>()),
        new ChatMessageOut("Ciao", 3, "client@test.it", "", new Set<StubAttachmentDTO>()),
        new ChatMessageOut("Ciao", 4, "expert@test.it", "", new Set<StubAttachmentDTO>()),
        new ChatMessageOut("Ciao", 5, "expert@test.it", "", new Set<StubAttachmentDTO>()),
        new ChatMessageOut("Ciao", 6, "client@test.it", "", new Set<StubAttachmentDTO>()),
        new ChatMessageOut("Ciao", 7, "expert@test.it", "", new Set<StubAttachmentDTO>()),
        new ChatMessageOut("Ciao", 8, "client@test.it", "", new Set<StubAttachmentDTO>()),
        new ChatMessageOut("Ciao", 9, "client@test.it", "", new Set<StubAttachmentDTO>()),
    ]
    return mockChatMessagesForTicket.sort((n1,n2) => {
        if (n1.id > n2.id) return 1;
        else if (n1.id < n2.id) return -1;
        else return 0;
    })
}

async function postChatMessages(token: string | undefined, ticketId: number, message: ChatMessageIn, files: FileList | undefined) {

    // try {
    //     const response = await fetch(REACT_APP_SERVER_URL + "/Api/tickets/" + ticketId + "/chat-messages",
    //         {
    //             method: "POST",
    //             headers: {
    //                 "Authorization": "Bearer " + token,
    //                 "accept": "application/json"
    //             }
    //         }
    //     )
    //     return response.ok
    // } catch (e) {
    //     throw e
    // }
    return true

}

async function getAttachmentByChatMessageId(token: string | undefined, ticketId: number, chatMessageId: number, attachmentId: number) {

    // try {
    //     const response = await fetch(REACT_APP_SERVER_URL + "/Api/tickets/" + ticketId + "/chat-messages/" + chatMessageId +"/attachments/" + attachmentId,
    //         {
    //             headers: {
    //                 "Authorization": "Bearer " + token,
    //                 "accept": "application/json"
    //             }
    //         }
    //     )
    //     if(response.ok) {
    //         return response.json()
    //     } else {
    //         return undefined
    //     }
    // } catch (e) {
    //     throw e
    // }

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
        return await response.json() as { [k: string]: number}
    }
    throw ProblemDetail.fromJSON(await response.json())
}

const ChatAPI = { getChatMessage, getChatMessages, postChatMessages, getAttachmentByChatMessageId, getUnreadMessages, getAllUnreadMessages }
export default ChatAPI

