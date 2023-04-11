//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.3.2 生成的
// 请访问 <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2019.11.11 时间 02:06:32 PM CST 
//


package com.bsjx.mes.pdm.xml.process;

import javax.xml.bind.annotation.*;
import java.math.BigInteger;


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
 *         &lt;element ref="{}Name"/&gt;
 *         &lt;element ref="{}User" minOccurs="0"/&gt;
 *         &lt;element ref="{}ItemStatus" minOccurs="0"/&gt;
 *         &lt;element ref="{}ReleaseTime"/&gt;
 *         &lt;element ref="{}ProcessType" minOccurs="0"/&gt;
 *         &lt;element ref="{}BlankType" minOccurs="0"/&gt;
 *         &lt;element ref="{}SubstituteMat" minOccurs="0"/&gt;
 *         &lt;element ref="{}TYItemID" minOccurs="0"/&gt;
 *         &lt;element ref="{}MEOPs"/&gt;
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
    "name",
    "user",
    "itemStatus",
    "releaseTime",
    "processType",
    "blankType",
    "substituteMat",
    "tyItemID",
    "meoPs"
})
@XmlRootElement(name = "MEProcess")
public class MEProcess {

    @XmlElement(name = "ID", required = true)
    protected String id;
    @XmlElement(name = "Rev", required = true)
    protected String rev;
    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "User")
    protected BigInteger user;
    @XmlElement(name = "ItemStatus")
    protected String itemStatus;
    @XmlElement(name = "ReleaseTime", required = true)
    protected String releaseTime;
    @XmlElement(name = "ProcessType")
    protected String processType;
    @XmlElement(name = "BlankType")
    protected String blankType;
    @XmlElement(name = "SubstituteMat")
    protected String substituteMat;
    @XmlElement(name = "TYItemID")
    protected String tyItemID;
    @XmlElement(name = "MEOPs", required = true)
    protected MEOPs meoPs;

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
     * 获取user属性的值。
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getUser() {
        return user;
    }

    /**
     * 设置user属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setUser(BigInteger value) {
        this.user = value;
    }

    /**
     * 获取itemStatus属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getItemStatus() {
        return itemStatus;
    }

    /**
     * 设置itemStatus属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setItemStatus(String value) {
        this.itemStatus = value;
    }

    /**
     * 获取releaseTime属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getReleaseTime() {
        return releaseTime;
    }

    /**
     * 设置releaseTime属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setReleaseTime(String value) {
        this.releaseTime = value;
    }

    /**
     * 获取processType属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProcessType() {
        return processType;
    }

    /**
     * 设置processType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProcessType(String value) {
        this.processType = value;
    }

    /**
     * 获取blankType属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBlankType() {
        return blankType;
    }

    /**
     * 设置blankType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBlankType(String value) {
        this.blankType = value;
    }

    /**
     * 获取substituteMat属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSubstituteMat() {
        return substituteMat;
    }

    /**
     * 设置substituteMat属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSubstituteMat(String value) {
        this.substituteMat = value;
    }

    /**
     * 获取tyItemID属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getTYItemID() {
        return tyItemID;
    }

    /**
     * 设置tyItemID属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setTYItemID(String value) {
        this.tyItemID = value;
    }

    /**
     * 获取meoPs属性的值。
     *
     * @return
     *     possible object is
     *     {@link MEOPs }
     *
     */
    public MEOPs getMEOPs() {
        return meoPs;
    }

    /**
     * 设置meoPs属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link MEOPs }
     *     
     */
    public void setMEOPs(MEOPs value) {
        this.meoPs = value;
    }

}
