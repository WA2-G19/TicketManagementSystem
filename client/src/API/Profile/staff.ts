import {CredentialStaff, Staff} from "../../classes/Profile";
import StatsAPI from "../Ticketing/statuses";

const {REACT_APP_SERVER_URL} = process.env;

async function getProfiles(token: string | undefined) {
    try {

        const response = await fetch(REACT_APP_SERVER_URL + "/API/staff/profiles", {
            headers: {
                "Authorization": "Bearer " + token,
                "Accept": "application/json"
            }
        })
        if (response.ok) {
            return await response.json() as Array<Staff>
        } else {
            return undefined
        }

    } catch (e) {
        throw e
    }
}

async function getProfilesWithStatistics(token: string | undefined) {
    try {

        const response = await fetch(REACT_APP_SERVER_URL + "/API/staff/profiles", {
            headers: {
                "Authorization": "Bearer " + token,
                "Accept": "application/json"
            }
        })
        if (response.ok) {
            const staff = await response.json() as Array<Staff>
            return await Promise.all(staff.map(async (member) => {
                member.avgTime = await StatsAPI.getAverageTimedByExpert(token, member.email)
                member.ticketClosed = await StatsAPI.getTicketClosedByExpert(token, member.email)
                return member
            }))
        } else {
            return undefined
        }

    } catch (e) {
        throw e
    }
}

async function getProfile(token: string, email: string) {
    try {

        const response = await fetch(REACT_APP_SERVER_URL + "/API/staff/" + email, {
            headers: {
                "Authorization": "Bearer " + token,
                "Accept": "application/json"
            }
        })
        if (response.ok) {
            return await response.json() as Staff
        } else {
            return undefined
        }

    } catch (e) {
        throw e
    }
}

async function createExpert(token: string, credentials: CredentialStaff) {
    try {

        const response = await fetch(REACT_APP_SERVER_URL + "/API/staff/createExpert", {
            method: "POST",
            headers: {
                "Authorization": "Bearer " + token,
                "Accept": "application/json",
                "Content-Type": "application/json"
            },
            body: credentials.toJsonObject()
        })
        return response.ok

    } catch (e) {
        throw e
    }
}

const StaffAPI = {getProfile, createExpert, getProfiles, getProfilesWithStatistics}
export default StaffAPI