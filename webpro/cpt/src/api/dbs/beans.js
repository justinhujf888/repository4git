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
    },



    workStatus() {
        return [
            {id:0,name:"未提交"},
            {id:1,name:"已提交"},
            {id:9,name:"驳回"}
        ]
    },
    menuTreeDatas() {
        return [
            {key:"index",label:"首页",menuType:0,isUserSetup:true,route:""},
            {key:"userCenter",label:"用户中心",menuType:0,route:"",isUserSetup:false,items:[
                {key:"userWork",label:"上传作品",route:""},
                {key:"userMsg",label:"我的消息",route:""}
            ]},
            {key:"pingJiang",label:"评奖",menuType:1,route:"",isUserSetup:true,items:[
                {key:"pingWei",label:"评委",isUserSetup:true,route:""},
                {key:"pingShenBiaoZun",label:"评审标准",isUserSetup:true,route:""}
            ]},
            {key:"huoJiangWork",label:"获奖作品",menuType:1,route:"",isUserSetup:true,items:[
                {key:"storyWork",label:"历届获奖作品",isUserSetup:true,route:""},
                {key:"theWork",label:"本次获奖作品",isUserSetup:true,route:""}
            ]},
            {key:"news",label:"新闻",menuType:0,route:""}
        ]
    }
}
