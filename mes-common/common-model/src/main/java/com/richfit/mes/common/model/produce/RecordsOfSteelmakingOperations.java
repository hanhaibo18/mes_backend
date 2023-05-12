package com.richfit.mes.common.model.produce;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * 炼钢作业记录表(RecordsOfSteelmakingOperations)表实体类
 *
 * @author makejava
 * @since 2023-05-12 13:59:01
 */
@Data
public class RecordsOfSteelmakingOperations extends BaseEntity<RecordsOfSteelmakingOperations> {

    //租户ID
    private String tenantId;
    //炉号
    private String furnaceNo;
    //耗电量（度）
    private Double powerConsumption;
    //主罐料重量（kg）
    private Double zhuGuanLiao;
    //余料1（kg）
    private Double yuLiao1;
    //余料2（kg）
    private Double yuLiao2;
    //余料3（kg）
    private Double yuLiao3;
    //增碳剂（kg）
    private Double zengTanJi;
    //石灰（kg）
    private Double shiHui;
    //配料总重（kg）
    private Double peiLiaoZongZhong;
    //工序1操作时间
    private Date time1;
    //工序2操作时间
    private Date time2;
    //工序3操作时间
    private Date time3;
    //工序4操作时间
    private Date time4;
    //工序5操作时间
    private Date time5;
    //工序6操作时间
    private Date time6;
    //工序7操作时间
    private Date time7;
    //工序8操作时间
    private Date time8;
    //工序9操作时间
    private Date time9;
    //工序10操作时间
    private Date time10;
    //工序11操作时间
    private Date time11;
    //工序12操作时间
    private Date time12;
    //工序13操作时间
    private Date time13;
    //工序14操作时间
    private Date time14;
    //工序15操作时间
    private Date time15;
    //工序16操作时间
    private Date time16;
    //工序17操作时间
    private Date time17;
    //工序18操作时间
    private Date time18;
    //工序19操作时间
    private Date time19;
    //固体炉料低于20？0否1是
    private Integer time3Choice;
    //工序4钼铁添加量kg
    private Double time4MuTie;
    //工序4镍板添加量kg
    private Double time4NieBan;
    //工序5温度
    private Double time5Temperature;
    //工序6烟味是否变淡0否1是
    private Integer time6Choice;
    //工序6锰铁添加量kg
    private Double time6Mengtie;
    //工序6温度
    private Double time6Temperature;
    //工序7硅铝钡添加量kg
    private Double time7GuiLvBei;
    //工序8高锰or金属锰 0高锰1金属锰
    private Integer time8Choice1;
    //工序8 高锰or金属锰重量kg
    private Double time8Num1;
    //工序8高铬or微铬 0-1
    private Integer time8Choice2;
    //工序8 高铬or微铬重量kg
    private Double time8Num2;
    //工序10渣料融化 0否1是
    private Integer time10Choice;
    //工序12加高锰or金属锰 0-1
    private Integer time12Choice1;
    //工序12高锰or金属锰重量 kg
    private Double time12Num1;
    //工序12加高铬or微铬 0-1
    private Integer time12Choice2;
    //工序12高铬or微铬重量 kg
    private Double time12Num2;
    //工序12加硅铁重量kg
    private Double time12GuiTie;
    //工序12加稀土重量kg
    private Double time12XiTu;
    //工序12加钛铁重量kg
    private Double time12TaiTie;
    //工序12加钒铁重量kg
    private Double time12FanTie;
    //工序13保持白渣20以上0否1是
    private Integer time13Choice;
    //工序14测温
    private Double time14Temperature;
    //工序14喂铝线长度m
    private Double time14LvXian;
    //工序15是否选择 1选择
    private Integer time15Choice;
    //工序16测温℃
    private Double time16Temperature;
    //工序18测温℃
    private Double time18Temperature;
    //硅铁总重量
    private Double guiTie;
    //高锰总重量
    private Double gaoMeng;
    //金属锰总重量
    private Double jinShuMeng;
    //高铬总重量
    private Double gaoGe;
    //微铬总重量
    private Double weiGe;
    //钼铁总重量
    private Double muTie;
    //镍板总重量
    private Double nieBan;
    //硅铝钡总重量
    private Double guiLvBei;
    //稀土总重量
    private Double xiTu;
    //铝线总重量
    private Double lvXian;
    //钒铁总重量
    private Double fanTie;
    //硼铁总重量
    private Double pengTie;
    //钛铁总重量
    private Double taiTie;
    //审核状态
    private Integer status;
    //结果分析数据
    @TableField(exist = false)
    ResultsOfSteelmaking resultsOfSteelmaking;
}

