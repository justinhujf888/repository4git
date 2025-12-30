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

    siteCompetition() {
        return {
            id: "",
            name: "",
            domain: "",
            description: "",
            setupFields: null,
            appId: "",
            masterCompetitionList: []
        }
    },

    siteWorkItem() {
        return {
            id: "",
            path: "",
            sourceType: -1,
            sourceId: "",
            type: -1,
            mediaType: -1,
            createDate: null,
            appId: "",
            fileFields: null
        }
    },

    orgHuman() {
        return {
            id: "",
            name: "",
            engName: "",
            sourceType: -1,
            sourceId: "",
            description: "",
            createDate: null,
            headImgUrl: "",
            appId: ""
        }
    },

    work() {
		return {
            id: "",
            name: "",
            gousiDescription: "",
            myMeanDescription: "",
            hangyeFields: null,
            otherFields: null,
            lat: "",
            lng: "",
            guiGe: this.guiGe(),
            guiGeId: "",
            buyer: this.buyer(),
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
            type: -1,
            exifInfo: "",
            work: this.work(),
            createDate: null,
            mediaFields: null
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
            status: -1,
            description: "",
            appId: "",
            masterCompetition: this.masterCompetition(),
            guiGeList: []
		}
	},

    guiGe() {
        return {
            id: "",
            name: "",
            guigeFields: null,
            appId: "",
            competition: this.competition(),
            workList: []
        }
    },

    masterCompetition() {
        return {
            id: "",
            name: "",
            pxBiaozun: null,
            setupFields: null,
            judgeSetup: null,
            cptDate: null,
            beginDate: null,
            pingShenDate: null,
            endDate: null,
            createDate: null,
            description: null,
            appId: "",
            siteCompetition: this.siteCompetition(),
            competitionList: []
        }
	},

    mcPageSetupPK() {
        return {
            competitionId: "",
            key: ""
        }
    },
    mcPageSetup() {
        return {
            mcPageSetupPK: this.mcPageSetupPK(),
            setupJson: null
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
            phone: "",
            name: "",
            engName: "",
            password: "",
            description: "",
            createDate: null,
            appId: "",
            headImgUrl: "",
            subDescription: "",
            zhiWei: ""
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
    },



    workStatus() {
        return [
            {id:0,name:"未提交"},
            {id:1,name:"已提交"},
            {id:9,name:"驳回"}
        ]
    }
}
