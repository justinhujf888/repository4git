import util from "@/api/util.js"
export const Beans = {
	buildPId(pid) {
		return pid + new Date().getTime() + util.random_string(5);
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
            lat: "",
            lng: "",
            buyer: this.buyer(),
            competition: this.competition(),
            createDate: null,
            workItemList: []
		}
	},

    workItem() {
		return {
			id: "",
			path: "",
            mediaType: -1,
            work: this.work(),
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
            beginDate: null,
            endDate: null,
            createDate: null,
            description: "",
            appId: "",
            competitionList: []
        }
	}
}
