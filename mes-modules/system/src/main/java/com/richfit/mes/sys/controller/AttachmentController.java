package com.richfit.mes.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.net.HttpHeaders;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.core.utils.ServletUtils;
import com.richfit.mes.common.model.produce.CheckAttachment;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.provider.ProduceServiceClient;
import com.richfit.mes.sys.service.AttachmentService;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 附件信息管理
 */
@Slf4j
@AllArgsConstructor
@Api("附件信息管理")
@RestController
@RequestMapping("/api/sys/attachment")
public class AttachmentController extends BaseController {

    @Autowired
    private AttachmentService attachmentService;
    @Resource
    private ProduceServiceClient produceService;


    @PostMapping("upload_file")
    @ApiOperation(value = "通过文件路径上传文件", notes = "通过文件路径上传文件")
    public CommonResult<Attachment> uploadFile(@ApiParam(value = "文件路径", required = true) @RequestParam String filePath) {
        try {
            Attachment attachment = new Attachment();
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            byte[] fileBytes = new byte[(int) file.length()];
            fis.read(fileBytes);
            fis.close();
            attachment.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            attachment.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            attachment.setAttachType(FileUtils.getFilenameExtension(file.getName()));
            attachment.setAttachSize(String.valueOf(file.length()));
            attachment.setAttachName(file.getName());
            attachment = attachmentService.upload(attachment, fileBytes);
            return CommonResult.success(attachment);
        } catch (Exception e) {
            log.error("upload attachment error: {}", e.getMessage(), e);
            e.printStackTrace();
            return CommonResult.failed(e.getMessage());
        }
    }

    @PostMapping("upload")
    @ApiOperation(value = "上传文件", notes = "上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "module", value = "业务模块", dataType = "String")
    })
    public CommonResult<Attachment> upload(@ApiParam(value = "要上传的文件", required = true) @RequestParam("file") MultipartFile file,
                                           Attachment attachment) {
        if (!file.isEmpty()) {
            try {
                attachment.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                attachment.setAttachType(FileUtils.getFilenameExtension(file.getOriginalFilename()));
                attachment.setAttachSize(String.valueOf(file.getSize()));
                if (StringUtils.isNullOrEmpty(attachment.getAttachName())) {
                    attachment.setAttachName(file.getOriginalFilename());
                }
                attachment = attachmentService.upload(attachment, file.getBytes());
                return CommonResult.success(attachment);
            } catch (Exception e) {
                log.error("upload attachment error: {}", e.getMessage(), e);
                e.printStackTrace();
                return CommonResult.failed(e.getMessage());
            }
        } else {
            return CommonResult.failed("请上传文件");
        }
    }

    @PostMapping("/filesUpload")
    @Transactional(rollbackFor = Exception.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "thId", value = "跟单ID", dataType = "String"),
            @ApiImplicitParam(name = "tiIdList", value = "跟单工序Id", dataType = "String"),
            @ApiImplicitParam(name = "classify", value = "类型", dataType = "String"),
            @ApiImplicitParam(name = "branchCode", value = "车间", dataType = "String")

    })
    @ApiOperation(value = "上传文件(质检)", notes = "上传文件(质检)")
    public CommonResult<List<Attachment>> filesUpload(@ApiParam(value = "要上传的文件", required = true) @RequestParam("file") MultipartFile[] files, String thId, String tiIds, String classify, String branchCode) {
        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String tenantId = SecurityUtils.getCurrentUser().getTenantId();
                    Attachment attachment = new Attachment();
                    attachment.setTenantId(tenantId);
                    attachment.setAttachType(FileUtils.getFilenameExtension(file.getOriginalFilename()));
                    attachment.setAttachSize(String.valueOf(file.getSize()));
                    attachment.setAttachName(file.getOriginalFilename());
                    attachment.setGroupName(thId);
                    attachment.setClassify(classify);
                    attachment = attachmentService.upload(attachment, file.getBytes());
                    attachments.add(attachment);
                    //关联文件
                    CheckAttachment checkAttachment = new CheckAttachment();
                    checkAttachment.setThId(thId);
                    checkAttachment.setClassify(classify);
                    checkAttachment.setBranchCode(branchCode);
                    checkAttachment.setTenantId(tenantId);
                    checkAttachment.setFileId(attachment.getId());
                    List<String> tiIdList = Arrays.stream(tiIds.split(",")).collect(Collectors.toList());
                    for (String tiId : tiIdList) {
                        checkAttachment.setTiId(tiId);
                        produceService.saveCheckFile(checkAttachment);
                    }
                } catch (Exception e) {
                    log.error("upload attachment error: {}", e.getMessage(), e);
                    e.printStackTrace();
                    return CommonResult.failed(e.getMessage());
                }
            } else {
                return CommonResult.failed("请上传文件");
            }
        }
        return CommonResult.success(attachments);
    }

    @GetMapping("save")
    @ApiOperation(value = "上传文件", notes = "上传文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "module", value = "业务模块", dataType = "String")
    })
    public CommonResult<Attachment> save(Attachment attachment) {

        try {
            attachment.setId(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
            attachmentService.add(attachment);
        } catch (Exception e) {
            log.error("upload attachment error: {}", e.getMessage(), e);
        }

        return CommonResult.success(attachment);
    }

    @GetMapping("download")
    @ApiOperation(value = "下载附件", notes = "根据ID下载附件")
    @ApiImplicitParam(name = "id", value = "附件ID", required = true, dataType = "String")
    public void download(HttpServletRequest request, HttpServletResponse response, @RequestParam String id) throws GlobalException {

        Attachment attachment = new Attachment();
        attachment.setId(id);
        attachment = attachmentService.get(id);
        try {
            InputStream inputStream = attachmentService.download(attachment);
            if (inputStream == null) {
                log.info("attachment is not exists");
                return;
            }
            OutputStream outputStream = response.getOutputStream();
            response.setContentType("application/zip");
            response.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=10");
            // IE之外的浏览器使用编码输出名称
            String contentDisposition = "";
            String httpUserAgent = request.getHeader("User-Agent");
            if (!StringUtils.isNullOrEmpty(httpUserAgent)) {
                httpUserAgent = httpUserAgent.toLowerCase();
                String fileName = attachment.getAttachName();
                contentDisposition = httpUserAgent.contains("wps") ? "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") : ServletUtils.getDownName(request, fileName);
            }
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
            response.setContentLength(inputStream.available());
            FileCopyUtils.copy(inputStream, outputStream);
            log.info("download {} success", attachment.getAttachName());
        } catch (Exception e) {
            log.error("Download attachment failed: {}", e.getMessage(), e);
        }
    }

    @PostMapping("/download_zip")
    @ApiOperation(value = "下载附件", notes = "根据ID列表下载ZIP附件")
    @ApiImplicitParam(name = "ids", value = "附件ID", required = true, dataType = "List<String>")
    public void downloadZip(HttpServletRequest request, HttpServletResponse response, @RequestBody List<String> ids, @RequestParam String fileName) throws GlobalException {

        List<Attachment> list = new ArrayList<>();
        for (String id : ids) {
            Attachment attachment = null;
            try {
                attachment = attachmentService.get(id);
                list.add(attachment);
            } catch (GlobalException e) {
                log.error(e.getMessage());
            }
        }
        if (list.size() == 0) {
            throw new GlobalException("attachment not found", ResultCode.ITEM_NOT_FOUND);
        }

        try {
            InputStream inputStream = attachmentService.downloadZip(list, fileName);
            if (inputStream == null) {
                log.info("attachment is not exists");
                return;
            }
            OutputStream outputStream = response.getOutputStream();
            response.setContentType("application/zip");
            response.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=10");
            // IE之外的浏览器使用编码输出名称
            String contentDisposition = "";
            String httpUserAgent = request.getHeader("User-Agent");
            if (!StringUtils.isNullOrEmpty(httpUserAgent)) {
                httpUserAgent = httpUserAgent.toLowerCase();
                contentDisposition = httpUserAgent.contains("wps") ? "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") : ServletUtils.getDownName(request, fileName);
            }
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
            response.setContentLength(inputStream.available());
            FileCopyUtils.copy(inputStream, outputStream);
            log.info("download {} success", fileName);
        } catch (Exception e) {
            log.error("Download attachment failed: {}", e.getMessage(), e);
        }
    }

    @GetMapping("/preview")
    @ApiOperation(value = "预览附件", notes = "根据附件ID预览附件")
    @ApiImplicitParam(name = "id", value = "附件id", required = true, dataType = "String")
    public void preview(HttpServletResponse response, @RequestParam String id) throws Exception {
        Attachment attachment = attachmentService.get(id);
        InputStream inputStream = attachmentService.download(attachment);
        OutputStream outputStream = response.getOutputStream();

        if (inputStream == null) {
            log.info("attachment is not exists");
            return;
        }

        response.setContentLength(inputStream.available());
        FileCopyUtils.copy(inputStream, outputStream);
    }

    @GetMapping("/url")
    @ApiOperation(value = "获取预览URL", notes = "根据附件ID获取文件URL")
    @ApiImplicitParam(name = "id", value = "附件id", required = true, dataType = "String")
    public CommonResult getPreviewUrl(@RequestParam String id) throws GlobalException {
        Attachment attachment = attachmentService.get(id);
        String url = attachmentService.getTokenUrl(attachment);
        return CommonResult.success(url);
    }

    @ApiOperation(value = "获取附件信息", notes = "根据附件id获取附件详细信息")
    @ApiImplicitParam(name = "id", value = "附件ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/get/{id}")
    public CommonResult<Attachment> attachment(@PathVariable String id) {
        return CommonResult.success(attachmentService.get(id));
    }


    @GetMapping("/getinput/{id}")
    public CommonResult<byte[]> getAttachmentInputStream(@PathVariable String id) {
        Attachment attachment = attachmentService.get(id);
        return CommonResult.success(attachmentService.downloadbyte(attachment));
    }


    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除附件", notes = "根据ID删除附件")
    @ApiImplicitParam(name = "id", value = "附件ID", required = true, dataType = "String", paramType = "path")
    public CommonResult<Boolean> delete(@PathVariable String id) {
        Attachment attachment = attachmentService.get(id);
        boolean success = false;
        if (attachment != null) {
            success = attachmentService.delete(attachment);
        }
        return CommonResult.success(success);
    }

    @GetMapping("/query")
    @ApiOperation(value = "查询", notes = "查询")
    public CommonResult<IPage<Attachment>> query(int page, int limit, String id, String attachName, String classify, String module, String relationId, String relationName) {

        QueryWrapper<Attachment> queryWrapper = new QueryWrapper<Attachment>();
        if (!StringUtils.isNullOrEmpty(attachName)) {
            queryWrapper.eq("attach_name", attachName);
        }
        // 根据关联ID过滤
        if (!StringUtils.isNullOrEmpty(relationId)) {
            queryWrapper.eq("relation_id", relationId);
        }
        // 根据关联ID过滤
        if (!StringUtils.isNullOrEmpty(relationName)) {
            queryWrapper.like("relation_name", relationName);
        }
        // 根据分类过滤
        if (!StringUtils.isNullOrEmpty(classify)) {
            queryWrapper.eq("classify", classify);
        }
        // 根据模块过滤
        if (!StringUtils.isNullOrEmpty(module)) {
            queryWrapper.eq("module", module);
        }
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }

        IPage<Attachment> docs = attachmentService.selectPage(new Page<Attachment>(page, limit), queryWrapper);
        return CommonResult.success(docs);

    }

    @PostMapping("update")
    @ApiOperation(value = "更新", notes = "更新")
    public int update(@RequestParam("id") String id, @RequestParam("attachName") String attachName, @RequestParam("classify") String classify, @RequestParam("status") String status, @RequestParam("previewUrl") String previewUrl, @RequestParam("relationName") String relationName, @RequestParam("relationType") String relationType) {
        Attachment oldAttachment = attachmentService.get(id);
        oldAttachment.setAttachName(attachName);
        oldAttachment.setClassify(classify);
        oldAttachment.setStatus(status);
        oldAttachment.setPreviewUrl(previewUrl);
        oldAttachment.setRelationName(relationName);
        oldAttachment.setRelationType(relationType);
        return attachmentService.update(oldAttachment);

    }

    @PostMapping("/selectAttachmentsList")
    @ApiOperation(value = "获取文件列表", notes = "获取文件列表")
    public List<Attachment> selectAttachmentsList(@RequestBody List<String> idList) {
        return attachmentService.selectAttachmentsList(idList);
    }
}
