
package com.kld.mes.erp.entity.router;

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
 *         &lt;element name="EMes" type="{urn:sap-com:document:sap:rfc:functions}char50"/&gt;
 *         &lt;element name="EPlnal" type="{urn:sap-com:document:sap:rfc:functions}char2"/&gt;
 *         &lt;element name="EPlnnr" type="{urn:sap-com:document:sap:rfc:functions}char8"/&gt;
 *         &lt;element name="EType" type="{urn:sap-com:document:sap:rfc:functions}char1"/&gt;
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
    "eMes",
    "ePlnal",
    "ePlnnr",
    "eType"
})
@XmlRootElement(name = "Zc80Ppif026Response")
public class Zc80Ppif026Response {

    @XmlElement(name = "EMes", required = true)
    protected String eMes;
    @XmlElement(name = "EPlnal", required = true)
    protected String ePlnal;
    @XmlElement(name = "EPlnnr", required = true)
    protected String ePlnnr;
    @XmlElement(name = "EType", required = true)
    protected String eType;

    /**
     * 获取eMes属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEMes() {
        return eMes;
    }

    /**
     * 设置eMes属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEMes(String value) {
        this.eMes = value;
    }

    /**
     * 获取ePlnal属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEPlnal() {
        return ePlnal;
    }

    /**
     * 设置ePlnal属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEPlnal(String value) {
        this.ePlnal = value;
    }

    /**
     * 获取ePlnnr属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEPlnnr() {
        return ePlnnr;
    }

    /**
     * 设置ePlnnr属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEPlnnr(String value) {
        this.ePlnnr = value;
    }

    /**
     * 获取eType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEType() {
        return eType;
    }

    /**
     * 设置eType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEType(String value) {
        this.eType = value;
    }

}
