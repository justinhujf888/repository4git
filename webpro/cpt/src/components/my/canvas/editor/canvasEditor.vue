<template>
    <div class="canvas-editor-wrap">
        <!-- 工具栏 -->
        <div class="toolbar">
            <button @click="addRect">矩形</button>
            <button @click="addCircle">圆形</button>
            <button @click="addText">文字</button>
            <button @click="uploadLocalImg">上传图片</button>
            <button @click="deleteSelected">删除选中</button>
            <button @click="exportCanvas">导出画布</button>
        </div>

        <!-- 画布容器 -->
        <div class="stage-box">
            <v-stage
                ref="stageRef"
                :config="stageConfig"
                @click="stageClick"
            >
                <v-layer ref="mainLayerRef" />
            </v-stage>
        </div>
    </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import Konva from 'konva'
import { preloadImg, waitAllImageReady } from '@/api/utils/konvaUtil'

// 节点Ref
const stageRef = ref(null)
const mainLayerRef = ref(null)
let transformer = null
let selectedNode = null

// 画布尺寸配置
const stageConfig = {
    width: 1000,
    height: 700,
    background: '#f6f7f9'
}

// 获取原生实例
const getStage = () => stageRef.value.getNode()
const getLayer = () => mainLayerRef.value.getNode()

// 清除选中框
function clearTransformer() {
    if (transformer) {
        transformer.destroy()
        transformer = null
    }
    selectedNode = null
}

// 创建变形器（缩放/旋转）
function createTransformer(node) {
    clearTransformer()
    selectedNode = node
    transformer = new Konva.Transformer({
        node: node,
        boundBoxFunc: (oldBox, newBox) => {
            // 限制最小尺寸
            newBox.width = Math.max(10, newBox.width)
            newBox.height = Math.max(10, newBox.height)
            return newBox
        }
    })
    getLayer().add(transformer)
    getStage().batchDraw()
}

// 画布点击事件
function stageClick(e) {
    const target = e.target
    // 点击空白区域清空选中
    if (target === getStage() || target === getLayer()) {
        clearTransformer()
        getStage().batchDraw()
        return
    }
    // 点击图形选中
    createTransformer(target)
}

// 添加矩形
function addRect() {
    const rect = new Konva.Rect({
        x: 80,
        y: 80,
        width: 160,
        height: 100,
        fill: '#409eff',
        draggable: true
    })
    getLayer().add(rect)
    getStage().batchDraw()
}

// 添加圆形
function addCircle() {
    const circle = new Konva.Circle({
        x: 120,
        y: 120,
        radius: 60,
        fill: '#00b42a',
        draggable: true
    })
    getLayer().add(circle)
    getStage().batchDraw()
}

// 添加文字
function addText() {
    const text = new Konva.Text({
        x: 100,
        y: 100,
        text: '画布文字',
        fontSize: 18,
        fill: '#1d2129',
        draggable: true
    })
    getLayer().add(text)
    getStage().batchDraw()
}

// 上传本地图片
async function uploadLocalImg() {
    const input = document.createElement('input')
    input.type = 'file'
    input.accept = 'image/*'
    input.onchange = async (ev) => {
        const file = ev.target.files[0]
        if (!file) return
        const tempUrl = URL.createObjectURL(file)
        const imgDom = await preloadImg(tempUrl)

        const imgNode = new Konva.Image({
            x: 150,
            y: 150,
            image: imgDom,
            width: imgDom.width / 2,
            height: imgDom.height / 2,
            draggable: true
        })
        getLayer().add(imgNode)
        getStage().batchDraw()
        URL.revokeObjectURL(tempUrl)
    }
    input.click()
}

// 删除选中元素
function deleteSelected() {
    if (!selectedNode) return
    selectedNode.destroy()
    clearTransformer()
    getStage().batchDraw()
}

// 导出画布图片
async function exportCanvas() {
    const stage = getStage()
    // 等待全部图片加载完成
    await waitAllImageReady(stage)
    const base64 = stage.toDataURL({
        pixelRatio: window.devicePixelRatio,
        quality: 1
    })
    // 下载
    const a = document.createElement('a')
    a.href = base64
    a.download = 'canvas-export.png'
    a.click()
}
</script>

<style lang="scss" scoped>
.canvas-editor-wrap {
    width: 100%;
    display: flex;
    flex-direction: column;
}
.toolbar {
    padding: 12px;
    background: #fff;
    border-bottom: 1px solid #e5e6eb;
    button {
        margin-right: 10px;
        padding: 6px 14px;
        border: 1px solid #dcdfe6;
        border-radius: 4px;
        background: #fff;
        cursor: pointer;
        &:hover {
            background: #f2f3f5;
        }
    }
}
.stage-box {
    padding: 20px;
    background: #e8eaed;
    flex: 1;
}
</style>
