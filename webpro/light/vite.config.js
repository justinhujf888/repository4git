import fs from "fs";
import { defineConfig } from "vite";
import uni from "@dcloudio/vite-plugin-uni";
import { resolve } from 'path'
// https://vitejs.dev/config/
export default async () => {
  const UnoCSS = (await import("unocss/vite")).default;

  return defineConfig({
    plugins: [uni(), UnoCSS()],
    resolve: {
      alias: [
        {
          find: '@',
          replacement: resolve(__dirname, 'src'), //配置@ 路径
        },
        // {
        //  find: 'mqtt',
        //  replacement: 'mqtt/dist/mqtt.min.js',
        // },

      ]
    },
    server: {
      // port: 3000,
      strictPort: true,
      open: true,
      https: {
        key: fs.readFileSync('./ssl/localhost+2-key.pem'),
        cert: fs.readFileSync('./ssl/localhost+2.pem'),
      },
      // proxy: {
      //   '/light': {
      //     target: 'http://192.168.1.46:8090/light',
      //     changeOrigin: true,
      //     secure: false, // https接口需要配置
      //      rewrite: (path) => path.replace(/^\/api/, '')  // 重写路径
      //   }
      // }
    }
  });
};

// import { defineConfig } from "vite";
// import Unocss from "unocss/vite";
// import transformerDirective from "@unocss/transformer-directives";

// export default defineConfig({
//   plugins: [
//     vue(),
//     Unocss({
//       transformers: [transformerDirective()],
//     })
//   ],
// });