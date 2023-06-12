const { REACT_APP_SERVER_URL } = process.env;

async function getTicketClosedByExpert(token: string | undefined, expertEmail: string) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/stats/tickets-closed/" + expertEmail,
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

async function getAverageTimedByExpert(token: string | undefined, expertEmail: string) {

    try {
        const response = await fetch(REACT_APP_SERVER_URL + "/API/stats/average-time/" + expertEmail,
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

const StatsAPI = { getTicketClosedByExpert, getAverageTimedByExpert }
export default StatsAPI