
package com.kld.mes.erp.entity.order;

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
 *       &lt;all&gt;
 *         &lt;element name="ZDATUM"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;pattern value="\d{4}-\d{2}-\d{2}"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ZWERKS" type="{urn:sap-com:document:sap:rfc:functions}WERKS"/&gt;
 *         &lt;element name="T_AUFK" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "ZC80_PPIF009", namespace = "urn:sap-com:document:sap:rfc:functions")
public class ZC80PPIF009 {

    @XmlElement(name = "ZDATUM", namespace = "urn:sap-com:document:sap:rfc:functions", required = true)
    protected String zdatum;
    @XmlElement(name = "ZWERKS", namespace = "urn:sap-com:document:sap:rfc:functions", required = true)
    protected WERKS zwerks;
    @XmlElement(name = "T_AUFK", namespace = "urn:sap-com:document:sap:rfc:functions")
    protected Object taufk;

    /**
     * 获取zdatum属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZDATUM() {
        return zdatum;
    }

    /**
     * 设置zdatum属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZDATUM(String value) {
        this.zdatum = value;
    }

    /**
     * 获取zwerks属性的值。
     * 
     * @return
     *     possible object is
     *     {@link WERKS }
     *     
     */
    public WERKS getZWERKS() {
        return zwerks;
    }

    /**
     * 设置zwerks属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link WERKS }
     *     
     */
    public void setZWERKS(WERKS value) {
        this.zwerks = value;
    }

    /**
     * 获取taufk属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getTAUFK() {
        return taufk;
    }

    /**
     * 设置taufk属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setTAUFK(Object value) {
        this.taufk = value;
    }

}
