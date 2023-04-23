
package com.kld.mes.erp.entity.order.creat;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Zc80Ppif032SI complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="Zc80Ppif032SI"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DocId" type="{urn:sap-com:document:sap:rfc:functions}char20"/&gt;
 *         &lt;element name="Material" type="{urn:sap-com:document:sap:rfc:functions}char18"/&gt;
 *         &lt;element name="Plant" type="{urn:sap-com:document:sap:rfc:functions}char4"/&gt;
 *         &lt;element name="OrderType" type="{urn:sap-com:document:sap:rfc:functions}char4"/&gt;
 *         &lt;element name="BasicStartDate" type="{urn:sap-com:document:sap:rfc:functions}date10"/&gt;
 *         &lt;element name="BasicStartTime" type="{urn:sap-com:document:sap:rfc:functions}time"/&gt;
 *         &lt;element name="BasicEndDate" type="{urn:sap-com:document:sap:rfc:functions}date10"/&gt;
 *         &lt;element name="BasicEndTime" type="{urn:sap-com:document:sap:rfc:functions}time"/&gt;
 *         &lt;element name="Quantity" type="{urn:sap-com:document:sap:rfc:functions}quantum13.3"/&gt;
 *         &lt;element name="QuantityUom" type="{urn:sap-com:document:sap:rfc:functions}unit3"/&gt;
 *         &lt;element name="UnloadingPoint" type="{urn:sap-com:document:sap:rfc:functions}char25"/&gt;
 *         &lt;element name="Zfield1" type="{urn:sap-com:document:sap:rfc:functions}char200"/&gt;
 *         &lt;element name="Zfield2" type="{urn:sap-com:document:sap:rfc:functions}char200"/&gt;
 *         &lt;element name="Zfield3" type="{urn:sap-com:document:sap:rfc:functions}char200"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Zc80Ppif032SI", propOrder = {
    "docId",
    "material",
    "plant",
    "orderType",
    "basicStartDate",
    "basicStartTime",
    "basicEndDate",
    "basicEndTime",
    "quantity",
    "quantityUom",
    "unloadingPoint",
    "zfield1",
    "zfield2",
    "zfield3"
})
public class Zc80Ppif032SI {

    @XmlElement(name = "DocId", required = true)
    protected String docId;
    @XmlElement(name = "Material", required = true)
    protected String material;
    @XmlElement(name = "Plant", required = true)
    protected String plant;
    @XmlElement(name = "OrderType", required = true)
    protected String orderType;
    @XmlElement(name = "BasicStartDate", required = true)
    protected String basicStartDate;
    @XmlElement(name = "BasicStartTime", required = true)
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar basicStartTime;
    @XmlElement(name = "BasicEndDate", required = true)
    protected String basicEndDate;
    @XmlElement(name = "BasicEndTime", required = true)
    @XmlSchemaType(name = "time")
    protected XMLGregorianCalendar basicEndTime;
    @XmlElement(name = "Quantity", required = true)
    protected BigDecimal quantity;
    @XmlElement(name = "QuantityUom", required = true)
    protected String quantityUom;
    @XmlElement(name = "UnloadingPoint", required = true)
    protected String unloadingPoint;
    @XmlElement(name = "Zfield1", required = true)
    protected String zfield1;
    @XmlElement(name = "Zfield2", required = true)
    protected String zfield2;
    @XmlElement(name = "Zfield3", required = true)
    protected String zfield3;

    /**
     * 获取docId属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocId() {
        return docId;
    }

    /**
     * 设置docId属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocId(String value) {
        this.docId = value;
    }

    /**
     * 获取material属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterial() {
        return material;
    }

    /**
     * 设置material属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterial(String value) {
        this.material = value;
    }

    /**
     * 获取plant属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlant() {
        return plant;
    }

    /**
     * 设置plant属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlant(String value) {
        this.plant = value;
    }

    /**
     * 获取orderType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderType() {
        return orderType;
    }

    /**
     * 设置orderType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderType(String value) {
        this.orderType = value;
    }

    /**
     * 获取basicStartDate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBasicStartDate() {
        return basicStartDate;
    }

    /**
     * 设置basicStartDate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBasicStartDate(String value) {
        this.basicStartDate = value;
    }

    /**
     * 获取basicStartTime属性的值。
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBasicStartTime() {
        return basicStartTime;
    }

    /**
     * 设置basicStartTime属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBasicStartTime(XMLGregorianCalendar value) {
        this.basicStartTime = value;
    }

    /**
     * 获取basicEndDate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBasicEndDate() {
        return basicEndDate;
    }

    /**
     * 设置basicEndDate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBasicEndDate(String value) {
        this.basicEndDate = value;
    }

    /**
     * 获取basicEndTime属性的值。
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getBasicEndTime() {
        return basicEndTime;
    }

    /**
     * 设置basicEndTime属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setBasicEndTime(XMLGregorianCalendar value) {
        this.basicEndTime = value;
    }

    /**
     * 获取quantity属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getQuantity() {
        return quantity;
    }

    /**
     * 设置quantity属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setQuantity(BigDecimal value) {
        this.quantity = value;
    }

    /**
     * 获取quantityUom属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQuantityUom() {
        return quantityUom;
    }

    /**
     * 设置quantityUom属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQuantityUom(String value) {
        this.quantityUom = value;
    }

    /**
     * 获取unloadingPoint属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnloadingPoint() {
        return unloadingPoint;
    }

    /**
     * 设置unloadingPoint属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnloadingPoint(String value) {
        this.unloadingPoint = value;
    }

    /**
     * 获取zfield1属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZfield1() {
        return zfield1;
    }

    /**
     * 设置zfield1属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZfield1(String value) {
        this.zfield1 = value;
    }

    /**
     * 获取zfield2属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZfield2() {
        return zfield2;
    }

    /**
     * 设置zfield2属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZfield2(String value) {
        this.zfield2 = value;
    }

    /**
     * 获取zfield3属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZfield3() {
        return zfield3;
    }

    /**
     * 设置zfield3属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZfield3(String value) {
        this.zfield3 = value;
    }

}
