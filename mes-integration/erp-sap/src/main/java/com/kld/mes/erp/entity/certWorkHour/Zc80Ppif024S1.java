
package com.kld.mes.erp.entity.certWorkHour;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Zc80ppif024S1 complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="Zc80ppif024S1"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Werks" type="{urn:sap-com:document:sap:rfc:functions}char4"/&gt;
 *         &lt;element name="Aufnr" type="{urn:sap-com:document:sap:rfc:functions}char12"/&gt;
 *         &lt;element name="Vornr" type="{urn:sap-com:document:sap:rfc:functions}char4"/&gt;
 *         &lt;element name="Lmnga" type="{urn:sap-com:document:sap:rfc:functions}quantum13.3"/&gt;
 *         &lt;element name="Xmnga" type="{urn:sap-com:document:sap:rfc:functions}quantum13.3"/&gt;
 *         &lt;element name="Meinh" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="Ile01" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="Ism01" type="{urn:sap-com:document:sap:rfc:functions}quantum13.3"/&gt;
 *         &lt;element name="Ile02" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="Ism02" type="{urn:sap-com:document:sap:rfc:functions}quantum13.3"/&gt;
 *         &lt;element name="Ile03" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="Ism03" type="{urn:sap-com:document:sap:rfc:functions}quantum13.3"/&gt;
 *         &lt;element name="Ile04" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="Ism04" type="{urn:sap-com:document:sap:rfc:functions}quantum13.3"/&gt;
 *         &lt;element name="Ile05" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="Ism05" type="{urn:sap-com:document:sap:rfc:functions}quantum13.3"/&gt;
 *         &lt;element name="Ile06" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="Ism06" type="{urn:sap-com:document:sap:rfc:functions}quantum13.3"/&gt;
 *         &lt;element name="FinConf" type="{urn:sap-com:document:sap:rfc:functions}char1"/&gt;
 *         &lt;element name="Budat" type="{urn:sap-com:document:sap:rfc:functions}date10"/&gt;
 *         &lt;element name="Zflag" type="{urn:sap-com:document:sap:rfc:functions}char1"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Zc80ppif024S1", propOrder = {
    "werks",
    "aufnr",
    "vornr",
    "lmnga",
    "xmnga",
    "meinh",
    "ile01",
    "ism01",
    "ile02",
    "ism02",
    "ile03",
    "ism03",
    "ile04",
    "ism04",
    "ile05",
    "ism05",
    "ile06",
    "ism06",
    "finConf",
    "budat",
    "zflag"
})
public class Zc80Ppif024S1 {

    @XmlElement(name = "Werks", required = true)
    protected String werks;
    @XmlElement(name = "Aufnr", required = true)
    protected String aufnr;
    @XmlElement(name = "Vornr", required = true)
    protected String vornr;
    @XmlElement(name = "Lmnga", required = true)
    protected BigDecimal lmnga;
    @XmlElement(name = "Xmnga", required = true)
    protected BigDecimal xmnga;
    @XmlElement(name = "Meinh", required = true)
    protected String meinh;
    @XmlElement(name = "Ile01", required = true)
    protected String ile01;
    @XmlElement(name = "Ism01", required = true)
    protected BigDecimal ism01;
    @XmlElement(name = "Ile02", required = true)
    protected String ile02;
    @XmlElement(name = "Ism02", required = true)
    protected BigDecimal ism02;
    @XmlElement(name = "Ile03", required = true)
    protected String ile03;
    @XmlElement(name = "Ism03", required = true)
    protected BigDecimal ism03;
    @XmlElement(name = "Ile04", required = true)
    protected String ile04;
    @XmlElement(name = "Ism04", required = true)
    protected BigDecimal ism04;
    @XmlElement(name = "Ile05", required = true)
    protected String ile05;
    @XmlElement(name = "Ism05", required = true)
    protected BigDecimal ism05;
    @XmlElement(name = "Ile06", required = true)
    protected String ile06;
    @XmlElement(name = "Ism06", required = true)
    protected BigDecimal ism06;
    @XmlElement(name = "FinConf", required = true)
    protected String finConf;
    @XmlElement(name = "Budat", required = true)
    protected String budat;
    @XmlElement(name = "Zflag", required = true)
    protected String zflag;

    /**
     * 获取werks属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWerks() {
        return werks;
    }

    /**
     * 设置werks属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWerks(String value) {
        this.werks = value;
    }

    /**
     * 获取aufnr属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAufnr() {
        return aufnr;
    }

    /**
     * 设置aufnr属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAufnr(String value) {
        this.aufnr = value;
    }

    /**
     * 获取vornr属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVornr() {
        return vornr;
    }

    /**
     * 设置vornr属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVornr(String value) {
        this.vornr = value;
    }

    /**
     * 获取lmnga属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLmnga() {
        return lmnga;
    }

    /**
     * 设置lmnga属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLmnga(BigDecimal value) {
        this.lmnga = value;
    }

    /**
     * 获取xmnga属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getXmnga() {
        return xmnga;
    }

    /**
     * 设置xmnga属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setXmnga(BigDecimal value) {
        this.xmnga = value;
    }

    /**
     * 获取meinh属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeinh() {
        return meinh;
    }

    /**
     * 设置meinh属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeinh(String value) {
        this.meinh = value;
    }

    /**
     * 获取ile01属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIle01() {
        return ile01;
    }

    /**
     * 设置ile01属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIle01(String value) {
        this.ile01 = value;
    }

    /**
     * 获取ism01属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getIsm01() {
        return ism01;
    }

    /**
     * 设置ism01属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setIsm01(BigDecimal value) {
        this.ism01 = value;
    }

    /**
     * 获取ile02属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIle02() {
        return ile02;
    }

    /**
     * 设置ile02属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIle02(String value) {
        this.ile02 = value;
    }

    /**
     * 获取ism02属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getIsm02() {
        return ism02;
    }

    /**
     * 设置ism02属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setIsm02(BigDecimal value) {
        this.ism02 = value;
    }

    /**
     * 获取ile03属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIle03() {
        return ile03;
    }

    /**
     * 设置ile03属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIle03(String value) {
        this.ile03 = value;
    }

    /**
     * 获取ism03属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getIsm03() {
        return ism03;
    }

    /**
     * 设置ism03属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setIsm03(BigDecimal value) {
        this.ism03 = value;
    }

    /**
     * 获取ile04属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIle04() {
        return ile04;
    }

    /**
     * 设置ile04属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIle04(String value) {
        this.ile04 = value;
    }

    /**
     * 获取ism04属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getIsm04() {
        return ism04;
    }

    /**
     * 设置ism04属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setIsm04(BigDecimal value) {
        this.ism04 = value;
    }

    /**
     * 获取ile05属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIle05() {
        return ile05;
    }

    /**
     * 设置ile05属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIle05(String value) {
        this.ile05 = value;
    }

    /**
     * 获取ism05属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getIsm05() {
        return ism05;
    }

    /**
     * 设置ism05属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setIsm05(BigDecimal value) {
        this.ism05 = value;
    }

    /**
     * 获取ile06属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIle06() {
        return ile06;
    }

    /**
     * 设置ile06属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIle06(String value) {
        this.ile06 = value;
    }

    /**
     * 获取ism06属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getIsm06() {
        return ism06;
    }

    /**
     * 设置ism06属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setIsm06(BigDecimal value) {
        this.ism06 = value;
    }

    /**
     * 获取finConf属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFinConf() {
        return finConf;
    }

    /**
     * 设置finConf属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFinConf(String value) {
        this.finConf = value;
    }

    /**
     * 获取budat属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBudat() {
        return budat;
    }

    /**
     * 设置budat属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBudat(String value) {
        this.budat = value;
    }

    /**
     * 获取zflag属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZflag() {
        return zflag;
    }

    /**
     * 设置zflag属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZflag(String value) {
        this.zflag = value;
    }

}
