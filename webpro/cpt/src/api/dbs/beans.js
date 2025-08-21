import util from "@/api/util.js"
export const Beans = {
	buildPId(pid) {
		return pid + new Date().getTime() + util.random_string(8);
	},

	buyer() {
		return {
			phone: "",
			name: "",
			loginName: "",
			password: "",
			wxid: "",
			wxopenid: "",
			area: "",
			address: "",
			tel: "",
			headImgUrl: "",
			wxNickName: "",
			wxNickEm: "",
			amb: 0,
			sex: 0,
			description: "",
			createDate: null,
            workList: []
		}
	},

	work() {
		return {
            id: "",
            name: "",
            gousiDescription: "",
            myMeanDescription: "",
            otherFields: "",
            lat: "",
            lng: "",
            buyer: this.buyer(),
            competition: this.competition(),
            createDate: null,
            appId: "",
            status: -1,
            workItemList: []
		}
	},

    workItem() {
		return {
			id: "",
			path: "",
            mediaType: -1,
            exifInfo: "",
            work: this.work(),
            createDate: null
		}
	},

    workLog() {
        return {
            id: "",
            workId: "",
            log: "",
            createDate: null
        }
    },

    competition() {
		return {
			id: "",
			name: "",
            status: "",
            description: "",
            masterCompetition: this.masterCompetition(),
            workList: []
		}
	},

    masterCompetition() {
        return {
            id: "",
            name: "",
            fields: "",
            beginDate: null,
            pingShenDate: null,
            endDate: null,
            createDate: null,
            description: "",
            competitionList: []
        }
	},

    rule() {
        return {
            id: "",
            name: ""
        }
    },

    rulePermissionPK() {
        return {
            ruleId: "",
            permissionId: ""
        }
    },

    rulePermission() {
        return {
            rulePermissionPK: this.rulePermissionPK()
        }
    },

    judge() {
        return {
            id: "",
            name: "",
            engName: "",
            password: "",
            description: "",
            createDate: null,
            appId: "",
            headImgUrl: ""
        }
    },

    competitionJudgePK() {
        return {
            competitionId: "",
            judgeId: "",
            competitionStatus: -1
        }
    },

    competitionJudge() {
        return {
            competitionJudgePK: this.competitionJudgePK()
        }
    }
}
