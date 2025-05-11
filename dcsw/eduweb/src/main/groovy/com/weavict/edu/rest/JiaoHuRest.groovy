package com.weavict.edu.rest

import cn.hutool.core.codec.Base64
import cn.hutool.core.date.DateUtil
import cn.hutool.core.io.FileUtil
import cn.hutool.core.net.url.UrlBuilder
import cn.hutool.core.util.EscapeUtil
import cn.hutool.core.util.ObjectUtil
import cn.hutool.http.HttpUtil
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.weavict.common.util.MathUtil
import com.weavict.edu.entity.Book
import com.weavict.edu.entity.BookSubBook
import com.weavict.edu.entity.BookSubBookPK
import com.weavict.edu.entity.EngEveryDay
import com.weavict.edu.entity.ProductsPrivater
import com.weavict.edu.entity.Word
import com.weavict.edu.module.SourcesService
import com.weavict.website.common.OtherUtils
import groovy.json.JsonSlurper
import jakarta.annotation.Resource
import jakarta.ws.rs.GET
import jodd.datetime.JDateTime
import jodd.http.HttpRequest

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.TransactionDefinition
import org.springframework.web.bind.annotation.RequestBody

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.nio.charset.Charset
import java.text.SimpleDateFormat

/**
 * Created by Justin on 2018/6/10.
 */
@Path("/jiaohu")
class JiaoHuRest extends BaseRest
{
    @Context
    HttpServletRequest request;

    @Autowired
    SourcesService sourcesService;

    static Map baiduAIAccessTokenMap = null;
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/baiduai-accesstoken")
    String baiduAIAccessToken()
    {
        try
        {
            if (baiduAIAccessTokenMap!=null)
            {
                JDateTime jedate = new JDateTime(new Date());
                JDateTime jbdate = jedate.addSecond((baiduAIAccessTokenMap["expires_in"] as int) * -1);
                if (jbdate.daysBetween(jedate)<30)
                {
                    println "use old";
                    return """{"status":"OK","baiduTokenInfo":{"scope":"audio_voice_assistant_get brain_enhanced_asr audio_tts_post public brain_all_scope wise_adapt lebo_resource_base lightservice_public hetu_basic lightcms_map_poi kaidian_kaidian ApsMisTest_Test权限 vis-classify_flower lpq_开放 cop_helloScope ApsMis_fangdi_permission smartapp_snsapi_base iop_autocar oauth_tp_app smartapp_smart_game_openapi oauth_sessionkey smartapp_swanid_verify smartapp_opensource_openapi smartapp_opensource_recapi","session_secret":"","expires_in":${baiduAIAccessTokenMap["expires_in"]},"refresh_token":"${baiduAIAccessTokenMap["refresh_token"]}","session_key":"9mzdA51qWHaU68mkNhf8OyL3iLkdIXYXy7bL1UYzd0nmJhvs9IhCzYTq9NYSJe/aEwjC/ozVhZCK/PHsWhzc4nGK6epyvA==","access_token":"${baiduAIAccessTokenMap["access_token"]}"}}""";
                }
            }
//            HttpUriRequest httpUriRequest = RequestBuilder.post().setUri("https://openapi.baidu.com/oauth/2.0/token").addParameter("grant_type", "client_credentials").addParameter("client_id", OtherUtils.givePropsValue("baiduAI_apiKey")).addParameter("client_secret", OtherUtils.givePropsValue("baiduAI_secretKey")).build();
//            baiduAIAccessTokenMap = LocalHttpClient.executeJsonResult(httpUriRequest, Map.class);
            Gson gson = new Gson();
            baiduAIAccessTokenMap = gson.fromJson(HttpRequest.post("https://openapi.baidu.com/oauth/2.0/token").query(["grant_type":"client_credentials","client_id":OtherUtils.givePropsValue("baiduAI_apiKey"),"client_secret":OtherUtils.givePropsValue("baiduAI_secretKey")]).send().body(),Map);
            ObjectMapper objectMapper = new ObjectMapper();
            println "use new";
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "baiduTokenInfo":({
                         return baiduAIAccessTokenMap;
                     }).call()
                    ]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/test")
    String test(@RequestBody Map<String,Object> query)
    {
//        https://api.dictionaryapi.dev/api/v2/entries/en/hello
//        http://dict.youdao.com/dictvoice?type=0&audio=goods
//        英音：http://dict.youdao.com/dictvoice?type=1&audio=
//        美音：http://dict.youdao.com/dictvoice?type=0&audio=

//        https://dict.youdao.com/jsonapi?q=goods
//        http://dict.youdao.com/suggest?num=1&doctype=json&q=goods
//        http://fanyi.youdao.com/translate?&doctype=json&type=AUTO&i=English-speaking%20world
//        coze 个人令牌 pat_ol3kwbHDzRkDmWus5NHKLN6tA3iMiYSYgvwwuowft1fNH1uTWpa1sMqyoEgmIfGl
//        space 7414885648668557348
//        coze app 1142267229063 25843096557190288457252215252368.app.coze
//                   yNYGOnKWWkN2cebbNwNiL9inqF8DgV8gpthUsOhVJP7gqFiV
//        https://www.coze.cn/store/bot/7412330810495991823?bot_id=true
        Map map = [
                (0 as byte):["type":0 as byte,"name":"每日英语","url":"https://apis.tianapi.com/everyday/index"],
                (1 as byte):["type":1 as byte,"name":"唐诗三百首","url":"https://apis.tianapi.com/poetry/index"],
                (2 as byte):["type":2 as byte,"name":"成语典故","url":"https://apis.tianapi.com/chengyu/index"],
                (3 as byte):["type":3 as byte,"name":"英语格言","url":"https://apis.tianapi.com/enmaxim/index"],
                (4 as byte):["type":4 as byte,"name":"词霸每日一句","url":"http://sentence.iciba.com/index.php?c=dailysentence&m=getdetail"],
        ];
        ObjectMapper objectMapper = new ObjectMapper();
        for(i in 0..100)
        {
            byte type = query.type as byte;
            String txt = null;
            if (type==4 as byte)
            {
                String d = DateUtil.offsetDay(DateUtil.parse("2024-08-10","yyyy-MM-dd"),i).format("yyyy-MM-dd");
                println d;
                txt = HttpUtil.get("${map[type].url}&title=${d}");
            }
            else
            {
                txt = HttpUtil.get("${map[type].url}?key=ac4258437bd6139522a8379fdfe8c383");
            }
            println txt;
            def slurper = new JsonSlurper();
            def obj = slurper.parseText(txt);
            println obj;
            if ((obj.code==200 && obj.msg=="success") || obj.errmsg=="success")
            {
                if (type==1 as byte)
                {
                    for(item in obj.result.list)
                    {
                        EngEveryDay engEveryDay = sourcesService.findObjectById(EngEveryDay.class,item.title);
                        if (engEveryDay==null)
                        {
                            engEveryDay = new EngEveryDay();
                            engEveryDay.id = item.title;
                            engEveryDay.content = item.content;
                            engEveryDay.note = item.intro;
                            engEveryDay.source = item.author;
                            engEveryDay.type = type;
                            engEveryDay.kind = item.kind;
                            sourcesService.updateTheObject(engEveryDay);
                        }
                    }
                }
                else if (type==0 as byte)
                {
                    EngEveryDay engEveryDay = sourcesService.findObjectById(EngEveryDay.class,"tianapi_${obj.result.id}" as String);
                    if (engEveryDay==null)
                    {
                        engEveryDay = new EngEveryDay();
                        engEveryDay.id = "tianapi_${obj.result.id}" as String;
                        engEveryDay.content = obj.result.content;
                        engEveryDay.note = obj.result.note;
                        engEveryDay.source = obj.result.source;
                        engEveryDay.type = type;
                        engEveryDay.kind = "";
                        sourcesService.updateTheObject(engEveryDay);
                    }
                }
                else if (type==2 as byte)
                {

                }
                else if (type==3 as byte)
                {
                    EngEveryDay engEveryDay = new EngEveryDay();
                    engEveryDay.id = MathUtil.getNewId();
                    engEveryDay.content = obj.result.en;
                    engEveryDay.note = obj.result.zh;
                    engEveryDay.source = "";
                    engEveryDay.type = type;
                    engEveryDay.kind = "";
                    sourcesService.updateTheObject(engEveryDay);
                }
                else if (type==4 as byte)
                {
                    EngEveryDay engEveryDay = sourcesService.findObjectById(EngEveryDay.class,"4_${obj.sid}" as String);
                    if (engEveryDay==null)
                    {
                        engEveryDay = new EngEveryDay();
                        engEveryDay.id = "4_${obj.sid}" as String;
                        engEveryDay.content = obj.content;
                        engEveryDay.note = obj.note;
                        engEveryDay.source = "";
                        engEveryDay.type = type;
                        engEveryDay.kind = "";
                        engEveryDay.images = objectMapper.writeValueAsString([obj.picture,obj.picture2,obj.picture3]);
                        engEveryDay.ttsPath = obj.tts;
                        sourcesService.updateTheObject(engEveryDay);
                    }
                }

            }
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/changeBook")
    String changeBook()
    {
        List l = sourcesService.newQueryUtils(true,true).masterTable("book","book",[
                ["sf":"id","bf":"id"],
                ["sf":"name","bf":"name"],
                ["sf":"booktype","bf":"bookType"],
                [isCop:true,cop:"(select b.id || ':' || b.booktype || ':' || b.name from book as b where (replace(b.name,'单元练习--','')=replace(book.name,'背单词--','') or replace(book.name,'单元练习--','')=replace(b.name,'背单词--','')) and b.bookcate=:bookCate and (b.booktype=1 or b.booktype=0))",sf:"tagetid",bf:"tempMap.tagetId"],
//                [isCop: true,cop: ({
//                    return """(${
//                        sourcesService.newQueryUtils(true,false).masterTable("book","b2",["id"])
//                                .where("(replace(b2.name,'单元练习--','')=replace(book.name,'背单词--','') or replace(book.name,'单元练习--','')=replace(b2.name,'背单词--',''))",null,null,null)
//                                .where("b2.bookcate=:bookCate",["bookCate":0 as byte],"and",null)
//                                .where("(b2.booktype=1 or b2.booktype=0)",null,"and",null)
//                                .buildSql().sbf.toString()
//                    })""";
//                }).call(),sf:"tname",bf:"tempMap.tname"]
//        ])
//                .joinTable("bookcate","bc","left join","book.bookcate=bc.id",[
//                ["sf":"id","bf":"tempMap.bookCate.id"],
//                ["sf":"name","bf":"tempMap.bookCate.name"]
        ]).where("book.bookcate=:bookCate and (book.booktype=0 or book.booktype=1)",["bookCate":0 as byte],null,null)
                .beanSetup(Book.class,null,null)
                .buildSql().run().content;
        for(def o in l)
        {
            println "${o.id}:${o.bookType}:${o.name}<======>${o.tempMap.tagetId}";
            if (o.tempMap.tagetId != null)
            {
                def ss = o.tempMap.tagetId.split(":");
                byte type = ss[1];
                String bookId = ss[0];
                if (bookId!=o.id)
                {
                    String parentBookId = MathUtil.getNewId();
                    Book book1 = null;
                    Book book0 = null;
                    if (type==1 as byte)
                    {
                        book1 = sourcesService.findObjectById(Book.class,bookId);
                        book0 = sourcesService.findObjectById(Book.class,o.id);
                    }
                    else
                    {
                        book1 = sourcesService.findObjectById(Book.class,o.id);
                        book0 = sourcesService.findObjectById(Book.class,bookId);
                    }
                    book0.cancelLazyEr();
                    book1.cancelLazyEr();
                    Book bnew = null;
                    if (book0.bookType==1 as byte)
                    {
                        bnew = ObjectUtil.clone(book0);
                    }
                    else
                    {
                        bnew = ObjectUtil.clone(book1);
                    }

                    bnew.id = parentBookId;
                    bnew.bookType = 5 as byte;
                    bnew.name = bnew.name.replace("单元练习--","");
                    sourcesService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                        sourcesService.updateObject(bnew);
//                        sourcesService.updateTheObjectFilds(Book.simpleName,"id=:id",[parentBookId:parentBookId],[id:o.id],false);
//                        sourcesService.updateTheObjectFilds(Book.simpleName,"id=:id",[parentBookId:parentBookId],[id:bookId],false);
                        BookSubBook bookSubBook1 = new BookSubBook();
                        bookSubBook1.bookSubBookPK = new BookSubBookPK(parentBookId,book0.id);
                        bookSubBook1.bookType = book0.bookType;
                        sourcesService.updateObject(bookSubBook1);

                        BookSubBook bookSubBook2 = new BookSubBook();
                        bookSubBook2.bookSubBookPK = new BookSubBookPK(parentBookId,book1.id);
                        bookSubBook2.bookType = book1.bookType;
                        sourcesService.updateObject(bookSubBook2);
                    });
                }
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(l);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/uploadMP3")
    String uploadMP3()
    {
        List temList = ["Why?", "How old…?", "Shall we...?", "Sydney 	", "Would you like…? ", "Sydney Harbour Bridge 	", "How are you?"];
        List errList = [];

//        for (String s in temList)
//        {
//            try
//            {
//                String sr = s.replaceAll("\\?","").trim();
//                HttpUtil.downloadFile("http://dict.youdao.com/dictvoice?type=0&audio=${sr}","D:/DeveloperDriver/database/aliyunOSS/kcvideos/teach/wordnew/${sr}.mp3");
//                HttpUtil.downloadFile("http://dict.youdao.com/dictvoice?type=1&audio=${sr}","D:/DeveloperDriver/database/aliyunOSS/kcvideos/teach/wordnew/${sr}_1.mp3");
//            }
//            catch (Exception e)
//            {
//                errList << s;
//            }
//        }

//        for (Word word in sourcesService.newQueryUtils(false,false).masterTable("Word","word",null).buildSql().run().content)
//        {
//            try
//            {
//                String sr = word.updateName.replaceAll("\\?","").replaceAll("\\.","").trim();
//                String fileName = word.name.replaceAll("\\?","").replaceAll("\\.","").trim();
//                HttpUtil.downloadFile("http://dict.youdao.com/dictvoice?type=0&audio=${sr}","D:/DeveloperDriver/database/aliyunOSS/kcvideos/teach/wordnew2/${fileName}.mp3");
//                HttpUtil.downloadFile("http://dict.youdao.com/dictvoice?type=1&audio=${sr}","D:/DeveloperDriver/database/aliyunOSS/kcvideos/teach/wordnew2/${fileName}_1.mp3");
//            }
//            catch (Exception e)
//            {
//                errList << word.name;
//            }
//        }
//        println errList;

//        HttpUtil.downloadFile("http://dict.youdao.com/dictvoice?type=0&audio=ocean","D:/DeveloperDriver/database/aliyunOSS/kcvideos/teach/wordnew/ocean.mp3");

        List l = [];
        for(File f in FileUtil.ls("D:/DeveloperDriver/database/aliyunOSS/kcvideos/teach/wordnew"))
        {
            if (!f.name.contains("_1"))
            {
                l << ["name":f.name,"size":f.size()];
            }
        }
        for(Map map in l)
        {
            if (map.size < 20 as long)
            {
                println map;
                Word w = sourcesService.queryObject("select w from Word w where w.name = :name",["name":map.name.replaceAll(".mp3","")])?.get(0);
                if (w!=null)
                {
                    String sr = w.updateName.replaceAll("\\?","").replaceAll("\\.","").trim();
                    String fileName = w.name.replaceAll("\\?","").replaceAll("\\.","").trim();
                    HttpUtil.downloadFile("http://dict.youdao.com/dictvoice?type=0&audio=${sr}","D:/DeveloperDriver/database/aliyunOSS/kcvideos/teach/wordnew/${fileName}.mp3");
                    HttpUtil.downloadFile("http://dict.youdao.com/dictvoice?type=1&audio=${sr}","D:/DeveloperDriver/database/aliyunOSS/kcvideos/teach/wordnew/${fileName}_1.mp3");
                }
            }
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getXunFeiWsUrl")
    String getXunFeiWsUrl()
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     xfAppId:OtherUtils.givePropsValue("xfAppId"),
                     url:(
                             {
                                 URL url = new URL("https://ise-api.xfyun.cn/v2/open-ise");
                                 SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                                 simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                                 String date = simpleDateFormat.format(new Date());
                                 Charset charset = Charset.forName("UTF-8");
                                 Mac mac = Mac.getInstance("hmacsha256");
                                 SecretKeySpec spec = new SecretKeySpec(OtherUtils.givePropsValue("xfApiSecret").getBytes(charset), "hmacsha256");
                                 mac.init(spec);
                                 return UrlBuilder.of().setScheme("https")
                                         .setHost("ise-api.xfyun.cn")
                                         .addPath("v2")
                                         .addPath("open-ise")
                                         .addQuery("authorization", Base64.encode("""api_key="${OtherUtils.givePropsValue("xfApiKey")}", algorithm="hmac-sha256", headers="host date request-line", signature="${Base64.encode(mac.doFinal("host: ${url.getHost()}\ndate: ${date}\nGET ${url.getPath()} HTTP/1.1".getBytes(charset)))}"
                                            """))
                                         .addQuery("date",date)
                                         .addQuery("host",url.getHost())
                                         .build().replace("http://", "ws://").replace("https://", "wss://");
                             }
                     ).call()
                    ]
            );
        }
        catch (Exception er)
        {
            er.printStackTrace();
        }
    }
}
