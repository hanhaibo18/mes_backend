//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.3.0 生成的
// 请访问 <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2022.07.25 时间 05:16:19 PM CST 
//


package com.kld.mes.erp.entity.router;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Zc80ppif026S1 complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="Zc80ppif026S1"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Werks" type="{urn:sap-com:document:sap:rfc:functions}char4"/&gt;
 *         &lt;element name="Matnr" type="{urn:sap-com:document:sap:rfc:functions}char18"/&gt;
 *         &lt;element name="Ktext" type="{urn:sap-com:document:sap:rfc:functions}char40"/&gt;
 *         &lt;element name="Verwe" type="{urn:sap-com:document:sap:rfc:functions}char3"/&gt;
 *         &lt;element name="Statu" type="{urn:sap-com:document:sap:rfc:functions}char3"/&gt;
 *         &lt;element name="Datuv" type="{urn:sap-com:document:sap:rfc:functions}date10"/&gt;
 *         &lt;element name="Maktx" type="{urn:sap-com:document:sap:rfc:functions}char40"/&gt;
 *         &lt;element name="Vornr" type="{urn:sap-com:document:sap:rfc:functions}char4"/&gt;
 *         &lt;element name="Arbpl" type="{urn:sap-com:document:sap:rfc:functions}char8"/&gt;
 *         &lt;element name="Steus" type="{urn:sap-com:document:sap:rfc:functions}char4"/&gt;
 *         &lt;element name="Ltxa1" type="{urn:sap-com:document:sap:rfc:functions}char40"/&gt;
 *         &lt;element name="Bmsch" type="{urn:sap-com:document:sap:rfc:functions}quantum13.3"/&gt;
 *         &lt;element name="Meins" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="Vgw01" type="{urn:sap-com:document:sap:rfc:functions}quantum9.3"/&gt;
 *         &lt;element name="Vge01" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="Vgw02" type="{urn:sap-com:document:sap:rfc:functions}quantum9.3"/&gt;
 *         &lt;element name="Vge02" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="Vgw03" type="{urn:sap-com:document:sap:rfc:functions}quantum9.3"/&gt;
 *         &lt;element name="Vge03" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="Vgw04" type="{urn:sap-com:document:sap:rfc:functions}quantum9.3"/&gt;
 *         &lt;element name="Vge04" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="Vgw05" type="{urn:sap-com:document:sap:rfc:functions}quantum9.3"/&gt;
 *         &lt;element name="Vge05" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="Vgw06" type="{urn:sap-com:document:sap:rfc:functions}quantum9.3"/&gt;
 *         &lt;element name="Vge06" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="Frdlb" type="{urn:sap-com:document:sap:rfc:functions}char1"/&gt;
 *         &lt;element name="Ekorg" type="{urn:sap-com:document:sap:rfc:functions}char4"/&gt;
 *         &lt;element name="Matkl" type="{urn:sap-com:document:sap:rfc:functions}char9"/&gt;
 *         &lt;element name="Ekgrp" type="{urn:sap-com:document:sap:rfc:functions}char3"/&gt;
 *         &lt;element name="Peinh" type="{urn:sap-com:document:sap:rfc:functions}decimal5.0"/&gt;
 *         &lt;element name="Sakto" type="{urn:sap-com:document:sap:rfc:functions}char10"/&gt;
 *         &lt;element name="Waers" type="{urn:sap-com:document:sap:rfc:functions}cuky5"/&gt;
 *         &lt;element name="Preis" type="{urn:sap-com:document:sap:rfc:functions}curr11.2"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Zc80ppif026S1", propOrder = {
    "werks",
    "matnr",
    "ktext",
    "verwe",
    "statu",
    "datuv",
    "maktx",
    "vornr",
    "arbpl",
    "steus",
    "ltxa1",
    "bmsch",
    "meins",
    "vgw01",
    "vge01",
    "vgw02",
    "vge02",
    "vgw03",
    "vge03",
    "vgw04",
    "vge04",
    "vgw05",
    "vge05",
    "vgw06",
    "vge06",
    "frdlb",
    "ekorg",
    "matkl",
    "ekgrp",
    "peinh",
    "sakto",
    "waers",
    "preis"
})
public class Zc80Ppif026S1 {

    @XmlElement(name = "Werks", required = true)
    protected String werks;
    @XmlElement(name = "Matnr", required = true)
    protected String matnr;
    @XmlElement(name = "Ktext", required = true)
    protected String ktext;
    @XmlElement(name = "Verwe", required = true)
    protected String verwe;
    @XmlElement(name = "Statu", required = true)
    protected String statu;
    @XmlElement(name = "Datuv", required = true)
    protected String datuv;
    @XmlElement(name = "Maktx", required = true)
    protected String maktx;
    @XmlElement(name = "Vornr", required = true)
    protected String vornr;
    @XmlElement(name = "Arbpl", required = true)
    protected String arbpl;
    @XmlElement(name = "Steus", required = true)
    protected String steus;
    @XmlElement(name = "Ltxa1", required = true)
    protected String ltxa1;
    @XmlElement(name = "Bmsch", required = true)
    protected BigDecimal bmsch;
    @XmlElement(name = "Meins", required = true)
    protected String meins;
    @XmlElement(name = "Vgw01", required = true)
    protected BigDecimal vgw01;
    @XmlElement(name = "Vge01", required = true)
    protected String vge01;
    @XmlElement(name = "Vgw02", required = true)
    protected BigDecimal vgw02;
    @XmlElement(name = "Vge02", required = true)
    protected String vge02;
    @XmlElement(name = "Vgw03", required = true)
    protected BigDecimal vgw03;
    @XmlElement(name = "Vge03", required = true)
    protected String vge03;
    @XmlElement(name = "Vgw04", required = true)
    protected BigDecimal vgw04;
    @XmlElement(name = "Vge04", required = true)
    protected String vge04;
    @XmlElement(name = "Vgw05", required = true)
    protected BigDecimal vgw05;
    @XmlElement(name = "Vge05", required = true)
    protected String vge05;
    @XmlElement(name = "Vgw06", required = true)
    protected BigDecimal vgw06;
    @XmlElement(name = "Vge06", required = true)
    protected String vge06;
    @XmlElement(name = "Frdlb", required = true)
    protected String frdlb;
    @XmlElement(name = "Ekorg", required = true)
    protected String ekorg;
    @XmlElement(name = "Matkl", required = true)
    protected String matkl;
    @XmlElement(name = "Ekgrp", required = true)
    protected String ekgrp;
    @XmlElement(name = "Peinh", required = true)
    protected BigDecimal peinh;
    @XmlElement(name = "Sakto", required = true)
    protected String sakto;
    @XmlElement(name = "Waers", required = true)
    protected String waers;
    @XmlElement(name = "Preis", required = true)
    protected BigDecimal preis;

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
     * 获取ktext属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKtext() {
        return ktext;
    }

    /**
     * 设置ktext属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKtext(String value) {
        this.ktext = value;
    }

    /**
     * 获取verwe属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVerwe() {
        return verwe;
    }

    /**
     * 设置verwe属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVerwe(String value) {
        this.verwe = value;
    }

    /**
     * 获取statu属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatu() {
        return statu;
    }

    /**
     * 设置statu属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatu(String value) {
        this.statu = value;
    }

    /**
     * 获取datuv属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDatuv() {
        return datuv;
    }

    /**
     * 设置datuv属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDatuv(String value) {
        this.datuv = value;
    }

    /**
     * 获取maktx属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaktx() {
        return maktx;
    }

    /**
     * 设置maktx属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaktx(String value) {
        this.maktx = value;
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
     * 获取arbpl属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArbpl() {
        return arbpl;
    }

    /**
     * 设置arbpl属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArbpl(String value) {
        this.arbpl = value;
    }

    /**
     * 获取steus属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSteus() {
        return steus;
    }

    /**
     * 设置steus属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSteus(String value) {
        this.steus = value;
    }

    /**
     * 获取ltxa1属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLtxa1() {
        return ltxa1;
    }

    /**
     * 设置ltxa1属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLtxa1(String value) {
        this.ltxa1 = value;
    }

    /**
     * 获取bmsch属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getBmsch() {
        return bmsch;
    }

    /**
     * 设置bmsch属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setBmsch(BigDecimal value) {
        this.bmsch = value;
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
     * 获取vgw01属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVgw01() {
        return vgw01;
    }

    /**
     * 设置vgw01属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVgw01(BigDecimal value) {
        this.vgw01 = value;
    }

    /**
     * 获取vge01属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVge01() {
        return vge01;
    }

    /**
     * 设置vge01属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVge01(String value) {
        this.vge01 = value;
    }

    /**
     * 获取vgw02属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVgw02() {
        return vgw02;
    }

    /**
     * 设置vgw02属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVgw02(BigDecimal value) {
        this.vgw02 = value;
    }

    /**
     * 获取vge02属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVge02() {
        return vge02;
    }

    /**
     * 设置vge02属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVge02(String value) {
        this.vge02 = value;
    }

    /**
     * 获取vgw03属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVgw03() {
        return vgw03;
    }

    /**
     * 设置vgw03属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVgw03(BigDecimal value) {
        this.vgw03 = value;
    }

    /**
     * 获取vge03属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVge03() {
        return vge03;
    }

    /**
     * 设置vge03属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVge03(String value) {
        this.vge03 = value;
    }

    /**
     * 获取vgw04属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVgw04() {
        return vgw04;
    }

    /**
     * 设置vgw04属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVgw04(BigDecimal value) {
        this.vgw04 = value;
    }

    /**
     * 获取vge04属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVge04() {
        return vge04;
    }

    /**
     * 设置vge04属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVge04(String value) {
        this.vge04 = value;
    }

    /**
     * 获取vgw05属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVgw05() {
        return vgw05;
    }

    /**
     * 设置vgw05属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVgw05(BigDecimal value) {
        this.vgw05 = value;
    }

    /**
     * 获取vge05属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVge05() {
        return vge05;
    }

    /**
     * 设置vge05属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVge05(String value) {
        this.vge05 = value;
    }

    /**
     * 获取vgw06属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getVgw06() {
        return vgw06;
    }

    /**
     * 设置vgw06属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setVgw06(BigDecimal value) {
        this.vgw06 = value;
    }

    /**
     * 获取vge06属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVge06() {
        return vge06;
    }

    /**
     * 设置vge06属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVge06(String value) {
        this.vge06 = value;
    }

    /**
     * 获取frdlb属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrdlb() {
        return frdlb;
    }

    /**
     * 设置frdlb属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrdlb(String value) {
        this.frdlb = value;
    }

    /**
     * 获取ekorg属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEkorg() {
        return ekorg;
    }

    /**
     * 设置ekorg属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEkorg(String value) {
        this.ekorg = value;
    }

    /**
     * 获取matkl属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMatkl() {
        return matkl;
    }

    /**
     * 设置matkl属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMatkl(String value) {
        this.matkl = value;
    }

    /**
     * 获取ekgrp属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEkgrp() {
        return ekgrp;
    }

    /**
     * 设置ekgrp属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEkgrp(String value) {
        this.ekgrp = value;
    }

    /**
     * 获取peinh属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPeinh() {
        return peinh;
    }

    /**
     * 设置peinh属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPeinh(BigDecimal value) {
        this.peinh = value;
    }

    /**
     * 获取sakto属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSakto() {
        return sakto;
    }

    /**
     * 设置sakto属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSakto(String value) {
        this.sakto = value;
    }

    /**
     * 获取waers属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWaers() {
        return waers;
    }

    /**
     * 设置waers属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWaers(String value) {
        this.waers = value;
    }

    /**
     * 获取preis属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getPreis() {
        return preis;
    }

    /**
     * 设置preis属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setPreis(BigDecimal value) {
        this.preis = value;
    }

}
