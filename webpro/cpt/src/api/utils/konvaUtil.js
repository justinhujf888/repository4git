/** 预加载图片 */
export function preloadImg(src) {
    return new Promise((resolve, reject) => {
        const img = new Image()
        img.crossOrigin = 'anonymous'
        img.src = src
        img.onload = () => resolve(img)
        img.onerror = reject
    })
}

/** 等待画布所有图片加载完成 */
export async function waitAllImageReady(stage) {
    const allImages = []
    stage.getLayers().forEach(layer => {
        layer.find('Image').forEach(img => allImages.push(img))
    })
    await Promise.all(allImages.map(node => new Promise(res => {
        const dom = node.image()
        dom.complete ? res(1) : dom.onload = () => res(1)
    })))
    stage.batchDraw()
}

/** base64 转 blob 临时地址 */
export function base64ToBlobUrl(base64Str) {
    let base64 = base64Str
    let mime = 'image/png'
    if (base64Str.includes(';base64,')) {
        const arr = base64Str.split(';base64,')
        mime = arr[0].replace('data:', '')
        base64 = arr[1]
    }
    const byteStr = atob(base64)
    const len = byteStr.length
    const u8Arr = new Uint8Array(len)
    for (let i = 0; i < len; i++) {
        u8Arr[i] = byteStr.charCodeAt(i)
    }
    const blob = new Blob([u8Arr], { type: mime })
    return URL.createObjectURL(blob)
}
