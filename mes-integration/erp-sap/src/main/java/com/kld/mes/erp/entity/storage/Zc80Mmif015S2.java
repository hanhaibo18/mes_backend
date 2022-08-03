
package com.kld.mes.erp.entity.storage;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Zc80mmif015S2 complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="Zc80mmif015S2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Werks" type="{urn:sap-com:document:sap:rfc:functions}char4"/&gt;
 *         &lt;element name="Lgort" type="{urn:sap-com:document:sap:rfc:functions}char4"/&gt;
 *         &lt;element name="Matnr" type="{urn:sap-com:document:sap:rfc:functions}char18"/&gt;
 *         &lt;element name="Labst" type="{urn:sap-com:document:sap:rfc:functions}quantum13.3"/&gt;
 *         &lt;element name="Meins" type="{urn:sap-com:document:sap:rfc:functions}char3"/&gt;
 *         &lt;element name="Charg" type="{urn:sap-com:document:sap:rfc:functions}char10"/&gt;
 *         &lt;element name="Sobkz" type="{urn:sap-com:document:sap:rfc:functions}char1"/&gt;
 *         &lt;element name="Zkcbm" type="{urn:sap-com:document:sap:rfc:functions}char18"/&gt;
 *         &lt;element name="Insme" type="{urn:sap-com:document:sap:rfc:functions}quantum13.3"/&gt;
 *         &lt;element name="Speme" type="{urn:sap-com:document:sap:rfc:functions}quantum13.3"/&gt;
 *         &lt;element name="Umlme" type="{urn:sap-com:document:sap:rfc:functions}quantum13.3"/&gt;
 *         &lt;element name="Zyuliu1" type="{urn:sap-com:document:sap:rfc:functions}char18"/&gt;
 *         &lt;element name="Zyuliu2" type="{urn:sap-com:document:sap:rfc:functions}char18"/&gt;
 *         &lt;element name="Zyuliu3" type="{urn:sap-com:document:sap:rfc:functions}char18"/&gt;
 *         &lt;element name="Zyuliu4" type="{urn:sap-com:document:sap:rfc:functions}char18"/&gt;
 *         &lt;element name="Zyuliu5" type="{urn:sap-com:document:sap:rfc:functions}char18"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Zc80mmif015S2", propOrder = {
    "werks",
    "lgort",
    "matnr",
    "labst",
    "meins",
    "charg",
    "sobkz",
    "zkcbm",
    "insme",
    "speme",
    "umlme",
    "zyuliu1",
    "zyuliu2",
    "zyuliu3",
    "zyuliu4",
    "zyuliu5"
})
public class Zc80Mmif015S2 {

    @XmlElement(name = "Werks", required = true)
    protected String werks;
    @XmlElement(name = "Lgort", required = true)
    protected String lgort;
    @XmlElement(name = "Matnr", required = true)
    protected String matnr;
    @XmlElement(name = "Labst", required = true)
    protected BigDecimal labst;
    @XmlElement(name = "Meins", required = true)
    protected String meins;
    @XmlElement(name = "Charg", required = true)
    protected String charg;
    @XmlElement(name = "Sobkz", required = true)
    protected String sobkz;
    @XmlElement(name = "Zkcbm", required = true)
    protected String zkcbm;
    @XmlElement(name = "Insme", required = true)
    protected BigDecimal insme;
    @XmlElement(name = "Speme", required = true)
    protected BigDecimal speme;
    @XmlElement(name = "Umlme", required = true)
    protected BigDecimal umlme;
    @XmlElement(name = "Zyuliu1", required = true)
    protected String zyuliu1;
    @XmlElement(name = "Zyuliu2", required = true)
    protected String zyuliu2;
    @XmlElement(name = "Zyuliu3", required = true)
    protected String zyuliu3;
    @XmlElement(name = "Zyuliu4", required = true)
    protected String zyuliu4;
    @XmlElement(name = "Zyuliu5", required = true)
    protected String zyuliu5;

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
     * 获取lgort属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLgort() {
        return lgort;
    }

    /**
     * 设置lgort属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLgort(String value) {
        this.lgort = value;
    }

    /**
     * 获取matnr属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMatnr() {
        return matnr;
    }

    /**
     * 设置matnr属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMatnr(String value) {
        this.matnr = value;
    }

    /**
     * 获取labst属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getLabst() {
        return labst;
    }

    /**
     * 设置labst属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setLabst(BigDecimal value) {
        this.labst = value;
    }

    /**
     * 获取meins属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMeins() {
        return meins;
    }

    /**
     * 设置meins属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMeins(String value) {
        this.meins = value;
    }

    /**
     * 获取charg属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCharg() {
        return charg;
    }

    /**
     * 设置charg属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCharg(String value) {
        this.charg = value;
    }

    /**
     * 获取sobkz属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSobkz() {
        return sobkz;
    }

    /**
     * 设置sobkz属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSobkz(String value) {
        this.sobkz = value;
    }

    /**
     * 获取zkcbm属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZkcbm() {
        return zkcbm;
    }

    /**
     * 设置zkcbm属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZkcbm(String value) {
        this.zkcbm = value;
    }

    /**
     * 获取insme属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getInsme() {
        return insme;
    }

    /**
     * 设置insme属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setInsme(BigDecimal value) {
        this.insme = value;
    }

    /**
     * 获取speme属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSpeme() {
        return speme;
    }

    /**
     * 设置speme属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSpeme(BigDecimal value) {
        this.speme = value;
    }

    /**
     * 获取umlme属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getUmlme() {
        return umlme;
    }

    /**
     * 设置umlme属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setUmlme(BigDecimal value) {
        this.umlme = value;
    }

    /**
     * 获取zyuliu1属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZyuliu1() {
        return zyuliu1;
    }

    /**
     * 设置zyuliu1属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZyuliu1(String value) {
        this.zyuliu1 = value;
    }

    /**
     * 获取zyuliu2属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZyuliu2() {
        return zyuliu2;
    }

    /**
     * 设置zyuliu2属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZyuliu2(String value) {
        this.zyuliu2 = value;
    }

    /**
     * 获取zyuliu3属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZyuliu3() {
        return zyuliu3;
    }

    /**
     * 设置zyuliu3属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZyuliu3(String value) {
        this.zyuliu3 = value;
    }

    /**
     * 获取zyuliu4属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZyuliu4() {
        return zyuliu4;
    }

    /**
     * 设置zyuliu4属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZyuliu4(String value) {
        this.zyuliu4 = value;
    }

    /**
     * 获取zyuliu5属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZyuliu5() {
        return zyuliu5;
    }

    /**
     * 设置zyuliu5属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZyuliu5(String value) {
        this.zyuliu5 = value;
    }

}
