package com.weavict.eduweb

import cn.hutool.core.util.CharsetUtil
import cn.hutool.core.util.EscapeUtil
import cn.hutool.core.util.HexUtil
import cn.hutool.http.HttpUtil
import com.agentsflex.core.document.Document
import com.agentsflex.core.llm.ChatContext
import com.agentsflex.core.llm.ChatOptions
import com.agentsflex.core.llm.Llm
import com.agentsflex.core.llm.LlmConfig
import com.agentsflex.core.llm.StreamResponseListener
import com.agentsflex.core.llm.embedding.EmbeddingOptions
import com.agentsflex.core.llm.functions.annotation.FunctionDef
import com.agentsflex.core.llm.functions.annotation.FunctionParam
import com.agentsflex.core.llm.response.AiMessageResponse
import com.agentsflex.core.prompt.FunctionPrompt
import com.agentsflex.core.prompt.Prompt
import com.agentsflex.core.store.VectorData
import com.agentsflex.llm.chatglm.ChatglmLlm
import com.agentsflex.llm.chatglm.ChatglmLlmConfig
import com.agentsflex.llm.deepseek.DeepseekConfig
import com.agentsflex.llm.deepseek.DeepseekLlm
import com.weavict.edu.app.SpApplication
import com.weavict.edu.entity.StudentStudyReport
import com.weavict.website.common.OtherUtils
import org.junit.jupiter.api.Test
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = EduwebApplicationTests.class)
class EduwebApplicationTests
{

    static void main(String[] args)
    {

    }

    @Test
    void contextLoads()
    {
    }

    @Test
    void test()
    {
        println EscapeUtil.unescape("\u4eba\u7c7b\u7684\u52c7\u6c14\u548c\u575a\u6bc5\u5fc5\u5c06\u88ab\u954c\u523b\u5728\u661f\u7a7a\u4e4b\u4e0b\u3002");
	    println HexUtil.encodeHexStr(17.toString(), CharsetUtil.CHARSET_UTF_8);
        println Character.toString((char)Integer.parseInt("17",16));
        println Integer.toHexString((0x01 + 0x0B + 0x1E) ^ 0xFF).toUpperCase();
    }

    @Test
    void testDeepseek()
    {
        LlmConfig llmConfig = new DeepseekConfig();
        llmConfig.model = "deepseek-chat";//deepseek-chat deepseek-reasoner glm-4
        llmConfig.apiKey = OtherUtils.givePropsValue("deepseekAPIKey");//"d7b0f5d27c304494aa38e20bee0e0b1f.s0gpBN3dqDeKip63"
        Llm llm = new DeepseekLlm(llmConfig);

        FunctionPrompt prompt = new FunctionPrompt("我叫Justin，001", FunctionUtil.class);
        AiMessageResponse response = llm.chat(prompt);
        println response.callFunctions();

//        llm.chatStream(prompt,new StreamResponseListener() {
//            void onMessage(ChatContext chatContext, AiMessageResponse aiMessageResponse)
//            {
//                print aiMessageResponse.callFunctions();
//            }
//        })
//
//
//        while (true)
//        {
//
//        }

//        llm.chatStream("""生成一个客户服务器维护欠费的页面，内容：客户ID：BCR047765；客户名称：爱丽丝；未缴维护费用1454.00元，未缴月份：2025-01、2025-02。提示客户请尽快缴款，缴款后服务器会在24小时内自动开通。要求页面符合科技公司风格，醒目，美观。""",new StreamResponseListener() {
//            @Override
//            void onMessage(ChatContext chatContext, AiMessageResponse aiMessageResponse)
//            {
//                print aiMessageResponse.getMessage().content;
//            }
//        });

//        println llm.chat("""生成一个客户服务器维护欠费的html页面，内容：客户ID：BCR047765；客户名称：爱丽丝；未缴维护费用1454.00元，未缴月份：2025-01、2025-02。提示客户请尽快缴款，缴款后服务器会在24小时内自动开通。要求页面符合科技公司风格，醒目，美观。""");
    }
}

class FunctionUtil
{
    @FunctionDef(name = "get_the_weather_info", description = "get the weather info")
    static String getWeatherInfo(@FunctionParam(name = "city", description = "the city name") String name)
    {
        //在这里，我们应该通过第三方接口调用 api 信息
        return name + "的天气是阴转多云。 ";
    }

    @FunctionDef(name = "get_the_sals_info", description = "get the sals info")
    static String getSalsInfo(@FunctionParam(name = "productName", description = "the product name") String name)
    {
        //在这里，我们应该通过第三方接口调用 api 信息
        return name + "的销售额度一百万美金。 ";
    }

    @FunctionDef(name = "ticketProcess", description = "ticket process")
    static Map ticketProcess(@FunctionParam(name = "name", description = "the human name") String name,@FunctionParam(name = "id", description = "the human id") String id)
    {
        //在这里，我们应该通过第三方接口调用 api 信息
        return ["id":id,"name":name];
    }
}