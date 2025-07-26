import { defineConfig } from "vite";
import uni from "@dcloudio/vite-plugin-uni";
// https://vitejs.dev/config/
export default async () => {
  const UnoCSS = (await import("unocss/vite")).default;

  return defineConfig({
    plugins: [uni(), UnoCSS()],
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