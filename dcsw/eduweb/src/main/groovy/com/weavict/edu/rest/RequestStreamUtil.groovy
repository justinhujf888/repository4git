package com.weavict.edu.rest

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.weavict.common.util.MathUtil
//import com.weavict.website.common.ImgCompress
import org.apache.commons.beanutils.BeanUtils

import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.text.SimpleDateFormat

/**
 * Created by Justin on 2018/6/10.
 */


class RequestStreamUtil
{
/**
 * 将request流中的数据转换成String
 * @param request
 * @return
 */
    static String toString(HttpServletRequest request)
    {
        String valueStr = "";
        try
        {
            StringBuffer sb = new StringBuffer();
            InputStream is = request.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String s = "";
            while ((s = br.readLine()) != null)
            {
                sb.append(s);
            }
            valueStr = sb.toString();
        } catch (IOException e)
        {
            e.printStackTrace();
            valueStr = "";
        }
        return valueStr;
    }

    /**
     * 将request流中的数据转换成Map
     * @param request
     * @return
     */
    static Map<String, String> toMap(HttpServletRequest request)
    {
        Map<String, String> parameter = new HashMap<String, String>();
        String valueStr = toString(request);
        try
        {
            if (null != valueStr && !"".equals(valueStr))
            {
                String[] valueArr = valueStr.split("&");
                for (String kvStr : valueArr)
                {
                    String[] kvStrArr = kvStr.split("=");
                    parameter.put(kvStrArr[0], kvStrArr[1]);
                }
            } else
            {
                parameter = getParameterMap(request);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return parameter;
    }

    /**
     * 将request流中的数据转换成bean
     * @param request
     * @return
     */
    static Object toBean(HttpServletRequest request, Class<?> beanClazz)
    {
        Map<String, String> mapObject = toMap(request);
        System.out.println("toBean:" + mapObject);
        Object beanO = null;
        try
        {
            beanO = beanClazz.newInstance();
            if (null != mapObject && !mapObject.isEmpty())
            {
                BeanUtils.copyProperties(beanO, mapObject);
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return beanO;
    }

    /**
     * 从request中获得参数Map，并返回可读的Map
     * @param request
     * @return
     */
    static Map<String, String> getParameterMap(HttpServletRequest request)
    {
        // 参数Map
        Map properties = request.getParameterMap();
        // 返回值Map
        Map<String, String> returnMap = new HashMap<String, String>();
        Iterator entries = properties.entrySet().iterator();
        Map.Entry<String, String> entry;
        String name = "";
        String value = "";
        while (entries.hasNext())
        {
            entry = (Map.Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj)
            {
                value = "";
            } else if (valueObj instanceof String[])
            {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++)
                {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            } else
            {
                value = valueObj.toString();
            }
            returnMap.put(name, value);
        }
        return returnMap;
    }

    /**
     * 将接收的base64转换成图片保存
     *
     * @param imgByte
     *            base64数据
     * @param cardNum
     *            号码
     * @return 成功返回图片保存路径，失败返回false
     */
    static Object uploadImg(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("图片上传开始...");
        String destDir = "/upload/image";

        /* response.setHeader("Access-Control-Allow-Origin", "*");
         response.setHeader("Access-Control-Allow-Methods", "POST");*/

        ServletInputStream inputStream = request.getInputStream();

        //获取文件上传的真实路径
        String uploadPath = request.getSession().getServletContext().getRealPath("/");
        //保存文件的路径
        String filepath = destDir + File.separator + createNewDir();
        File destfile = new File(uploadPath + filepath);
        if (!destfile.exists()) {
            destfile.mkdirs();
        }
        //文件新名称
        String fileNameNew = getFileNameNew() + ".png";
        File f = new File(destfile.getAbsoluteFile() + File.separator + fileNameNew);
        if (!f.exists()) {println "asdfasdf";
            OutputStream os = new FileOutputStream(f);
            BufferedOutputStream bos = new BufferedOutputStream(os);

            byte[] buf = new byte[1024];
            int length;
            length = inputStream.read(buf, 0, buf.length);
            while (length != -1) {
                bos.write(buf, 0, length);
                length = inputStream.read(buf);
            }
            bos.close();
            os.close();
            inputStream.close();
            String lastpath = filepath + File.separator + fileNameNew;
            System.out.println("返回图片路径：" + lastpath);
            return lastpath;
        }
        return false;
    }

    /**
     * 为文件重新命名，命名规则为当前系统时间毫秒数
     *
     * @return string
     */
    private static String getFileNameNew() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return fmt.format(new Date());
    }

/**
 * 以当前日期为名，创建新文件夹
 *
 * @return
 */
    private static String createNewDir() {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(new Date());
    }

//    static uploadFile(HttpServletRequest request,String filePath,boolean useOld,boolean userResize,int maxImgPoint,int updateImgPoint,Closure fun)
//    {
//        InputStream is = request.getInputStream();
//        StringBuffer sb = new StringBuffer();
//        int count = 0;
//        while(true){
//            int a = is.read();
//            sb.append((char)a);
//            if(a == '\r')
//            {
//                count++;
//            }
//            if(count==8){
//                is.read();
//                break;
//            }
//        }
//        String title = sb.toString();
//        String[] titles = title.split("\r\n");
//        println titles[3];
//        String fileName = titles[5].split(";")[2].split("=")[1].replace("\"","");
//        println fileName;
//        Gson gson = new Gson();
//        Map jm = gson.fromJson(titles[3],Map);
//        String filePathName;
//
//        if (useOld)
//        {
//            filePathName = "${filePath}/${fileName}";
//        }
//        else
//        {
//            fileName = "${MathUtil.getPNewId()}_${fileName}";
//            filePathName = "${filePath}/${fileName}";
//        }
//
////        ImgCompress imgCom = new ImgCompress(is);
////        if (userResize && imgCom.getWidth()>maxImgPoint && false)
////        {
////            imgCom.resizeByWidth(updateImgPoint,filePathName);
////        }
////        else
////        {
////            FileOutputStream os = new FileOutputStream(new File(filePathName));
////            byte[] ob = new byte[1024];
////            int length = 0;
////            while((length = is.read(ob, 0, ob.length))>0){
////                os.write(ob,0,length);
////                os.flush();
////            }
////            os.close();
////        }
//
//        FileOutputStream os = new FileOutputStream(new File(filePathName));
//        byte[] ob = new byte[1024];
//        int length = 0;
//        while((length = is.read(ob, 0, ob.length))>0){
//            os.write(ob,0,length);
//            os.flush();
//        }
//        os.close();
//        is.close();
//        ImgCompress imgCom = new ImgCompress(filePathName);
//        if (userResize && imgCom.getWidth() > maxImgPoint)
//        {
//            imgCom.resizeByWidth(updateImgPoint,filePathName);
//        }
//        fun.call(fileName);
//    }
}
