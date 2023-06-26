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
    throw ProblemDetail.fromJSON(await response.json())
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
        const { email, name, type, surname, skills } = await response.json()
        return new Staff(email, name, surname, type, skills)
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function putProfile(token: string, staff: Staff) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/staff/" + staff.email, {
        method: "PUT",
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json',
            "Authorization": "Bearer " + token
        },
        body: staff.toJsonObject()
    })
    if (!response.ok) {
        throw ProblemDetail.fromJSON(await response.json())
    }
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
    if (!response.ok) {
        throw ProblemDetail.fromJSON(await response.json())
    }
}

const StaffAPI = {getProfile, createExpert, getProfiles, putProfile, getProfilesWithStatistics}
export default StaffAPI