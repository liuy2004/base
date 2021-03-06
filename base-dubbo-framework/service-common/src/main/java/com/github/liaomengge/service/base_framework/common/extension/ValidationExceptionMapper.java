package com.github.liaomengge.service.base_framework.common.extension;

import com.github.liaomengge.service.base_framework.base.DataResult;

import com.alibaba.dubbo.rpc.protocol.rest.RpcExceptionMapper;
import com.alibaba.dubbo.rpc.protocol.rest.support.ContentType;
import com.github.liaomengge.service.base_framework.common.consts.ServiceConst;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;

/**
 * Created by liaomengge on 16/12/3.
 */
public class ValidationExceptionMapper extends RpcExceptionMapper {

    @Override
    protected Response handleConstraintViolationException(ConstraintViolationException cve) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation cv : cve.getConstraintViolations()) {
            sb.append(String.format("args: %s, value: %s, message: %s", cv.getPropertyPath().toString(),
                    (cv.getInvalidValue() == null ? "null" : cv.getInvalidValue().toString()),
                    cv.getMessage()));
        }

        DataResult<String> dataResult = new DataResult<>(ServiceConst.ResponseStatus.ErrorCodeEnum.PARAM_ERROR.getCode(), sb.toString());

        return Response.status(Response.Status.OK).entity(dataResult).type(ContentType.APPLICATION_JSON_UTF_8).build();
    }
}
