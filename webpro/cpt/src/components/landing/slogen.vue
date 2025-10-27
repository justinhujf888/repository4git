<template>
    <div class="px-6 lg:px-20 mt-8 mx-0 lg:mx-20 py-20">
        <div class="grid grid-cols-12 gap-4 justify-center">
            <div
                class="col-span-12 p-2 md:p-20 bg-opacity-10"
                _style="border-radius: 20px; background: linear-gradient(0deg, rgba(255, 255, 255, 0.6), rgba(255, 255, 255, 0.6)), radial-gradient(77.36% 256.97% at 77.36% 57.52%, #efe1af 0%, #c3dcfa 100%)"
            >
                <div class="flex flex-col justify-center items-center text-center px-4 py-4 md:py-0 font-semibold">
                    <div class="text-white mb-2 text-3xl mix-blend-difference">汇聚全球技艺与智慧的国际生态缸造景大赛</div>
                    <p class="text-white mix-blend-difference sm:line-height-2 md:line-height-4 text-xl mt-6" style="max-width: 800px">
                        通过生态造景这个独特的载体，以艺术的手段,呈现世界各地的生态表现，
                        让每一件作品都成为跨越语言的生命赞歌
                    </p>
                    <a class="text-gray-600 text-2xl text-white mix-blend-difference mt-16">关于{{Config.appName}}</a>
                    <!--                    <img src="/demo/images/landing/peak-logo.svg" class="mt-6" alt="Company logo" />-->
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { Config } from "@/api/config";
</script>
