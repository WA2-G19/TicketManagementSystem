class ProblemDetail extends Error {
    type: string
    title: string
    status: number
    detail: string | { [key: string]: string}
    instance: string

    constructor(type: string, title: string, status: number, detail: string | { [key: string]: string}, instance: string) {
        super(`${instance} [${status}] (${title}) ${detail}`)
        this.type = type;
        this.title = title;
        this.status = status;
        this.detail = detail;
        this.instance = instance;

        Object.setPrototypeOf(this, ProblemDetail.prototype)
    }

    getDetails(separator: string = "\n"): string {
        if (typeof(this.detail) === "string")
            return this.detail
        return Object.entries(this.detail).map(([k, v]) => `The field '${k}' has error '${v}'`).join(separator)
    }

    static fromJSON(json: { [key: string]: any}) {
        if (
            "type" in json && typeof(json.type) === "string" &&
            "title" in json && typeof(json.title) === "string" &&
            "status" in json && typeof(json.status) === "number" &&
            "detail" in json && (typeof(json.detail) === "string" || typeof(json.detail) == "object") &&
            "instance" in json && typeof(json.instance) === "string") {
            return new ProblemDetail(json.type, json.title, json.status, json.detail, json.instance)
        }
        throw new Error("Can't extract ProblemDetail")
    }

    toString(): string {
        return `${this.instance} [${this.status}] (${this.title}) ${JSON.stringify(this.detail)}`
    }
}

export default ProblemDetail