class ProblemDetail extends Error {
    type: string
    title: string
    status: number
    detail: string
    instance: string

    constructor(type: string, title: string, status: number, detail: string, instance: string) {
        super(`${instance} [${status}] (${title}) ${detail}`)
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;
    }

    toString(): string {
        return `${this.instance} [${this.status}] (${this.title}) ${this.detail}`
    }
}

export default ProblemDetail