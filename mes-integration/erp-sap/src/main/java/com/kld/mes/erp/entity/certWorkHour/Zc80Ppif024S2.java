//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.3.0 生成的
// 请访问 <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2022.07.21 时间 11:02:35 AM CST 
//


package com.kld.mes.erp.entity.certWorkHour;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Zc80ppif024S2 complex type的 Java 类。
 *
 * <p>以下模式片段指定包含在此类中的预期内容。
 *
 * <pre>
 * &lt;complexType name="Zc80ppif024S2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Aufnr" type="{urn:sap-com:document:sap:rfc:functions}char12"/&gt;
 *         &lt;element name="Vornr" type="{urn:sap-com:document:sap:rfc:functions}char4"/&gt;
 *         &lt;element name="Zflag" type="{urn:sap-com:document:sap:rfc:functions}char1"/&gt;
 *         &lt;element name="Rueck" type="{urn:sap-com:document:sap:rfc:functions}numeric10"/&gt;
 *         &lt;element name="Rmzhl" type="{urn:sap-com:document:sap:rfc:functions}numeric8"/&gt;
 *         &lt;element name="ZreturnType" type="{urn:sap-com:document:sap:rfc:functions}char1"/&gt;
 *         &lt;element name="ZreturnMsg" type="{urn:sap-com:document:sap:rfc:functions}char255"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Zc80ppif024S2", propOrder = {
        "aufnr",
        "vornr",
        "zflag",
        "rueck",
        "rmzhl",
        "zreturnType",
        "zreturnMsg"
})
public class Zc80Ppif024S2 {

    @XmlElement(name = "Aufnr", required = true)
    protected String aufnr;
    @XmlElement(name = "Vornr", required = true)
    protected String vornr;
    @XmlElement(name = "Zflag", required = true)
    protected String zflag;
    @XmlElement(name = "Rueck", required = true)
    protected String rueck;
    @XmlElement(name = "Rmzhl", required = true)
    protected String rmzhl;
    @XmlElement(name = "ZreturnType", required = true)
    protected String zreturnType;
    @XmlElement(name = "ZreturnMsg", required = true)
    protected String zreturnMsg;

    /**
     * 获取aufnr属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getAufnr() {
        return aufnr;
    }

    /**
     * 设置aufnr属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setAufnr(String value) {
        this.aufnr = value;
    }

    /**
     * 获取vornr属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getVornr() {
        return vornr;
    }

    /**
     * 设置vornr属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setVornr(String value) {
        this.vornr = value;
    }

    /**
     * 获取zflag属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getZflag() {
        return zflag;
    }

    /**
     * 设置zflag属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setZflag(String value) {
        this.zflag = value;
    }

    /**
     * 获取rueck属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getRueck() {
        return rueck;
    }

    /**
     * 设置rueck属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRueck(String value) {
        this.rueck = value;
    }

    /**
     * 获取rmzhl属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getRmzhl() {
        return rmzhl;
    }

    /**
     * 设置rmzhl属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setRmzhl(String value) {
        this.rmzhl = value;
    }

    /**
     * 获取zreturnType属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getZreturnType() {
        return zreturnType;
    }

    /**
     * 设置zreturnType属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setZreturnType(String value) {
        this.zreturnType = value;
    }

    /**
     * 获取zreturnMsg属性的值。
     *
     * @return possible object is
     * {@link String }
     */
    public String getZreturnMsg() {
        return zreturnMsg;
    }

    /**
     * 设置zreturnMsg属性的值。
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setZreturnMsg(String value) {
        this.zreturnMsg = value;
    }

}
