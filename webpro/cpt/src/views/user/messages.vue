<template>
    <animationPage ref="mainPage" :show="true" _class="w-full absolute top-0 z-40" class="">
        <div class="card text-xl !pt-1">
            <div v-if="workLogList?.length>0" class="gap-4 mt-4">
                <DataView :value="workLogList">
                    <template #list="slotProps">
                        <div v-for="item of slotProps.items" :legend="''">
                            <p class="between">
                                <span class="text-base">{{item.tempMap?.workName}}</span>
                                <span class="text-sm">{{item.createDate}}</span>
                            </p>
                            <p class="mt-2 text-sm">
                                {{item.log}}
                            </p>
                            <p>
                                <Divider/>
                            </p>
                        </div>
                    </template>
                </DataView>
            </div>
            <div v-else class="center mt-16 text-base">
                <span>无消息数据</span>
            </div>
        </div>
    </animationPage>
</template>

<script setup>
import animationPage from "@/components/my/animationPage.vue";
import workRest from '@/api/dbs/workRest';
import { inject, onMounted, ref, useTemplateRef } from 'vue';
import { useConfirm } from "primevue/useconfirm";
import uploadWork from "@/views/user/work/uploadWork.vue";
import lodash from "lodash-es";
import oss from "@/api/oss";
import { Beans } from "@/api/dbs/beans";
import util from "@/api/util";

const mainPage = useTemplateRef("mainPage");
const workLogList = ref(null);

let userId = ""//注意，userId不是ref对象

let host = inject("domain");
let masterCompetition = null;

onMounted(async () => {
    if (util.giveStorgeMessage("userId")) {
        userId = util.giveStorgeCry("userId");
    }
    await loadWorkLogsByUser();
});

const loadWorkLogsByUser = async ()=>{
    // console.log(userId);
    if (!userId) {
        return;
    }
    let res = await workRest.qyWorkLog8Work({userId:userId},null);
    if (res.status=="OK") {
        workLogList.value = res.data;
        // console.log(userId,workLogList.value);
    }
}
</script>

<style scoped lang="scss">

</style>
