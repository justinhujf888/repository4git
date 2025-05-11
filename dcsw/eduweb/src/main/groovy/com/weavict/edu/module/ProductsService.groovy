package com.weavict.edu.module

import com.weavict.edu.entity.Product
import com.weavict.edu.entity.ProductAttribute
import com.weavict.edu.entity.ProductAttribute4Product
import com.weavict.edu.entity.ProductAttributeItems
import com.weavict.edu.entity.ProductAttributeItems4Product
import com.weavict.edu.entity.ProductCategory
import com.weavict.edu.entity.ProductImages
import com.weavict.edu.entity.ProductSpecSetup
import com.weavict.edu.entity.ProductSpecification
import com.weavict.edu.entity.ProductSpecification4Prodcut
import com.weavict.edu.entity.ProductSpecificationItems
import com.weavict.edu.entity.ProductSpecificationItems4Prodcut
import com.weavict.common.util.MathUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("productsBean")
class ProductsService extends ModuleBean
{
    List<ProductImages> queryImagesByTheProduct(String productId)
    {
        return this.queryObject("select imgs from ProductImages as imgs where imgs.product.id = :productId", ["productId":productId]);
    }

    //根据商品名称模糊查询商品
    List<Product> queryProductsByName(String name)
    {
        return this.queryObject("select p from Product as p where p.name like :name",["name":"%" + "${name}" + "%"]);
    }

    List queryAllProductCategoryList()
    {
        return this.createNativeQuery("select p.id as productId,p.name as productName,c.id as categoryId,c.name as categoryName,p.price,p.marketPrice,p.cbPrice,p.issalenone,p.masterimg,p.ismarketable from product as p left join product_category as pc on p.id = pc.product_id left join productcategory as c on c.id = pc.category_id and pc.product_id = p.id where p.ismarketable = true").getResultList();
    }

    //根据商品名称模糊查询商品
    List<Product> queryProductsByPrivater(String privaterId)
    {
        return this.queryObject("select p from Product as p where p.productPrivater.id = :privaterId",["privaterId":privaterId]);
    }

    List<Product> queryPrivaterProducts8Brand(String privaterId,String brandId)
    {
        return this.queryObject("select p from Product as p where p.productPrivater.id = :privaterId ${brandId in [null,""] ? "" : " and p.brand.id = '$brandId'"}",["privaterId":privaterId]);
    }

    Product queryTheProductById(String id)
    {
        return this.queryObject("select p from Product as p left join fetch p.productProductattributemapstores where p.id=:id",["id":id])[0];
    }

    List<ProductAttribute> queryProductAttributeList(String privateId)
    {
        return this.queryObject("select pt from ProductAttribute as pt where pt.productsPrivater.id = :id",["id":privateId]);
    }

    List<ProductAttributeItems> queryProductAttributeItemsList(String attId)
    {
        return this.queryObject("select pts from ProductAttributeItems as pts where pt.productAttribute.id = :id",["id":attId]);
    }

    List<ProductAttribute4Product> queryProductAttribute4ProductList(String productId)
    {
        return this.queryObject("select pt from ProductAttribute4Product as pt where pt.product.id = :id",["id":productId]);
    }

    List<ProductAttributeItems4Product> queryProductAttributeItems4ProductList(String attId)
    {
        return this.queryObject("select pts from ProductAttributeItems4Product as pts where pt.productAttribute4Product.id = :id",["id":attId]);
    }

    List<ProductSpecification> queryProductSpecificationList(String privateId)
    {
        return this.queryObject("select ps from ProductSpecification as ps where ps.productsPrivater.id = :id",["id":privateId]);
    }

    List<ProductSpecificationItems> queryProductSpecificationItemsList(String specId)
    {
        return this.queryObject("select psi from ProductSpecificationItems as psi where psi.productSpecification.id = :id",["id":specId]);
    }

    List<ProductSpecification4Prodcut> queryProductSpecification4ProdcutList(String productId)
    {
        return this.queryObject("select ps from ProductSpecification4Prodcut as ps where ps.product.id = :id",["id":productId]);
    }

    List<ProductSpecification4Prodcut> queryProductSpecification4ProdcutListFetch(String productId)
    {
        return this.queryObject("select ps from ProductSpecification4Prodcut as ps left join fetch ps.productSpecificationItems4ProdcutList where ps.product.id = :id",["id":productId]);
    }

    List<ProductSpecificationItems4Prodcut> queryProductSpecificationItems4ProdcutList(String specId)
    {
        return this.queryObject("select psi from ProductSpecificationItems4Prodcut as psi where psi.productSpecification4Prodcut.id = :id",["id":specId]);
    }

    List<ProductCategory> queryProductCategoryList()
    {
        return this.queryObject("select pc from ProductCategory as pc order by pc.orderListNum");
    }

    List<ProductCategory> queryTheProductCategory(String id)
    {
        return this.queryObject("select pc from ProductCategory as pc where po.id = :id",["id":id]);
    }

    List queryTheProductProductCategoryList(String productId)
    {
        return this.createNativeQuery("select pc0.id as pc0Id,pc0.name as pc0Name,pc1.id as pc1Id,pc1.name as pc1Name,pc2.id as pc2Id,pc2.name as pc2Name from productcategory as pc2 left join product_category as pcj on pc2.id = pcj.category_id left join productcategory as pc1 on pc2.parent_id = pc1.id left join productcategory as pc0 on pc1.parent_id = pc0.id where pcj.product_id='${productId}'").getResultList();
    }

    @Transactional
    Product updateProduct(Product p)
    {
//		this.executeEQL("update Cartitem set isval = :isval where product.id=:id", ["id":p.id,"isval":((p.isMarketable) ? 0 as byte : 1 as byte)]);
        if (p.id in [null,""])
        {
            p.id = MathUtil.getPNewId();
            return this.updateObject(p);
        }
        else
        {
            this.createNativeQuery("update product set productprivater_id = '${p.productPrivater.id}',name = '${p.name}',productsn = '${p.productSn}',brand_id = '${p.brand.id}',tgpersonscount = ${p.tgPersonsCount},tgenddays = ${p.tgEndDays},price = ${p.price},tuanprice = ${p.tuanPrice},cbprice = ${p.cbPrice},marketprice = ${p.marketPrice},store = ${p.store},isbest = ${p.isBest},ishot = ${p.isHot},ismarketable = ${p.isMarketable},isnew = ${p.isNew},issalenone = ${p.saleNone} where id = '${p.id}'").executeUpdate();
            return this.findObjectById(Product.class,p.id);
        }
    }

    @Transactional
    ProductImages updateProductImage(ProductImages image)
    {
        return this.updateObject(image);
    }

    @Transactional
    void delProdcutImages(String imageId)
    {
        this.executeEQL("delete ProductImages where id=:id", ["id":imageId]);
    }
    @Transactional
//	运用了OSS服务，此方法为图片保存在服务器，暂时不使用
    void deleteTheProductImg(String id,String imgPath)
    {
        File f = new File(imgPath);
        if (f.exists())
        {
            f.delete();
        }
        this.executeEQL("delete ProductImages where id=:id", ["id":id]);
    }

    @Transactional
    void addTheProductAttribute(ProductAttribute pa,List<ProductAttributeItems> paItemList)
    {
        pa.productAttributeItemsList = new ArrayList<ProductAttributeItems>();
        pa.productAttributeItemsList = paItemList;
        paItemList.productAttribute = pa;
        this.updateObject(pa);
    }

    @Transactional
    void addTheProductAttribute4Product(ProductAttribute4Product pa,List<ProductAttributeItems4Product> paItemList)
    {
        pa.productAttributeItems4ProductList = new ArrayList<ProductAttributeItems4Product>();
        pa.productAttributeItems4ProductList = paItemList;
        paItemList.productAttribute4Product = pa;
        this.updateObject(pa);
    }

    @Transactional
    void addTheProductSpecification(ProductSpecification ps,List<ProductSpecificationItems> psItemList)
    {
        ps.productSpecificationItemsList = new ArrayList<ProductSpecificationItems>();
        ps.productSpecificationItemsList = psItemList;
        psItemList.productSpecification = ps;
        this.updateObject(ps);
    }

    @Transactional
    void addTheProductSpecification4Product(ProductSpecification4Prodcut ps,List<ProductSpecificationItems4Prodcut> psItemList)
    {
        ps.productSpecificationItems4ProdcutList = new ArrayList<ProductSpecificationItems4Prodcut>();
        ps.productSpecificationItems4ProdcutList = psItemList;
        psItemList.productSpecification4Prodcut = ps;
        this.updateObject(ps);
    }

    @Transactional
    void addProductSpecification4ProductList(List<ProductSpecification4Prodcut> specList)
    {
        specList.eachWithIndex{s,index ->
            if (!(s.name in [null,""]))
            {
//				ProductSpecification4Prodcut psp = this.findObjectById(ProductSpecification4Prodcut.class,s.id);
                ProductSpecification4Prodcut psp = null;
                if (s.id in [null,""])
                {
                    psp = new ProductSpecification4Prodcut();
                    psp.id = MathUtil.getPNewId();
                }
                else
                {
                    psp = this.findObjectById(ProductSpecification4Prodcut.class,s.id);
                }
                psp.name = s.name;
                psp.orderNum = index;
                psp.product = new Product();
                psp.product.id = s.product.id;
                this.updateObject(psp);
                s.productSpecificationItems4ProdcutList.eachWithIndex{ss,jndex ->
                    if (!(ss.name in [null,""]))
                    {
//						ProductSpecificationItems4Prodcut psip = this.findObjectById(ProductSpecificationItems4Prodcut.class,ss.id);
                        ProductSpecificationItems4Prodcut psip = null;
                        if (ss.id in [null,""])
                        {
                            psip = new ProductSpecificationItems4Prodcut();
                            psip.id = MathUtil.getPNewId();
                        }
                        else
                        {
                            psip = this.findObjectById(ProductSpecificationItems4Prodcut.class,ss.id);
                        }
                        psip.name = ss.name;
                        psip.orderNum = jndex;
                        psip.price = 0;
                        psip.store = 0;
                        psip.productSpecification4Prodcut = psp;
                        this.updateObject(psip);
                    }
                }
            }
        }
    }

    @Transactional
    void delTheProductSpecification4Product(String specId,String productId)
    {
        this.executeEQL("delete ProductSpecificationItems4Prodcut where productSpecification4Prodcut.id = :specId",["specId":specId]);
        this.executeEQL("delete ProductSpecification4Prodcut where id = :specId",["specId":specId]);
        this.executeEQL("delete ProductSpecSetup where product.id = :productId",["productId":productId]);
    }

    @Transactional
    void delTheProductSpecificationItem4Product(String id,String productId)
    {
        this.executeEQL("delete ProductSpecificationItems4Prodcut where id = :id",["id":id]);
//		this.executeEQL("delete ProductSpecSetup where product.id = :productId and ids like :specItemId",["productId":productId,"specItemId":"%${id}%"]);
        for(ProductSpecSetup pss in this.queryProductSpecSetupList8Product(productId))
        {
            if (pss.ids.indexOf(id)>-1)
            {
                this.executeEQL("delete ProductSpecSetup where id = :pssId ",["pssId":pss.id]);
            }
        }
    }

    @Transactional
    void clearProductSpecSetup8Product(String productId)
    {
        this.executeEQL("delete ProductSpecSetup where product.id = :productId",["productId":productId]);
    }

    @Transactional
    void uploadProductImages(List<ProductImages> imgList)
    {
        for(ProductImages img in imgList)
        {
            this.updateObject(img);
        }
    }

    @Transactional
    void updateProdcutCategory8Product(ProductCategory category,Product product)
    {
        this.createNativeQuery("insert into product_category (product_id,category_id) values ('${product.id}','${category.id}')").executeUpdate();
    }

    @Transactional
    void delProdcutCategory(String categoryId)
    {
        this.executeEQL("delete ProductCategory where id = :categoryId",["categoryId":categoryId]);
    }

    @Transactional
    void delProdcutCategory8Product(String productId,String categoryId)
    {
        this.createNativeQuery("delete from product_category where product_id = '${productId}' and category_id = '${categoryId}'").executeUpdate();
    }

    @Transactional
    void updateProductImgsSetup(String productId,Object mImg,List bannerList,List productImgList,List specList)
    {
        if (mImg!=null)
        {
            this.executeEQL("update ProductImages set isMasterImg = false where product.id = :productId",["productId":productId]);
            this.executeEQL("update ProductImages set isMasterImg = true where id = :id",["id":mImg.id]);
            this.executeEQL("update Product set masterImg = :masterImg where id = :productId",["productId":productId,"masterImg":mImg.path]);
        }
        if (bannerList!=null && bannerList.size()>0)
        {
            this.executeEQL("update ProductImages set isBannerImg = false where product.id = :productId",["productId":productId]);
            bannerList.eachWithIndex({m,index ->
                this.executeEQL("update ProductImages set isBannerImg = true,bannerOrderListNum = :bannerOrderListNum where id = :id",["id":m.id,"bannerOrderListNum":index]);
            });
        }
        if (productImgList!=null && productImgList.size()>0)
        {
            this.executeEQL("update ProductImages set isProductImg = false where product.id = :productId",["productId":productId]);
            productImgList.eachWithIndex({m,index ->
                this.executeEQL("update ProductImages set isProductImg = true,productOrderListNum = :productOrderListNum where id = :id",["id":m.id,"productOrderListNum":index]);
            });
        }
        if (specList!=null && specList.size()>0)
        {
            this.executeEQL("update ProductImages set isSpecImg = false where product.id = :productId",["productId":productId]);
            productImgList.eachWithIndex({m,index ->
                this.executeEQL("update ProductImages set isSpecImg = true,description = :description where id = :id",["id":m.id,"description":m.description]);
            });
        }
    }

    List<ProductImages> queryProductImages4TypeSort(String prodcutId,String type)
    {
        switch (type)
        {
            case "mImg":
                return this.queryObject("select m from ProductImages as m where m.product.id = :prodcutId and isMasterImg = :isMasterImg",["prodcutId":prodcutId,"isMasterImg":true]);
                break;
            case "banner":
                return this.queryObject("select m from ProductImages as m where m.product.id = :prodcutId and isBannerImg = :isBannerImg order by bannerOrderListNum",["prodcutId":prodcutId,"isBannerImg":true]);
                break;
            case "product":
                return this.queryObject("select m from ProductImages as m where m.product.id = :prodcutId and isProductImg = :isProductImg order by productOrderListNum",["prodcutId":prodcutId,"isProductImg":true]);
                break;
            case "spec":
                return this.queryObject("select m from ProductImages as m where m.product.id = :prodcutId and isSpecImg = :isSpecImg",["prodcutId":prodcutId,"isSpecImg":true]);
                break;
            case "video":
                return this.queryObject("select m from ProductImages as m where m.product.id = :prodcutId and isVideo = :isVideo",["prodcutId":prodcutId,"isVideo":true]);
                break;
            default:
                return null;
        }
    }

    @Transactional
    void saveProductSpecSetupList8Product(String productId,List<ProductSpecSetup> productSpecSetupList)
    {
        for(def p in productSpecSetupList)
        {
            ProductSpecSetup pss = new ProductSpecSetup();
            pss.id = p.id;
            if (pss.id in [null,""])
            {
                pss.id = MathUtil.getPNewId();
            }
            pss.product = new Product();
            pss.product.id = productId;
            pss.price = p.price as Integer;
            pss.tuanPrice = p.tuanPrice as Integer;
            pss.cbPrice = p.cbPrice as Integer;
            pss.ids = p.ids;
            pss.store = p.store as int;
            pss.used = p.used;
            pss.specImg = p.specImg;
            this.updateObject(pss);
        }
    }

    List<ProductSpecSetup> queryProductSpecSetupList8Product(String productId)
    {
        this.queryObject("select pss from ProductSpecSetup as pss where pss.product.id = :productId",["productId":productId]);
    }

    @Transactional
    void saveProductListTableSetup(List<Product> productList)
    {
        productList?.each {product->
            this.executeEQL("update Product set isMarketable = :isMarketable, saleNone = :saleNone where id = :id",["id":product.id,"isMarketable":product.isMarketable,"saleNone":product.saleNone]);
        }
    }

    List genOrgProducts(String orgId)
    {
        def l = [];
        this.queryObject("""select org from Orgration org where 1=1 ${orgId==null ? "" : "and org.id = '${orgId}'"}""").each {org->
            org.cancelLazyEr();
            def orgMap = ["orgId":org.id,"orgName":org.name,"privaterList":[]];
            this.queryObject("select ppr from ProductsPrivater ppr where ppr.privaterType = :privaterType and ppr.orgrationId = :orgrationId",["privaterType":1 as byte,"orgrationId":org.id]).each {ppr->
                ppr.cancelLazyEr();
                def pprMap = ["privaterId":ppr.id,"privaterName":ppr.name,"privaterAddress":ppr.address,"privaterArea":ppr.area,"products":[]];
                this.queryObject("select pprImg from ProductsPrivaterImages pprImg where pprImg.productsPrivater.id = :privaterId",["privaterId":ppr.id]).each {pprImg->
                    if (pprImg.isYingYeImg) {
                        pprMap["yingYeImg"] = pprImg.path;
                    } else if (pprImg.isZiZhiImg) {
                        pprMap["ziZhiImg"] = pprImg.path;
                    } else if (pprImg.isJiangLiImg) {
                        pprMap["jiangLiImg"] = pprImg.path;
                    }
                }
                this.queryObject("select p from Product p where p.isMarketable = :isMarketable and p.isDel = :isDel and p.productPrivater.id = :privaterId",["isMarketable":true,"isDel":false,"privaterId":ppr.id]).each {product->
                    product.cancelLazyEr();
                    pprMap["products"] << ["productId":product.id,"productName":product.name,"description":product.description,"marketPrice":product.marketPrice,"price":product.price,"tuanPrice":product.tuanPrice,"masterImg":product.masterImg];
                }
                orgMap["privaterList"] << pprMap;
            }
            l << orgMap;
        }
        return l;
    }
}
