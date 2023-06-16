import {Skill} from "../../classes/Skill";

const { REACT_APP_SERVER_URL } = process.env;
async function getAll(token: string) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/skill", {
        headers: {
            "Authorization": "Bearer " + token,
            "Accept": "application/json"
        }
    })
    if (response.ok) {
        return await response.json() as Array<Skill>
    }
}

async function insertSkill(token: string, skill: Skill) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/skill", {
        method: "POST",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json",
            "Accept": "application/json"
        },
        body: skill.toJsonObject()
    })
    if (response.ok) {
        return await response.json() as Skill
    }
}

async function deleteSkill(token: string, skill: Skill) {
    const response = await fetch(REACT_APP_SERVER_URL + "/API/skill", {
        method: "DELETE",
        headers: {
            "Authorization": "Bearer " + token,
            "Content-Type": "application/json",
            "Accept": "application/json"
        },
        body: skill.toJsonObject()
    })
    if (response.ok) {
        return await response.json() as Skill
    }
}

const SkillAPI = { getAll, insertSkill, deleteSkill }
export default SkillAPI