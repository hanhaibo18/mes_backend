
package com.kld.mes.erp.entity.order.creat;

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
 *         &lt;element name="TOut" type="{urn:sap-com:document:sap:soap:functions:mc-style}Zc80Ppif032TO"/&gt;
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
    "tOut"
})
@XmlRootElement(name = "Zc80Ppif032Response")
public class Zc80Ppif032Response {

    @XmlElement(name = "TOut", required = true)
    protected Zc80Ppif032TO tOut;

    /**
     * 获取tOut属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Zc80Ppif032TO }
     *     
     */
    public Zc80Ppif032TO getTOut() {
        return tOut;
    }

    /**
     * 设置tOut属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Zc80Ppif032TO }
     *     
     */
    public void setTOut(Zc80Ppif032TO value) {
        this.tOut = value;
    }

}
