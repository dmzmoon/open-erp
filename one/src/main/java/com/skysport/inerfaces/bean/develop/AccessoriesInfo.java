package com.skysport.inerfaces.bean.develop;import java.math.BigDecimal;import java.util.List;/** * 面料信息 */public class AccessoriesInfo extends MaterialInfo {    private String id;    private String accessoriesId;    private String accessoriessName;    private String serialNumber;    private String specificationId;    private List<KFMaterialPantone> pantoneIds;    private List<KFMaterialPosition> positionIds;    private int delFlag;    private String remark;    private String updateTime;    /**     * 辅料序号     */    private String nameNum;    /**     * 工艺要求     */    private String techRequired;    /**     * 工艺要求     */    private BigDecimal length;    /**     * 工艺要求     */    private BigDecimal width;    @Override    public String getId() {        return id;    }    @Override    public void setId(String id) {        this.id = id;    }    public String getAccessoriesId() {        return accessoriesId;    }    public void setAccessoriesId(String accessoriesId) {        this.accessoriesId = accessoriesId;    }    public String getAccessoriessName() {        return accessoriessName;    }    public void setAccessoriessName(String accessoriessName) {        this.accessoriessName = accessoriessName;    }    public String getSerialNumber() {        return serialNumber;    }    public void setSerialNumber(String serialNumber) {        this.serialNumber = serialNumber;    }    public String getSpecificationId() {        return specificationId;    }    public void setSpecificationId(String specificationId) {        this.specificationId = specificationId;    }    @Override    public int getDelFlag() {        return delFlag;    }    @Override    public void setDelFlag(int delFlag) {        this.delFlag = delFlag;    }    @Override    public String getRemark() {        return remark;    }    @Override    public void setRemark(String remark) {        this.remark = remark;    }    @Override    public String getUpdateTime() {        return updateTime;    }    @Override    public void setUpdateTime(String updateTime) {        this.updateTime = updateTime;    }    public String getNameNum() {        return nameNum;    }    public void setNameNum(String nameNum) {        this.nameNum = nameNum;    }    public String getTechRequired() {        return techRequired;    }    public void setTechRequired(String techRequired) {        this.techRequired = techRequired;    }    public BigDecimal getLength() {        return length;    }    public void setLength(BigDecimal length) {        this.length = length;    }    public BigDecimal getWidth() {        return width;    }    public void setWidth(BigDecimal width) {        this.width = width;    }    public List<KFMaterialPosition> getPositionIds() {        return positionIds;    }    public void setPositionIds(List<KFMaterialPosition> positionIds) {        this.positionIds = positionIds;    }    public List<KFMaterialPantone> getPantoneIds() {        return pantoneIds;    }    public void setPantoneIds(List<KFMaterialPantone> pantoneIds) {        this.pantoneIds = pantoneIds;    }}