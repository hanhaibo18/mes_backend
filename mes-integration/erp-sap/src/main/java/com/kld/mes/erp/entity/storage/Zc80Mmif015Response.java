
package com.kld.mes.erp.entity.storage;

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
 *         &lt;element name="TMg" type="{urn:sap-com:document:sap:soap:functions:mc-style}TableOfZc80mmif015S1" minOccurs="0"/&gt;
 *         &lt;element name="TTable" type="{urn:sap-com:document:sap:soap:functions:mc-style}TableOfZc80mmif015S2" minOccurs="0"/&gt;
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
    "tMg",
    "tTable"
})
@XmlRootElement(name = "Zc80Mmif015Response")
public class Zc80Mmif015Response {

    @XmlElement(name = "TMg")
    protected TableOfZc80Mmif015S1 tMg;
    @XmlElement(name = "TTable")
    protected TableOfZc80Mmif015S2 tTable;

    /**
     * 获取tMg属性的值。
     * 
     * @return
     *     possible object is
     *     {@link TableOfZc80Mmif015S1 }
     *     
     */
    public TableOfZc80Mmif015S1 getTMg() {
        return tMg;
    }

    /**
     * 设置tMg属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link TableOfZc80Mmif015S1 }
     *     
     */
    public void setTMg(TableOfZc80Mmif015S1 value) {
        this.tMg = value;
    }

    /**
     * 获取tTable属性的值。
     * 
     * @return
     *     possible object is
     *     {@link TableOfZc80Mmif015S2 }
     *     
     */
    public TableOfZc80Mmif015S2 getTTable() {
        return tTable;
    }

    /**
     * 设置tTable属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link TableOfZc80Mmif015S2 }
     *     
     */
    public void setTTable(TableOfZc80Mmif015S2 value) {
        this.tTable = value;
    }

}
