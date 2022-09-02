package com.richfit.mes.produce.utils;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
@Component
public class WordUtil {

    /**
     * 根据ftl文件导出doc
     * @param response
     * @param dataMap  填充数据
     * @param tempName 模板名称
     * @param docName  导出文件名
     * @throws IOException
     * @throws TemplateException
     */
    public void exoprtReport(HttpServletResponse response, Map<String, Object> dataMap, String tempName, String docName) throws IOException, TemplateException {

        //配置对象
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //获取模板路径（可以避免打jar包后找不到路径问题）
        configuration.setClassForTemplateLoading(this.getClass(),"/excel");
        //获取模板
        Template template = configuration.getTemplate(tempName, "utf-8");
        //设置响应类型为word
        response.setContentType("application/msword");
        //设置文旦编码
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment; filename=" + java.net.URLEncoder.encode(docName, "UTF-8") + ".doc");
        PrintWriter out = response.getWriter();
        //填充数据
        template.process(dataMap,out);
        out.close();

    }
}
