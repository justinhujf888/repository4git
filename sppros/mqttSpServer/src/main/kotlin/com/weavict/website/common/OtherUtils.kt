package com.weavict.website.common

import cn.hutool.core.io.resource.ClassPathResource
import cn.hutool.core.util.ClassLoaderUtil
import cn.hutool.setting.dialect.Props
import java.io.File

class OtherUtils {
    companion object {
        private var props: Props? = null

        fun giveTheProps(): Props? {
            try {
                if (props == null) {
                    props = Props()
                    //				props.load(new File(ClassLoaderUtil.getResourceUrl(
//						"/config/global.props").getPath()));
                    props?.load(ClassPathResource("config/global.props").getStream())
                }
                return props
            } catch (e: Exception) {
                return null
            }
        }

        fun givePropsValue(key: String?): String? {
            try {
                return giveTheProps()?.getValue(key) as String?
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                return ""
            }
        }
    }
}