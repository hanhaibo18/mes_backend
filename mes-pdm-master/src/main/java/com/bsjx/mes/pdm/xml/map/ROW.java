//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.3.2 生成的
// 请访问 <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2019.11.26 时间 05:01:51 PM CST
//


package com.bsjx.mes.pdm.xml.map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{}C0"/&gt;
 *         &lt;element ref="{}PITEM_ID"/&gt;
 *         &lt;element ref="{}PITEM_REVISION_ID"/&gt;
 *         &lt;element ref="{}PNAME"/&gt;
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
        "c0",
        "pitemid",
        "pitemrevisionid",
        "pname"
})
@XmlRootElement(name = "ROW")
public class ROW {

    @XmlElement(name = "C0", required = true)
    protected String c0;
    @XmlElement(name = "PITEM_ID", required = true)
    protected String pitemid;
    @XmlElement(name = "PITEM_REVISION_ID", required = true)
    protected String pitemrevisionid;
    @XmlElement(name = "PNAME", required = true)
    protected String pname;

    /**
     * 获取c0属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getC0() {
        return c0;
    }

    /**
     * 设置c0属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setC0(String value) {
        this.c0 = value;
    }

    /**
     * 获取pitemid属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPITEMID() {
        return pitemid;
    }

    /**
     * 设置pitemid属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPITEMID(String value) {
        this.pitemid = value;
    }

    /**
     * 获取pitemrevisionid属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPITEMREVISIONID() {
        return pitemrevisionid;
    }

    /**
     * 设置pitemrevisionid属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPITEMREVISIONID(String value) {
        this.pitemrevisionid = value;
    }

    /**
     * 获取pname属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPNAME() {
        return pname;
    }

    /**
     * 设置pname属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPNAME(String value) {
        this.pname = value;
    }

}
