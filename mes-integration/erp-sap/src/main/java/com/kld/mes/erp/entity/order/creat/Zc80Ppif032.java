
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
 *         &lt;element name="TIn" type="{urn:sap-com:document:sap:soap:functions:mc-style}Zc80Ppif032TI"/&gt;
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
    "tIn"
})
@XmlRootElement(name = "Zc80Ppif032")
public class Zc80Ppif032 {

    @XmlElement(name = "TIn", required = true)
    protected Zc80Ppif032TI tIn;

    /**
     * 获取tIn属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Zc80Ppif032TI }
     *     
     */
    public Zc80Ppif032TI getTIn() {
        return tIn;
    }

    /**
     * 设置tIn属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Zc80Ppif032TI }
     *     
     */
    public void setTIn(Zc80Ppif032TI value) {
        this.tIn = value;
    }

}
