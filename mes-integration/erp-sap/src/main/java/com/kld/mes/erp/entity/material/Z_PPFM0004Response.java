package com.kld.mes.erp.entity.material;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element name="T_AUFK" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "tmara"
})
@XmlRootElement(name = "Z_PPFM0004.Response", namespace = "urn:sap-com:document:sap:rfc:functions.response")
public class Z_PPFM0004Response {

    @XmlElement(name = "T_MARA", namespace = "urn:sap-com:document:sap:rfc:functions.response")
    protected Z_PPFM0004.TMARA tmara;

    /**
     * 获取tmara属性的值。
     *
     * @return possible object is
     * {@link Object }
     */
    public Z_PPFM0004.TMARA getTMARA() {
        return tmara;
    }

    /**
     * 设置tmara属性的值。
     *
     * @param value allowed object is
     *              {@link Object }
     */
    public void setTMARA(Z_PPFM0004.TMARA value) {
        this.tmara = value;
    }


}
