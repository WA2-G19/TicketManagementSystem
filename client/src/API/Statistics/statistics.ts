import ProblemDetail from "../../classes/ProblemDetail";
import Statistics from "../../classes/Statistics";

const { REACT_APP_SERVER_URL } = process.env;

async function getAllStatistics(token: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/stats/all",
        {
            headers: {
                "Authorization": "Bearer " + token,
                "accept": "application/json"
            }
        }
    )
    if(response.ok) {
        return await response.json() as { [expertEmail: string]: Statistics }
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function getAllStatisticsByExpert(token: string, expertEmail: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/stats/all/" + expertEmail,
        {
            headers: {
                "Authorization": "Bearer " + token,
                "accept": "application/json"
            }
        }
    )
    if(response.ok) {
        return await response.json() as Statistics
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function getTicketsClosedByExpert(token: string, expertEmail: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/stats/tickets-closed/" + expertEmail,
        {
            headers: {
                "Authorization": "Bearer " + token,
                "accept": "application/json"
            }
        }
    )
    if(response.ok) {
        return parseInt(await response.text())
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function getTicketsInProgressByExpert(token: string, expertEmail: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/stats/tickets-in-progress/" + expertEmail,
        {
            headers: {
                "Authorization": "Bearer " + token,
                "accept": "application/json"
            }
        }
    )
    if(response.ok) {
        return parseInt(await response.text())
    }
    throw ProblemDetail.fromJSON(await response.json())
}

async function getAverageTimedByExpert(token: string, expertEmail: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/stats/average-time/" + expertEmail,
        {
            headers: {
                "Authorization": "Bearer " + token,
                "accept": "application/json"
            }
        }
    )
    if(response.ok) {
        return parseFloat(await response.text())
    }
    throw ProblemDetail.fromJSON(await response.json())
}

const StatsAPI = { getAllStatistics, getAllStatisticsByExpert, getTicketsClosedByExpert, getTicketsInProgressByExpert, getAverageTimedByExpert }
export default StatsAPI