class APIObject{
    getKeys(): string[] {
        return Object.keys(this)
    }

    getValues(): string[]{
        return Object.values(this)
    }
}

export default APIObject