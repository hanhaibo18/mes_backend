//
// 此文件是由 JavaTM Architecture for XML Binding (JAXB) 引用实现 v2.3.2 生成的
// 请访问 <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// 在重新编译源模式时, 对此文件的所有修改都将丢失。
// 生成时间: 2019.11.14 时间 05:01:25 PM CST 
//


package com.bsjx.mes.pdm.xml.document.response;

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
 *         &lt;element name="ItemID" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *         &lt;element ref="{}RevID"/&gt;
 *         &lt;element ref="{}Result" minOccurs="0"/&gt;
 *         &lt;element ref="{}Message" minOccurs="0"/&gt;
 *         &lt;element ref="{}Files" minOccurs="0"/&gt;
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
    "itemID",
    "revID",
    "result",
    "message",
    "files"
})
@XmlRootElement(name = "Item")
public class Item {

    @XmlElement(name = "ItemID", required = true)
    protected Object itemID;
    @XmlElement(name = "RevID", required = true)
    protected String revID;
    @XmlElement(name = "Result")
    protected String result;
    @XmlElement(name = "Message")
    protected String message;
    @XmlElement(name = "Files")
    protected Files files;

    /**
     * 获取itemID属性的值。
     *
     * @return
     *     possible object is
     *     {@link Object }
     *
     */
    public Object getItemID() {
        return itemID;
    }

    /**
     * 设置itemID属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link Object }
     *
     */
    public void setItemID(Object value) {
        this.itemID = value;
    }

    /**
     * 获取revID属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getRevID() {
        return revID;
    }

    /**
     * 设置revID属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setRevID(String value) {
        this.revID = value;
    }

    /**
     * 获取result属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getResult() {
        return result;
    }

    /**
     * 设置result属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setResult(String value) {
        this.result = value;
    }

    /**
     * 获取message属性的值。
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置message属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * 获取files属性的值。
     *
     * @return
     *     possible object is
     *     {@link Files }
     *
     */
    public Files getFiles() {
        return files;
    }

    /**
     * 设置files属性的值。
     *
     * @param value
     *     allowed object is
     *     {@link Files }
     *     
     */
    public void setFiles(Files value) {
        this.files = value;
    }

}
