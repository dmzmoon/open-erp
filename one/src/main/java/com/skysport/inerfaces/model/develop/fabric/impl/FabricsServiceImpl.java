package com.skysport.inerfaces.model.develop.fabric.impl;

import com.skysport.core.model.seqno.service.IncrementNumber;
import com.skysport.inerfaces.bean.develop.BomInfo;
import com.skysport.inerfaces.bean.develop.FabricsInfo;
import com.skysport.inerfaces.bean.develop.join.FabricsJoinInfo;
import com.skysport.inerfaces.constant.WebConstants;
import com.skysport.inerfaces.mapper.info.FabricsManageMapper;
import com.skysport.core.model.common.impl.CommonServiceImpl;
import com.skysport.inerfaces.model.develop.fabric.IFabricsService;
import com.skysport.inerfaces.model.develop.pantone.helper.KFMaterialPantoneServiceHelper;
import com.skysport.inerfaces.model.develop.pantone.service.IKFMaterialPantoneService;
import com.skysport.inerfaces.model.develop.position.helper.KFMaterialPositionServiceHelper;
import com.skysport.inerfaces.model.develop.position.service.IKFMaterialPositionService;
import com.skysport.inerfaces.utils.BuildSeqNoHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 类说明:面料相关的业务处理
 * Created by zhangjh on 2015/6/29.
 */
@Service("fabricsManageService")
public class FabricsServiceImpl extends CommonServiceImpl<FabricsInfo> implements IFabricsService, InitializingBean {
    @Resource(name = "fabricsManageDao")
    private FabricsManageMapper fabricsManageDao;

    @Resource(name = "incrementNumber")
    private IncrementNumber incrementNumber;

    @Resource(name = "kFMaterialPositionService")
    private IKFMaterialPositionService kFMaterialPositionService;

    @Resource(name = "kFMaterialPantoneService")
    private IKFMaterialPantoneService kFMaterialPantoneService;

    @Override
    public void afterPropertiesSet() {
        commonDao = fabricsManageDao;
    }

    @Override
    public List<FabricsInfo> queryFabricByBomId(String bomId) {
        return fabricsManageDao.queryFabricByBomId(bomId);
    }

    @Override
    public List<FabricsInfo> queryFabricList(String natrualKey) {
        return fabricsManageDao.queryFabricList(natrualKey);
    }

    /**
     * 保存面料信息
     *
     * @param fabricItems List<FabricsJoinInfo>
     * @param bomInfo     BomInfo
     */
    @Override
    public void updateBatch(List<FabricsJoinInfo> fabricItems, BomInfo bomInfo) {


        //找出被删除的面料id，并删除
        String bomId = StringUtils.isBlank(bomInfo.getNatrualkey()) ? bomInfo.getBomId() : bomInfo.getNatrualkey();

        deleteFabircsByIds(fabricItems, bomId);

        if (null != fabricItems) {
            //面料id存在，修改；面料id不存在则新增
            for (FabricsJoinInfo fabricsJoinInfo : fabricItems) {

                String fabricId = fabricsJoinInfo.getFabricsInfo().getFabricId();

                //有id，更新
                if (StringUtils.isNotBlank(fabricId)) {
                    setFabricId(fabricsJoinInfo, fabricId, bomId);
                    fabricsManageDao.updateInfo(fabricsJoinInfo.getFabricsInfo());
                    fabricsManageDao.updateDetail(fabricsJoinInfo.getFabricsDetailInfo());
                    fabricsManageDao.updateDosage(fabricsJoinInfo.getMaterialUnitDosage());
                    fabricsManageDao.updateSp(fabricsJoinInfo.getMaterialSpInfo());
                    kFMaterialPositionService.del(fabricId);//删除物料位置信息
                }
                //无id，新增
                else {
                    String kind_name = buildKindName(bomInfo, fabricsJoinInfo);
                    String seqNo = BuildSeqNoHelper.SINGLETONE.getFullSeqNo(kind_name, incrementNumber, WebConstants.MATERIAL_SEQ_NO_LENGTH);
                    //年份+客户+地域+系列+NNN
                    fabricId = kind_name + seqNo;
                    setFabricId(fabricsJoinInfo, fabricId, bomId);
                    fabricsManageDao.add(fabricsJoinInfo.getFabricsInfo());
                    //新增面料详细
                    fabricsManageDao.addDetail(fabricsJoinInfo.getFabricsDetailInfo());
                    //新增面料用量
                    fabricsManageDao.addDosage(fabricsJoinInfo.getMaterialUnitDosage());
                    //新增面料供应商信息
                    fabricsManageDao.addSp(fabricsJoinInfo.getMaterialSpInfo());
                }

                //保存物料位置信息
                if (null != fabricsJoinInfo.getFabricsInfo().getPositionIds() && !fabricsJoinInfo.getFabricsInfo().getPositionIds().isEmpty()) {
                    kFMaterialPositionService.addBatch(fabricsJoinInfo.getFabricsInfo().getPositionIds());
                }


                //保留物料颜色信息
                if (null != fabricsJoinInfo.getFabricsInfo().getPantoneIds() && !fabricsJoinInfo.getFabricsInfo().getPantoneIds().isEmpty()) {
                    kFMaterialPantoneService.addBatch(fabricsJoinInfo.getFabricsInfo().getPantoneIds());
                }


            }
        }

    }

    @Override
    public List<FabricsInfo> queryAllFabricByBomId(String bomId) {
        return fabricsManageDao.queryAllFabricByBomId(bomId);
    }

    /**
     * @param fabricsJoinInfo FabricsJoinInfo
     * @param fabricId        String
     * @param bomId           String
     */
    private void setFabricId(FabricsJoinInfo fabricsJoinInfo, String fabricId, String bomId) {

        fabricsJoinInfo.getFabricsInfo().setFabricId(fabricId);
        fabricsJoinInfo.getFabricsInfo().setNatrualkey(fabricId);
        fabricsJoinInfo.getFabricsInfo().setBomId(bomId);
        fabricsJoinInfo.getFabricsDetailInfo().setFabricId(fabricId);
        fabricsJoinInfo.getMaterialUnitDosage().setMaterialId(fabricId);
        fabricsJoinInfo.getMaterialSpInfo().setMaterialId(fabricId);

        //设置物料位置的物料id
        KFMaterialPositionServiceHelper.SINGLETONE.setPositionFabricId(fabricsJoinInfo.getFabricsInfo().getPositionIds(), fabricId);
        //设置物料颜色的物料id
        KFMaterialPantoneServiceHelper.SINGLETONE.setPantoneFabricId(fabricsJoinInfo.getFabricsInfo().getPantoneIds(), fabricId);

    }

    /**
     * 构建面料id:材料类别+供应商代码+年份+材质+品名+序号
     *
     * @param bomInfo         BomInfo
     * @param fabricsJoinInfo FabricsJoinInfo
     * @return String
     */
    private String buildKindName(BomInfo bomInfo, FabricsJoinInfo fabricsJoinInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        String materialTypeId = StringUtils.isBlank(fabricsJoinInfo.getFabricsInfo().getMaterialTypeId()) ? WebConstants.FABRIC_MATERIAL_TYPE_ID : fabricsJoinInfo.getFabricsInfo().getMaterialTypeId();
        stringBuilder.append(materialTypeId);
        stringBuilder.append(fabricsJoinInfo.getFabricsInfo().getSpId());
        stringBuilder.append(fabricsJoinInfo.getFabricsInfo().getYearCode());
        stringBuilder.append(fabricsJoinInfo.getFabricsInfo().getProductTypeId());
        return stringBuilder.toString();

    }

    /**
     * 找出被删除的面料id，并删除
     *
     * @param fabricItems List<FabricsJoinInfo>
     * @param bomId       String
     */
    private void deleteFabircsByIds(List<FabricsJoinInfo> fabricItems, String bomId) {
        List<String> allFabricIds = fabricsManageDao.selectAllFabricId(bomId);
        List<String> needToSaveFabricId = buildNeedSaveFabricId(fabricItems);
        allFabricIds.removeAll(needToSaveFabricId);
        if (null != allFabricIds && !allFabricIds.isEmpty()) {
            fabricsManageDao.deleteFabircsByIds(allFabricIds);
        }

    }

    /**
     * @param fabricItems List<FabricsJoinInfo>
     * @return List<String>
     */
    private List<String> buildNeedSaveFabricId(List<FabricsJoinInfo> fabricItems) {
        List<String> needToSaveFabricId = new ArrayList<String>();
        if (null != fabricItems) {
            for (FabricsJoinInfo fabricsJoinInfo : fabricItems) {
                String fabricId = fabricsJoinInfo.getFabricsInfo().getFabricId();
                needToSaveFabricId.add(fabricId);
            }
        }
        return needToSaveFabricId;
    }


}