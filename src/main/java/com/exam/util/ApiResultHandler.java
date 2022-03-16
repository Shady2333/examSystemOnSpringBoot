package com.exam.util;

import com.exam.entity.ApiResult;   // 状态码

public class ApiResultHandler {

    public static ApiResult success(Object object) {
        ApiResult apiResult = new ApiResult();
        apiResult.setData(object);   // 将返回到的数据object放入
        apiResult.setCode(200);
        apiResult.setMessage("请求成功");
        return apiResult;
    }
    public static ApiResult fail(){
        ApiResult apiResult = new ApiResult();
        apiResult.setData(null);   // 将返回到的数据object放入
        apiResult.setCode(500);
        apiResult.setMessage("找不到资源");
        return apiResult;
    }

    public static ApiResult success() {
        return success(null);
    }

    public static <T> ApiResult buildApiResult(Integer code, String message, T data) {
        ApiResult apiResult = new ApiResult();

        apiResult.setCode(code);
        apiResult.setMessage(message);
        apiResult.setData(data);
        return apiResult;
    }
}
