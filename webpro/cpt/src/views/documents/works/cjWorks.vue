<template>
    <div class="col center mx-10">
        <div v-for="work of workList">
            <v-stage ref="stage" :config="{width:1000,height:600}" _v-if="show">
                <v-layer ref="layer">
                    <v-rect :config="{width: 1000,height: 600,fill: '#000000'}"/>
                    <v-image v-if="work.tempMap.imgObj" :config="{x:100,y:0,width:800,height:600,image:work.tempMap.imgObj,scaleX:1,scaleY:1}" :crop="{x:0,y:0,width:800,height:600}"/>
                    <v-rect :config="{x:0,y:0,width: 250,height: 10,fill: '#345634'}"/>
                    <v-rect :config="{x:250,y:0,width: 250,height: 10,fill: '#456346'}"/>
                    <v-rect :config="{x:500,y:0,width: 250,height: 10,fill: '#854342'}"/>
                    <v-rect :config="{x:750,y:0,width: 250,height: 10,fill: '#243532'}"/>
                    <v-rect :config="{x:0,y:10,width: 1000,height: 80,fill: '#856786'}"/>
                    <v-image v-if="logoImg" :config="{x:30,y:30,width:100,height: 100 * logoImg.height / logoImg.width,image:logoImg,scaleX:1,scaleY:1}"/>
                </v-layer>
            </v-stage>
        </div>
        <span>asdfasdf</span>
    </div>
</template>

<script setup>
import { ref, onMounted, inject, useTemplateRef } from 'vue';
import {useImage} from "vue-konva";
import oss from "@/api/oss";
import useGlobal from "@/api/hooks/useGlobal";
import { Beans } from '@/api/dbs/beans';

let footDatas = null;

const workList = ref([]);
const logoImgUrl = ref("");
const [logoImg] = useImage(logoImgUrl,"anonymous");

(async ()=>{
    footDatas = await useGlobal.pageSetupDatas("foot");
    // let asWorkImg = useImage(await oss.buildPathAsync("cpt/cpt.arkydesign.cn/work/2026/13288888888/88881778664330607HMSdB8wE/1778664330607PtMcjbFZ_版纳森林02.jpg",true,null));
    // let asLogoImg = useImage(await oss.buildPathAsync(footDatas.boundArea.setup.mImg.value.img));
    // setTimeout(()=>{
    //     workImg.value = asWorkImg[0].value;
    //     logoImg.value = asLogoImg[0].value;
    // },3000);
    logoImgUrl.value = await oss.buildPathAsync(footDatas.boundArea.setup.mImg.value.img,true,null);
})();

onMounted(async () => {
    let w1 = Beans.work();
    w1.id = "0";
    w1.tempMap = {workImgUrl:"cpt/cpt.arkydesign.cn/work/2026/13288888888/88881778664330607HMSdB8wE/1778664330607PtMcjbFZ_版纳森林02.jpg"};
    workList.value.push(w1);

    let w2 = Beans.work();
    w2.id = "1";
    w2.tempMap = {workImgUrl:"cpt/cpt.arkydesign.cn/work/2026/13288888888/888817790852888868FNiYaWS/1779085288886hKpdt6Kk_06 - 细节孔雀藓.JPG"};
    workList.value.push(w2);

    for(let w of workList.value) {
        [w.tempMap.imgObj] = useImage(await oss.buildPathAsync(w.tempMap.workImgUrl,true,null),"anonymous");
    }
});
</script>

<style scoped lang="scss">

</style>
