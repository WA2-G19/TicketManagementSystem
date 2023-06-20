class Statistics {
    ticketsClosed: number
    ticketsInProgress: number
    averageTime: number


    constructor(ticketsClosed: number, ticketsInProgress: number, averageTime: number) {
        this.ticketsClosed = ticketsClosed;
        this.ticketsInProgress = ticketsInProgress;
        this.averageTime = averageTime;
    }
}

export default Statistics