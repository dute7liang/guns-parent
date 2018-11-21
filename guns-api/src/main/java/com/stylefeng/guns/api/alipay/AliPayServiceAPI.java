package com.stylefeng.guns.api.alipay;

import com.stylefeng.guns.api.alipay.vo.AliPayInfoVo;
import com.stylefeng.guns.api.alipay.vo.AliPayResultVo;

public interface AliPayServiceAPI {

    AliPayInfoVo getQRCode(String orderId);

    AliPayResultVo getOrderStatus(String orderId);

}
