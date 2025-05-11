package com.weavict.shop.rest

//import com.weavict.website.common.ImgCompress
import jodd.datetime.JDateTime

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType

/**
 * Created by Justin on 2018/6/10.
 */
@Path("/upload")
class UploadRest extends BaseRest
{
    @Context
    HttpServletRequest request;

    /**
     * 图片上传PrivaterProducts
     *
     * @param fileInputStream
     * @param disposition
     * @return
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/uploadprodcutimages")
    void uploadProdcutsImages()
    {
        JDateTime jdt = new JDateTime(new Date());
        String ym = "${jdt.getYear() as String}/${jdt.getMonth() as String}";
        File fd = new File("""${request.getSession().getServletContext().getRealPath("/")}uploads/products/images/${ym}""");
        if (!fd.exists())
        {
            fd.mkdir();
        }
        RequestStreamUtil.uploadFile(request, fd.getPath(), false, {
            fileName, filePathName, jm ->
                println "${filePathName}";
                println jm.dump();

//                ImgCompress imgCom = new ImgCompress(filePathName);
//                if (imgCom.getWidth() > 2000)
//                {
//                    imgCom.resizeByWidth(2000, filePathName);
//                }
        });

    }

    /**
     * 图片上传Privater
     *
     * @param fileInputStream
     * @param disposition
     * @return
     */
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/uploadprivaterimages")
    void uploadProductsPrivaterImages()
    {
        JDateTime jdt = new JDateTime(new Date());
        String ym = "${jdt.getYear() as String}/${jdt.getMonth() as String}";
        File fd = new File("""${request.getSession().getServletContext().getRealPath("/")}uploads/products/images/${ym}""");
        if (!fd.exists())
        {
            fd.mkdir();
        }
        RequestStreamUtil.uploadFile(request, fd.getPath(), false, {
            fileName, filePathName, jm ->
                println "${filePathName}";
                println jm.dump();

//                ImgCompress imgCom = new ImgCompress(filePathName);
//                if (imgCom.getWidth() > 2000)
//                {
//                    imgCom.resizeByWidth(2000, filePathName);
//                }
        });

    }

//    @POST
//    @Path("image1")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    String upload(@FormDataParam("file") InputStream fileInputStream,
//                         @FormDataParam("file") FormDataContentDisposition disposition, @Context ServletContext ctx)
//    {
//        File upload = new File(ctx.getRealPath("/upload"),
//                UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(disposition.getFileName()));
//        try
//        {
//            FileUtils.copyInputStreamToFile(fileInputStream, upload);
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
//        return "success";
//
//    }
}
