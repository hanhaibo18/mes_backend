
package com.kld.mes.erp.entity.certWorkHour;

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
 *         &lt;element name="EMes" type="{urn:sap-com:document:sap:rfc:functions}char20"/&gt;
 *         &lt;element name="EOutput" type="{urn:sap-com:document:sap:soap:functions:mc-style}Zc80ppif024T2"/&gt;
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
    "eOutput",
    "eType"
})
@XmlRootElement(name = "Zc80Ppif024Response")
public class Zc80Ppif024Response {

    @XmlElement(name = "EMes", required = true)
    protected String eMes;
    @XmlElement(name = "EOutput", required = true)
    protected Zc80Ppif024T2 eOutput;
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
     * 获取eOutput属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Zc80Ppif024T2 }
     *     
     */
    public Zc80Ppif024T2 getEOutput() {
        return eOutput;
    }

    /**
     * 设置eOutput属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Zc80Ppif024T2 }
     *     
     */
    public void setEOutput(Zc80Ppif024T2 value) {
        this.eOutput = value;
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
