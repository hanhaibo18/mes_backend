package com.bsjx.mes.pdm.service;

import com.bsjx.mes.pdm.config.PdmClient;
import com.bsjx.mes.pdm.entity.*;
import com.bsjx.mes.pdm.repository.*;
import com.bsjx.mes.pdm.util.LogStatusEnum;
import com.bsjx.mes.pdm.util.LogTypeEnum;
import com.bsjx.mes.pdm.xml.bom.BOM;
import com.bsjx.mes.pdm.xml.bom.GetBOMInfoReturnXml;
import com.bsjx.mes.pdm.xml.bom.GetBomInfoXml;
import com.bsjx.mes.pdm.xml.document.GetDocumentURLXml;
import com.bsjx.mes.pdm.xml.document.ReqItem;
import com.bsjx.mes.pdm.xml.document.ReqItems;
import com.bsjx.mes.pdm.xml.document.response.GetDatasetURL;
import com.bsjx.mes.pdm.xml.process.GetProcessInfo;
import com.bsjx.mes.pdm.xml.process.MEProcess;
import com.bsjx.mes.pdm.xml.process.ProcessInfoResponseXml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Service
@Slf4j
public class ProcessServiceImpl implements ProcessService {

    @Value(value = "${pdm.webservice.page_size:10}")
    private int pageSize;
    @Value(value = "${pdm.webservice.interval_ms:1000}")
    private int interval;
    @Autowired
    private PdmProcessRepository pdmProcessRepository;
    @Autowired
    private PdmOptionRepository pdmOptionRepository;
    @Autowired
    private PdmStepRepository pdmStepRepository;
    @Autowired
    private PdmObjectRepository pdmObjectRepository;
    @Autowired
    private DrawingApplyRepository drawingApplyRepository;
    @Autowired
    private PdmClient pdmClient;
    @Autowired
    private PdmDrawRepository pdmDrawRepository;
    @Autowired
    private PdmBomRepository pdmBomRepository;
    @Autowired
    private PdmLogRepository pdmLogRepository;
    @Autowired
    private PdmTaskRepository pdmTaskRepository;

    private static Map<String, Object> bomCacheMap = new ConcurrentHashMap<String, Object>();

    public ProcessServiceImpl() {
    }

    @Override
    public void clearCache() {
        log.info("清理BOM缓存......");
        bomCacheMap.clear();
    }

    @Override
    public void executeMonitorTask() {
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<PdmTask> pdmTasks = pdmTaskRepository.findByStatus("0", pageable);
        log.info("MonitorTask size:{}", pdmTasks.getTotalElements());
        try {
            executeMonitorTask(pdmTasks);
        }catch (Exception e){
            log.error("ExecuteMonitorTask Exception:{}",e.getMessage());
        }

        for (int i = 1; i < pdmTasks.getTotalPages(); i++) {
            try {
                Thread.sleep(interval);
                pageable = PageRequest.of(i, pageSize);
                pdmTasks = pdmTaskRepository.findByStatus("0", pageable);
                executeMonitorTask(pdmTasks);
            } catch (Exception e) {
                log.error("ExecuteMonitorTask Exception:{}",e.getMessage());
            }
        }
    }

    public void executeMonitorTask(Page<PdmTask> pdmTasks) {
        for (PdmTask pdmTask : pdmTasks) {
            log.info("DrawNo:{},PdmDraw:{}", pdmTask.getPdmDrawNo(), pdmTask.getDataGroup());
            String drawNoVer = "01";
            boolean result = false;
            //BOM
            if ("1".equals(pdmTask.getReqBom())) {
                List<PdmBom> pdmBomList = getBomInfo(pdmTask.getPdmDrawNo(), pdmTask.getDataGroup());
                if (!pdmBomList.isEmpty()) {
                    result = true;
                }
                //是否继续获取工艺和图纸
                if ("1".equals(pdmTask.getReqProcess()) || "1".equals(pdmTask.getReqDraw())) {
                    pdmBomList = pdmBomList.stream().filter(pdmBom -> "自制件".equals(pdmBom.getObjectType())).collect(Collectors.toList());
                    List<PdmBom> uniqueBomList = pdmBomList.stream().collect(
                            collectingAndThen(
                                    toCollection(() -> new TreeSet<>(comparing(o -> o.getId() + ";" + o.getRev()))), ArrayList::new)
                    );
                    log.info("uniqueBomList:{}", uniqueBomList.isEmpty() ? "0" : uniqueBomList.size());
                    for (PdmBom pdmBom : uniqueBomList) {
                        if (pdmBom.getId().equals(pdmTask.getPdmDrawNo())) {
                            //请求列表里的bomId获取工艺（getProcessInfo）和图纸（getDocumentURL）由外层处理
                            //用获取到的bom版本号更新请求列表版本号
                            drawNoVer = pdmBom.getRev();
                            continue;
                        }

                        try {
                            Thread.sleep(interval);
                            //Process
                            if ("1".equals(pdmTask.getReqProcess())) {
                                getProcessInfo(pdmBom.getId(), pdmBom.getDataGroup());
                            }
                            //document
                            if ("1".equals(pdmTask.getReqDraw())) {
                                getDocumentURL(pdmBom.getId(), pdmBom.getRev(), pdmBom.getDataGroup());
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            //Process
            if ("1".equals(pdmTask.getReqProcess())) {
                result = getProcessInfo(pdmTask.getPdmDrawNo(), pdmTask.getDataGroup());
            }
            //document
            if ("1".equals(pdmTask.getReqDraw())) {
                result = getDocumentURL(pdmTask.getPdmDrawNo(), drawNoVer, pdmTask.getDataGroup());
            }
            //更新任务
            pdmTask.setChangeOn(new Date());
            pdmTask.setResult(result ? "1" : "0");
            pdmTask.setStatus("1");
            pdmTaskRepository.save(pdmTask);
        }
    }

    @Override
    public void getPdmData() throws Exception {
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<DrawingApply> drawingApplys = drawingApplyRepository.findByStatus("1", pageable);
        log.info("ReqList size:{}", drawingApplys.getTotalElements());
        try {
            getPdmDataFromReqList(drawingApplys);
        }catch (Exception e){
            log.error("GetPdmDataFromReqList Exception:{}",e.getMessage());
        }

        for (int i = 1; i < drawingApplys.getTotalPages(); i++) {
            try {
                Thread.sleep(interval);
                pageable = PageRequest.of(i, pageSize);
                drawingApplys = drawingApplyRepository.findByStatus("1", pageable);
                getPdmDataFromReqList(drawingApplys);
            } catch (Exception e) {
                log.error("Page {}, getPdmDataFromReqList exception:",i,e.getMessage());
            }
        }
    }

    @Override
    public void getPdmDataByPage(int page) {
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<DrawingApply> reqLists = drawingApplyRepository.findByStatus("1", pageable);
        getPdmDataFromReqList(reqLists);
    }

    @Override
    public void getPdmDataByDrawNo(String drawNo) {
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<DrawingApply> reqLists = drawingApplyRepository.findByDrawingNoAndStatus(drawNo, "1", pageable);
        getPdmDataFromReqList(reqLists);
    }

    private void getPdmDataFromReqList(Page<DrawingApply> drawingApplys) {

        for (DrawingApply drawingApply : drawingApplys) {
            log.info("DrawNo:{},PdmDraw:{}", drawingApply.getDrawingNo(), drawingApply.getPdmDrawingNo());
            boolean updateReqList = false;
            if (drawingApply.getPdmDrawingNo() == null || drawingApply.getPdmDrawingNo().isEmpty()) {
                //needQuery标识，使用MES图号代替PDM图号
                if (drawingApply.getNeedQuery() == null || "0".equals(drawingApply.getNeedQuery())) {
                    continue;
                } else {
                    updateReqList = true;
                    drawingApply.setPdmDrawingNo(drawingApply.getDrawingNo());
                }
            }
            log.info("DrawNo:{},PdmDraw:{}", drawingApply.getDrawingNo(), drawingApply.getPdmDrawingNo());

            try {
                Thread.sleep(interval);
                if (log.isDebugEnabled()) {
                    log.debug("ReqList drawNo:{},ver:{},dataGroup:{}", drawingApply.getDrawingNo(), drawingApply.getVer(), drawingApply.getDataGroup());
                }

                //BOM
                List<PdmBom> pdmBomList = getBomInfo(drawingApply.getPdmDrawingNo(), drawingApply.getDataGroup());

                //updateReqList标识，使用MES图号查询到PDM图号更新数据库
                if (updateReqList && !pdmBomList.isEmpty()) {
                    drawingApply.setNeedQuery("0");
                    drawingApplyRepository.save(drawingApply);
                    log.info("Update ReqList,Id:{},pdmDraw:{},ver:{},dataGroup:{}", drawingApply.getId(), drawingApply.getPdmDrawingNo(), drawingApply.getVer(), drawingApply.getDataGroup());
                }

                pdmBomList = pdmBomList.stream().filter(pdmBom -> ("自制件").equals(pdmBom.getObjectType())).collect(Collectors.toList());

                List<PdmBom> uniqueBomList = pdmBomList.stream().collect(
                        collectingAndThen(
                                toCollection(() -> new TreeSet<>(comparing(o -> o.getId() + ";" + o.getRev()))), ArrayList::new)
                );
                log.info("uniqueBomList:{}", uniqueBomList.isEmpty() ? "0" : uniqueBomList.size());
                for (PdmBom pdmBom : uniqueBomList) {

                    if (pdmBom.getId().equals(drawingApply.getPdmDrawingNo())) {
                        //请求列表里的bomId获取工艺（getProcessInfo）和图纸（getDocumentURL）由外层处理
                        //用获取到的bom版本号更新请求列表版本号
                        drawingApply.setVer(pdmBom.getRev());
                        continue;
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("GetBomInfo return:drawNo:{},ver:{},dataGroup{}", pdmBom.getId(), pdmBom.getRev(), drawingApply.getDataGroup());
                    }

                    try {
                        Thread.sleep(interval);
                        if (log.isDebugEnabled()) {
                            log.debug("Request processInfo and documentURL based on returned BOM, drawNo:{},ver:{},dataGroup:{}", pdmBom.getId(), pdmBom.getRev(), drawingApply.getDataGroup());
                        }
                        getProcessInfo(pdmBom.getId(), drawingApply.getDataGroup());
                        getDocumentURL(pdmBom.getId(), pdmBom.getRev(), drawingApply.getDataGroup());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                //Process
                getProcessInfo(drawingApply.getPdmDrawingNo(), drawingApply.getDataGroup());

                //document
                //请求列表中版本号容错处理
                if (drawingApply.getVer() == null || (drawingApply.getVer() != null && drawingApply.getVer().length() != 2)) {
                    drawingApply.setVer("01");
                }
                getDocumentURL(drawingApply.getPdmDrawingNo(), drawingApply.getVer(), drawingApply.getDataGroup());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public boolean getProcessInfo(String drawNo, String dataGroup) {

        GetProcessInfo getProcessInfo = new GetProcessInfo();
        getProcessInfo.setItemID(drawNo);

        String xml = convertObjToXML(GetProcessInfo.class, getProcessInfo);

        String getProcessInfoReturn ;
        ProcessInfoResponseXml processInfoResponse;
        //请求pdm或返回值转换对象异常
        try {
            getProcessInfoReturn = pdmClient.getProcessInfo(xml);
            processInfoResponse = (ProcessInfoResponseXml) xmlStrToObj(ProcessInfoResponseXml.class, getProcessInfoReturn);
        } catch (Exception e) {
            log.error("Exception:{},getProcessInfo:drawNo:{}", e.getMessage(), drawNo);
            saveLog(LogTypeEnum.PROCESS.name(), drawNo, LogStatusEnum.ERROR.toString(), e.getMessage());
            return false;
        }

        //PLM系统中没有该对象
        if (processInfoResponse.getMEProcesses() == null || processInfoResponse.getMEProcesses().getMEProcess() == null || processInfoResponse.getMEProcesses().getMEProcess().isEmpty()) {
            log.error("GetProcessInfo {},return:{}", drawNo, getProcessInfoReturn);
            String remark = getProcessInfoReturn;
            if (processInfoResponse.getMessage() != null) {
                remark = processInfoResponse.getMessage();
            }
            log.error("Exception:{},getProcessInfo:drawNo:{}", getProcessInfoReturn, drawNo);
            saveLog(LogTypeEnum.PROCESS.name(), drawNo, LogStatusEnum.ERROR.toString(), remark);
            return false;
        }

        List<MEProcess> meProcessListReturn = processInfoResponse.getMEProcesses().getMEProcess();
        if (meProcessListReturn.isEmpty()) {
            saveLog(LogTypeEnum.PROCESS.name(), drawNo, LogStatusEnum.ERROR.toString(), "meProcessListReturn is empty");
            return false;
        }
        //校验ReleaseTime
        List<MEProcess> meProcessList = meProcessListReturn.stream().filter(meProcess -> {
            String primaryId = drawNo + "@" + meProcess.getID() + "@" + dataGroup;
            Optional<PdmProcess> pdmProcess = pdmProcessRepository.findById(primaryId);
            if (pdmProcess.isPresent() && pdmProcess.get().getReleaseTime() != null && !pdmProcess.get().getReleaseTime().isEmpty() && pdmProcess.get().getReleaseTime().equals(meProcess.getReleaseTime())) {
                if (log.isDebugEnabled()) {
                    log.debug("Ignore process, drawNo:{},dataGroup:{}", drawNo, dataGroup);
                }
                return false;
            }
            return true;
        }).collect(Collectors.toList());

        if (meProcessList.isEmpty()) {
            saveLog(LogTypeEnum.PROCESS.name(), drawNo, LogStatusEnum.UPDATE.toString(), "0");
            return true;
        }

        List<PdmProcess> pdmProcessList = meProcessList.stream().map(meProcess -> {
            PdmProcess pdmProcess = new PdmProcess();
            BeanUtils.copyProperties(meProcess, pdmProcess);
            pdmProcess.setDrawNo(drawNo);
            pdmProcess.setId(meProcess.getID());
            pdmProcess.setSycTime(new Date());
            //主键
            pdmProcess.setDrawIdGroup(drawNo + "@" + meProcess.getID() + "@" + dataGroup);
            pdmOptionRepository.deleteByProcessId(meProcess.getID(), dataGroup);
            pdmProcess.setDataGroup(dataGroup);
            return pdmProcess;
        }).collect(Collectors.toList());
        String remark = "";
        List<PdmProcess> pdmProcessListSave = pdmProcessRepository.saveAll(pdmProcessList);
        remark = remark + "Process:" + pdmProcessListSave.size() + ",";
        List<PdmOption> pdmOptionList = meProcessList.stream().flatMap(meProcess ->
                meProcess.getMEOPs().getMEOP().stream().map(op -> {
                    PdmOption pdmOption = new PdmOption();
                    BeanUtils.copyProperties(op, pdmOption);
                    pdmOption.setId(op.getID() + "@" + dataGroup);
                    pdmOption.setGzs(op.getGZS());

                    //使用PdmProcess主键
                    pdmOption.setProcessId(drawNo + "@" + meProcess.getID() + "@" + dataGroup);
                    pdmOption.setDataGroup(dataGroup);

                    pdmStepRepository.deleteByOpId(op.getID() + "@" + dataGroup, dataGroup);
                    pdmObjectRepository.deleteByOpId(op.getID() + "@" + dataGroup, dataGroup);

                    return pdmOption;
                })).collect(Collectors.toList());

        List<PdmOption> pdmOptionListSave = pdmOptionRepository.saveAll(pdmOptionList);
        remark = remark + "Option:" + pdmOptionListSave.size() + ",";
        List<PdmStep> pdmStepList = meProcessList.stream().flatMap(meProcess ->
                meProcess.getMEOPs().getMEOP().stream().flatMap(op ->
                        op.getMESteps().getMEStep().stream().map(meStep -> {
                            PdmStep pdmStep = new PdmStep();
                            BeanUtils.copyProperties(meStep, pdmStep);
                            pdmStep.setOpId(op.getID() + "@" + dataGroup);
                            pdmStep.setDataGroup(dataGroup);
                            return pdmStep;
                        }))).collect(Collectors.toList());
        List<PdmStep> pdmStepListSave = pdmStepRepository.saveAll(pdmStepList);
        remark = remark + "Step:" + pdmStepListSave.size() + ",";
        List<PdmObject> pdmObjectList = meProcessList.stream().flatMap(meProcess ->
                meProcess.getMEOPs().getMEOP().stream().flatMap(op ->
                        op.getItems().getItem().stream().map(item -> {
                            PdmObject pdmObject = new PdmObject();
                            BeanUtils.copyProperties(item, pdmObject);
                            pdmObject.setOpId(op.getID() + "@" + dataGroup);
                            pdmObject.setId(item.getID());
                            pdmObject.setDataGroup(dataGroup);
                            return pdmObject;
                        }))).collect(Collectors.toList());
        List<PdmObject> pdmObjectListSave = pdmObjectRepository.saveAll(pdmObjectList);
        remark = remark + "Object:" + pdmObjectListSave.size();

        saveLog(LogTypeEnum.PROCESS.name(), drawNo, (meProcessListReturn.size() != meProcessList.size()) ? LogStatusEnum.UPDATE.toString() : LogStatusEnum.INSERT.toString(), remark);
        return true;
    }

    @Override
    public boolean getDocumentURL(String id, String revId, String dataGroup) {

        GetDocumentURLXml getDocumentURLXml = new GetDocumentURLXml();
        ReqItem item = new ReqItem();
        item.setItemID(id);
        item.setItemRev(revId);
        ReqItems items = new ReqItems();
        items.setItem(item);
        getDocumentURLXml.setReqItems(items);

        String xml = convertObjToXML(GetDocumentURLXml.class, getDocumentURLXml);
        String getDocumentURLReturn;
        GetDatasetURL getDatasetURL;

        //请求PDM或返回值转换对象异常
        try {
            getDocumentURLReturn = pdmClient.getDocumentURL(xml);
            getDatasetURL = (GetDatasetURL) xmlStrToObj(GetDatasetURL.class, getDocumentURLReturn);
        } catch (Exception e) {
            log.error("Exception:{},getDocumentURL:drawId:{},revId:{}", e.getMessage(), id, revId);
            saveLog(LogTypeEnum.DOCUMENT.name(), id + "@" + revId, LogStatusEnum.ERROR.toString(), e.getMessage());
            return false;
        }

        //PLM系统中没有该对象
        if (getDatasetURL.getItems().getItem() == null || getDatasetURL.getItems().getItem().getFiles() == null) {
            log.error("GetDocumentURL {} {}, return:{}", id, revId, getDocumentURLReturn);
            String remark = getDocumentURLReturn;
            if (getDatasetURL.getItems().getItem().getMessage() != null) {
                remark = getDatasetURL.getItems().getItem().getMessage();
            }

            saveLog(LogTypeEnum.DOCUMENT.name(), id + "@" + revId, LogStatusEnum.ERROR.toString(), remark);
            return false;
        }

        //正常
        List<com.bsjx.mes.pdm.xml.document.response.File> files = getDatasetURL.getItems().getItem().getFiles().getFile();
        AtomicBoolean updateFlag = new AtomicBoolean(false);
        List<PdmDraw> pdmDrawList = files.stream().map(file -> {
            //BeanUtils.copyProperties(file, pdmDraw);
            List<PdmDraw> pdmDraws = pdmDrawRepository.findByFile(id, revId, file.getFileURL(), dataGroup);
            PdmDraw pdmDraw = (pdmDraws != null && pdmDraws.size() > 0) ? pdmDraws.get(0) : new PdmDraw();
            if (pdmDraw.getDrawId() != null) {
                if (log.isDebugEnabled()) {
                    log.debug("Update DrawId:{}", pdmDraw.getDrawId());
                }
                updateFlag.set(true);
            }
            pdmDraw.setFileType(file.getFileType());
            pdmDraw.setFileName(file.getFileName());
            pdmDraw.setFileUrl(file.getFileURL());
            pdmDraw.setOp(file.getIsOP());
            //pdmDraw.setOpId(file.getFileRelID());
            if ("1".equals(file.getIsOP())) {
                pdmDraw.setOpId(id + "@" + file.getFileRelID() + "@" + dataGroup);
            } else {
                pdmDraw.setOpId(file.getFileRelID() + "@" + dataGroup);
            }
            pdmDraw.setOpVer(file.getFileRelRev());
            pdmDraw.setItemId(id);
            pdmDraw.setItemRev(revId);
            pdmDraw.setDataGroup(dataGroup);
            pdmDraw.setSycTime(new Date());
            return pdmDraw;
        }).collect(Collectors.toList());
        List<PdmDraw> pdmDrawListSave = pdmDrawRepository.saveAll(pdmDrawList);
        String remark = Integer.toString(pdmDrawListSave.size());
        saveLog(LogTypeEnum.DOCUMENT.name(), id + "@" + revId, updateFlag.get() ? LogStatusEnum.UPDATE.toString() : LogStatusEnum.INSERT.toString(), remark);
        return true;
    }

    @Override
    public List<PdmBom> getBomInfo(String id, String dataGroup) {

        List<PdmBom> result;
        BOM bom = new BOM();
        bom.setId(id);
        List<PdmBom> pdmBomList = getPdmBomInfo("0", 1, bom, dataGroup);

        if (pdmBomList.isEmpty()) {
            log.info("PdmBomList is Empty,id:{},dataGroup:{}", id, dataGroup);
            return pdmBomList;
        }

        log.info("PdmBomList size:{}", pdmBomList.size());
        result = pdmBomRepository.saveAll(pdmBomList);
        log.info("PdmBomList save size:{}", result.size());
        saveLog(LogTypeEnum.BOM.name(), id, LogStatusEnum.INSERT.toString(), Integer.toString(result.size()));

        return pdmBomList;
    }

    private List<BOM> getPdmBomXml(String id) {
        List<BOM> result = new ArrayList<>();
        GetBomInfoXml getBomInfoXml = new GetBomInfoXml();
        getBomInfoXml.setID(id);

        String xml = convertObjToXML(GetBomInfoXml.class, getBomInfoXml);
        String getBomInfoReturn;
        GetBOMInfoReturnXml getBOMInfoReturnXml;
        //请求PDM或返回值转换对象异常
        try {
            getBomInfoReturn = pdmClient.getBomInfo(xml);
            getBOMInfoReturnXml = (GetBOMInfoReturnXml) xmlStrToObj(GetBOMInfoReturnXml.class, getBomInfoReturn);
        } catch (Exception e) {
            log.error("Exception:{},GetBOMInfo:drawId:{}", e.getMessage(), id);
            saveLog(LogTypeEnum.BOM.name(), id, LogStatusEnum.ERROR.toString(), e.getMessage());

            return result;
        }

        List<BOM> bomList = getBOMInfoReturnXml.getBOM();
        //PLM系统中没有该对象
        if (bomList == null || bomList.isEmpty() || getBOMInfoReturnXml.getMessage() != null) {
            log.error("GetBOMInfo {},return:{}", id, getBomInfoReturn);
            String remark = getBomInfoReturn;
            if (getBOMInfoReturnXml.getMessage() != null) {
                remark = getBOMInfoReturnXml.getMessage();
            }
            saveLog(LogTypeEnum.BOM.name(), id, LogStatusEnum.ERROR.toString(), remark);
            return result;
        }
        StringBuilder remark = new StringBuilder("BOM-" + bomList.size());
        for (BOM bom : bomList) {
            if (bom.getBOM().isEmpty()) {
                continue;
            }
            remark.append("-").append(bom.getBOM().size());
        }
        saveLog(LogTypeEnum.BOM.name(), id, LogStatusEnum.QUERY.toString(), remark.toString());
        return bomList;
    }

    private List<PdmBom> getPdmBomInfo(String parentBomId, int orderNo, BOM currentBom, String dataGroup) {

        List<PdmBom> pdmBomList = new ArrayList<>();
        List<BOM> bomList = new ArrayList<>();

        //从缓存获取，判断是否获取过该bom数据（子bom）
        String bomKey = currentBom.getId();
        Object bomId = bomCacheMap.get(bomKey);
        //缓存中存在，不再重新获取是否有子bom
        if (currentBom.getId().equals(bomId)) {
            log.info("Get BOM From Cache,{}", bomKey);
            //只存储本级bom
            bomList.add(currentBom);
        } else {
            bomList = getPdmBomXml(currentBom.getId());
            //更新到缓存
            bomCacheMap.put(bomKey, currentBom.getId());
            log.info("Update BOM Cache,{}", bomKey);
        }


        for (BOM bom : bomList) {
            PdmBom pdmBom = new PdmBom();

            //使用上次查询的数据
            if (!"0".equals(parentBomId) && currentBom != null && currentBom.getId().equals(bom.getId())) {
                bom.setQuantity(currentBom.getQuantity());
            }
            pdmBom.setId(bom.getId());
            pdmBom.setPId(parentBomId);
            pdmBom.setRev(bom.getRev());
            pdmBom.setDataGroup(dataGroup);

            Example<PdmBom> bomExample = Example.of(pdmBom);
            List<PdmBom> pdmBomSaved = pdmBomRepository.findAll(bomExample);
            if (!pdmBomSaved.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("Bom id {} already exist, size:{}, id:{}, pid:{}", pdmBom.getId(), pdmBomSaved.size(), pdmBomSaved.get(0).getBomId(), parentBomId);
                }
                pdmBom.setBomId(pdmBomSaved.get(0).getBomId());
            }

            BeanUtils.copyProperties(bom, pdmBom);
            pdmBom.setMateriaWeight(bom.getWeight());
            pdmBom.setSycTime(new Date());
            pdmBom.setOrderNo(Integer.toString(orderNo));

            pdmBomList.add(pdmBom);

            //获取子BOM
            int i = 1;
            for (BOM subBom : bom.getBOM()) {
                try {
                    Thread.sleep(interval);
                    List<PdmBom> subPdmBomList = getPdmBomInfo(bom.getId() + "@" + bom.getRev(), i++, subBom, dataGroup);
                    if (!subPdmBomList.isEmpty()) {
                        pdmBomList.addAll(subPdmBomList);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return pdmBomList;
    }


    private String convertObjToXML(Class<?> clazz, Object obj) {
        String dataXML = "";
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Marshaller marshaller = context.createMarshaller();
            //编码格式
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            //是否格式化生成的xml串
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //是否省略xml头信息，默认不省略（false）
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
            StringWriter writer = new StringWriter();
            marshaller.marshal(obj, writer);
            dataXML = writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return dataXML;
    }

    private static Object xmlStrToObj(Class<?> clazz, String xmlStr) throws Exception {
        Object xmlObject;
        Reader reader;
        JAXBContext context = JAXBContext.newInstance(clazz);

        // XML 转为对象的接口
        Unmarshaller unmarshaller = context.createUnmarshaller();

        reader = new StringReader(xmlStr);
        xmlObject = unmarshaller.unmarshal(reader);

        reader.close();

        return xmlObject;
    }

    private void saveLog(String type, String par, String status, String remark) {
        PdmLog pdmLog = new PdmLog();
        pdmLog.setType(type);
        pdmLog.setPar(par);
        pdmLog.setStatus(status);
        pdmLog.setQueryTime(new Date());
        pdmLog.setRemark(remark);
        pdmLogRepository.save(pdmLog);
    }

}
