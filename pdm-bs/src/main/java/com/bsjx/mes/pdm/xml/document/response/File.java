//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.3.2 生成的
// 请访问 <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2019.11.14 时间 05:01:25 PM CST 
//


package com.bsjx.mes.pdm.xml.document.response;

import javax.xml.bind.annotation.*;


/**
 * <p>anonymous complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}FileType"/&gt;
 *         &lt;element ref="{}FileName"/&gt;
 *         &lt;element ref="{}FileURL"/&gt;
 *         &lt;element ref="{}FileRelID"/&gt;
 *         &lt;element ref="{}FileRelRev"/&gt;
 *         &lt;element ref="{}IsOP" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "fileType",
    "fileName",
    "fileURL",
    "fileRelID",
    "fileRelRev",
    "isOP"
})
@XmlRootElement(name = "File")
public class File {

    @XmlElement(name = "FileType", required = true)
    protected String fileType;
    @XmlElement(name = "FileName", required = true)
    protected String fileName;
    @XmlElement(name = "FileURL", required = true)
    protected String fileURL;
    @XmlElement(name = "FileRelID", required = true)
    protected String fileRelID;
    @XmlElement(name = "FileRelRev", required = true)
    protected String fileRelRev;
    @XmlElement(name = "IsOP")
    protected String isOP;

    /**
     * 获取fileType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * 设置fileType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileType(String value) {
        this.fileType = value;
    }

    /**
     * 获取fileName属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置fileName属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

    /**
     * 获取fileURL属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileURL() {
        return fileURL;
    }

    /**
     * 设置fileURL属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileURL(String value) {
        this.fileURL = value;
    }

    /**
     * 获取fileRelID属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileRelID() {
        return fileRelID;
    }

    /**
     * 设置fileRelID属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileRelID(String value) {
        this.fileRelID = value;
    }

    /**
     * 获取fileRelRev属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileRelRev() {
        return fileRelRev;
    }

    /**
     * 设置fileRelRev属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileRelRev(String value) {
        this.fileRelRev = value;
    }

    /**
     * 获取isOP属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsOP() {
        return isOP;
    }

    /**
     * 设置isOP属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsOP(String value) {
        this.isOP = value;
    }

}
