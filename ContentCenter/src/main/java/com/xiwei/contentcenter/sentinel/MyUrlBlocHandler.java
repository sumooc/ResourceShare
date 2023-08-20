package com.xiwei.contentcenter.sentinel;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.print.FlavorException;

/**
 * 自定义错误返回
 */
@Component
public class MyUrlBlocHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
        ErrorMsg msg = null;
        if (e instanceof FlavorException) {
            msg = ErrorMsg.builder().status(100).msg("限流了").build();
        } else if (e instanceof DegradeException) {
            msg = ErrorMsg.builder().status(100).msg("热点参数限流").build();
        } else if (e instanceof ParamFlowException) {
            msg = ErrorMsg.builder().status(100).msg("参数流异常").build();
        } else if (e instanceof SystemBlockException) {
            msg = ErrorMsg.builder().status(100).msg("系统规则（负载/...不满足要求）").build();
        } else if (e instanceof AuthorityException) {
            msg = ErrorMsg.builder().status(100).msg("授权规则不通过").build();
        }
        httpServletResponse.setStatus(500);
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setHeader("Content-Type", "application/json;charset=utf-8");
        httpServletResponse.setContentType("application/json;charset=utf-8");
        new ObjectMapper().writeValue(httpServletResponse.getWriter(), msg);
    }
}

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
class ErrorMsg {
    private Integer status;
    private String msg;
}