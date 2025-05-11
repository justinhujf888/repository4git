package com.weavict.edu.rest


import com.aliyun.oss.OSSClient
import com.aliyun.oss.model.CannedAccessControlList
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.edu.entity.Assess
import com.weavict.edu.entity.AssessAnswerReport
import com.weavict.edu.entity.AssessItems
import com.weavict.edu.entity.Book
import com.weavict.edu.entity.BookCateBookDetail
import com.weavict.edu.entity.BookSubBook
import com.weavict.edu.entity.BookUni
import com.weavict.edu.entity.QuestionnaireItems
import com.weavict.edu.entity.VideoContent
import com.weavict.edu.entity.Word
import com.weavict.edu.entity.WordLabel
import com.weavict.edu.entity.WordLabelPK
import com.weavict.edu.module.TeachService
import com.weavict.common.util.MathUtil
import com.weavict.website.common.OtherUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.TransactionDefinition
import org.springframework.web.bind.annotation.RequestBody

import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType

/**
 * Created by Justin on 2018/6/10.
 */
@Path("/teach")
class TeachRest extends BaseRest
{
    @Autowired
    TeachService teachService;

    @Context
    HttpServletRequest request;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/queryTheBook")
    String queryTheBook(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "book": ({
                        Book book = teachService.findObjectById(Book.class,query.bookId);
                        book?.cancelLazyEr();
                        return book;
                    }).call()
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
    @Path("/queryTheBookUni")
    String queryTheBookUni(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "bookUni": ({
                        BookUni bookUni = teachService.findObjectById(BookUni.class,query.bookUniId);
                        bookUni?.cancelLazyEr();
                        return bookUni;
                    }).call()
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
    @Path("/queryBookList4Type")
    String queryBookList4Type(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "bookList": ({
                        return this.teachService.queryBookList4Type(query.bookType as byte,query.zxId)?.each {b->
                            b?.cancelLazyEr();
                        };
                    }).call()
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
    @Path("/queryBookSubBookList")
    String queryBookSubBookList(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "subBookList": ({
                        return teachService.newQueryUtils(true,true).masterTable("booksubbook","bs",[
                                [sf:"bookid",bf:"bookSubBookPK.bookId"],
                                [sf:"subbookid",bf:"bookSubBookPK.subBookId"],
                                [sf:"booktype",bf:"bookType"]
                        ]).where("bs.bookid = :bookId",[bookId:query.bookId],null,null)
                            .orderBy("bs.booktype")
                            .beanSetup(BookSubBook.class,null,null)
                            .buildSql().run().content?.each {
                            Book book = teachService.findObjectById(Book.class,it.bookSubBookPK.subBookId);
                            book.cancelLazyEr();
                            it.tempMap = [:];
                            it.tempMap.book = book;
                        };
                    }).call()
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
    @Path("/delSubBook6TypeBook")
    String delSubBook6TypeBook(@RequestBody Map<String, Object> query)
    {
        try
        {
            teachService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                teachService.deleteTheObject8Fields("booksubbook","bookid=:bookId and subbookId=:subBookId and booktype=:bookType",[bookId:query.bookId,subBookId:query.subBookId,bookType:query.bookType as byte],true);
            });
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
    @Path("/saveSubBookList")
    String saveSubBookList(@RequestBody Map<String, Object> query)
    {
        try
        {
            List<BookSubBook> bookList = objToBean(query.bookList,List.class,null);
            teachService.transactionCall(TransactionDefinition.PROPAGATION_REQUIRES_NEW,{
                for(BookSubBook bookSubBook in bookList)
                {
                    teachService.updateObject(bookSubBook);
                }
            });
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
    @Path("/queryBookUniList8Book")
    String queryBookUniList8Book(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "bookUniList": ({
                        return this.teachService.queryBookUniList8Book(query.bookId)?.each {bu->
                            bu.cancelLazyEr();
                        };
                    }).call()
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
    @Path("/queryBookCateList")
    String queryBookCateList(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "bookCateList": ({
                        return teachService.queryBookCateList()?.each {
                            it.cancelLazyEr();
                        };
                    }).call()
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
    @Path("/queryBookCateDetailAllLayer")
    String queryBookCateDetailAllLayer(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "bookCateList": ({
                        if (query.bookInfo as boolean)
                        {
                            List l = teachService.queryBookCateList()?.each {bc->
                                bc.bookCateDetailList = teachService.queryBookCateDetailAllLayer(bc.id,null,0 as byte,true);
                            }
//                            oss begin
                            OSSClient ossClient = OtherUtils.genOSSClient();
                            ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"),"teach/json/book/bookCate.json",new ByteArrayInputStream(objectMapper.writeValueAsString(l).getBytes("UTF-8")));
                            ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "teach/json/book/bookCate.json", CannedAccessControlList.PublicRead);
                            ossClient.shutdown();
//                            oss end
                        }
                        else
                        {
                            return teachService.queryBookCateList()?.each {bc->
                                bc.bookCateDetailList = teachService.queryBookCateDetailAllLayer(bc.id,null,0 as byte,false);
                            }
                        }
                    }).call()
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
    @Path("/queryBooks8BookCateBookDetail")
    String queryBooks8BookCateBookDetail(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "bookList": ({
                        return teachService.queryBooks8BookCateBookDetail(query.bookCateBookDetailId);
                    }).call()
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
    @Path("/updateBookCateBookDetailList")
    String updateBookCateBookDetailList(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            List bcbdList = objToBean(query.bcbdList, List.class, objectMapper);
            bcbdList?.each {
                teachService.updateTheObject(it as BookCateBookDetail);
            }
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
    @Path("/deleteTheBooks8BookCateBookDetail")
    String deleteTheBooks8BookCateBookDetail(@RequestBody Map<String, Object> query)
    {
        try
        {
            teachService.deleteTheBooks8BookCateBookDetail(query.bookId,query.bookCateDetailId);
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
    @Path("/queryWordList4WordType")
    String queryWordList4WordType(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "wordList": ({
                        return teachService.queryWordList4WordType(query.wordType as int,query.label);
                    }).call()
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
    @Path("/queryWordList4WordTypeFatch")
    String queryWordList4WordTypeFatch(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "wordList": ({
                        return teachService.queryWordList4WordTypeFatch(query.wordType,query.label);
                    }).call()
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
    @Path("/queryTheBook8Name")
    String queryTheBook8Name(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "book": ({
                        return teachService.queryTheBook8Name(query.bookName)?.each {
                            it.cancelLazyEr();
                        };
                    }).call()
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
    @Path("/queryTheBookUni8Name4Book")
    String queryTheBookUni8Name4Book(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "bookUni": ({
                        return teachService.queryTheBookUni8Name4Book(query.bookId,query.bookUniName)?.each {
                            it.cancelLazyEr();
                        };;
                    }).call()
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
    @Path("/queryTheWordFatch")
    String queryTheWordFatch(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "word": ({
                        Word word = teachService.queryTheWordFatch(query.wordId.trim());
                        word?.wordXingList?.each {
                            it.cancelLazyEr();
                        }
                        word?.wordEgList?.each {
                            it.cancelLazyEr();
                        }
                        word?.wordAssociateList?.each {
                            it.cancelLazyEr();
                        }
                        return word;
                    }).call()
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
    @Path("/queryWordListFatch8WordUpdateName")
    String queryWordListFatch8WordUpdateName(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "wordList": ({
                        List wl = teachService.queryWordListFatch8WordUpdateName(query.updateName.trim());
                        wl?.each {word->
                            word?.wordXingList?.each {
                                it.cancelLazyEr();
                            }
                            word?.wordEgList?.each {
                                it.cancelLazyEr();
                            }
                            word?.wordAssociateList?.each {
                                it.cancelLazyEr();
                            }
                        }
                        return wl;
                    }).call()
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
    @Path("/updateBook")
    String updateBook(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Book book = objToBean(query.book, Book.class, objectMapper);
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "book"   : (
                             {
                                 if (book.id in [null, ""])
                                 {
                                     book.id = MathUtil.getPNewId();
                                     book.createDate = new Date();
                                 }
                                 book = teachService.updateTheObject(book);
                                 book.cancelLazyEr();
//                                 teachService.ganBookList();
                                 if (book.isMarket)
                                 {
                                     teachService.ganTheBookJson(book.id);
                                     switch (book.bookType)
                                     {
                                         case 0 as byte:
                                             teachService.ganTheBookWordJson(book.id);
                                             teachService.queryBookUniList8Book(book.id).each {uni->
                                                 teachService.ganTheBookUniWordJson(book.id,0,uni.id);
                                             }
                                             break;
                                         case 1 as byte:
                                            teachService.updateQuestionItemComFen(book.id)
                                            teachService.ganBookQuestionJson(book.id);
                                            break;
                                         case 3 as byte:
                                            teachService.ganBookVideoJson(book.id);
                                            break;
                                         default:
                                            break;
                                     }
                                 }
                                 return book;
                             }
                     ).call()
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
    @Path("/updateBookUni")
    String updateBookUni(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            BookUni bookUni = objToBean(query.bookUni, BookUni.class, objectMapper);
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "bookUni"   : (
                             {
                                 if (bookUni.id in [null, ""])
                                 {
                                     bookUni.id = MathUtil.getPNewId();
                                 }
                                 bookUni = teachService.updateTheObject(bookUni);
                                 bookUni.cancelLazyEr();
                                 return bookUni;
                             }
                     ).call()
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
    @Path("/updateWordJoin")
    String updateWordJoin(@RequestBody Map<String, Object> query)
    {
        try
        {
            WordLabel wordLabel = objToBean(query.wordLabel, WordLabel.class, null);
            teachService.updateTheObject(wordLabel);
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
    @Path("/updateWord")
    String updateWord(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            Word word = objToBean(query.word, Word.class, objectMapper);
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "word"   : (
                             {
                                 word = teachService.updateWord(query.wordType as int,query.label,word);
                                 word.cancelLazyEr();
                                 return word;
                             }
                     ).call()
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
    @Path("/deleteTheWordXing8Id")
    String deleteTheWordXing8Id(@RequestBody Map<String, Object> query)
    {
        try
        {
            this.teachService.deleteTheWordXing8Id(query.xingId);
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
    @Path("/deleteTheWordEg8Id")
    String deleteTheWordEg8Id(@RequestBody Map<String, Object> query)
    {
        try
        {
            this.teachService.deleteTheWordEg8Id(query.egId);
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
    @Path("/deleteTheWordAssociate8Id")
    String deleteTheWordAssociate8Id(@RequestBody Map<String, Object> query)
    {
        try
        {
            this.teachService.deleteTheWordAssociate8Id(query.assId);
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
    @Path("/deleteTheWord")
    String deleteTheWord(@RequestBody Map<String, Object> query)
    {
        try
        {
            this.teachService.deleteTheWord(query.wordId);
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
    @Path("/deleteTheWordJoin")
    String deleteTheWordJoin(@RequestBody Map<String, Object> query)
    {
        try
        {
            this.teachService.deleteTheWordJoin(this.objToBean(query.wordLabelPK, WordLabelPK.class,null));
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
    @Path("/updateVideo")
    String updateVideo(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            VideoContent videoContent = objToBean(query.videoContent, VideoContent.class, objectMapper);
            teachService.updateTheObject(videoContent);
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
    @Path("/queryVideos8UniId")
    String queryVideos8UniId(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "videoList": ({
                        return teachService.queryVideos8UniId(query.bookId,query.bookUniId)?.each {
                            it.cancelLazyEr();
                        }
                    }).call()
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
    @Path("/queryQuestionnaireItemss8UniId")
    String queryQuestionnaireItemss8UniId(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "questionList": ({
                        return teachService.queryQuestionnaireItemss8UniId(query.bookId,query.bookUniId)?.each {
                            it.cancelLazyEr();
                        }
                    }).call()
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
    @Path("/deleteQuestionnaireItemDetail")
    String deleteQuestionnaireItemDetail(@RequestBody Map<String, Object> query)
    {
        try
        {
            teachService.deleteQuestionnaireItemDetail(query.id);
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
    @Path("/deleteQuestionnaireItem")
    String deleteQuestionnaireItem(@RequestBody Map<String, Object> query)
    {
        try
        {
            teachService.deleteQuestionnaireItems(query.id);
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
    @Path("/saveQuestionnaireItem")
    String saveQuestionnaireItem(@RequestBody Map<String, Object> query)
    {
        try
        {
            QuestionnaireItems questionnaireItems = this.objToBean(query.questionnaireItems,QuestionnaireItems.class,null);
            teachService.updateQuestionItem(questionnaireItems);
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
    @Path("/queryTheQuestionnaireItems")
    String queryTheQuestionnaireItems(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "questionnaireItems": ({
                        return teachService.queryTheQuestionnaireItemsFatch(query.questionId);
                    }).call()
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
    @Path("/updateSort")
    String updateSort(@RequestBody Map<String, Object> query)
    {
        try
        {
            List sortList = this.objToBean(query.sortData,List.class,null);
            teachService.updateSort(query.type as byte,sortList);
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
    @Path("/queryTheVideoContent")
    String queryTheVideoContent(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "videoContent": ({
                        VideoContent videoContent = teachService.findObjectById(VideoContent.class,query.videoId);
                        videoContent?.cancelLazyEr();
                        return videoContent;
                    }).call()
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
    @Path("/updateVideoContent")
    String updateVideoContent(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            VideoContent videoContent = objToBean(query.videoContent, VideoContent.class, objectMapper);
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "videoContent"   : (
                             {
                                 if (videoContent.id in [null, ""])
                                 {
                                     videoContent.id = MathUtil.getPNewId();
                                 }
                                 videoContent = teachService.updateTheObject(videoContent);
                                 videoContent.cancelLazyEr();
                                 return videoContent;
                             }
                     ).call()
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
    @Path("/delTheVideoContent")
    String delTheVideoContent(@RequestBody Map<String, Object> query)
    {
        try
        {
            teachService.delTheVideoContent(query.id);
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
    @Path("/buildIndexPageBookList")
    String buildIndexPageBookList(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            def map = [:];
            map["02"] = teachService.queryBookListIndexPage("02");
            map["00"] = teachService.queryBookListIndexPage("00");
            OSSClient ossClient = OtherUtils.genOSSClient();
            ossClient.putObject(OtherUtils.givePropsValue("ali_oss_bucketName"), "system/indexBooks.json", new ByteArrayInputStream(objectMapper.writeValueAsString(
                    map
            ).getBytes("UTF-8")));
            ossClient.setObjectAcl(OtherUtils.givePropsValue("ali_oss_bucketName"), "system/indexBooks.json", CannedAccessControlList.PublicRead);
            ossClient.shutdown();
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
    @Path("/moveOrCopyTheQuestionnaireItems")
    String moveOrCopyTheQuestionnaireItems(@RequestBody Map<String, Object> query)
    {
        try
        {
            this.teachService.moveOrCopyTheQuestionnaireItems(query.type as byte,query.questionnaireItemId,query.newBookId,query.newBookUniId);
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
    @Path("/queryAssess8ObjId")
    String queryAssess8ObjId(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "datas": ({
                        return teachService.newQueryUtils(false).masterTable("Assess","aes",null)
                                .where("aes.objId=:objId",["objId":query.objId as String],null,null)
                                .where("aes.appId=:appId",["appId":query.appId as String],"and",null)
                                .where("aes.theType=:type",["type":query.type as byte],"and",{return query.type as byte > -1 as byte})
                                .buildSql().run()
                    }).call()
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
    @Path("/queryAssessItems8AssessId")
    String queryAssessItems8AssessId(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "assessItemsList": ({
                        return teachService.newQueryUtils(false).masterTable("AssessItems",null,null)
                                                                .where("AssessItems.assess.id=:assessId",["assessId":query.assessId as String],null,null)
                                                                .orderBy("AssessItems.id")
//                                                                .pageLimit(10,0,"AssessItems.id")
                                                                .buildSql().run();
                    }).call()
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
    @Path("/queryAssessItemsDetailList8AssessItemId")
    String queryAssessItemsDetailList8AssessItemId(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "assessItemsDetailList": ({
                        return teachService.newQueryUtils(false).masterTable("AssessItemDetail",null,null)
                                .where("AssessItemDetail.assessItems.id=:assessItemId",["assessItemId":query.assessItemId as String],null,null)
                                .orderBy("AssessItemDetail.sortNum")
//                                                                .pageLimit(10,0,"AssessItems.id")
                                .buildSql().run();
                    }).call()
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
    @Path("/deleteAssessItemDetail")
    String deleteAssessItemDetail(@RequestBody Map<String, Object> query)
    {
        try
        {
            teachService.deleteTheObject8Fields("AssessItemDetail","id=:id",["id":query.id],true);
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
    @Path("/deleteAssess")
    String deleteAssess(@RequestBody Map<String, Object> query)
    {
        try
        {
            teachService.deleteTheObject8Fields("Assess","id=:id",["id":query.id],true);
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
    @Path("/updateAssessItems")
    String updateAssessItems(@RequestBody Map<String, Object> query)
    {
        try
        {
            teachService.updateAssessItems(objToBean(query.assessItems, AssessItems.class,null ));
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
    @Path("/updateAssess")
    String updateAssess(@RequestBody Map<String, Object> query)
    {
        try
        {
            teachService.updateTheObject(objToBean(query.assess, Assess.class,null ));
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
    @Path("/updateAssessAnswerReportList")
    String updateAssessAnswerReportList(@RequestBody Map<String, Object> query)
    {
        try
        {
            List l = objToBean(query.assessAnswerReportList, List.class,null );
            for(def r in l)
            {
                r = objToBean(r, AssessAnswerReport.class,null );
                teachService.updateTheObject(r);
            }
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
    @Path("/queryAssessAnswerReport")
    String queryAssessAnswerReport(@RequestBody Map<String, Object> query)
    {
        try
        {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString([
                    "status":"OK",
                    "assessAnswerReportList": ({
                        return teachService.newQueryUtils(false).masterTable("AssessAnswerReport","ar",null)
                                .where("ar.objId = :objId",["objId":query.objId],null,{return !(query.objId in [null,""])})
                                .where("ar.assessId = :assessId",["assessId":query.assessId],"and",{return !(query.assessId in [null,""])})
                                .where("ar.assessItemId = :assessItemId",["assessItemId":query.assessItemId],"and",{return !(query.assessItemId in [null,""])})
                                .where("ar.assessItemDetailId = :assessItemDetailId",["assessItemDetailId":query.assessItemDetailId],"and",{return !(query.assessItemDetailId in [null,""])})
                                .orderBy("ar.id")
//                                                                .pageLimit(10,0,"AssessItems.id")
                                .buildSql().run();
                    }).call()
            ]);
        }
        catch (Exception e)
        {
            processExcetion(e);
            return """{"status":"FA_ER"}""";
        }
    }

}
