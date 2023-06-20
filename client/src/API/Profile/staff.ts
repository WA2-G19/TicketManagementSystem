import {CredentialStaff, Staff} from "../../classes/Profile";
import StatsAPI from "../Statistics/statistics";
import ProblemDetail from "../../classes/ProblemDetail";

const {REACT_APP_SERVER_URL} = process.env;

async function getProfiles(token: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/staff/profiles", {
        headers: {
            "Authorization": "Bearer " + token,
            "Accept": "application/json"
        }
    })
    if (response.ok) {
        return await response.json() as Array<Staff>
    }
    throw await response.json() as ProblemDetail
}

async function getProfilesWithStatistics(token: string) {
    const [staffMembers, statistics] = await Promise.all([
        getProfiles(token),
        StatsAPI.getAllStatistics(token)
    ])
    return staffMembers.map(staffMember => {
        staffMember.avgTime = statistics[staffMember.email].averageTime
        staffMember.ticketsClosed = statistics[staffMember.email].ticketsClosed
        staffMember.ticketsInProgress = statistics[staffMember.email].ticketsInProgress
        return staffMember
    })
}

async function getProfile(token: string, email: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/staff/" + email, {
        headers: {
            "Authorization": "Bearer " + token,
            "Accept": "application/json"
        }
    })
    if (response.ok) {
        return await response.json() as Staff
    }
    throw await response.json() as ProblemDetail
}

async function createExpert(token: string, credentials: CredentialStaff) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/staff/createExpert", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token,
            "Accept": "application/json",
            "Content-Type": "application/json"
        },
        body: credentials.toJsonObject()
    })
    if (response.ok)
        return true
    throw await response.json() as ProblemDetail
}

const StaffAPI = {getProfile, createExpert, getProfiles, getProfilesWithStatistics}
export default StaffAPI