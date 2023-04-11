//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.3.2 生成的
// 请访问 <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2019.11.13 时间 04:08:45 PM CST
//


package com.bsjx.mes.pdm.xml.bom;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element ref="{}BlankType" minOccurs="0"/&gt;
 *         &lt;element ref="{}ItemKey" minOccurs="0"/&gt;
 *         &lt;element ref="{}ItemStatus" minOccurs="0"/&gt;
 *         &lt;element ref="{}ItemUser" minOccurs="0"/&gt;
 *         &lt;element ref="{}MateriaName" minOccurs="0"/&gt;
 *         &lt;element ref="{}MateriaNo" minOccurs="0"/&gt;
 *         &lt;element ref="{}Name" minOccurs="0"/&gt;
 *         &lt;element ref="{}ObjectType" minOccurs="0"/&gt;
 *         &lt;element ref="{}ProductName" minOccurs="0"/&gt;
 *         &lt;element ref="{}Quantity" minOccurs="0"/&gt;
 *         &lt;element ref="{}ReleaseTime" minOccurs="0"/&gt;
 *         &lt;element ref="{}Remark" minOccurs="0"/&gt;
 *         &lt;element ref="{}Rev" minOccurs="0"/&gt;
 *         &lt;element ref="{}User" minOccurs="0"/&gt;
 *         &lt;element ref="{}Weight" minOccurs="0"/&gt;
 *         &lt;element ref="{}BOM" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "blankType",
        "itemKey",
        "itemStatus",
        "itemUser",
        "materiaName",
        "materiaNo",
        "name",
        "objectType",
        "productName",
        "quantity",
        "releaseTime",
        "remark",
        "rev",
        "user",
        "weight",
        "bom"
})
@XmlRootElement(name = "BOM")
public class BOM {

    @XmlElement(name = "ID", required = true)
    protected String id;
    @XmlElement(name = "BlankType")
    protected String blankType;
    @XmlElement(name = "ItemKey")
    protected String itemKey;
    @XmlElement(name = "ItemStatus")
    protected String itemStatus;
    @XmlElement(name = "ItemUser")
    protected String itemUser;
    @XmlElement(name = "MateriaName")
    protected String materiaName;
    @XmlElement(name = "MateriaNo")
    protected String materiaNo;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "ObjectType")
    protected String objectType;
    @XmlElement(name = "ProductName")
    protected String productName;
    @XmlElement(name = "Quantity")
    protected String quantity;
    @XmlElement(name = "ReleaseTime")
    protected String releaseTime;
    @XmlElement(name = "Remark")
    protected String remark;
    @XmlElement(name = "Rev")
    protected String rev;
    @XmlElement(name = "User")
    protected String user;
    @XmlElement(name = "Weight")
    protected String weight;
    @XmlElement(name = "BOM")
    protected List<BOM> bom;

    /**
     * 获取id属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
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
    public void setId(String value) {
        this.id = value;
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
     * 获取itemKey属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getItemKey() {
        return itemKey;
    }

    /**
     * 设置itemKey属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setItemKey(String value) {
        this.itemKey = value;
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
     * 获取itemUser属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getItemUser() {
        return itemUser;
    }

    /**
     * 设置itemUser属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setItemUser(String value) {
        this.itemUser = value;
    }

    /**
     * 获取materiaName属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMateriaName() {
        return materiaName;
    }

    /**
     * 设置materiaName属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMateriaName(String value) {
        this.materiaName = value;
    }

    /**
     * 获取materiaNo属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMateriaNo() {
        return materiaNo;
    }

    /**
     * 设置materiaNo属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMateriaNo(String value) {
        this.materiaNo = value;
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
     * 获取objectType属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * 设置objectType属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setObjectType(String value) {
        this.objectType = value;
    }

    /**
     * 获取productName属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProductName() {
        return productName;
    }

    /**
     * 设置productName属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProductName(String value) {
        this.productName = value;
    }

    /**
     * 获取quantity属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getQuantity() {
        return quantity;
    }

    /**
     * 设置quantity属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setQuantity(String value) {
        this.quantity = value;
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
     * 获取user属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUser() {
        return user;
    }

    /**
     * 设置user属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUser(String value) {
        this.user = value;
    }

    /**
     * 获取weight属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getWeight() {
        return weight;
    }

    /**
     * 设置weight属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setWeight(String value) {
        this.weight = value;
    }

    /**
     * Gets the value of the bom property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the bom property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBOM().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BOM }
     *
     *
     */
    public List<BOM> getBOM() {
        if (bom == null) {
            bom = new ArrayList<BOM>();
        }
        return this.bom;
    }

}
