package com.iyin.sign.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iyin.sign.system.dto.req.TwoPressureCodeReqDTO;
import com.iyin.sign.system.entity.SignSysSealPictureDataTmp;

/**
 * @ClassName: SignSysSealPictureDataTmpService
 * @Description: 签章系统印章图片数据服务
 * @Author: wdf
 * @CreateDate: 2019/8/7
 * @UpdateUser: wdf
 * @UpdateDate: 2019/8/7
 * @Version: 1.0.0
 */
public interface SignSysSealPictureDataTmpService extends IService<SignSysSealPictureDataTmp> {
    String savePictureDataPart(TwoPressureCodeReqDTO reqDTO);
}
