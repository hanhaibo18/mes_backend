
package com.kld.mes.erp.entity.material;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ZPPS0007 complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="ZPPS0007"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="MATNR" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="18"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="MAKTX" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="40"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZEINR" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="22"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="MEINS" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="3"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="WERKS" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="4"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="LVORM" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZYL1" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZYL2" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZYL3" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZYL4" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZYL5" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="100"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ZPPS0007", propOrder = {
    "matnr",
    "maktx",
    "zeinr",
    "meins",
    "werks",
    "lvorm",
    "zyl1",
    "zyl2",
    "zyl3",
    "zyl4",
    "zyl5"
})
public class ZPPS0007 {

    @XmlElement(name = "MATNR")
    protected String matnr;
    @XmlElement(name = "MAKTX")
    protected String maktx;
    @XmlElement(name = "ZEINR")
    protected String zeinr;
    @XmlElement(name = "MEINS")
    protected String meins;
    @XmlElement(name = "WERKS")
    protected String werks;
    @XmlElement(name = "LVORM")
    protected String lvorm;
    @XmlElement(name = "ZYL1")
    protected String zyl1;
    @XmlElement(name = "ZYL2")
    protected String zyl2;
    @XmlElement(name = "ZYL3")
    protected String zyl3;
    @XmlElement(name = "ZYL4")
    protected String zyl4;
    @XmlElement(name = "ZYL5")
    protected String zyl5;

    /**
     * 获取matnr属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMATNR() {
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
    public void setMATNR(String value) {
        this.matnr = value;
    }

    /**
     * 获取maktx属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMAKTX() {
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
    public void setMAKTX(String value) {
        this.maktx = value;
    }

    /**
     * 获取zeinr属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZEINR() {
        return zeinr;
    }

    /**
     * 设置zeinr属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZEINR(String value) {
        this.zeinr = value;
    }

    /**
     * 获取meins属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMEINS() {
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
    public void setMEINS(String value) {
        this.meins = value;
    }

    /**
     * 获取werks属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWERKS() {
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
    public void setWERKS(String value) {
        this.werks = value;
    }

    /**
     * 获取lvorm属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLVORM() {
        return lvorm;
    }

    /**
     * 设置lvorm属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLVORM(String value) {
        this.lvorm = value;
    }

    /**
     * 获取zyl1属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZYL1() {
        return zyl1;
    }

    /**
     * 设置zyl1属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZYL1(String value) {
        this.zyl1 = value;
    }

    /**
     * 获取zyl2属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZYL2() {
        return zyl2;
    }

    /**
     * 设置zyl2属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZYL2(String value) {
        this.zyl2 = value;
    }

    /**
     * 获取zyl3属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZYL3() {
        return zyl3;
    }

    /**
     * 设置zyl3属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZYL3(String value) {
        this.zyl3 = value;
    }

    /**
     * 获取zyl4属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZYL4() {
        return zyl4;
    }

    /**
     * 设置zyl4属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZYL4(String value) {
        this.zyl4 = value;
    }

    /**
     * 获取zyl5属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZYL5() {
        return zyl5;
    }

    /**
     * 设置zyl5属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZYL5(String value) {
        this.zyl5 = value;
    }

}
