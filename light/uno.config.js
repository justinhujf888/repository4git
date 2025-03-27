// import { defineConfig } from "unocss";
import { defineConfig,transformerDirectives,transformerVariantGroup,transformerClass,transformerCompileClass} from 'unocss'
import { presetUni } from "@uni-helper/unocss-preset-uni";
// import transformerDirectives from '@unocss/transformer-directives'

const include = [/\.wxml$/]

export default defineConfig({
  presets: [presetUni()],
  transformers: [
      transformerDirectives(),
	  transformerVariantGroup(),
	  transformerClass({
	        include,
	        classTags: true
	      }),
	  transformerCompileClass(),
    ],
});
