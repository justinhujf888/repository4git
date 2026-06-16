<template>
    <div class="center mx-10" :class="{'col':flexLayer==0,'grid grid-cols-1 md:grid-cols-1 lg:grid-cols-2 xl:grid-cols-3 gap-3':flexLayer==1}" :_style="{'min-width':flexLayer==1 ? 0.45*layerWidth*3 + 'px' : '100%'}">
        <div v-for="(work,index) of workList" class="mb-5" @click="imagesShow(index)">
            <div ref="cvsDivRef" class="bg-blue-300 w-full">
<!--                :config="{width:flexLayer==1 ? (cvsDivRef[index].offsetWidth/layerWidth)*layerWidth : 1*layerWidth,height:flexLayer==1 ? (cvsDivRef[index].offsetWidth/layerWidth)*layerHeight : 1*layerHeight}" scaleX:compSize(1),scaleY:compSize(1)-->
                <v-stage ref="stageRef"  :config="{width:compSize(stageConfig.width),height:compSize(stageConfig.height)}" v-if="show">
<!--                    :config="{scaleX:flexLayer==1 ? (cvsDivRef[index].offsetWidth/layerWidth) : 1,scaleY:flexLayer==1 ? (cvsDivRef[index].offsetWidth/layerWidth) : 1}"-->
                    <v-layer ref="layerRef">
                        <v-rect :config="{width: compSize(layerWidth),height: compSize(layerHeight),fill: '#000000'}"/>
                        <v-image v-if="work.tempMap.imgObj" :config="getWorkImgConfig(work.tempMap.imgObj)" :_crop="getWorkImgCrop(work.tempMap.imgObj)"/>
                        <v-rect :config="{x:0,y:0,width: compSize(layerWidth)/4,height: compSize(10),fill: '#1e6c86'}"/>
                        <v-rect :config="{x:compSize(layerWidth)/4,y:0,width: compSize(layerWidth)/4,height: compSize(10),fill: '#1d7b41'}"/>
                        <v-rect :config="{x:compSize(layerWidth)/4*2,y:0,width: compSize(layerWidth)/4,height: compSize(10),fill: '#62a718'}"/>
                        <v-rect :config="{x:compSize(layerWidth)/4*3,y:0,width: compSize(layerWidth)/4,height: compSize(10),fill: '#e28e44'}"/>
                        <v-rect :config="{x:compSize(0),y:compSize(10),width: compSize(layerWidth),height: compSize(80),fill: '#122b3d'}"/>
                        <v-image v-if="logoImg" :config="{x:compSize(30),y:compSize(30),width:compSize(100),height: compSize(100) * logoImg.height / logoImg.width,image:logoImg,scaleX:1,scaleY:1}"/>
                        <v-text :config="{x:compSize(150),y:compSize(30),text:siteDatas?.cptInfo?.masterCompetitionInfo.name,fontSize:compSize(35),fill:'#dddddd',fontStyle: 700}"/>
                        <v-text :config="{x:compSize(246),y:compSize(27),text:'|',fontSize:compSize(35),fill:'#59653d'}"/>
                        <v-text ref="cpRef" :config="{x:compSize(270),y:compSize(39),text:'微景观组',fontSize:compSize(18),fill:'#ffffff',letterSpacing: 2}"/>
                        <v-text v-if="cpRef?.[index]" :config="{x:compSize(270) + cpRef[0].getNode().textWidth + compSize(20),y:compSize(37),text:'排名',fontSize:compSize(20),fill:'#bababa',letterSpacing: 1}"/>
                        <v-text v-if="cpRef?.[index]" :config="{x:compSize(270) + cpRef[0].getNode().textWidth + compSize(80),y:compSize(30),text:work.tempMap.sortStr,fontSize:compSize(35),fill:'#ffffff',fontStyle: 700}"/>
                        <v-image v-if="work.tempMap.qcjx" :config="{x:compSize(layerWidth-180),y:compSize(22),image:work.tempMap.qcjx,scaleX:compSize(0.6),scaleY:compSize(0.6)}"/>
                        <!--                    <v-text :config="{x:layerWidth-200,y:22,fontFamily: 'Noto Serif SC',fontStyle: 'bold',text:'全场金奖',fontSize:30,_fill:'#ffffff',width:200,fillLinearGradientStartPoint: { x: 0, y: 0 },fillLinearGradientEndPoint: { x: 200, y: 10 },fillLinearGradientColorStops:[0, '#ff4444',1, '#44ddff']}"/>-->
                        <v-text :config="{x:compSize(30),y:compSize(layerHeight-70),text:work.name,fontSize:compSize(30),fill:'#ffffff',letterSpacing: 2,align:'right'}"/>
                        <v-text :config="{x:compSize(layerWidth-120),y:compSize(layerHeight-70),text:'胡纪锋',fontSize:compSize(30),fill:'#ffffff',letterSpacing: 2,align:'right'}"/>
<!--                        <v-image v-if="work.tempMap?.imgPath" :config="{x:0,y:0,image:work.tempMap.imgPath}"/>-->
                    </v-layer>
                </v-stage>
            </div>
            <img :src="work.tempMap?.imgPath"/>
            <div class="mt-2 mb-5 col gap-y-2">
                <span class="text-xl font-semibold">{{work.tempMap.sortStr}}</span>
                <span class="text-xl font-semibold">胡纪锋</span>
            </div>
        </div>
        <priviewImage ref="refPriviewImage" v-if="workList?.length>0" :files="workList" :shiShowImgGrid="false" _class="hidden" @activeIndexChange="activeIndexChange"/>
    </div>
</template>

<script setup>
import { ref, onMounted, inject, useTemplateRef, watch } from 'vue';
import {useImage} from "vue-konva";
import oss from "@/api/oss";
import useGlobal from "@/api/hooks/useGlobal";
import { Beans } from '@/api/dbs/beans';
import lodash from 'lodash-es';
import priviewImage from "@/components/my/priviewImage.vue";

let footDatas = null;
let siteDatas = null;

const workList = ref([]);
const logoImgUrl = ref("");
const [logoImg] = useImage(logoImgUrl,"anonymous");

const layerWidth = 1000;
const layerHeight = 600;
const layerMx = 200;
const layerPaddingTop = 90;

const refPriviewImage = useTemplateRef("refPriviewImage");
const stageRef = useTemplateRef("stageRef");
const layerRef = useTemplateRef("layerRef");
const cpRef = useTemplateRef("cpRef");
const cvsDivRef = useTemplateRef("cvsDivRef");
const show = ref(false);
const flexLayer = ref(1);
const stageConfig = ref({});
const imgStatusList = ref([]);

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
    stageConfig.value = {width:layerWidth,height:layerHeight};
    let ary = [
        {id:"0",path:"cpt/cpt.arkydesign.cn/work/2026/13288888888/88881778664330607HMSdB8wE/1778664330607PtMcjbFZ_版纳森林02.jpg",name:"版纳森林"},
        {id:"1",path:"cpt/cpt.arkydesign.cn/work/2026/13288888888/888817790852888868FNiYaWS/1779085288886hKpdt6Kk_06 - 细节孔雀藓.JPG",name:"细节孔雀藓"},
        {id:"2",path:"cpt/cpt.arkydesign.cn/work/2026/13288888888/888817790852888868FNiYaWS/17790852888867hNJTimd_08---树林丁达尔视频截图.jpg",name:"树林丁达尔"},
        {id:"3",path:"cpt/cpt.arkydesign.cn/work/2026/13288888888/888817790852888868FNiYaWS/1779085288886r4cNTb6f_02-右侧45度.JPG",name:"右侧45度"},
        {id:"4",path:"cpt/cpt.arkydesign.cn/work/2026/13288888888/88881778664330607HMSdB8wE/1778664330607XYnrTAPj_版纳森林07.jpg",name:"版纳森林07"},
        {id:"5",path:"cpt/cpt.arkydesign.cn/work/2026/13288888888/888817790852888868FNiYaWS/1779085288886fJB5tzaH_02-右侧45度.JPG",name:"右侧45度2"}];
    lodash.forEach(ary,(v,i)=>{
        let w = Beans.work();
        w.id = v.id;
        w.tempMap = {workImgUrl:v.path,sort:i,sortStr:lodash.padStart(i+1,3,"0"),imgPath:"",imgObj:null,imgStatus:null};
        w.name = v.name;
        workList.value.push(w);
    });

    let index = 0;
    for(let w of workList.value) {
        [w.tempMap.imgObj,imgStatusList.value[index]] = useImage(await oss.buildPathAsync(w.tempMap.workImgUrl,true,null),"anonymous");
        if (w.tempMap.sort==0) {
            [w.tempMap.qcjx] = useImage("/images/qcjj.png","anonymous");
        } else if (w.tempMap.sort==1) {
            [w.tempMap.qcjx] = useImage("/images/qcyj.png","anonymous");
        } else if (w.tempMap.sort==2) {
            [w.tempMap.qcjx] = useImage("/images/qctj.png","anonymous");
        }
        index+=1;
    }

    await loadFont();
    lodash.forEach(workList.value,(w,i)=>{
        watch(imgStatusList.value[i],(v)=>{
            if (v === 'loaded') {
                console.log(v);
                stageRef.value[i].getNode().batchDraw();
                w.tempMap.imgPath = stageRef.value[i].getNode().toDataURL({pixelRatio: 2});
            }
        });
    });

    setTimeout(()=>{
        // while (true) {
        //     let status = lodash.findIndex(workList.value,(o)=>{return o.tempMap.imgStatus!="loaded"});
        //     console.log(status);
        //     if (lodash.findIndex(workList.value,(o)=>{return o.tempMap.imgStatus!="loaded"})>-1) {
        //         break;
        //     }
        // }

        // lodash.forEach(workList.value,(v,i)=>{
        //     v.tempMap.imgPath = stageRef.value[i].getNode().toDataURL({pixelRatio: 2});
        // });

    },1000);


    // console.log(stageRef.value[0].getNode().toDataURL({pixelRatio: 2}));
    // console.log(cpRef.value[0].getNode().textWidth);
    // setTimeout(()=>{
    //     show.value = true;
    // },5000);
});

const getWorkImgConfig = (imgObj)=>{
    // console.log(imgObj.src,imgObj.width,imgObj.height);
    let config = {};
    if (imgObj.width > imgObj.height) {
        // 横拍
        config = {x:(compSize(layerWidth)-(compSize(layerWidth)-compSize(layerMx))) / 2,y:compSize(layerPaddingTop),width:compSize(layerWidth-layerMx),height:(compSize(layerWidth-layerMx)) * (imgObj.height / imgObj.width),image:imgObj,scaleX:1,scaleY:1};
    } else {
        // 竖拍
        config = {x:(compSize(layerWidth)-(compSize(layerHeight)-compSize(layerPaddingTop)) * (imgObj.width / imgObj.height)) / 2,y:compSize(layerPaddingTop),width:(compSize(layerHeight-layerPaddingTop)) * (imgObj.width / imgObj.height),height:compSize(layerHeight-layerPaddingTop),image:imgObj,scaleX:1,scaleY:1};
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

const loadFont = ()=>{
    // const link = document.createElement('link');
    // link.href = 'https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;700&display=swap';
    // link.rel = 'stylesheet';
    // document.head.appendChild(link);

    // 等待字体加载完成
    return document.fonts.ready.then(() => {
        show.value = true;
    });
};

const imagesShow = (index)=>{
    // stageRef.value[index].getNode().width(layerWidth);
    // stageRef.value[index].getNode().height(layerHeight);
    // stageRef.value[index].getNode().batchDraw();
    workList.value[index].tempMap.imgPath = stageRef.value[index].getNode().toDataURL({pixelRatio: 2});
    refPriviewImage.value.imagesShow(workList.value,index);
};

const activeIndexChange = (index)=>{
    workList.value[index].tempMap.imgPath = stageRef.value[index].getNode().toDataURL({pixelRatio: 2});
};

const compSize = (v)=>{
    // let a = flexLayer.value==1 ? (cvsDivRef.value?.[0].offsetWidth/layerWidth)*v : v;
    // console.log(a);
    return flexLayer.value==1 ? (cvsDivRef.value?.[0].offsetWidth/layerWidth)*v : v;
};
</script>

<style scoped lang="scss">

</style>

