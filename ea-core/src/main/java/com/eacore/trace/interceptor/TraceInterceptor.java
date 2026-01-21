package com.eacore.trace.interceptor;

import com.eacore.trace.dto.TraceInfo;
import com.eacore.trace.service.TraceInfoRegistry;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class TraceInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TraceInterceptor.class);
    private static final String MSG_NO_KEY = "msgno";
    private final String hostIp;

    private final TraceInfoRegistry traceInfoRegistry;

    public TraceInterceptor(TraceInfoRegistry traceInfoRegistry) {
        this.traceInfoRegistry = traceInfoRegistry;
        this.hostIp = initializeHostIp();
    }

    private String initializeHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.warn("Could not determine host IP address. Falling back to 'unknown'.", e);
            return "unknown";
        }
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 生成唯一的追踪ID
        String msgno = UUID.randomUUID().toString().replace("-", "");
        // 放入MDC，以便日志框架可以自动打印
        MDC.put(MSG_NO_KEY, msgno);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            // 只有当处理器是Controller方法时才记录
            if (handler instanceof HandlerMethod) {
                HandlerMethod handlerMethod = (HandlerMethod) handler;
                // 使用 "全限定类名#方法名" 作为接口的唯一标识
                String interfaceName = handlerMethod.getBeanType().getName() + "#" + handlerMethod.getMethod().getName();
                String msgno = MDC.get(MSG_NO_KEY);

                if (msgno != null) {
                    TraceInfo traceInfo = new TraceInfo(msgno, hostIp);
                    traceInfoRegistry.updateTraceInfo(interfaceName, traceInfo);
                    // 显式打印日志，确保即使不修改 logback 配置也能在日志文件中搜到该 msgno
                    logger.info("Trace recorded: interface=[{}], msgno=[{}], ip=[{}]", interfaceName, msgno, hostIp);
                }
            }
        } finally {
            // 清理MDC，防止内存泄漏
            MDC.remove(MSG_NO_KEY);
        }
    }
}