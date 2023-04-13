package com.bsjx.mes.pdm.xml.bom;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "ID"
})
@XmlRootElement(name = "getBOMInfo")
public class GetBomInfoXml {

    @XmlElement(name = "ID", required = true)
    protected String ID;

    /**
     * 获取ID属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getID() {
        return ID;
    }

    /**
     * 设置ID属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setID(String value) {
        this.ID = value;
    }

}
