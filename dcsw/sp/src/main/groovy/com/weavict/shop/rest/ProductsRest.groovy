package com.weavict.shop.rest

import cn.hutool.core.collection.CollUtil
import cn.hutool.core.lang.tree.Tree
import cn.hutool.core.lang.tree.TreeNode
import cn.hutool.core.lang.tree.TreeUtil
import com.aliyun.oss.OSSClient
import com.aliyun.oss.model.CannedAccessControlList
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.shop.entity.*
import com.weavict.shop.module.ProductsBean
import com.weavict.shop.module.RedisApi
import com.weavict.shop.module.ScheduledBean
import com.weavict.shop.module.UserBean
import com.weavict.shop.redis.RedisUtil
import com.weavict.website.common.OtherUtils
import jakarta.ws.rs.POST
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.GET

/**
 * Created by Justin on 2018/6/10.
 */

@Path("/products")
class ProductsRest extends BaseRest
{
    @Autowired
    ProductsBean productsBean;

    @Autowired
    ScheduledBean scheduledBean;

    @Autowired
    UserBean userBean;

    @Autowired
    RedisApi redisApi

    @Autowired
    RedisUtil redisUtil;

    @Context
    HttpServletRequest request;

    int countStr(String longStr, String mixStr) {
        //如果确定传入的字符串不为空，可以把下面这个判断去掉，提高执行效率
//        if(longStr == null || mixStr == null || "".equals(longStr.trim()) || "".equals(mixStr.trim()) ){
//             return 0;
//        }
        int count = 0;
        int index = 0;
        while((index = longStr.indexOf(mixStr,index))!= -1){
            index = index + mixStr.length();
            count++;
        }
        return count;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/productCategoryList")
    String productCategoryList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "productCategoryList":({
                         List<TreeNode<String>> treeNodeList = CollUtil.newArrayList();
                         productsBean.queryProductCategoryList(query.appId,query.privaterId)?.each {

//生成Sql的纠错代码
//                            int c = countStr(it.id,"-");
//                             if (c==0) {
//                                 println "update productcategory set orderlistnum = 0,parent_id = null,layer = 0 where id = '${it.id}';";
//                             } else if (c==1) {
//                                 String pId = it.id.split("-")[0];
//                                 int xh = it.id.split("-")[1] as int;
//                                 println "update productcategory set orderlistnum = ${xh},parent_id = '${pId}',layer = ${c} where id = '${it.id}';";
//                             } else if (c==2) {
//                                 String pId = it.id.split("-")[0] + "-" + it.id.split("-")[1];
//                                 int xh = it.id.split("-")[2] as int;
//                                 println "update productcategory set orderlistnum = ${xh},parent_id = '${pId}',layer = ${c} where id = '${it.id}';";
//                             }

                             it.temp = it.productCategory?.id;
                             TreeNode<String> treeNode = new TreeNode<String>()
                                     .setId(it.id)
                                     .setParentId(it.productCategory==null ? "0" : it.productCategory.id)
                                     .setName(it.name)
                                     .setWeight(it.orderListNum)
                                     .setExtra(["layer":it.layer]);//"appId":it.appId,"privaterId":it.privaterId
                             treeNodeList << treeNode;

                         }
//                         TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
//                         treeNodeConfig.setWeightKey("sortOrder");
//                         treeNodeConfig.setChildrenKey("categoryList");
                         List<Tree<String>> treeNodes = TreeUtil.build(treeNodeList,"0");
//                         List<Tree<String>> treeNodes = TreeUtil.build(treeNodeList,"0",treeNodeConfig,{ TreeNode treeNode,Tree tree ->
//                             tree.setId(treeNode.getId());
//                             tree.setParentId(treeNode.getParentId());
//                             tree.setWeight(treeNode.getWeight());
//                             tree.setName(treeNode.getName());
//
//                             tree.putExtra("layer", treeNode.getExtra().getOrDefault("layer", null));
//                             tree.putExtra("appId", treeNode.getExtra().getOrDefault("appId", null));
//                             tree.putExtra("privaterId", treeNode.getExtra().getOrDefault("privaterId", null));
//                             tree.putExtra("layer", treeNode.getExtra().getOrDefault("layer", null));
//                         } as NodeParser);
//                         println JSONUtil.toJsonStr(treeNodes);
                         return treeNodes;
                     }).call()]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/theProduct")
    String theProduct(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "product":({
                         Product p = productsBean.findObjectById(Product.class,query.productId);
                         p.cancelLazyEr();
                         return p;
                     }).call()]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/updateProduct")
    String updateProduct(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Product p = objToBean(query.product, Product.class, objectMapper);
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "product":({
                         p = productsBean.updateProduct(p);
                         p.cancelLazyEr();
                         return p;
                     }).call()]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/privaterProductList")
    String privaterProductList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "privaterProductList":({
                         List l = productsBean.queryPrivaterProducts8Brand(query.privaterId,query.brandId)?.each {p->
                             p.cancelLazyEr();
                         };
                         if (l==null)
                         {
                             return new ArrayList<Product>();
                         }
                         else
                         {
                              return l;
                         }
                     }).call()]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/productCategoryList8Product")
    String productCategoryList8Product(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "productCategoryList":({
                         List l = productsBean.queryTheProductProductCategoryList(query.productId);
                         if (l==null)
                         {
                             return new ArrayList();
                         }
                         else
                         {
                             List ll = new ArrayList();
                             l.each {pc->
                                 ll.add([pc0Id:pc[0],pc0Name:pc[1],pc1Id:pc[2],pc1Name:pc[3],pc2Id:pc[4],pc2Name:pc[5]]);
                             };
                             return ll;
                         }
                     }).call()]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/updateProductCategory8Product")
    String updateProductCategory8Product(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            ProductCategory pc = objToBean(query.productCategory, ProductCategory.class, objectMapper);
            Product p = objToBean(query.product,Product.class,objectMapper);
            ProductCategory p0 = null;
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "productCategory8Product":({
                         productsBean.updateProdcutCategory8Product(pc,p);
                         pc = productsBean.findObjectById(ProductCategory.class,pc.id);
                         p0 = pc.productCategory.productCategory;
                         pc.productCategory.productCategory = null;
                         pc.productCategory?.cancelLazyEr();
                         pc?.cancelLazyEr();
                         return pc;
                     }).call(),
                    "firstProductCategory":({
                        p0?.cancelLazyEr();
                        return [id:p0?.id,name:p0?.name];
                    }).call()]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/delProductCategory8Product")
    String delProductCategory8Product(@RequestBody Map<String,Object> query)
    {
        try
        {
            productsBean.delProdcutCategory8Product(query.productId,query.categoryId);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addProductImg")
    String addProductImg(@RequestBody Map<String,Object> query)
    {
        try
        {
            ProductImages productImages = objToBean(query.productImages, ProductImages.class, null);
            productsBean.updateProductImage(productImages);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/productImages")
    String productImages(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "productImageList":({
                         List l = productsBean.queryImagesByTheProduct(query.productId)?.each {p->
                            p.cancelLazyEr();
                         };
                         if (l==null)
                         {
                             return new ArrayList();
                         }
                         else
                         {
                             return l;
                         }
                     }).call()]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/delProductImg")
    String delProductImg(@RequestBody Map<String,Object> query)
    {
        try
        {
            //ossaddProductImg
            OSSClient ossClient = OtherUtils.genOSSClient();
            ossClient.deleteObject(OtherUtils.givePropsValue("ali_oss_bucketName"), query.imagePath);
            ossClient.shutdown();
            //oss end
            productsBean.delProdcutImages(query.imageId);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/setupProductImgs")
    String setupProductImgs(@RequestBody Map<String,Object> query)
    {
        try
        {
            productsBean.updateProductImgsSetup(
                    query.productImgInfo.productId,
                    query.productImgInfo.mImg, objToBean(query.productImgInfo.bannerList, List.class, null),
                    objToBean(query.productImgInfo.productImgList, List.class, null),
                    objToBean(query.productImgInfo.specImgList, List.class, null));
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/productImages4TypeSort")
    String productImages4TypeSort(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "mImg":({
                         List l = productsBean.queryProductImages4TypeSort(query.productId,"mImg")?.each {p->
                             p.cancelLazyEr();
                         };
                         if (l==null || l.size()<1)
                         {
                             return null;
                         }
                         else
                         {
                             return l.get(0);
                         }
                     }).call(),
                     "bannerImageList":({
                         List l = productsBean.queryProductImages4TypeSort(query.productId,"banner")?.each {p->
                             p.cancelLazyEr();
                         };
                         if (l==null)
                         {
                             return new ArrayList();
                         }
                         else
                         {
                             return l;
                         }
                     }).call(),
                     "productImageList":({
                         List l = productsBean.queryProductImages4TypeSort(query.productId,"product")?.each {p->
                             p.cancelLazyEr();
                         };
                         if (l==null)
                         {
                             return new ArrayList();
                         }
                         else
                         {
                             return l;
                         }
                     }).call(),
                     "specImageList":({
                         List l = productsBean.queryProductImages4TypeSort(query.productId,"spec")?.each {p->
                             p.cancelLazyEr();
                         };
                         if (l==null)
                         {
                             return new ArrayList();
                         }
                         else
                         {
                             return l;
                         }
                     }).call()]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/productSpecs8TheProduct")
    String productSpecs8TheProduct(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "productSpecList":({
                         List l = productsBean.queryProductSpecification4ProdcutList(query.productId)?.each {p->
                             p.cancelLazyEr();
                         };
                         if (l==null)
                         {
                             return new ArrayList();
                         }
                         else
                         {
                             for (ProductSpecification4Prodcut ps in l)
                             {
                                 ps.productSpecificationItems4ProdcutList = productsBean.queryProductSpecificationItems4ProdcutList(ps.id)?.each {
                                     it.cancelLazyEr();
                                 };
                             }
                             return l;
                         }
                     }).call()]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveProductSpecification4Prodcut")
    String saveProductSpecification4Prodcut(@RequestBody Map<String,Object> query)
    {
        try
        {
            List<ProductSpecification4Prodcut> specList = objToBean(query.productSpecification4Prodcut, List.class, null);
            productsBean.addProductSpecification4ProductList(specList);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/delProductSpec")
    String delProductSpec(@RequestBody Map<String,Object> query)
    {
        try
        {
            productsBean.delTheProductSpecification4Product(query.specId,query.productId);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/delProductSpecItem")
    String delProductSpecItem(@RequestBody Map<String,Object> query)
    {
        try
        {
            productsBean.delTheProductSpecificationItem4Product(query.itemId,query.productId);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/clearProductSpecSetup8Product")
    String clearProductSpecSetup8Product(@RequestBody Map<String,Object> query)
    {
        try
        {
            productsBean.clearProductSpecSetup8Product(query.productId);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveProductSpecSetupList8Product")
    String saveProductSpecSetupList8Product(@RequestBody Map<String,Object> query)
    {
        try
        {
            productsBean.saveProductSpecSetupList8Product(query.productId,objToBean(query.productSpecSetupList, List.class, null));
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/productSpecSetupList8TheProduct")
    String productSpecSetupList8TheProduct(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "productSpecSetupList":({
                         List l = productsBean.queryProductSpecSetupList8Product(query.productId)?.each {pss->
                             pss.cancelLazyEr();
                         };
                         if (l==null)
                         {
                             return new ArrayList();
                         }
                         else
                         {
                             return l;
                         }
                     }).call()]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveProductListTableSetup")
    String saveProductListTableSetup(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Product> productList = objToBean(query.productList, List.class, objectMapper);
            productsBean.saveProductListTableSetup(productList);
            // oss
            OSSClient ossClient = OtherUtils.genOSSClient();
//            ObjectMetadata meta = new ObjectMetadata();
//            meta.setContentType("application/json");
//            meta.setContentEncoding("gbk");

            productList.each {product->
//加上判断的原因是 伙大啦的供应商不分社区，而 万家选 是区分的
                Orgration org = new Orgration();
                if (!(product.productPrivater.orgrationId in [null,""]))
                {
                    org = productsBean.findObjectById(Orgration.class,product.productPrivater.orgrationId);
                    org?.cancelLazyEr();
                }
                List imgList = productsBean.queryProductImages4TypeSort(product.id,"all");
                imgList?.each{
                    it.cancelLazyEr();
                    it.product = null;
                }
                ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "system/json/product/${product.id}.json", new ByteArrayInputStream(objectMapper.writeValueAsString([
                        "images":["mImg":({
                            List l = new ArrayList();
                             imgList?.each {
                                 if (it.isMasterImg)
                                 {
                                     l << it;
                                 }
                            };
                            return l.size()>0 ? l?.get(0)?.path : null;
                        }).call(),
                        "banner":({
                            List l = new ArrayList();
                            imgList?.each {
                                if (it.isBannerImg)
                                {
                                    l << it;
                                }
                            };
                            return l;
                        }).call(),
                        "product":({
                            List l = new ArrayList();
                            imgList?.each {
                                if (it.isProductImg)
                                {
                                    l << it;
                                }
                            };
                            return l;
                        }).call(),
                          "videos":({
                              List l = new ArrayList();
                              imgList?.each {
                                  if (it.isVideo)
                                  {
                                      l << it;
                                  }
                              };
                              return l;
                          }).call(),
                        "spec":({
                            List l = new ArrayList();
                            imgList?.each {
                                if (it.isSpecImg)
                                {
                                    l << it;
                                }
                            };
                            return l;
                        }).call()],
                        "productInfo":[
                                "productId":product.id,
                                "productName":product.name,
                                "takeType":product.takeType,
                                "price":product.price,
                                "tuanPrice":product.tuanPrice,
                                "tgPersonsCount":product.tgPersonsCount,
                                "tgEndDays":product.tgEndDays,
                                "cbPrice":product.cbPrice,
                                "marketPrice":product.marketPrice,
                                "store":product.store,
                                "yhAmount":product.yhAmount,
                                "saleNone":product.saleNone,
                                "isMarketable":product.isMarketable,
                                "markeDate":new Date(),
                                "brand":({
                                    return ["brandId":product.brand?.id,"brandName":product.brand?.name,"brandLogo":product.brand?.logo]
                                }).call(),
                                "description":product.description,
                                "productPrivater":({
                                    return ["privaterId":product.productPrivater.id,"privaterName":product.productPrivater.name,"area":product.productPrivater.area,"address":product.productPrivater.address,"lng":product.productPrivater.lng,"lat":product.productPrivater.lat,"tel":product.productPrivater.tel,"phone":product.productPrivater.phone,
                                            "orgrationId":product.productPrivater.orgrationId,
                                            "orgrationName":org?.name,"productsPrivaterImagesList":({
                                        return userBean.queryPrivaterImages(product.productPrivater.id)?.each {pimg->
                                            pimg.productsPrivater = null;
                                        };
                                    }).call()]
                                }).call(),
                                "specSetupList":({
                                    List l = new ArrayList();
                                    productsBean.queryProductSpecSetupList8Product(product.id)?.each {
                                        it.cancelLazyEr();
                                        l << ["id":it.id,"ids":it.ids,"price":it.price,"tuanPrice":it.tuanPrice,"cbPrice":it.cbPrice,"store":it.store as int,"imgPath":it.specImg,"used":it.used];
                                    };
                                    //redis
                                    redisApi.buildProductStore(objectMapper,product,l);
                                    //redis end
                                    return l;
                                }).call(),
                                "specList":({
                                    List l = new ArrayList();
                                    productsBean.queryProductSpecification4ProdcutList(product.id)?.each {s->
                                        s.cancelLazyEr();
                                        l << ["id":s.id,"name":s.name,"orderNum":s.orderNum,"specItemList":({
                                            List l2 = new ArrayList();
                                            s.productSpecificationItems4ProdcutList = productsBean.queryProductSpecificationItems4ProdcutList(s.id);
                                            s.productSpecificationItems4ProdcutList?.each {s2->
                                                s2.cancelLazyEr();
                                                l2 << ["id":s2.id,"name":s2.name,"orderNum":s2.orderNum,"price":s2.price,"store":s2.store]
                                            }
                                            return l2;
                                        }).call()];
                                    };
                                    return l;
                                }).call()
                        ]
                ]).getBytes("UTF-8")));
                ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "system/json/product/${product.id}.json", CannedAccessControlList.PublicRead);
            };

            Map categoryMap = [:];
            productsBean.queryAllProductCategoryList().each {
                if (categoryMap[it[2]]==null)
                {
                    categoryMap[it[2]] = new ArrayList();
                }
                if (it[9])
                {
                    categoryMap[it[2]].add(["productId":it[0],"productName":it[1],"categoryId":it[2],"categoryName":it[3],"price":it[4],"marketPrice":it[5],"cbPrice":it[6],"isSaleNone":it[7],"mImg":it[8],"isMarkeTable":it[9],"tuanPrice":it[10]]);
                }
            }
            categoryMap.each {key,value->
                ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "system/json/product/category/${key}.json", new ByteArrayInputStream(objectMapper.writeValueAsString(value).getBytes("UTF-8")));
                ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "system/json/product/category/${key}.json", CannedAccessControlList.PublicRead);
            }

            //生成社区商户的JSON文件到OSS
//            productsBean.genOrgProducts(query?.orgId).each {
//                //save to oss
//                ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"),"org/${it.orgId}/orgProducts.json",new ByteArrayInputStream(objectMapper.writeValueAsString(it).getBytes("UTF-8")));
//                ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "org/${it.orgId}/orgProducts.json", CannedAccessControlList.PublicRead);
//                //oss end
//            };
            ossClient.shutdown();
            // oss end
            scheduledBean.productsIntoRedis();
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/ganOrgMarketedProductList")
//    用来生成指定社区的所有已经上架的产品到OSS
    String ganOrgMarketedProductList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            // oss
            OSSClient ossClient = OtherUtils.genOSSClient();
            ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "system/json/product/org/${query.orgId}.json", new ByteArrayInputStream(objectMapper.writeValueAsString(productsBean.queryOrgProductList(query.orgId)).getBytes("UTF-8")));
            ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "system/json/product/org/${query.orgId}.json", CannedAccessControlList.PublicRead);
            ossClient.shutdown();
            // oss end
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/productStore")
    String productStore(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            if (!redisUtil.hExists("product_${query.productId}","store") || !redisUtil.hExists("product_${query.productId}","specSetupList"))
            {
                Product product = new Product();
                product.id = query.productId;
                redisApi.buildProductStore(objectMapper,product,null);
            }
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                        "productStore":[
                            "store":({
                                //redis
                                return redisUtil.hGet("product_${query.productId}","store") as int;
                                //redis end
                            }).call(),
                            "specSetupList":({
                                //redis
                                List sl = objectMapper.readValue(redisUtil.hGet("product_${query.productId}","specSetupList"),List.class);
                                //从redis的Spec明细中更新最新的库存数量
                                sl?.each {
                                    if (redisUtil.hExists("product_${query.productId}","store"))
                                    {
                                        it.store = redisUtil.hGet("product_${query.productId}","store_${it.id}") as int;
                                    }
                                }
                                //redis end
                            }).call()
                        ]
                     ]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/indexPageProductList")
    String indexPageProductList(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            if (!redisUtil.hasKey("indexPage"))
            {
                productsBean.queryObject("select p from Product as p where p.isMarketable = true",null,0,50)?.each { product->
                    product.cancelLazyEr();
                    redisUtil.zAdd("indexPage",objectMapper.writeValueAsString(product),0 as double);
                };

            }

            return objectMapper.writeValueAsString(
                    ["status":"OK",
                            "data0":({
                                return redisUtil.zRange("indexPage",0l,49l);
                            }).call()
                    ]
            );
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
    @Path("/ganIndexPageProductList")
    String ganIndexPageProductList(@RequestBody Map<String,Object> query)
    {
        try
        {
            scheduledBean.productsIntoRedis();
            return """{"status":"OK"}""";
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
    @Path("/orgProducts")
    String orgProducts(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            OSSClient ossClient = OtherUtils.genOSSClient();
            productsBean.genOrgProducts(query?.orgId).each {
                println it.dump();
                //save to oss
                ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"),"org/${it.orgId}/orgProducts.json",new ByteArrayInputStream(objectMapper.writeValueAsString(it).getBytes("UTF-8")));
                ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "org/${it.orgId}/orgProducts.json", CannedAccessControlList.PublicRead);
                //oss end
            };
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/cartProductList8Buyer")
    String cartProductList8Buyer(@RequestBody Map<String,Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status":"OK",
                     "datas":({
                         return productsBean.cartProductList8Buyer(query.appId,query.buyerId);
                     }).call()
                    ]
            );
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/addProducts2Cart")
    String addProducts2Cart(@RequestBody Map<String,Object> query)
    {
        try
        {
            Cart cart = objToBean(query.cart, Cart.class, null);
            productsBean.addProducts2Cart(cart);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/delTheCartItem")
    String delTheCartItem(@RequestBody Map<String,Object> query)
    {
        try
        {
            CartPK cartPK = objToBean(query.cartPK, CartPK.class, null);
            productsBean.delTheCartItem(cartPK);
            return """{"status":"OK"}""";
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }
}
