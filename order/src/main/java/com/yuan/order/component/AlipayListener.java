package com.yuan.order.component;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/order")
public class AlipayListener {

    //用户付款以后从支付宝拿到回调
    @PostMapping("/notify")
    public String alipayHandler(HttpServletRequest request) throws AlipayApiException {
        Map<String, String[]> requestParams = request.getParameterMap();
        Map<String, String> paramsMap = new HashMap<>();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            valueStr = new String(valueStr.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
            paramsMap.put(name, valueStr);
        }
        boolean signVerified = AlipaySignature.rsaCheckV1(paramsMap, "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAy8aH1f0NYcSRrGfC84+QTRl+89i9vzmEJ/4tzDjXAXpysPiObu0p2Pwq/97G5Y8miX8znqbgiYF46ncye+e9LF44gMgsvPrOXVDah/eXzcGsbiU1M0RDvLw27G3xokbCSpo1UfDIVT/fzwNc/J6w7YkRETmOWXP79yrVyrsFQeowIMLbxozB07R4hDvZMSZFrkPbAOzy7vJddLFANylezmon0RFIAGIF3oXDaU5rBGLooSFmF5YfBPSGDhRYIBCqBR/Ws6QfpnxtCSjqt1GyRxCTCh69V3WisDQqcYmdbpH76xKAW48RWBegkEGojlwKEK7KB6qibXjZ2kIIDfCsMQIDAQAB", "utf-8", "RSA2"); //调用SDK验证签名
        if (signVerified) {
            return "success";
            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
        } else {
            // TODO 验签失败则记录异常日志，并在response中返回failure.
            return "failure";
        }

    }
}
