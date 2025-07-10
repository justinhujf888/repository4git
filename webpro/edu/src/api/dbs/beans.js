import util from '@/api/util';

function get_state() {
    return {
        hasLogin: false,
        loginProvider: null,
        userId: null,
        openid: null,
        uniId: null,
        userInfo: null,
        param: null,
        url: null,
        code: null,
        loginToken: null,
        password: null
    };
}

function get_pageUtil() {
    return {
        size: 0,
        number: 0,
        first: false,
        last: false,
        totalPages: 0,
        totalElements: 0,
        numberOfElements: 0,
        content: []
    };
}

function get_assess() {
    return {
        id: '',
        title: '',
        content: '',
        theType: 0,
        objId: '',
        appId: '',
        assessItemsList: []
    };
}

function get_assessItems() {
    return {
        id: '',
        title: '',
        content: '',
        theType: 0,
        sortNum: 0,
        mediaType: 0,
        assess: get_assess(),
        assessItemDetailList: []
    };
}

function get_assessItemDetail() {
    return {
        id: '',
        sortNum: 0,
        imgUrl: '',
        mediaType: 0,
        content: '',
        optionContent: '',
        rightAnswer: '',
        assessItems: get_assessItems()
    };
}

function get_assessAnswerReport() {
    return {
        id: '',
        assessId: '',
        assessItemId: '',
        assessItemDetailId: '',
        objId: '',
        rightAnswer: '',
        optionContent: '',
        optionId: '',
        createDate: null
    };
}

function get_huoDong() {
    return {
        id: '',
        name: '',
        zxId: '',
        xiaoId: '',
        imgPath: '',
        content: '',
        createDate: null,
        endDate: null,
        modifyDate: null,
        description: '',
        status: -1
    };
}

function get_demoClassSchool() {
    return {
        id: '',
        name: '',
        schoolStatus: 0,
        createDate: null,
        schoolImg: '',
        logoImg: '',
        description: '',
        demoClassList: []
    };
}

function get_demoClass() {
    return {
        id: '',
        name: '',
        status: 0,
        createDate: null,
        demoClassSchool: get_demoClassSchool()
    };
}

function get_demoClassMaster() {
    return {
        id: '',
        description: '',
        status: 0,
        beginDate: null,
        endDate: null
    };
}

function get_demoClassPersonKesPK() {
    return {
        demoClassPersonId: '',
        demoClassId: '',
        demoClassMasterId: ''
    };
}

function get_demoClassPersonKes() {
    return {
        demoClassPersonKesPK: get_demoClassPersonKesPK(),
        dailiNo: '',
        createDate: null
    };
}

function get_teachStudy() {
    return {
        id: '',
        keCheng: '',
        teachContext: '',
        point: '',
        greeting: '',
        games: '',
        homework: '',
        teachDate: null,
        createDate: null,
        teacher: {},
        teacherGroup: []
    };
}

function get_teacherGroup() {
    return {
        id: '',
        name: '',
        teacher: {},
        xiaoId: '',
        status: 0,
        beginDate: null,
        endDate: null,
        weekDaysTime: '',
        createDate: null,
        keSubject: '',
        teachStudyList: []
    };
}

function get_groupStudyDayPK() {
    return {
        dateStr: '',
        groupId: ''
    };
}

function get_groupStudyDay() {
    return {
        groupStudyDayPK: get_groupStudyDayPK(),
        teacherId: '',
        studyType: '',
        studyInfo: '',
        createDate: null
    };
}

function get_groupStudentDtailPK() {
    return {
        xiaoId: '',
        studentId: '',
        teacherId: '',
        groupId: '',
        dateStr: ''
    };
}

function get_groupStudentDtail() {
    return {
        groupStudentDtailPK: get_groupStudentDtailPK(),
        status: 0,
        answer: '',
        amb: 0,
        samb: 0,
        totalAmb: 0,
        createDate: null,
        mediaPath: ''
    };
}

function get_studentStudyReportPK() {
    return {
        beginDateStr: '',
        endDateStr: '',
        groupId: ''
    };
}

function get_studentStudyReport() {
    return {
        studentStudyReportPK: get_studentStudyReportPK(),
        dataUrl: '',
        beginDate: null,
        endDate: null
    };
}

function get_studentStudyReportDetailPK() {
    return {
        beginDateStr: '',
        endDateStr: '',
        groupId: '',
        studentId: ''
    };
}

function get_studentStudyReportDetail() {
    return {
        studentStudyReportDetailPK: get_studentStudyReportDetailPK(),
        teacherHope: '',
        parentHope: '',
        studentStudyReport: get_studentStudyReport()
    };
}

function get_manager() {
    return {
        id: '',
        zxId: '',
        wxid: '',
        wxUniId: '',
        headimgurl: '',
        wxNickName: '',
        wxNickEm: '',
        name: '',
        engName: '',
        phone1: '',
        phone2: '',
        phone3: '',
        sex: 0,
        status: 0,
        address: '',
        areaPath: '',
        bySchool: '',
        birthdayDate: null,
        createDate: null
    };
}

function get_teacher() {
    return {
        id: '',
        wxid: '',
        wxUniId: '',
        headimgurl: '',
        wxNickName: '',
        wxNickEm: '',
        name: '',
        engName: '',
        phone1: '',
        phone2: '',
        phone3: '',
        sex: 0,
        status: 0,
        address: '',
        areaPath: '',
        zxId: '',
        xiaoId: 0,
        bySchool: '',
        birthdayDate: null,
        createDate: null,
        grouprList: [],
        teachStudyList: []
    };
}

function get_parent() {
    return {
        id: '',
        wxid: '',
        wxUniId: '',
        headimgurl: '',
        wxNickName: '',
        wxNickEm: '',
        name: '',
        engName: '',
        phone1: '',
        phone2: '',
        phone3: '',
        sex: 0,
        address: '',
        areaPath: '',
        birthdayDate: null,
        createDate: null,
        billsList: []
    };
}

function get_studentPK() {
    return {
        studentId: 0,
        xiaoId: 0
    };
}

function get_student() {
    return {
        id: get_studentPK(),
        zxId: '',
        wxid: '',
        wxUniId: '',
        headimgurl: '',
        wxNickName: '',
        wxNickEm: '',
        name: '',
        engName: '',
        phone1: '',
        phone2: '',
        phone3: '',
        sex: 0,
        status: 0,
        address: '',
        areaPath: '',
        currentSchool: '',
        currentClass: '',
        familyBackground: '',
        birthdayDate: null,
        createDate: null,
        amb: 0,
        keLong: 0,
        keTimes: 0,
        keRuned: 0
    };
}

function get_studentSals() {
    return {
        id: '',
        zxId: '',
        xiaoId: 0,
        name: '',
        engName: '',
        age: 0,
        phone1: '',
        phone2: '',
        phone3: '',
        sex: 0,
        status: 0,
        address: '',
        areaPath: '',
        currentSchool: '',
        currentClass: '',
        birthdayDate: null,
        createrId: '',
        createDate: null,
        sourceType: 0,
        sourceID: '',
        description: '',
        studentSalsDetailList: []
    };
}

function get_studentSalsDetail() {
    return {
        id: '',
        description: '',
        nextDescription: '',
        comeType: 0,
        humanId: '',
        nextHumanId: '',
        modifyId: '',
        intention: 0,
        nextDate: null,
        modifyDate: null,
        studentSals: get_studentSals()
    };
}

function get_studentKeShiInfo() {
    return {
        id: '',
        studentId: '',
        xiaoId: '',
        type: 0,
        fee: 0,
        keLong: 0,
        keTimes: 0,
        createDate: '',
        modifier: '',
        remark: ''
    };
}

function get_jifenGoLog() {
    return {
        id: '',
        productId: '',
        productJifen: 0,
        jifenAfter: 0,
        createDate: null,
        userId: '',
        userType: -1
    };
}

function get_bills() {
    return {
        id: '',
        name: '',
        status: 0,
        billsType: 0,
        amout: 0,
        sourceXiaoId: 0,
        distXiaoId: 0,
        createDate: null,
        usedDate: null,
        endDate: null,
        description: '',
        condition: '',
        creater: '',
        modifier: '',
        parent: get_parent()
    };
}

function get_book() {
    return {
        id: '',
        name: '',
        imgUrl: '',
        bookType: 0,
        bookCate: 0,
        bookSubCate: 0,
        sortNum: -1,
        isHot: false,
        isMarket: false,
        description: '',
        createDate: null,
        zxId: '',
        price: 0,
        yxDays: 0,
        tuanPrice: '',
        bookUniList: []
    };
}

// 不是数据库表，映射book中的tuanPrice
function get_bookTuanPrice() {
    return {
        id: '',
        minCount: 1,
        maxCount: 1,
        price: 0,
        tuanDays: 1
    };
}

function get_bookUni() {
    return {
        id: '',
        name: '',
        imgUrl: '',
        description: '',
        sortNum: -1,
        isBuy: true,
        book: get_book()
    };
}

function get_bookCate() {
    return {
        id: '',
        name: '',
        layer: 0,
        parentId: '',
        sortNum: -1,
        bookCateDetailList: []
    };
}

function get_bookCateDetail() {
    return {
        id: '',
        name: '',
        layer: 0,
        parentId: '',
        sortNum: -1,
        bookCate: get_bookCate()
    };
}

function get_bookCateBookDetailPK() {
    return {
        bookId: '',
        bookCateDetailId: ''
    };
}

function get_bookCateBookDetail() {
    return {
        bookCateBookDetailPK: get_bookCateBookDetailPK()
    };
}

function get_bookSubBook() {
    return {
        bookSubBookPK: get_bookSubBookPK(),
        bookType: ''
    };
}

function get_bookSubBookPK() {
    return {
        bookId: '',
        subBookId: ''
    };
}

function get_wordLabelPK() {
    return {
        wordId: '',
        wordType: -1,
        label: ''
    };
}

function get_wordLabel() {
    return {
        wordLabelPK: get_wordLabelPK(),
        sortNum: -1
    };
}

function get_word() {
    return {
        name: '',
        updateName: '',
        imgUrl: '',
        imgUrl2: '',
        description: '',
        phonetics: '',
        sortNum: -1,
        zxId: '',
        wordXingList: [],
        wordEgList: [],
        wordAssociateList: []
    };
}

function get_wordXing() {
    return {
        id: '',
        chnXing: '',
        word: get_word()
    };
}

function get_wordEg() {
    return {
        id: '',
        engEg: '',
        chnEg: '',
        word: get_word()
    };
}

function get_wordAssociate() {
    return {
        id: '',
        engAss: '',
        chnAss: '',
        word: get_word()
    };
}

function get_buyerStudyPK() {
    return {
        bookId: '',
        bookUniId: '',
        buyerId: '',
        //    0：单词听写练习；1：单词书写练习；2：单词卡片
        type: -1
    };
}

function get_buyerStudy() {
    return {
        buyerStudyPK: get_buyerStudyPK(),
        dataUrl: '',
        rightCount: 0,
        unfamiliarCount: 0,
        errorCount: 0,
        totalCount: 0,
        createDate: null
    };
}

function get_buyerStudyPlanPK() {
    return {
        bookId: '',
        buyerId: ''
    };
}

function get_buyerStudyPlan() {
    return {
        buyerStudyPlanPK: get_buyerStudyPlanPK(),
        beginDate: null,
        endDate: null,
        createDate: null
    };
}

function get_videoContent() {
    return {
        id: '',
        bookId: '',
        bookUniId: '',
        imgUrl: '',
        poster: '',
        name: '',
        description: '',
        sortNum: -1,
        duration: -1
    };
}

function get_questionnaireItems() {
    return {
        id: '',
        title: '',
        content: '',
        theType: -1,
        sortNum: -1,
        mediaType: -1,
        bookId: '',
        bookUniId: '',
        fen: 0,
        questionnaireItemDetailList: []
    };
}

function get_questionnaireItemDetail() {
    return {
        id: '',
        sortNum: -1,
        imgUrl: '',
        mediaType: -1,
        content: '',
        optionContent: '',
        rightAnswer: '',
        fen: 0,
        questionnaireItems: get_questionnaireItems()
    };
}

//并不是数据库表，只是对get_questionnaireItemDetail.optionContent的映射
function get_questionDetailOption() {
    return {
        id: '',
        sortNum: -1,
        content: '',
        prxContent: ''
    };
}

function get_productsPrivater() {
    return {
        id: '',
        appId: '',
        privaterId: '',
        name: '',
        privaterType: 0,
        productListType: 0,
        orgrationId: '',
        loginName: '',
        password: '',
        wxid: '',
        wxopenid: '',
        area: '',
        address: '',
        phone: '',
        tel: '',
        description: '',
        lat: '',
        lng: '',
        zxId: '',
        productsPrivaterImagesList: [],
        productList: [],
        brandList: [],
        rulesList: [],
        productSpecificationList: [],
        productAttributeList: [],
        couponList: []
    };
}

function get_productsPrivaterImages() {
    return {
        id: '',
        path: '',
        isYingYeImg: false,
        isZiZhiImg: false,
        isJiangLiImg: false,
        productsPrivater: get_productsPrivater()
    };
}

function get_userShop() {
    return {
        userShopPK: get_userShopPK(),
        name: '',
        //    0：在职；1：离职；2：注册管理员
        status: -1,
        //    0：普通员工；1：管理层；2：老板级别；3：仅代理
        ruleStatus: -1,
        rules: '',
        //是否为代理
        shiDaiLi: false,
        zhiWei: '',
        loginName: '',
        password: ''
    };
}

function get_userShopPK() {
    return {
        userId: '',
        shopId: ''
    };
}

function get_brand() {
    return {
        id: '',
        createDate: null,
        introduction: '',
        logo: '',
        modifyDate: null,
        name: '',
        orderList: 0,
        url: '',
        productsPrivater: get_productsPrivater(),
        productList: []
    };
}

function get_coupon() {
    return {
        id: '',
        appId: '',
        name: '',
        price: 0,
        moneyValue: 0,
        salsMinValue: 0,
        youXiaoDays: 0,
        canGetCount: 0,
        shiYouXiao: true,
        shiLoopUse: true,
        createDate: null,
        categoryId: '',
        productId: '',
        remark: '',
        productsPrivater: get_productsPrivater()
    };
}

function get_buyerCoupon() {
    return {
        id: '',
        couponId: '',
        appId: '',
        name: '',
        buyerId: '',
        privaterId: '',
        price: 0,
        moneyValue: 0,
        salsMinValue: 0,
        youXiaoDays: 0,
        beginDate: null,
        endDate: null,
        useDate: null,
        shiUsed: true,
        shiGuoQi: true,
        remark: ''
    };
}

function get_product() {
    return {
        id: '',
        takeType: 0,
        createDate: null,
        description: '',
        htmlFilePath: '',
        isBest: false,
        isHot: false,
        isMarketable: false,
        isNew: false,
        isDel: false,
        saleNone: false,
        marketPrice: 0,
        cbPrice: 0,
        yhAmount: 0,
        metaDescription: '',
        metaKeywords: '',
        modifyDate: null,
        name: '',
        price: 0,
        tuanPrice: 0,
        productSn: '',
        store: 0,
        tgPersonsCount: 0,
        tgEndDays: 0,
        brand: get_brand(),
        productPrivater: get_productsPrivater(),
        productCategoryList: [],
        masterImg: '',
        productSpecification4ProdcutList: [],
        productImagesList: [],
        productAttribute4ProductList: [],
        productSpecSetupList: [],
        orderItemList: [],
        orderBuyersList: []
    };
}

function get_productCategory() {
    return {
        id: '',
        appId: '',
        privaterId: '',
        createDate: null,
        metaDescription: '',
        metaKeywords: '',
        modifyDate: null,
        name: '',
        orderListNum: 0,
        path: '',
        productList: [],
        productCategory: {},
        productCategoryList: []
    };
}

function get_productImages() {
    return {
        id: '',
        path: '',
        posterPath: '',
        bannerOrderListNum: 0,
        productOrderListNum: 0,
        isMasterImg: false,
        isBannerImg: false,
        isProductImg: false,
        isSpecImg: false,
        isVideo: false,
        description: '',
        product: {}
    };
}

function get_productSpecification4Prodcut() {
    return {
        id: '',
        name: '',
        orderNum: 0,
        productSpecificationItems4ProdcutList: [],
        product: get_product()
    };
}

function get_productSpecificationItems4Prodcut() {
    return {
        id: '',
        name: '',
        tuanPrice: 0,
        price: 0,
        store: 0,
        orderNum: 0,
        productSpecification4Prodcut: get_productSpecification4Prodcut()
    };
}

function get_productSpecSetup() {
    return {
        id: '',
        price: 0,
        tuanPrice: 0,
        cbPrice: 0,
        store: 0,
        ids: '',
        used: false,
        specImg: '',
        product: get_product()
    };
}

function get_cartPK() {
    return {
        appId: '',
        productId: '',
        buyerId: '',
        specId: ''
    };
}

function get_cart() {
    return {
        cartPK: get_cartPK(),
        qutityNum: 0
    };
}

function get_orgration() {
    return {
        id: '',
        name: '',
        area: '',
        address: '',
        tel: '',
        description: '',
        manageDesc: '',
        buyDesc: '',
        createDate: null,
        mImage: '',
        lat: '',
        lng: '',
        buyerList: [],
        orderList: []
    };
}

function get_buyer() {
    return {
        phone: '',
        name: '',
        loginName: '',
        password: '',
        wxid: '',
        wxopenid: '',
        area: '',
        address: '',
        tel: '',
        headImgUrl: '',
        wxNickName: '',
        wxNickEm: '',
        sex: 0,
        description: '',
        amb: 0,
        createDate: null,
        orderList: [],
        orgrationList: [],
        orderBuyersList: []
    };
}

function get_rules() {
    return {
        id: '',
        ruleName: '',
        rules: '',
        productsPrivater: get_productsPrivater()
    };
}

function get_buyerAppInfo() {
    return {
        buyerAppInfoPK: get_buyerAppInfoPK(),
        loginName: '',
        password: '',
        wxid: '',
        wxopenid: '',
        headImgUrl: '',
        wxNickName: '',
        wxNickEm: '',
        money: 0,
        description: '',
        workCompany: ''
    };
}

function get_buyerAppInfoPK() {
    return {
        appId: '',
        buyerId: ''
    };
}

function get_buyerOrgrationPK() {
    return {
        orgrationId: '',
        buyerId: ''
    };
}

function get_buyerOrgration() {
    return {
        buyerOrgrationPK: get_buyerOrgrationPK(),
        niName: '',
        area: '',
        address: '',
        latitude: '',
        longitude: '',
        headImg: '',
        status: -1,
        createDate: null,
        isCreater: false,
        isManager: false,
        description: '',
        twiterList: []
    };
}

function get_tuanInfo() {
    return {
        id: '',
        productId: '',
        ended: false,
        createDate: null,
        endDate: null,
        orderTotalAmount: 0,
        orderTotalBuyerNum: 0,
        orderTotalQutityNum: 0,
        orderTotalOrgNum: 0
    };
}

function get_order() {
    return {
        id: '',
        appId: '',
        privaterId: '',
        payReturnOrderId: '',
        tuanInfoId: '',
        createDate: null,
        description: '',
        dailiNo: '',
        orderStatus: -1,
        orderType: 0,
        deliveryType: 0,
        paymentFee: 0,
        paymentStatus: -1,
        orderTotalAmount: 0,
        orderTotalBuyerNum: 0,
        orderTotalQutityNum: 0,
        shipAddress: '',
        shipAreaPath: '',
        shipMobile: '',
        shipName: '',
        shipPhone: '',
        shipZipCode: '',
        deliveryFee: 0,
        deliveryId: '',
        deliveryName: '',
        deliveryNo: '',
        orgration: get_orgration(),
        buyer: get_buyer(),
        orderItemList: [],
        orderBuyersList: []
    };
}

function get_orderItem() {
    return {
        id: '',
        createDate: null,
        deliveryQuantity: 0,
        price: 0,
        ids: '',
        productName: '',
        specImg: '',
        product: get_product(),
        order: get_order()
    };
}

function get_orderBuyers() {
    return {
        id: '',
        createDate: null,
        deliveryQuantity: 0,
        price: 0,
        paymentFee: 0,
        paymentStatus: 0,
        ids: '',
        specImg: '',
        orgrationId: '',
        deliveryId: '',
        deliveryName: '',
        deliveryNo: '',
        shipAddress: '',
        shipAreaPath: '',
        shipMobile: '',
        shipName: '',
        shipPhone: '',
        shipZipCode: '',
        deliveryFee: 0,
        deliveryType: 0,
        product: {},
        order: {},
        buyer: {}
    };
}

function get_payReturnOrder() {
    return {
        id: '',
        appId: '',
        payType: 0,
        createDate: null,
        deliveryQuantity: 0,
        price: 0,
        paymentFee: 0,
        paymentStatus: 0,
        orderType: 0,
        ids: '',
        specImg: '',
        productId: '',
        specId: '',
        orgrationId: '',
        buyerId: '',
        dailiNo: '',
        tradeNo: '',
        description: '',
        orderId: ''
    };
}

function get_twiter() {
    return {
        id: '',
        title: '',
        url: '',
        description: '',
        twiterType: 0,
        twiterDetailCount: 0,
        createDate: null,
        modifyDate: null,
        twiterImagesList: [],
        twiterDetailsList: [],
        twiterZanList: [],
        buyerOrgration: get_buyerOrgration()
    };
}

function get_twiterImages() {
    return {
        id: '',
        path: '',
        thumbPath: '',
        size: 0,
        duration: 0,
        listNum: 0,
        mediaType: 0,
        twiter: get_twiter()
    };
}

function get_twiterDetails() {
    return {
        id: '',
        description: '',
        createDate: null,
        modifyDate: null,
        twiterDetailsImagesList: [],
        twiter: get_twiter(),
        buyerOrgration: get_buyerOrgration(),
        distBuyerOrgration: get_buyerOrgration()
    };
}

function get_twiterDetailsImages() {
    return {
        id: '',
        path: '',
        listNum: 0,
        mediaType: 0,
        twiterDetails: get_twiterDetails()
    };
}

function get_twiterZan() {
    return {
        id: '',
        buyerId: '',
        createDate: null,
        twiter: get_twiter()
    };
}

function get_buyerBookPK() {
    return {
        bookId: '',
        buyerId: ''
    };
}

function get_buyerBook() {
    return {
        buyerBookPK: get_buyerBookPK(),
        beginDate: null,
        endDate: null
    };
}

function get_footageType() {
    return {
        id: '',
        name: '',
        appId: '',
        xiaoId: '',
        footageList: []
    };
}

function get_footage() {
    return {
        id: '',
        name: '',
        sourcePath: '',
        sourceInfo: '',
        poster: '',
        createDate: null,
        appId: '',
        xiaoId: '',
        mediaType: -1,
        footageType: get_footageType()
    };
}

function buildToBookUrl(book) {
    switch (book.bookType) {
        case 0:
            return (
                '/pages/school/huikc/word/sections?param=' +
                JSON.stringify({
                    bookId: book.id,
                    bookType: book.bookType,
                    bookName: book.name
                })
            );
            break;
        case 1:
            return (
                '/pages/school/huikc/word/sections?param=' +
                JSON.stringify({
                    bookId: book.id,
                    bookType: book.bookType,
                    bookName: book.name
                })
            );
            break;
        case 3:
            return (
                '/pages/school/huikc/video/videoPlay?param=' +
                JSON.stringify({
                    bookId: book.id,
                    bookType: book.bookType,
                    bookName: book.name
                })
            );
            break;
        default:
            break;
    }
}

function buildPId(pid) {
    // let util = require("@/common/util.js");
    return pid + new Date().getTime() + util.random_string(8);
}

export default {
    get_state,
    get_parentStudentGx() {
        return [
            { id: 0, name: '母亲' },
            { id: 1, name: '父亲' },
            { id: 2, name: '爷爷' },
            {
                id: 3,
                name: '奶奶'
            },
            { id: 4, name: '其他' }
        ];
    },
    get_bookType() {
        return [
            { id: 0, type: '单词/文字' },
            { id: 1, type: '习题' },
            { id: 2, type: '绘本' },
            {
                id: 3,
                type: '视频'
            },
            { id: 4, type: '文章' },
            { id: 5, type: '综合练习' }
        ];
    },
    get_bookWordStudyType() {
        return [
            { id: 0, type: '听写练习' },
            { id: 1, type: '书写练习' },
            { id: 2, type: '卡片练习' },
            {
                id: 3,
                type: '单选练习'
            }
        ];
    },
    get_bookCates() {
        return [
            { id: 0, cate: '英语' },
            { id: 1, cate: '语文' },
            { id: 2, cate: '数学' },
            { id: 3, cate: '物理' }
        ];
    },
    get_bookSubCate() {
        return [
            { id: 0, cate: '早教' },
            { id: 1, cate: '幼儿' },
            { id: 2, cate: '小学' },
            {
                id: 3,
                cate: '初中'
            },
            { id: 4, cate: '高中' },
            { id: 5, cate: '大学' },
            { id: 6, cate: '成人' }
        ];
    },
    get_questionType() {
        return [
            { id: 0, type: '填空' },
            { id: 1, type: '多选' },
            { id: 2, type: '单选' },
            {
                id: 3,
                type: '问答'
            },
            { id: 4, type: '判断' },
            { id: 5, type: '文本' }
        ];
    },

    get_orderStatus() {
        return [
            { id: 0, name: '待付款' },
            { id: 1, name: '待发货' },
            { id: 4, name: '待收货' },
            {
                id: 2,
                name: '已完成'
            },
            { id: 3, name: '已取消' },
            { id: 11, name: '准备退款' },
            { id: 12, name: '已经退款' },
            {
                id: 9,
                name: '售后'
            }
        ];
    },

    get_orderTypeList() {
        return [
            { id: 0, name: '个人线上消费订单' },
            { id: 1, name: '大货预购订单' },
            {
                id: 2,
                name: '发起拼团订单'
            },
            { id: 3, name: '拼团订单' },
            { id: 4, name: '到店消费订单' }
        ];
    },

    get_userRuleStatusList() {
        return [
            { id: 0, name: '普通员工以及管理（发件、处理取货等基本权限）' },
            {
                id: 1,
                name: '经营管理（有处理学习资料；商品资料、上架等管理权限）'
            },
            { id: 2, name: '老板（员工管理等所有权限）' },
            { id: 3, name: '代理分销（代理分销，没有其他操作权限）' },
            {
                id: 4,
                name: '书籍管理权限'
            }
        ];
    },

    get_userRulesList() {
        return [
            // {id:"0005",name:"浏览机构资料"},
            { id: '0006', name: '设置机构资料' },
            { id: '0001', name: '浏览商品资料' },
            { id: '0002', name: '处理商品资料' },
            { id: '0003', name: '上架商品' },
            { id: '0010', name: '订单查询' },
            { id: '0011', name: '处理订单' },
            { id: '0004', name: '发件、取货' },
            { id: '0012', name: '报表' },

            { id: '3000', name: '浏览活动信息' },
            { id: '3001', name: '编辑活动信息' },
            { id: '3010', name: '约单系统' },
            { id: '3020', name: '浏览素材' },
            { id: '3021', name: '编辑素材' },

            { id: '0007', name: '浏览学生资料' },
            { id: '0008', name: '处理学生资料' },
            { id: '0009', name: '分班管理' },
            { id: '0301', name: '浏览所有分班信息' },

            { id: '1001', name: '浏览书籍资料' },
            { id: '1002', name: '处理书籍资料' },
            { id: '1003', name: '书籍开通' },

            { id: '2001', name: '浏览员工资料' },
            { id: '2002', name: '处理员工资料' },
            { id: '2003', name: '角色权限' },

            { id: '9999', name: '代理分销' }
        ];
    },

    get_zhiWei() {
        return ['主管', '一般员工', '代理', '其它'];
    },

    get_productListType() {
        return [
            { id: 0, name: '2纵列' },
            { id: 1, name: '3纵列（产品图小）' },
            {
                id: 2,
                name: '1纵列（产品图大）'
            },
            { id: 3, name: '餐饮菜单展示（不需要选择商品规格，不重视详情图）' }
        ];
    },

    get_productTakeType() {
        return [
            { id: 0, name: '支持到店取货或快递' },
            { id: 1, name: '只支持到店取货' },
            {
                id: 2,
                name: '只支持快递'
            }
        ];
    },

    get_huoDongStatus() {
        return [
            { id: 0, name: '活动进行' },
            { id: 1, name: '活动暂停' },
            { id: 2, name: '活动结束' }
        ];
    },

    get_keShiTypes() {
        return [
            { id: 0, name: '续费' },
            { id: 1, name: '奖励' },
            { id: 2, name: '退费' },
            { id: 3, name: '其它' }
        ];
    },
    get_assessTypes() {
        return [
            { id: 20, name: '学生点评' },
            { id: 21, name: '课程主题' }
        ];
    },
    get_studentAttendanceStatus() {
        return [
            { id: 0, name: '出勤' },
            { id: 1, name: '请假' },
            { id: 2, name: '缺勤' },
            { id: 3, name: '补课' }
        ];
    },
    get_weekNames() {
        return [
            { id: 0, name: '周日' },
            { id: 1, name: '周一' },
            { id: 2, name: '周二' },
            {
                id: 3,
                name: '周三'
            },
            { id: 4, name: '周四' },
            { id: 5, name: '周五' },
            { id: 6, name: '周六' }
        ];
    },
    get_media() {
        return {
            type: '',
            uploaded: false,
            source: '',
            tempPath: '',
            posterTempPath: '',
            poster: '',
            videoInfo: {},
            imageInfo: {}
        };
    },

    get_assess,
    get_assessItems,
    get_assessItemDetail,
    get_assessAnswerReport,
    get_footageType,
    get_footage,
    get_huoDong,
    get_demoClass,
    get_demoClassMaster,
    get_demoClassPersonKes,
    get_demoClassPersonKesPK,
    get_teachStudy,
    get_teacherGroup,
    get_groupStudyDayPK,
    get_groupStudyDay,
    get_groupStudentDtailPK,
    get_groupStudentDtail,
    get_studentStudyReportPK,
    get_studentStudyReport,
    get_studentStudyReportDetailPK,
    get_studentStudyReportDetail,
    get_manager,
    get_teacher,
    get_studentPK,
    get_student,
    get_parent,
    get_jifenGoLog,
    get_bills,
    get_studentSals,
    get_studentSalsDetail,
    get_studentKeShiInfo,
    get_book,
    get_bookTuanPrice,
    get_bookSubBook,
    get_bookSubBookPK,
    get_bookUni,
    get_bookCate,
    get_bookCateDetail,
    get_bookCateBookDetailPK,
    get_bookCateBookDetail,
    get_wordLabelPK,
    get_wordLabel,
    get_word,
    get_wordXing,
    get_wordEg,
    get_wordAssociate,

    get_buyerStudyPK,
    get_buyerStudy,
    get_buyerStudyPlanPK,
    get_buyerStudyPlan,
    get_buyerBookPK,
    get_buyerBook,
    buildPId,
    buildToBookUrl,
    get_videoContent,
    get_questionnaireItems,
    get_questionnaireItemDetail,
    get_questionDetailOption,

    get_productsPrivater,
    get_productsPrivaterImages,
    get_userShop,
    get_userShopPK,
    get_brand,
    get_coupon,
    get_buyerCoupon,
    get_product,
    get_productCategory,
    get_productImages,
    get_productSpecification4Prodcut,
    get_productSpecificationItems4Prodcut,
    get_productSpecSetup,
    get_buyer,
    get_rules,
    get_buyerAppInfoPK,
    get_buyerAppInfo,
    get_cartPK,
    get_cart,
    get_orgration,
    get_buyerOrgrationPK,
    get_buyerOrgration,
    get_tuanInfo,
    get_jifenGoLog,
    get_order,
    get_orderItem,
    get_orderBuyers,
    get_payReturnOrder,
    get_twiter,
    get_twiterImages,
    get_twiterDetails,
    get_twiterDetailsImages,
    get_twiterZan
};
