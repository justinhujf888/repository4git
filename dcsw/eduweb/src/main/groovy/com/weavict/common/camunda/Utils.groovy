package com.weavict.common.camunda

import org.apache.commons.lang3.ObjectUtils
import org.apache.commons.lang3.StringUtils
import org.camunda.bpm.engine.HistoryService
import org.camunda.bpm.engine.RepositoryService
import org.camunda.bpm.engine.history.HistoricTaskInstance
import org.camunda.bpm.engine.repository.ProcessDefinition
import org.camunda.bpm.engine.task.Task
import org.camunda.bpm.model.bpmn.BpmnModelInstance
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants
import org.camunda.bpm.model.bpmn.instance.FlowNode
import org.camunda.bpm.model.bpmn.instance.MultiInstanceLoopCharacteristics
import org.camunda.bpm.model.bpmn.instance.SequenceFlow
import org.camunda.bpm.model.bpmn.instance.UserTask
import org.camunda.bpm.model.xml.instance.DomElement
import org.springframework.stereotype.Service


import java.util.concurrent.CopyOnWriteArrayList

@Service
class Utils
{
//    HistoryService historyService;

    RepositoryService repositoryService;

    static Utils camundaUtils = null;

    static Utils getIntance(RepositoryService repositoryService)
    {
        if (this.camundaUtils == null)
        {
            this.camundaUtils = new Utils();
        }
//        this.camundaUtils.historyService = historyService;
        this.camundaUtils.repositoryService = repositoryService;
        return this.camundaUtils;
    }

    List getNextAuditNodes(String processDefinitionKey,String elementId)
    {
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(repositoryService.createProcessDefinitionQuery().active().processDefinitionKey(processDefinitionKey).latestVersion().singleResult().id);
        def node = bpmnModelInstance.getModelElementById(elementId);
//        UserTask userTask = bpmnModelInstance.getModelElementById(t.getTaskDefinitionKey());
//        Collection<SequenceFlow> outgoing = userTask.getOutgoing();
//        Iterator<SequenceFlow> iterator = outgoing.stream().iterator();
        List<Map<String, String>> nodeList = new ArrayList<>();
        this.loopTargat(node.getOutgoing().stream().iterator(),nodeList);
//        while (iterator.hasNext())
//        {
//            SequenceFlow flow = iterator.next();
//            FlowNode target = flow.getTarget();
//            if (target.getElementType().getTypeName() in ["exclusiveGateway", "parallelGateway"])
//            {
//                Collection<SequenceFlow> targetOutgoing = target.getOutgoing();
//                Iterator<SequenceFlow> targetIt = targetOutgoing.stream().iterator();
//                while (targetIt.hasNext())
//                {
//                    SequenceFlow targetFlow = targetIt.next();
//                    FlowNode gatewayTarget = targetFlow.getTarget();
//                    Map<String, String> map = generateTaskNode(gatewayTarget);
//                    if (!Objects.isNull(map))
//                    {
//                        nodeList.add(map);
//                    }
//                }
//            } else if ("userTask".equals(target.getElementType().getTypeName()))
//            {
//                Map<String, String> map = generateTaskNode(target);
//                if (!Objects.isNull(map))
//                {
//                    nodeList.add(map);
//                }
//            }
//        }
        return nodeList;
    }

    List loopTargat(Iterator iterator,List nodeList)
    {
        while (iterator.hasNext())
        {
            SequenceFlow flow = iterator.next();
            FlowNode target = flow.getTarget();
//            println target.getElementType().getTypeName();
//
            if ("userTask".equals(target.getElementType().getTypeName()))
            {
                Map<String, String> map = generateTaskNode(target);
                if (!Objects.isNull(map))
                {
                    nodeList.add(map);
                }
            }
            if (target.getElementType().getTypeName()=="endEvent")
            {
                nodeList.add(["id":target.id,"lable":target.name,"type":"endEvent"]);
            }
            loopTargat(target.getOutgoing().stream().iterator(),nodeList);
        }
        return nodeList;
    }

    /**
     * 生成任务节点
     *
     * @param target
     * @return
     */
    Map<String, String> generateTaskNode(FlowNode target)
    {
        if ("userTask".equals(target.getElementType().getTypeName()))
        {
            return ["id":target.id,"label":target.name,"assignee":getTaskAssign(target)]
        }
        return null;
    }

    /**
     * 获取任务办理人
     *
     * @param target
     * @return
     */
    String getTaskAssign(FlowNode target)
    {
        //代理人
        String assignee = target.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "assignee");
        if (StringUtils.isNotBlank(assignee))
        {
            return assignee;
        } else
        {
            //代理人不存在，获取组任务办理人
            String candidateUsers = target.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "candidateUsers");
            if (StringUtils.isNotBlank(candidateUsers))
            {
                return candidateUsers;
            } else
            {
                //如果组任务办理人不存在，获取多实例任务办理人
                MultiInstanceLoopCharacteristics modelElementInstance = (MultiInstanceLoopCharacteristics) target
                        .getUniqueChildElementByNameNs(BpmnModelConstants.BPMN20_NS, "multiInstanceLoopCharacteristics");
                if (!Objects.isNull(modelElementInstance))
                {
                    return modelElementInstance.getAttributeValueNs(BpmnModelConstants.CAMUNDA_NS, "collection");
                }
                return null;
            }
        }
    }


    HashMap getNode(DomElement e)
    {
        HashMap nodeMap = new HashMap();
        e.getChildElements().stream()
//                .filter(it -> "startEvent".equals(it.getLocalName()) || "userTask".equals(it.getLocalName()) || "subProcess".equals(it.getLocalName()))
                .filter(it -> "userTask".equals(it.getLocalName()))
                .forEach(item -> {
//                    println item.dump();
                    nodeMap[item.getAttribute("id")] = ["name":item.getAttribute("name"),"startQuantity":item.getAttribute("startQuantity"),"assignee":item.getAttribute(BpmnModelConstants.CAMUNDA_NS, "assignee")];
//                    switch (item.getLocalName())
//                    {
//                        case "userTask":
//                            nodeMap.put(item.getAttribute("id"), item.getAttribute("name"));
//                            break;
//                        case "startEvent":
//                            nodeMap.put(item.getAttribute("id"), item.getAttribute("name"));
//                            break;
//                        case "subProcess":
//                            nodeMap.putAll(getNode(item));
//                            break;
//                    }
                });
        return nodeMap;
    }

    HashMap<String, String> getSequenceFlow(DomElement e)
    {
        HashMap<String, String> sequenceFlow = new HashMap<>();
        e.getChildElements().stream()
                .filter(it -> "sequenceFlow".equals(it.getLocalName()))
                .forEach(item -> {
                    if (sequenceFlow.get(item.getAttribute("sourceRef")) == null)
                    {
                        String sourceRef = item.getAttribute("sourceRef");
                        if (sourceRef.startsWith("StartEvent"))
                        {
                            sequenceFlow.put("StartEvent", item.getAttribute("sourceRef"));
                        }
                        sequenceFlow.put(sourceRef, item.getAttribute("targetRef"));
                    } else
                    {
                        sequenceFlow.put(item.getAttribute("sourceRef"), sequenceFlow.get(item.getAttribute("sourceRef")) + "," + item.getAttribute("targetRef"));
                    }
                });
        return sequenceFlow;
    }

    ArrayList<HashMap<String, String>> getBpmNodeList(BpmnModelInstance bpmnModelInstance)
    {
        if (ObjectUtils.isEmpty(bpmnModelInstance))
        {
            return null;
        }
        List<DomElement> domElementList = bpmnModelInstance.getDocument().getRootElement().getChildElements();
        if (ObjectUtils.isEmpty(domElementList))
        {
            return new ArrayList<>();
        }
        DomElement domElement = domElementList.stream().filter(it -> "process".equals(it.getLocalName())).findFirst().orElse(null);
        ArrayList<HashMap<String, String>> nodeList = new ArrayList<>();
        HashMap<String, String> seqMap = this.getSequenceFlow(domElement);
        HashMap<String, String> nodeMao = this.getNode(domElement);
        String start = seqMap.get("StartEvent");
        this.node2List(nodeList, start, seqMap, nodeMao);
        return nodeList;
    }

    private void node2List(ArrayList nodeList, String seqKey, HashMap seqMap, HashMap nodeMao)
    {
        if (seqKey.contains(","))
        {
            String[] keyArr = seqKey.split(",");
            for (int i = 0; i < keyArr.length; i++)
            {
                node2List(nodeList, keyArr[i], seqMap, nodeMao);
            }
            return;
        }
        if (seqMap.get(seqKey) == null)
        {
            return;
        }
        if (seqKey.startsWith("Task") || seqKey.startsWith("StartEvent"))
        {
            HashMap<String, String> node = new HashMap<>();
            node.put("code", seqKey);
            node.put("name", nodeMao.get(seqKey));
            nodeList.add(node);
        }

        String req = seqMap.get(seqKey);
        seqMap.remove(seqKey);
        nodeMao.remove(seqKey);
        seqKey = req;
        node2List(nodeList, seqKey, seqMap, nodeMao);
    }
}
