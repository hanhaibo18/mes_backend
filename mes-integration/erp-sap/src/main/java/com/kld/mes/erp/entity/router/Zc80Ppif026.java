//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.3.0 生成的
// 请访问 <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2022.07.25 时间 05:16:19 PM CST 
//


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
 *         &lt;element name="IInput" type="{urn:sap-com:document:sap:soap:functions:mc-style}Zc80ppif026T1"/&gt;
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
    "iInput"
})
@XmlRootElement(name = "Zc80Ppif026")
public class Zc80Ppif026 {

    @XmlElement(name = "IInput", required = true)
    protected Zc80Ppif026T1 iInput;

    /**
     * 获取iInput属性的值。
     * 
     * @return
     *     possible object is
     *     {@link Zc80Ppif026T1 }
     *     
     */
    public Zc80Ppif026T1 getIInput() {
        return iInput;
    }

    /**
     * 设置iInput属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link Zc80Ppif026T1 }
     *     
     */
    public void setIInput(Zc80Ppif026T1 value) {
        this.iInput = value;
    }

}
