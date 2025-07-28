import fs from "fs";
import { defineConfig } from "vite";
import uni from "@dcloudio/vite-plugin-uni";
// https://vitejs.dev/config/
export default async () => {
  const UnoCSS = (await import("unocss/vite")).default;

  return defineConfig({
    plugins: [uni(), UnoCSS()],
    server: {
      https: {
        key: fs.readFileSync('./ssl/localhost-key.pem'),
        cert: fs.readFileSync('./ssl/localhost.pem'),
        strictPort: true,
        port: 3000,
        open: true
      }
    },
    // proxy: {
    //   '/light': {
    //     target: 'http://192.168.1.46:8090/light',
    //     changeOrigin: true,
    //     secure: false // https接口需要配置
    //   }
    // }
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