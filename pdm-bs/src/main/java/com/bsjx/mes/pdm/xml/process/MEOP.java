//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.3.2 生成的
// 请访问 <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2019.11.11 时间 02:06:32 PM CST 
//


package com.bsjx.mes.pdm.xml.process;

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
 *         &lt;element ref="{}ID"/&gt;
 *         &lt;element ref="{}Rev"/&gt;
 *         &lt;element ref="{}OpNo"/&gt;
 *         &lt;element ref="{}Type"/&gt;
 *         &lt;element ref="{}Drawing" minOccurs="0"/&gt;
 *         &lt;element ref="{}Name" minOccurs="0"/&gt;
 *         &lt;element ref="{}Content" minOccurs="0"/&gt;
 *         &lt;element ref="{}GZS" minOccurs="0"/&gt;
 *         &lt;element ref="{}Remark" minOccurs="0"/&gt;
 *         &lt;element ref="{}MESteps"/&gt;
 *         &lt;element ref="{}Items"/&gt;
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
    "id",
    "rev",
    "opNo",
    "type",
    "drawing",
    "name",
    "content",
    "gzs",
    "remark",
    "meSteps",
    "items"
})
@XmlRootElement(name = "MEOP")
public class MEOP {

    @XmlElement(name = "ID", required = true)
    protected String id;
    @XmlElement(name = "Rev", required = true)
    protected String rev;
    @XmlElement(name = "OpNo", required = true)
    protected String opNo;
    @XmlElement(name = "Type", required = true)
    protected String type;
    @XmlElement(name = "Drawing")
    protected String drawing;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Content")
    protected String content;
    @XmlElement(name = "GZS")
    protected String gzs;
    @XmlElement(name = "Remark")
    protected String remark;
    @XmlElement(name = "MESteps", required = true)
    protected MESteps meSteps;
    @XmlElement(name = "Items", required = true)
    protected Items items;

    /**
     * 获取id属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getID() {
        return id;
    }

    /**
     * 设置id属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setID(String value) {
        this.id = value;
    }

    /**
     * 获取rev属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRev() {
        return rev;
    }

    /**
     * 设置rev属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRev(String value) {
        this.rev = value;
    }

    /**
     * 获取opNo属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOpNo() {
        return opNo;
    }

    /**
     * 设置opNo属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOpNo(String value) {
        this.opNo = value;
    }

    /**
     * 获取type属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        return type;
    }

    /**
     * 设置type属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * 获取drawing属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDrawing() {
        return drawing;
    }

    /**
     * 设置drawing属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDrawing(String value) {
        this.drawing = value;
    }

    /**
     * 获取name属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        return name;
    }

    /**
     * 设置name属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * 获取content属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置content属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContent(String value) {
        this.content = value;
    }

    /**
     * 获取gzs属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGZS() {
        return gzs;
    }

    /**
     * 设置gzs属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGZS(String value) {
        this.gzs = value;
    }

    /**
     * 获取remark属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置remark属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRemark(String value) {
        this.remark = value;
    }

    /**
     * 获取meSteps属性的值。
     *
     * @return
     *     possible object is
     *     {@link MESteps }
     *
     */
    public MESteps getMESteps() {
        return meSteps;
    }

    /**
     * 设置meSteps属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link MESteps }
     *
     */
    public void setMESteps(MESteps value) {
        this.meSteps = value;
    }

    /**
     * 获取items属性的值。
     *
     * @return
     *     possible object is
     *     {@link Items }
     *
     */
    public Items getItems() {
        return items;
    }

    /**
     * 设置items属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link Items }
     *     
     */
    public void setItems(Items value) {
        this.items = value;
    }

}
