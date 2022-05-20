package com.richfit.mes.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.net.HttpHeaders;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.core.utils.ServletUtils;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.service.AttachmentService;
import io.swagger.annotations.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

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
                if (StringUtils.isNotEmpty(attachment.getAttachName())) {
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
            if (StringUtils.isNotEmpty(httpUserAgent)) {
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
        if (StringUtils.isNotEmpty(attachName)) {
            queryWrapper.eq("attach_name", attachName);
        }
        // 根据关联ID过滤
        if (StringUtils.isNotEmpty(relationId)) {
            queryWrapper.eq("relation_id", relationId);
        }
        // 根据关联ID过滤
        if (StringUtils.isNotEmpty(relationName)) {
            queryWrapper.like("relation_name", "%" + relationName + "%");
        }
        // 根据分类过滤
        if (StringUtils.isNotEmpty(classify)) {
            queryWrapper.eq("classify", classify);
        }
        // 根据模块过滤
        if (StringUtils.isNotEmpty(module)) {
            queryWrapper.eq("module", module);
        }
        if (StringUtils.isNotEmpty(id)) {
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

}
