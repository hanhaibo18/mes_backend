package com.bsjx.mes.pdm.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name = "base_i_pdm_draw")
public class PdmDraw {
    /**
     * 图纸
     */
    @Id
    @GenericGenerator(name = "jpa-uuid", strategy = "uuid")
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String drawId;
    /**
     * 图号
     */
    private String itemId;
    /**
     * 版本
     */
    private String itemRev;
/*    //文档类别
   private String fileCategory;
*/
    /**
     * 文件类型
     */
    private String fileType;
    /**
     * 文件名称
     */
    private String fileName;
    /**
     * 文件
     */
    private String fileUrl;
    /**
     * 关联工序
     */
    private String opId;
    /**
     * 关联工序版本
     */
    private String opVer;
    /**
     * 保留
     */
    private String reserve1;
    /**
     * 保留
     */
    private String reserve2;
    /**
     * 保留
     */
    private String reserve3;
    /**
     * 同步时间
     */
    private Date sycTime;

    @Column(name = "isop")
    private String op;

    @Column(name = "datagroup")
    private String dataGroup;


}
