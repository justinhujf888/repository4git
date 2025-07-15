 import { fileURLToPath, URL } from 'node:url';

import { PrimeVueResolver } from '@primevue/auto-import-resolver';
import vue from '@vitejs/plugin-vue';
import Components from 'unplugin-vue-components/vite';
import { defineConfig } from 'vite';
import requireTransform from 'vite-plugin-require-transform';

// https://vitejs.dev/config/
export default defineConfig({
    base:"/",
    // optimizeDeps: {
    //     noDiscovery: true,
    //     optimizeDeps: {
    //         include: ['lodash']
    //     }
    // },
    plugins: [
        vue(),
        Components({
            resolvers: [PrimeVueResolver()]
        }),
        requireTransform({
            fileRegex: /\.ts$|\.tsx$|\.vue$|\.js$/
        })
    ],
    resolve: {
        alias: {
            '@': fileURLToPath(new URL('./src', import.meta.url)),
            'ali-oss': 'ali-oss/dist/aliyun-oss-sdk.js'
        }
    }
});
