package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.AliPayInfoVo;
import com.stylefeng.guns.api.alipay.vo.AliPayResultVo;

/*
    业务降级方法
 */
public class AliPayServiceMock implements AliPayServiceAPI{
    @Override
    public AliPayInfoVo getQRCode(String orderId) {
        return null;
    }

    @Override
    public AliPayResultVo getOrderStatus(String orderId) {

        AliPayResultVo aliPayResultVO = new AliPayResultVo();
        aliPayResultVO.setOrderId(orderId);
        aliPayResultVO.setOrderStatus(0);
        aliPayResultVO.setOrderMsg("尚未支付成功");

        return aliPayResultVO;
    }
}
