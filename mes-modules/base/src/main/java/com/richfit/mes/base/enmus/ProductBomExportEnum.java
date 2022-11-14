package com.richfit.mes.base.enmus;

/**
 * @author renzewen
 * @date 2022/10/31
 * @apiNote
 */
public enum ProductBomExportEnum {

    /*
     *   产品bom导入校验为空的字段
     */

    mainDrawingNo("mainDrawingNo", "上级产品图号"),
    branchCode("branchCode", "车间"),
    grade("grade", "等级"),
    drawingNo("drawingNo", "零部件图号"),
    materialNo("materialNo", "SAP物料编码"),
    number("number", "用量"),
    trackType("trackType", "跟踪方式"),
    isNumFrom("isNumFrom", "产品编号来源"),
    isNeedPicking("isNeedPicking", "是否仓储领料"),
    isKeyPart("isKeyPart", "是否关键件"),
    isEdgeStore("isEdgeStore", "实物配送区分"),
    isCheck("isCheck", "是否齐套检查");

    private final String code;

    private final String name;

    private ProductBomExportEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static ProductBomExportEnum getSenderEnum(String code) {
        for (ProductBomExportEnum recipientsEnum : ProductBomExportEnum.values()) {
            if (recipientsEnum.code.equals(code)) {
                return recipientsEnum;
            }
        }
        throw new IllegalArgumentException("No element matches " + code);
    }

    public static String getName(String code) {
        for (ProductBomExportEnum recipientsEnum : ProductBomExportEnum.values()) {
            if (recipientsEnum.code.equals(code)) {
                return recipientsEnum.getName();
            }
        }
        return null;
    }

}
