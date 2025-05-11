package com.weavict.shop.rest;

import cn.hutool.core.date.DateUtil
import cn.hutool.core.io.resource.ClassPathResource
import cn.hutool.core.util.XmlUtil
import com.aliyun.oss.OSSClient
import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.common.camunda.Utils
import com.weavict.website.common.OtherUtils
import jakarta.annotation.Resource
import jodd.props.Props
import jodd.util.ClassLoaderUtil
import org.camunda.bpm.ProcessApplicationService
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult
import org.camunda.bpm.engine.DecisionService
import org.camunda.bpm.engine.HistoryService
import org.camunda.bpm.engine.RepositoryService
import org.camunda.bpm.engine.RuntimeService
import org.camunda.bpm.engine.TaskService
import org.camunda.bpm.engine.delegate.DelegateExecution
import org.camunda.bpm.engine.delegate.JavaDelegate
import org.camunda.bpm.engine.history.HistoricTaskInstance
import org.camunda.bpm.engine.repository.ProcessDefinition
import org.camunda.bpm.engine.runtime.ProcessInstance
import org.camunda.bpm.engine.task.Task
import org.camunda.bpm.engine.variable.Variables
import org.camunda.bpm.model.bpmn.BpmnModelInstance
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants
import org.camunda.bpm.model.bpmn.instance.ItemDefinition
import org.camunda.bpm.model.bpmn.instance.UserTask
import org.camunda.bpm.model.dmn.instance.Variable
import org.camunda.bpm.model.xml.instance.ModelElementInstance
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import org.w3c.dom.Document
import org.w3c.dom.NodeList


@Controller
@RequestMapping("/sm")
class TestEndPoint
{
    @Resource
    TaskService taskService;

    @Resource
    HistoryService historyService;

    @Resource
    RepositoryService repositoryService;

    @Resource
    RuntimeService runtimeService;

    @GetMapping(value = "/paytest")
    String payTest()
    {
        println "1111111111111111111111111";
        Props props = new Props();
        props.load(new ClassPathResource("/config/global.props").getStream());
        println props.getAllProfiles();
        println OtherUtils.givePropsValue("prxurl");
        OSSClient ossClient = OtherUtils.genOSSClient();
        return "OK";
    }

//    @ResponseBody
//    @GetMapping(value = "/test",produces = "application/json", consumes = "application/json")
    @GetMapping(value = "/test")
    String test()
    {
        try
        {
            println "aaaaaaaaaaaaabbbbbbbbbbbbbbbbbccccccccccccccccc";
//            println repositoryService.createProcessDefinitionQuery().active().processDefinitionKey("Process_mytest").latestVersion().list();

//            ProcessInstance processInstance = runtimeService.startProcessInstanceById(repositoryService.createProcessDefinitionQuery().active().processDefinitionKey("Process_mytest").latestVersion().singleResult().id,["userId":"justinhujf"]);

//            Document document = XmlUtil.readXML("D:\\DeveloperDriver\\eclipseworkspaces\\dcsw\\sp\\src\\main\\resources\\processes\\myTest.bpmn");
//            NodeList v = XmlUtil.getNodeListByXPath("//*[name()='bpmn:userTask']",document);
//            for (int i = 0 ; i < v.getLength() ; i ++)
//            {
//                println v.item(i).getAttributes().getNamedItemNS(BpmnModelConstants.CAMUNDA_NS, "assignee");
//            }

            BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(repositoryService.createProcessDefinitionQuery().active().processDefinitionKey("Process_mytest").latestVersion().singleResult().id);
//            for(UserTask ut in bpmnModelInstance.getModelElementsByType(UserTask.class))
//            {
//                println ut.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "assignee");
//            }

            for(Task t in taskService.createTaskQuery().taskAssignee("justinhujf").list())
            {
//                println t.dump();
                println Utils.getIntance(repositoryService).getNextAuditNodes("Process_mytest",t.taskDefinitionKey);
//                taskService.complete(t.id,["userId":"danny","objId":DateUtil.now(),"paymentFee":9200,"jifen":200]);
            }

//            for (Task t in taskService.createTaskQuery().taskAssignee("danny").list())
//            {
////                println t.dump();
////                println taskService.getVariables(t.id).dump();
////                println taskService.getVariable(t.id, "fee");
//
//                println Utils.getIntance(repositoryService).getNextAuditNodes(t);
//                taskService.complete(t.id, ["userId": "danny", "objId": DateUtil.now()]);
//            }

//            docker run -it --name postgres -v /home/postgres/data:/var/lib/postgresql -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=justin@db -e POSTGRES_DB=postgres -e ALLOW_IP_RANGE=0.0.0.0/0 -d -p 5432:5432 postgres:11
//            docker run -it --name postgres -v /mydatas/pgdatas:/var/lib/postgresql/data -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=justin@db -e POSTGRES_DB=postgres -e ALLOW_IP_RANGE=0.0.0.0/0 -d -p 5432:5432 postgres:11
//
//            docker exec -it 60a93af0bba5 bash
//             docker cp sources.list postgres:/etc/apt/sources.list
//            docker exec -it 容器名 /bin/bash
//            执行 apt-get update 等会，刷刷刷下完以后，执行apt-get install vim

//            # powershell / cmd
//            dism.exe /Online /Disable-Feature:Microsoft-Hyper-V
//
//            # powershell / cmd 管理员权限
//            # start 起始端口  num 表示可用端口数     按自己的需求来
//            netsh int ipv4 set dynamicport tcp start=30000 num=16383
//
//            # 排除ipv4动态端口占用 startport 起始端口 numberofports 端口数
//            netsh int ipv4 add excludedportrange protocol=tcp startport=50051 numberofports=1
//
//            dism.exe /Online /Enable-Feature:Microsoft-Hyper-V /All

//            docker run --name redis -p 6379:6379 -v /D/DeveloperDriver/DevelopTools/ApplicationServers/redis/redis.conf:/etc/redis/redis.conf -v /D/DeveloperDriver/DevelopTools/ApplicationServers/redis/data:/data/ -d redis:5.0 redis-server /etc/redis/redis.conf --appendonly yes

//            docker run --name nginx -p 80:80 -p 443:443 \
//-v /usr/local/src/docker/nginx/html:/usr/share/nginx/html \
//-v /usr/local/src/docker/nginx/conf/nginx.conf:/etc/nginx/nginx.conf/ \
//-v /usr/local/src/docker/nginx/conf.d:/etc/nginx/conf.d/ \
//-v /usr/local/src/docker/nginx/logs:/var/log/nginx \
//-v /usr/local/src/docker/nginx/ssl:/etc/nginx/ssl \
//--privileged=true -d --restart=always nginx:1.26.1

//            docker run --name nginx -p 80:80 -p 443:443 --privileged=true -d nginx:1.26.1


//            安装 acme.sh
//            注意： 以下所有操作均在容器内执行。
//
//            安装 acme.sh 依赖
//            # 设置阿里云镜像源
//            sed -i s@/deb.debian.org/@/mirrors.aliyun.com/@g /etc/apt/sources.list
//            apt-get clean
//            apt-get update
//            apt-get install -y git socat cron vim
//
//            安装 acme.sh
//            # 下载源码
//            git clone https://github.com/acmesh-official/acme.sh.git
//            cd ./acme.sh
//# 查看帮助
//./acme.sh -h
//            # 安装 acme.sh
//                ./acme.sh --install -m xxxxxxxx@qq.com
//
//安装成功后自动会添加更新证书定时任务
//# 查看 acme.sh 更新证书定时任务
//crontab -l

//            生成 SSL 证书
//            ./acme.sh --install-cert -d mydomain.com \
//--key-file       /etc/nginx/ssl/mydomain.com.key  \
//--fullchain-file /etc/nginx/ssl/mydomain.com.pem

//            if has old docker version
//            yum remove docker  docker-common docker-selinux docker-engine
//
//            yum install -y yum-utils device-mapper-persistent-data lvm2
//
//            choice one
//            yum-config-manager --add-repo http://download.docker.com/linux/centos/docker-ce.repo（中央仓库）
//            yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo（阿里仓库）
//
//            install list show
//            yum list docker-ce --showduplicates | sort -r
//
//            choice one beging install.
//            yum -y install docker-ce-18.03.1.ce
//
//            systemctl start docker
//            systemctl enable docker

//            用于export镜像后，另一台机器import后，被docker export出来的镜像在启动的时候需要指定command，用下面命令可以查看
//            docker ps -a --no-trunc











//			Deployment deployment = repositoryService.createDeployment().name("Process_mytest").addClasspathResource("processes/myTest.bpmn").deploy();
//          println deployment.id;
//          println deployment.name;


            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(
                    ["status": "OK",
                     "datas1": (
                             {
                                 List l = new ArrayList();
                                 for (ProcessDefinition o in repositoryService.createProcessDefinitionQuery().list())
                                 {
                                     l << ["pid": o.id, "pname": o.name];
                                 }
                                 return l;
                             }
                     ).call(),
                     "datas2": ({
                         List l = new ArrayList();
                         for (Task o in taskService.createTaskQuery().taskCandidateUser("justinhujf").list())
                         {
                             l << ["tid": o.id, "tname": o.name];
                         }
                         return l;
                     }).call()
                    ]
            );
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return """{"status":"FA_ER"}""";
        }
    }

    @Bean("serviceTask")
    JavaDelegate serviceTask()
    {
        return { execution ->
            println "ServiceTaskMy:";
            println execution.processBusinessKey;
            println execution.processInstanceId;
            println execution.processDefinitionId;
            println execution.getVariables()?.dump();
//            DecisionService decisionService  = execution.getProcessEngine().getDecisionService();
//            DmnDecisionTableResult dmnDecisionTableResult = decisionService.evaluateDecisionTableByKey("decisionTest", ["paymentFee":3200,"jifen":200]);
//            println dmnDecisionTableResult.getSingleResult()?.dump();
        } as JavaDelegate
    }

    @Bean("getDiscount")
    JavaDelegate getDiscount()
    {
        return { execution ->
            println "getDiscount:";
            println execution.getVariables()?.dump();
            execution.setVariable("discount", execution.getVariable("payDisdo"));
            execution.setVariable("fee", execution.getVariable("payFee"));
//            DecisionService decisionService  = execution.getProcessEngine().getDecisionService();
//            DmnDecisionTableResult dmnDecisionTableResult = decisionService.evaluateDecisionTableByKey("decisionTest", ["paymentFee":500,"jifen":2000]);
//            println dmnDecisionTableResult.getSingleResult()?.dump();
        } as JavaDelegate
    }
}

//@Service("serviceTask")
//class ServiceTaskMy implements JavaDelegate
//{
//    @Override
//    void execute(DelegateExecution execution) throws Exception
//    {
//        println "ServiceTaskMy";
//        println execution.processBusinessKey;
//        println execution.processInstanceId;
//        println execution.processDefinitionId;
//    }
//}

//@Service
//class ServiceTask
//{
//
//
//
//}
//
//@Service
//class GetDiscount
//{
//
//}



