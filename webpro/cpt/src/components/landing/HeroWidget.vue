<template>
    <div
        id="hero"
        class="flex flex-col pt-6 px-6 lg:px-20 overflow-hidden"
        style="background: linear-gradient(0deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.2)), radial-gradient(77.36% 256.97% at 77.36% 57.52%, rgb(238, 239, 175) 0%, rgb(195, 227, 250) 100%); clip-path: ellipse(150% 87% at 93% 13%)"
    >
        <div class="mx-6 md:mx-20 mt-0 md:mt-6">
            <h3 class="font-bold text-gray-900 leading-tight text-center"><span class="font-light block">每件作品都是一首生命的赞歌</span></h3>
            <h1 class="font-normal leading-normal md:mt-4 text-gray-700 text-center">{{Config.appName}} 2025</h1>
            <h5 class="text-center">2026.4.6 — 2026.6.02</h5>
            <Button label="Get Started" as="router-link" to="/manage/index" rounded clas s="!text-xl mt-8 !px-4"></Button>
        </div>
        <div class="flex justify-center md:justify-end">
            <img src="/demo/images/landing/screen-1.png" alt="Hero Image" class="w-9/12 md:w-auto" />
        </div>
    </div>
</template>

<script setup>
import { Config } from "@/api/config";
</script>
