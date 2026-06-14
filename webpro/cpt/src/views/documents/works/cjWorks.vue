<template>
    <div class="col center mx-10">
        <div v-for="(work,index) of workList" class="mb-5">
            <v-stage ref="stage" :config="{width:layerWidth,height:layerHeight}" _v-if="show">
                <v-layer ref="layer" :config="{scaleX:1,scaleY:1}">
                    <v-rect :config="{width: layerWidth,height: layerHeight,fill: '#000000'}"/>
                    <v-image v-if="work.tempMap.imgObj" :config="getWorkImgConfig(work.tempMap.imgObj)" :_crop="getWorkImgCrop(work.tempMap.imgObj)"/>
                    <v-rect :config="{x:0,y:0,width: layerWidth/4,height: 10,fill: '#1e6c86'}"/>
                    <v-rect :config="{x:layerWidth/4,y:0,width: layerWidth/4,height: 10,fill: '#1d7b41'}"/>
                    <v-rect :config="{x:layerWidth/4*2,y:0,width: layerWidth/4,height: 10,fill: '#62a718'}"/>
                    <v-rect :config="{x:layerWidth/4*3,y:0,width: layerWidth/4,height: 10,fill: '#e28e44'}"/>
                    <v-rect :config="{x:0,y:10,width: layerWidth,height: 80,fill: '#122b3d'}"/>
                    <v-image v-if="logoImg" :config="{x:30,y:30,width:100,height: 100 * logoImg.height / logoImg.width,image:logoImg,scaleX:1,scaleY:1}"/>
                    <v-text :config="{x:150,y:30,text:siteDatas?.cptInfo?.masterCompetitionInfo.name,fontSize:35,fill:'#dddddd'}"/>
                    <v-text :config="{x:246,y:27,text:'|',fontSize:35,fill:'#59653d'}"/>
                    <v-text ref="cpRef" :config="{x:270,y:37,text:'微景观组',fontSize:20,fill:'#ffffff'}"/>
                    <v-text v-if="cpRef?.[index]" :config="{x:270 + cpRef[0].getNode().textWidth + 20,y:37,text:'排名',fontSize:20,fill:'#bababa'}"/>
                    <v-text v-if="cpRef?.[index]" :config="{x:270 + cpRef[0].getNode().textWidth + 80,y:30,text:'001',fontSize:35,fill:'#ffffff'}"/>
                    <v-image v-if="work.tempMap.qcjx" :config="{x:layerWidth-200,y:22,image:work.tempMap.qcjx,scaleX:0.6,scaleY:0.6}"/>
                </v-layer>
            </v-stage>
        </div>
        <span>{{cpRef}}</span>
    </div>
</template>

<script setup>
import { ref, onMounted, inject, useTemplateRef } from 'vue';
import {useImage} from "vue-konva";
import oss from "@/api/oss";
import useGlobal from "@/api/hooks/useGlobal";
import { Beans } from '@/api/dbs/beans';
import lodash from 'lodash-es';

let footDatas = null;
let siteDatas = null;

const workList = ref([]);
const logoImgUrl = ref("");
const [logoImg] = useImage(logoImgUrl,"anonymous");

const layerWidth = 1000;
const layerHeight = 600;
const layerMx = 200;
const layerPaddingTop = 90;

const cpRef = useTemplateRef("cpRef");

(async ()=>{
    footDatas = await useGlobal.pageSetupDatas("foot");
    siteDatas = await useGlobal.siteDatas();
    // console.log(siteDatas.cptInfo.masterCompetitionInfo.name);
    // let asWorkImg = useImage(await oss.buildPathAsync("cpt/cpt.arkydesign.cn/work/2026/13288888888/88881778664330607HMSdB8wE/1778664330607PtMcjbFZ_版纳森林02.jpg",true,null));
    // let asLogoImg = useImage(await oss.buildPathAsync(footDatas.boundArea.setup.mImg.value.img));
    // setTimeout(()=>{
    //     workImg.value = asWorkImg[0].value;
    //     logoImg.value = asLogoImg[0].value;
    // },3000);
    logoImgUrl.value = await oss.buildPathAsync(footDatas.boundArea.setup.mImg.value.img,true,null);
})();

onMounted(async () => {
    let ary = [
        "cpt/cpt.arkydesign.cn/work/2026/13288888888/88881778664330607HMSdB8wE/1778664330607PtMcjbFZ_版纳森林02.jpg","cpt/cpt.arkydesign.cn/work/2026/13288888888/888817790852888868FNiYaWS/1779085288886hKpdt6Kk_06 - 细节孔雀藓.JPG",
        "cpt/cpt.arkydesign.cn/work/2026/13288888888/888817790852888868FNiYaWS/17790852888867hNJTimd_08---树林丁达尔视频截图.jpg",
        "cpt/cpt.arkydesign.cn/work/2026/13288888888/888817790852888868FNiYaWS/1779085288886r4cNTb6f_02-右侧45度.JPG",
        "cpt/cpt.arkydesign.cn/work/2026/13288888888/88881778664330607HMSdB8wE/1778664330607XYnrTAPj_版纳森林07.jpg",
        "cpt/cpt.arkydesign.cn/work/2026/13288888888/888817790852888868FNiYaWS/1779085288886fJB5tzaH_02-右侧45度.JPG"];
    lodash.forEach(ary,(v,i)=>{
        let w = Beans.work();
        w.id = `${i}`;
        w.tempMap = {workImgUrl:v};
        workList.value.push(w);
    });

    for(let w of workList.value) {
        [w.tempMap.imgObj] = useImage(await oss.buildPathAsync(w.tempMap.workImgUrl,true,null),"anonymous");
        [w.tempMap.qcjx] = useImage("/images/qcjj.png","anonymous");
    }

    // console.log(cpRef.value[0].getNode().textWidth);
});

const getWorkImgConfig = (imgObj)=>{
    // console.log(imgObj.src,imgObj.width,imgObj.height);
    let config = {};
    if (imgObj.width > imgObj.height) {
        // 横拍
        config = {x:(layerWidth-(layerWidth-layerMx)) / 2,y:layerPaddingTop,width:layerWidth-layerMx,height:(layerWidth-layerMx) * (imgObj.height / imgObj.width),image:imgObj,scaleX:1,scaleY:1};
    } else {
        // 竖拍
        config = {x:(layerWidth-(layerHeight-layerPaddingTop) * (imgObj.width / imgObj.height)) / 2,y:layerPaddingTop,width:(layerHeight-layerPaddingTop) * (imgObj.width / imgObj.height),height:layerHeight-layerPaddingTop,image:imgObj,scaleX:1,scaleY:1};
    }
    return config;
};

const getWorkImgCrop = (imgObj)=>{
    let crop = {};
    if (imgObj.width > imgObj.height) {
        // 横拍
        crop = {x:0,y:0,width:layerWidth-layerMx,height:layerHeight-layerPaddingTop};
    } else {
        // 竖拍
        crop = {x:0,y:imgObj.height/4,width:layerWidth-layerMx,height:layerHeight-layerPaddingTop};
    }
    return crop;
};

const getQcJiangImg = (imgUrl)=>{
    return useImage(imgUrl)[0].value;
};
</script>

<style scoped lang="scss">

</style>
