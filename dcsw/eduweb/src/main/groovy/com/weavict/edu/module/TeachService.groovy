package com.weavict.edu.module

import com.aliyun.oss.OSSClient
import com.aliyun.oss.model.CannedAccessControlList
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.edu.entity.AssessItems
import com.weavict.edu.entity.Book
import com.weavict.edu.entity.BookCate
import com.weavict.edu.entity.BookCateDetail
import com.weavict.edu.entity.BookUni
import com.weavict.edu.entity.QuestionnaireItems
import com.weavict.edu.entity.VideoContent
import com.weavict.edu.entity.Word
import com.weavict.edu.entity.WordAssociate
import com.weavict.edu.entity.WordEg
import com.weavict.edu.entity.WordLabel
import com.weavict.edu.entity.WordLabelPK
import com.weavict.edu.entity.WordXing
import com.weavict.edu.enumutil.AccType
import com.weavict.common.util.MathUtil
import com.weavict.website.common.OtherUtils
import groovy.json.JsonSlurper
import jodd.bean.BeanCopy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("teachBean")
class TeachService extends ModuleBean
{
    List<Book> queryBookList4Type(byte bookType, String zxId)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select b from Book b where b.bookType = :bookType";
        paramsMap["bookType"] = bookType;
        if (!(zxId in ["all", "ALL"]))
        {
            sbf << " and b.zxId = :zxId";
            paramsMap["zxId"] = zxId;
        }
        sbf << " order by b.sortNum";
        return this.queryObject(sbf.toString(), paramsMap);
    }

    Book queryTheBook8Name(String name)
    {
        this.queryObject("select b from Book b where b.name = :name", ["name": name])?.get(0);
    }

    BookUni queryTheBookUni8Name4Book(String bookId, String bookUniName)
    {
        this.queryObject("select bu from BookUni bu where bu.book.id = :bookId and bu.name = :bookUniName", ["bookId": bookId, "bookUniName": bookUniName])?.get(0);
    }

    List<BookUni> queryBookUniList8Book(String bookId)
    {
        return this.queryObject("select bu from BookUni bu where bu.book.id = :bookId order by sortNum", ["bookId": bookId]);
    }

    List<BookCate> queryBookCateList()
    {
        return this.queryObject("select bc from BookCate bc order by bc.sortNum");
    }

    List<BookCateDetail> queryBookCateDetail(String bookCateId, String parentId, byte layer)
    {
        StringBuffer sbf = new StringBuffer();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        sbf << "select bcd from BookCateDetail bcd where 1=1";
        if (!(bookCateId in [null, ""]))
        {
            sbf << " and bcd.bookCate.id = :bookCateId";
            paramsMap["bookCateId"] = bookCateId;
        }
        if (!(parentId in [null, ""]))
        {
            sbf << " and bcd.parentId = :parentId";
            paramsMap["parentId"] = parentId;
        }
        if (layer > -2)
        {
            sbf << " and bcd.layer = :layer";
            paramsMap["layer"] = layer;
        }
        sbf << " order by bcd.sortNum"
        return this.queryObject(sbf.toString(), paramsMap);
    }

    List queryBookCateDetailAllLayer(String bookCateId, String parentId, byte layer, boolean bookInfo)
    {
        return this.queryBookCateDetail(bookCateId, parentId, layer)?.each {
            it.cancelLazyEr();
            it.bookCate = null;
//            这里的判断是为了限制层数，如果无线层估计可以吧判断拿掉
            if (it.layer < (2 as byte))
            {
                it.subList = this.queryBookCateDetailAllLayer(bookCateId, it.id, it.layer + 1 as byte, bookInfo);
            }
//            是否查询分类中的书籍，用于生成静态JSON于OSS
            if (bookInfo)
            {
                if (it.layer == 2)
                {
                    it.tempMap = [:];
                    it.tempMap["bookList"] = this.queryBooks8BookCateBookDetail(it.id)?.each { book ->
                        book.cancelLazyEr();
                    };
                }
            }
        }
    }

    List<Book> queryBooks8BookCateBookDetail(String bookCateBookDetailId)
    {
        List<Book> bookList = new ArrayList();
        this.createNativeQuery4Params("select b.id,b.name,b.imgurl,b.ishot,b.sortnum,b.zxid,b.booktype,b.bookcate,b.booksubcate,bcbd.bookcatedetailid from bookcatebookdetail bcbd left join book b on bcbd.bookid = b.id where b.ismarket = :isMarket and bcbd.bookcatedetailid = :bookCateBookDetailId order by b.sortnum", ["isMarket": true, "bookCateBookDetailId": bookCateBookDetailId]).getResultList()?.each {
            Book book = new Book();
            book.id = it[0];
            book.name = it[1];
            book.imgUrl = it[2];
            book.isHot = it[3];
            book.sortNum = it[4];
            book.zxId = it[5];
            book.bookType = it[6];
            book.bookCate = it[7];
            book.bookSubCate = it[8];
            book.tempMap = [:];
            book.tempMap["bookcatedetailid"] = it[9];
            bookList << book;
        }
        return bookList;
    }

    @Transactional
    void deleteTheBooks8BookCateBookDetail(String bookId, String bookCateDetailId)
    {
        this.executeEQL("delete BookCateBookDetail where bookCateBookDetailPK.bookId = :bookId and bookCateBookDetailPK.bookCateDetailId = :bookCateDetailId", ["bookId": bookId, "bookCateDetailId": bookCateDetailId]);
    }

    List<Word> queryWordList4WordType(int wordType, String label)
    {
        List wordList = new ArrayList();
        this.createNativeQuery4Params("select w.name,w.description,w.imgurl,w.phonetics,w.sortnum,w.updatename,wl.sortnum as wlsortnum from word w left join wordlabel wl on w.name = wl.wordid where wl.wordtype = :wordtype and wl.label = :label order by wl.sortnum",
                ["wordtype": wordType, "label": label]).getResultList()?.each {
            Word word = new Word();
            word.name = it[0];
            word.description = it[1];
            word.imgUrl = it[2];
            word.phonetics = it[3];
            word.sortNum = it[6] ? it[6] : -1;
            word.updateName = it[5];
            wordList << word;
        };
        return wordList;
    }

    List<WordXing> queryWordXingList8Word(String wordId)
    {
        return this.queryObject("select xing from WordXing xing where xing.word.name = :wordId", ["wordId": wordId]);
    }

    List<WordEg> queryWordEgList8Word(String wordId)
    {
        return this.queryObject("select eg from WordEg eg where eg.word.name = :wordId", ["wordId": wordId]);
    }

    List<WordAssociate> queryWordAssociateList8Word(String wordId)
    {
        return this.queryObject("select ass from WordAssociate ass where ass.word.name = :wordId", ["wordId": wordId]);
    }

    List<Word> queryWordList4WordTypeFatch(int wordType, String label)
    {
        this.queryWordList4WordType(wordType, label)?.each { word ->
            word.wordXingList = this.queryWordXingList8Word(word.name);
            word.wordEgList = this.queryWordEgList8Word(word.name);
            word.wordAssociateList = this.queryWordAssociateList8Word(word.name);
        }
    }

    Word queryTheWordFatch(String wordId)
    {
        List wl = this.queryObject("select w from Word w where trim(w.name) = :name", ["name": wordId.trim()]);
        Word word = (wl == null || wl.size() == 0) ? null : wl.get(0);
        if (word != null)
        {
            word.wordXingList = this.queryWordXingList8Word(word.name);
            word.wordEgList = this.queryWordEgList8Word(word.name);
            word.wordAssociateList = this.queryWordAssociateList8Word(word.name);
        }
        return word;
    }

    List<Word> queryWordListFatch8WordUpdateName(String wordUpdateName)
    {
        List wl = this.queryObject("select w from Word w where trim(w.updateName) = :name", ["name": wordUpdateName]);
        wl?.each { word ->
            word.wordXingList = this.queryWordXingList8Word(word.name);
            word.wordEgList = this.queryWordEgList8Word(word.name);
            word.wordAssociateList = this.queryWordAssociateList8Word(word.name);
        }
        return wl;
    }

    @Transactional
    void deleteTheWordXing8Id(String id)
    {
        this.executeEQL("delete WordXing where id = :id", ["id": id]);
    }

    @Transactional
    void deleteTheWordEg8Id(String id)
    {
        this.executeEQL("delete WordEg where id = :id", ["id": id]);
    }

    @Transactional
    void deleteTheWordAssociate8Id(String id)
    {
        this.executeEQL("delete WordAssociate where id = :id", ["id": id]);
    }

    @Transactional
    void deleteTheWord(String wordId)
    {
        Word word = this.findObjectById(Word.class, wordId);
        //oss
        if (!(word.imgUrl in [null, ""]))
        {
            OSSClient ossClient = OtherUtils.genOSSClient();
            ossClient.deleteObject(OtherUtils.givePropsValue("ali_oss_bucketName"), word.imgUrl);
            ossClient.shutdown();
        }
        //oss end
        this.executeEQL("delete WordXing where word.name = :wordId", ["wordId": wordId]);
        this.executeEQL("delete WordEg where word.name = :wordId", ["wordId": wordId]);
        this.executeEQL("delete WordAssociate where word.name = :wordId", ["wordId": wordId]);
        this.executeEQL("delete Word where name = :wordId", ["wordId": wordId]);
        this.executeEQL("delete WordLabel where wordLabelPK.wordId = :wordId", ["wordId": wordId]);
    }

    @Transactional
    deleteTheWordJoin(WordLabelPK wordLabelPK)
    {
        this.executeEQL("delete WordLabel where wordLabelPK = :wordLabelPK", ["wordLabelPK": wordLabelPK])
    }

    @Transactional
    Word updateWord(int wordType, String label, Word word)
    {
        Word newWord = new Word();
        BeanCopy.beans(word, newWord).copy();
        newWord.cancelLazyEr();
        this.updateObject(newWord);
        word?.wordXingList?.each { xing ->
            if (xing.id in [null, ""])
            {
                xing.id = MathUtil.getPNewId();
            }
            xing.word = new Word();
            xing.word.name = word.name;
            this.updateObject(xing);
        }
        word?.wordAssociateList?.each { ass ->
            if (ass.id in [null, ""])
            {
                ass.id = MathUtil.getPNewId();
            }
            ass.word = new Word();
            ass.word.name = word.name;
            this.updateObject(ass);
        }
        word?.wordEgList?.each { eg ->
            if (eg.id in [null, ""])
            {
                eg.id = MathUtil.getPNewId();
            }
            eg.word = new Word();
            eg.word.name = word.name;
            this.updateObject(eg);
        }
        WordLabel wordLabel = new WordLabel();
        wordLabel.wordLabelPK = new WordLabelPK();
        wordLabel.wordLabelPK.wordId = word.name;
        wordLabel.wordLabelPK.wordType = wordType;
        wordLabel.wordLabelPK.label = label;
        this.updateObject(wordLabel);
        return newWord;
    }

    void ganBookList()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        // oss
        OSSClient ossClient = OtherUtils.genOSSClient();
        ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/bookList.json", new ByteArrayInputStream(objectMapper.writeValueAsString([
                "book": ({
                    Map bookMap = [:];
                    for (byte t in AccType.getBookTypes())
                    {
                        for (byte c in AccType.getBookCates())
                        {
                            bookMap["$t$c"] = this.queryObject("select b from Book b where b.bookType = :bookType and b.bookCate = :bookCate and b.isMarket = :isMarket order by b.sortNum", ["bookType": t, "bookCate": c, "isMarket": true])?.each {
                                it.cancelLazyEr();
                            }
                            if (bookMap["$t$c"] != null)
                            {
                                ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/${t}${c}.json", new ByteArrayInputStream(objectMapper.writeValueAsString(bookMap["$t$c"]).getBytes("UTF-8")));
                                ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/${t}${c}.json", CannedAccessControlList.PublicRead);
                            }
                        }
                        bookMap["type_$t"] = this.queryObject("select b from Book b where b.bookType = :bookType and b.isMarket = :isMarket order by b.sortNum", ["bookType": t, "isMarket": true])?.each {
                            it.cancelLazyEr();
                        }
                        if (bookMap["type_$t"] != null)
                        {
                            ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/type_${t}.json", new ByteArrayInputStream(objectMapper.writeValueAsString(bookMap["type_$t"]).getBytes("UTF-8")));
                            ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/type_${t}.json", CannedAccessControlList.PublicRead);
                        }
                    }
                    return bookMap;
                }).call()
        ]).getBytes("UTF-8")));
        ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/bookList.json", CannedAccessControlList.PublicRead);
        // oss end
    }

    void ganTheBookJson(String bookId)
    {
        Book book = this.findObjectById(Book.class, bookId);
        book.cancelLazyEr();
        book.bookUniList = new ArrayList();
        this.queryBookUniList8Book(bookId)?.each { bookUni ->
            bookUni.cancelLazyEr();
            bookUni.book = null;
            book.bookUniList.add(bookUni);
        };
        ObjectMapper objectMapper = new ObjectMapper();
        // oss
        OSSClient ossClient = OtherUtils.genOSSClient();
        ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/${bookId}/bookSections.json", new ByteArrayInputStream(objectMapper.writeValueAsString(book).getBytes("UTF-8")));
        ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/${bookId}/bookSections.json", CannedAccessControlList.PublicRead);


        // oss end
    }

    void ganTheBookUniWordJson(String bookId, int wordType, String label)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        // oss
        OSSClient ossClient = OtherUtils.genOSSClient();
        ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/${bookId}/${label}/words.json", new ByteArrayInputStream(objectMapper.writeValueAsString(
                [words: ({
                    this.queryWordList4WordTypeFatch(wordType, label)?.each { word ->
                        word?.wordXingList?.each {
                            it.cancelLazyEr();
                            it.word = null;
                        }
                        word?.wordEgList?.each {
                            it.cancelLazyEr();
                            it.word = null;
                        }
                        word?.wordAssociateList?.each {
                            it.cancelLazyEr();
                            it.word = null;
                        }
                    }
                }).call()]
        ).getBytes("UTF-8")));
        ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/${bookId}/${label}/words.json", CannedAccessControlList.PublicRead);
        // oss end
    }

    void ganTheBookWordJson(String bookId)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        // oss
        OSSClient ossClient = OtherUtils.genOSSClient();
        ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/${bookId}/bookWords.json", new ByteArrayInputStream(objectMapper.writeValueAsString([
                "words": ({
                    List<Word> wordList = new ArrayList();
                    this.createNativeQuery4Params("""
select w.name,w.description,w.imgurl,w.phonetics,w.sortnum,w.updatename from word w left join wordlabel wl on w.name = wl.wordid left join bookuni bu on bu.id = wl.label where bu.book_id = :bookId
""", ["bookId": bookId]).getResultList()?.each {
                        Word word = new Word();
                        word.name = it[0];
                        word.description = it[1];
                        word.imgUrl = it[2];
                        word.phonetics = it[3];
                        word.sortNum = it[4];
                        word.updateName = it[5];
                        word = this.queryTheWordFatch(word.name);
                        word?.wordXingList?.each {
                            it.cancelLazyEr();
                            it.word = null;
                        }
                        word?.wordEgList?.each {
                            it.cancelLazyEr();
                            it.word = null;
                        }
                        word?.wordAssociateList?.each {
                            it.cancelLazyEr();
                            it.word = null;
                        }
                        wordList << word;
                    }
                    return wordList;
                }).call()
        ]).getBytes("UTF-8")));
        ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/${bookId}/bookWords.json", CannedAccessControlList.PublicRead);
        // oss end
    }

    void ganBookVideoJson(String bookId)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        // oss
        OSSClient ossClient = OtherUtils.genOSSClient();
        ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/${bookId}/bookVideos.json", new ByteArrayInputStream(objectMapper.writeValueAsString(
                [videos: ({
                    def map = [:];
                    this.queryBookUniList8Book(bookId)?.each { uni ->
                        map[uni.id] = this.queryObject("select vc from VideoContent vc where vc.bookId = :bookId and vc.bookUniId = :bookUniId", ["bookId": bookId, "bookUniId": uni.id])?.each { video ->
                            video.cancelLazyEr();
                        }
                    }
                    println map.dump();
                    return map;
                }).call()]
        ).getBytes("UTF-8")));
        ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/${bookId}/bookVideos.json", CannedAccessControlList.PublicRead);
        // oss end
    }

    void ganBookQuestionJson(String bookId)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        // oss
        OSSClient ossClient = OtherUtils.genOSSClient();
        this.queryBookUniList8Book(bookId)?.each { bookUni ->
            ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/${bookId}/${bookUni.id}/question.json", new ByteArrayInputStream(objectMapper.writeValueAsString(
                    [totalFen:({
                        return this.createNativeQuery4Params("select sum(fen) from questionnaireItems where bookid = :bookId and bookuniid = :bookUniId",["bookId":bookId,"bookUniId":bookUni.id]).getSingleResult();
                    }).call(),
                     questionnaireItems: ({
                        List l = new ArrayList();
                        this.queryQuestionnaireItemss8UniId(bookId,bookUni.id).each {q1->
                            QuestionnaireItems qis = this.queryTheQuestionnaireItemsFatch(q1.id);
                            JsonSlurper jsl = new JsonSlurper();
                            qis.tempMap = [:];
                            qis.tempMap["content"] = jsl.parseText(qis.content);
                            qis.content = null;
                            qis.questionnaireItemDetailList.each {qid->
                                qid.tempMap = [:];
                                qid.tempMap["content"] = jsl.parseText(qid.content);
                                qid.tempMap["optionContent"] = jsl.parseText(qid.optionContent);
                                qid.content = null;
                                qid.optionContent = null;
                            }
                            l << qis;
                        }
                        return l;
                    }).call()]
            ).getBytes("UTF-8")));
            ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/${bookId}/${bookUni.id}/question.json", CannedAccessControlList.PublicRead);
        };
        // oss end
    }

    List<VideoContent> queryVideos8UniId(String bookId, String bookUniId)
    {
        return this.queryObject("select v from VideoContent v where v.bookId = :bookId and v.bookUniId = :bookUniId order by v.sortNum", ["bookId": bookId, "bookUniId": bookUniId]);
    }

    @Transactional
    void delTheVideoContent(String id)
    {
        this.executeEQL("delete VideoContent where id = :id", ["id": id]);
    }

    List<QuestionnaireItems> queryQuestionnaireItemss8UniId(String bookId, String bookUniId)
    {
        return this.queryObject("select q from QuestionnaireItems q where q.bookId = :bookId and q.bookUniId = :bookUniId order by q.sortNum", ["bookId": bookId, "bookUniId": bookUniId]);
    }

    QuestionnaireItems queryTheQuestionnaireItemsFatch(String questionId)
    {
        QuestionnaireItems questionnaireItems = this.findObjectById(QuestionnaireItems.class, questionId);
        questionnaireItems.cancelLazyEr();
        questionnaireItems.questionnaireItemDetailList = this.queryObject("select qd from QuestionnaireItemDetail qd where qd.questionnaireItems.id = :id order by qd.sortNum", ["id": questionId])?.each {
            it.cancelLazyEr();
            it.questionnaireItems = null;
        }
        return questionnaireItems;
    }

    @Transactional
    void updateQuestionItemComFen(String bookId)
    {
        this.queryObject("select uni from BookUni uni where uni.book.id = :bookId",["bookId":bookId])?.each {uni->
            this.queryQuestionnaireItemss8UniId(bookId,uni.id)?.each {qi->
                this.createNativeQuery4Params("update questionnaireitems set fen = (select case when sum(fen) is null then 0 else sum(fen) end from questionnaireitemdetail where questionnaireitems_id = :quesId) where id = :quesId and thetype != 5",["quesId":qi.id]).executeUpdate();
            }
        }
    }

    @Transactional
    QuestionnaireItems updateQuestionItem(QuestionnaireItems questionnaireItems)
    {
        QuestionnaireItems newQuestionnaireItems1 = new QuestionnaireItems();
        BeanCopy.beans(questionnaireItems, newQuestionnaireItems1).copy();
        newQuestionnaireItems1.cancelLazyEr();
        if (newQuestionnaireItems1.id in [null, ""])
        {
            newQuestionnaireItems1.id = MathUtil.getPNewId();
        }
        if (newQuestionnaireItems1.fen == null || newQuestionnaireItems1.fen < 0)
        {
            newQuestionnaireItems1.fen = 0;
        }
        this.updateObject(newQuestionnaireItems1);
        questionnaireItems?.questionnaireItemDetailList?.each { qd ->
            if (qd.id in [null, ""])
            {
                qd.id = MathUtil.getPNewId();
            }
            qd.questionnaireItems = new QuestionnaireItems();
            qd.questionnaireItems.id = newQuestionnaireItems1.id;
            this.updateObject(qd);
        }
        return newQuestionnaireItems1;
    }

    @Transactional
    void deleteQuestionnaireItemDetail(String id)
    {
        this.executeEQL("delete QuestionnaireItemDetail where id = :id", ["id": id]);
    }

    @Transactional
    void deleteQuestionnaireItems(String id)
    {
        this.executeEQL("delete QuestionnaireItemDetail where questionnaireItems.id = :id", ["id": id]);
        this.executeEQL("delete QuestionnaireItems where id = :id", ["id": id]);
    }

    @Transactional
    void updateSort(byte type,List sortList)
    {
        switch (type)
        {
//            book
            case (0 as byte):
                for(Map map in sortList)
                {
                    this.executeEQL("update Book set sortNum = :sortNum where id = :id",["id":map.bookId,"sortNum":map.sortNum]);
                }
                break;
//            bookUni
            case (1 as byte):
                for(Map map in sortList)
                {
                    this.executeEQL("update BookUni set sortNum = :sortNum where id = :id",["id":map.bookUniId,"sortNum":map.sortNum]);
                }
                break;
        //            video
            case (2 as byte):
                for(Map map in sortList)
                {
                    this.executeEQL("update VideoContent set sortNum = :sortNum where id = :id",["id":map.videoId,"sortNum":map.sortNum]);
                }
                break;
        //            word
            case (3 as byte):
                for(Map map in sortList)
                {
                    this.executeEQL("update WordLabel set sortNum = :sortNum where wordLabelPK.wordId = :wordId and wordLabelPK.label = :label and wordLabelPK.wordType = :wordType",["wordId":map.wordId,"label":map.label,"wordType":map.wordType,"sortNum":map.sortNum]);
                }
                break;
        //            question
            case (4 as byte):
                for(Map map in sortList)
                {
                    this.executeEQL("update QuestionnaireItems set sortNum = :sortNum where id = :id",["id":map.questionId,"sortNum":map.sortNum as int]);
                }
                break;
        //            questionDetail
            case (5 as byte):
                for(Map map in sortList)
                {
                    this.executeEQL("update QuestionnaireItemDetail set sortNum = :sortNum where id = :id",["id":map.questionDetailId,"sortNum":map.sortNum as int]);
                }
                break;
//            AssessItemDetail
            case (6 as byte):
                for(Map map in sortList)
                {
                    this.executeEQL("update AssessItemDetail set sortNum = :sortNum where id = :id",["id":map.assessItemDetailId,"sortNum":map.sortNum as int]);
                }
                break;
        }
    }

    List queryBookListIndexPage(String prxStr)
    {
        List l = new ArrayList();
        this.createNativeQuery4Params("select book.id,book.name,book.imgurl,SUBSTRING(bcbd.bookcatedetailid,1,2),bcbd.bookcatedetailid from book left join bookcatebookdetail as bcbd on bcbd.bookid = book.id where book.ismarket = TRUE and SUBSTRING(bcbd.bookcatedetailid,1,2) = :prx order by book.createdate desc LIMIT 18",["prx":prxStr]).getResultList()?.each {
            Book book = new Book();
            book.id = it[0];
            book.name = it[1];
            book.imgUrl = it[2];
            book.tempMap = [:];
            book.tempMap["subCate"] = it[3];
            book.tempMap["cate"] = it[4];
            l << book;
        }
        return l;
    }

    @Transactional
    void moveOrCopyTheQuestionnaireItems(byte type,String questionnaireItemsId,String newBookId,String newBookUniId)
    {
        switch (type)
        {
            case 0 as byte:
                this.executeEQL("update QuestionnaireItems set bookId = :bookId,bookUniId = :bookUniId where id = :id",["bookId":newBookId,"bookUniId":newBookUniId,"id":questionnaireItemsId]);
                break;
            case 1 as byte:
//                暂未实现
                break;
        }
    }

    @Transactional
    AssessItems updateAssessItems(AssessItems assessItems)
    {
        AssessItems newAssessItems = new AssessItems();
        BeanCopy.beans(assessItems, newAssessItems).copy();
        newAssessItems.cancelLazyEr();
        if (newAssessItems.id in [null, ""])
        {
            newAssessItems.id = MathUtil.getPNewId();
        }
        this.updateObject(newAssessItems);
        assessItems?.assessItemDetailList?.each { qd ->
            if (qd.id in [null, ""])
            {
                qd.id = MathUtil.getPNewId();
            }
            qd.assessItems = new AssessItems();
            qd.assessItems.id = newAssessItems.id;
            this.updateObject(qd);
        }
        return newAssessItems;
    }

}
