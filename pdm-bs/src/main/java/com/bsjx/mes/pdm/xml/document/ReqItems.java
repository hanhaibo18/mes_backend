//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.3.2 生成的
// 请访问 <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2019.11.11 时间 03:47:46 PM CST 
//


package com.bsjx.mes.pdm.xml.document;

import javax.xml.bind.annotation.*;


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
 *         &lt;element ref="{}Item"/&gt;
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
    "item"
})
@XmlRootElement(name = "Items")
public class ReqItems {

    @XmlElement(name = "Item", required = true)
    protected ReqItem item;

    /**
     * 获取item属性的值。
     *
     * @return
     *     possible object is
     *     {@link ReqItem }
     *
     */
    public ReqItem getItem() {
        return item;
    }

    /**
     * 设置item属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link ReqItem }
     *     
     */
    public void setItem(ReqItem value) {
        this.item = value;
    }

}
