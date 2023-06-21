export class Skill {
    name: string

    constructor(name: string) {
        this.name = name
    }

    toJsonObject(): string {
        return JSON.stringify({
            name: this.name
        })
    }
}