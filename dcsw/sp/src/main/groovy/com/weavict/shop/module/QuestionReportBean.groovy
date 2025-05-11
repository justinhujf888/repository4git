package com.weavict.shop.module

import com.weavict.shop.entity.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service("quesReportBean")
class QuestionReportBean extends ModuleBean
{
    Questionnaire queryTheQuestionnaire(String qid)
    {
        this.findObjectById(Questionnaire.class,qid);
    }

    List<QuestionnaireItems> queryQuestionnaireItemsByTheQuestion(String qid)
    {
        this.queryObject("select q from QuestionnaireItems as q where q.question.id = :qid order by q.step",["qid":qid]);
    }

    List<QuestionnaireItemDetail> queryQuestionnaireItemsDetailsByTheItem(String itemId)
    {
        this.queryObject("select qd from QuestionnaireItemDetail as qd where qd.questionItem.id = :itemId order by qd.orderNum",["itemId":itemId]);
    }

    @Transactional
    void addTheQuestion(Questionnaire question)
    {
        this.addObject(question);
        //是否可以自动新增从表的List？
    }

    @Transactional
    void addTheQuesReport(QuestionnaireReport questionnaireReport)
    {
        if (questionnaireReport!=null)
        {
            questionnaireReport.createDate = new Date();
            this.updateObject(questionnaireReport);
        }
    }

    Questionnaire queryTheQuestionnaireAll(String qid)
    {
        Questionnaire questionnaire = this.queryTheQuestionnaire(qid);
        questionnaire.cancelLazyEr();
        questionnaire.questionItems = this.queryQuestionnaireItemsByTheQuestion(qid);
        questionnaire.questionItems?.each {qi->
            qi.cancelLazyEr();
            qi.question = null;
            qi.questionnaireItemDetailList = this.queryQuestionnaireItemsDetailsByTheItem(qi.id);
            qi.questionnaireItemDetailList?.each{quid->
                quid.cancelLazyEr();
                quid.questionItem = null;
            }
        }
        return questionnaire;
    }

    QuestionnaireReport queryQuestionReportCustomerId(String questionaireId,String customerId)
    {
        return this.findObjectById(QuestionnaireReport.class,new QuestionnaireReportPK(customerId,questionaireId));
    }
}
