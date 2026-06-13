<template>
    <div class="col center mx-10">
        <v-stage
            ref="stage"
            :config="{width:1000,height:600}" v-if="show"
        >
            <v-layer ref="layer">
                <v-rect :config="{width: 1000,height: 600,fill: '#000000'}"/>
                <v-image :config="{x:100,y:0,width:800,height:600,image:workImg,scaleX:1,scaleY:1}" :crop="{x:0,y:300,width:800,height:600}"/>
                <v-rect :config="{x:0,y:0,width: 250,height: 10,fill: '#345634'}"/>
                <v-rect :config="{x:250,y:0,width: 250,height: 10,fill: '#456346'}"/>
                <v-rect :config="{x:500,y:0,width: 250,height: 10,fill: '#854342'}"/>
                <v-rect :config="{x:750,y:0,width: 250,height: 10,fill: '#243532'}"/>
                <v-rect :config="{x:0,y:10,width: 1000,height: 80,fill: '#856786'}"/>
                <v-image :config="{x:30,y:30,width:100,image:logoImg,scaleX:1,scaleY:1}"/>
            </v-layer>
        </v-stage>
        <span>asdfasdf</span>
    </div>
</template>

<script setup>
import {ref, onMounted, inject} from 'vue';
import {useImage} from "vue-konva";
import oss from "@/api/oss";
import useGlobal from "@/api/hooks/useGlobal";

const show = ref(false);

let footDatas = null;
let [workImg] = [];
let [logoImg,logoImgStatus] = [];

(async ()=>{
    footDatas = await useGlobal.pageSetupDatas("foot");
    [workImg] = useImage(await oss.buildPathAsync("cpt/cpt.arkydesign.cn/work/2026/13288888888/88881778664330607HMSdB8wE/1778664330607PtMcjbFZ_版纳森林02.jpg",true,null));
    [logoImg,logoImgStatus] = useImage(await oss.buildPathAsync(footDatas.boundArea.setup.mImg.value.img));
    console.log(logoImg);
    show.value = true;
})();

onMounted(async () => {

});
</script>

<style scoped lang="scss">

</style>