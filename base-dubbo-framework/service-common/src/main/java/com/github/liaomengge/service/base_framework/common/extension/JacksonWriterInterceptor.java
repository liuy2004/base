package com.github.liaomengge.service.base_framework.common.extension;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.liaomengge.base_common.utils.string.LyStringUtil;
import com.github.liaomengge.service.base_framework.base.BaseResponse;
import com.github.liaomengge.service.base_framework.base.DataResult;
import com.github.liaomengge.service.base_framework.common.consts.ServiceConst;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by liaomengge on 16/12/3.
 */
public class JacksonWriterInterceptor implements WriterInterceptor {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        Object entry = context.getEntity();
        if (entry instanceof DataResult) {
            context.proceed();
            return;
        }

        DataResult<BaseResponse> result = new DataResult<>(true);
        BaseResponse restResponse = new BaseResponse() {
            private static final long serialVersionUID = -4891503559850961000L;
        };
        restResponse.setCode(ServiceConst.ResponseStatus.ErrorCodeEnum.RPC_ERROR.getCode());
        String rpcErrorMsg = LyStringUtil.getValue(context.getEntity());
        if (rpcErrorMsg.contains("Unrecognized field")) {
            rpcErrorMsg = "[请求参数格式错误](" + rpcErrorMsg + ")";
        }
        restResponse.setMsg(rpcErrorMsg);
        result.setData(restResponse);

        this.writeTo(context.getOutputStream(), result);
    }

    private void writeTo(OutputStream outputStream, Object obj) throws IOException {
        JsonGenerator jsonGenerator = null;
        try {
            jsonGenerator = objectMapper.getFactory().createGenerator(outputStream);
            jsonGenerator.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
            objectMapper.writeValue(outputStream, obj);
        } finally {
            if (jsonGenerator != null) {
                jsonGenerator.flush();
                if (!jsonGenerator.isClosed()) {
                    jsonGenerator.close();
                }
            }
        }
    }
}
