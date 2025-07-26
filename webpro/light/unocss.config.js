import presetWeapp from 'unocss-preset-weapp'

import { transformerClass, transformerAttributify } from 'unocss-preset-weapp/transformer';

export default ({
    presets: [
        presetWeapp({
			whRpx: false,
		})
    ],
    transformers: [
        transformerAttributify(),
        transformerClass()
    ],
    shortcuts: [
        {
			start: 'flex justify-start items-start',
			center: 'flex items-center justify-center',
			end: 'flex items-end justify-end',
			between: 'flex justify-between',
			row: 'flex flex-row',
			col: 'flex flex-col',
			hwcenter: 'absolute left-1/2 top-1/2 translate-x-[-50%] translate-y-[-50%]',
			wcenter: 'absolute left-1/2 translate-x-[-50%]'
		}
    ]
    
})
