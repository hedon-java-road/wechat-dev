package com.hedon.filter;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;

import com.google.gson.Gson;
import com.hedon.base.BaseInfoProperties;
import com.hedon.grace.result.GraceJSONResult;
import com.hedon.grace.result.ResponseStatusEnum;
import com.hedon.utils.IPUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RefreshScope // 动态刷新配置中心的配置
public class IPLimitFilter extends BaseInfoProperties implements GlobalFilter, Ordered {
    @Value("${black-ip.continue-count}")
    private Integer continueCount;
    @Value("${black-ip.time-interval}")
    private Duration timeInterval;
    @Value("${black-ip.limit-time}")
    private Duration limitTime;

    /**
     * 判断某个请求的 IP 在 20 秒内的请求次数是否超过 3 次
     * 如果超过 3 次，则限制访问 30 秒
     * 等待 30 秒静默后，才能够继续恢复访问
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return doLimit(exchange, chain);
    }

    /**
     * 限制 IP 请求次数的判断
     * 
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> doLimit(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取 IP 地址
        ServerHttpRequest request = exchange.getRequest();
        String ip = IPUtil.getIP(request);

        // 被拦截的黑名单 IP，如果在 redis 中存在，则表示目前被关小黑屋
        final String ipRedisKeyLimit = "gateway-ip:limit:" + ip;
        long limitLeftTime = redis.ttl(ipRedisKeyLimit);
        if (limitLeftTime > 0) {
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        // 正常的 IP 定义
        final String ipRedisKey = "gateway-ip:" + ip;
        long requestCount = redis.incrment(ipRedisKey, 1);
        if (requestCount == 1) {
            redis.expire(ipRedisKey, timeInterval);
        }

        // 判断是否超限
        if (requestCount > continueCount) {
            redis.set(ipRedisKeyLimit, ipRedisKeyLimit, limitTime);
            return renderErrorMsg(exchange, ResponseStatusEnum.SYSTEM_ERROR_BLACK_IP);
        }

        // 放行
        return chain.filter(exchange);
    }

    /**
     * 重新包装并且返回错误信息
     * 
     * @param exchange 请求上下文
     * @param status   错误状态码
     * @return 空 Mono
     */
    public Mono<Void> renderErrorMsg(ServerWebExchange exchange, ResponseStatusEnum status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        if (!response.getHeaders().containsKey("Content-Type")) {
            response.getHeaders().add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
        }

        GraceJSONResult jsonResult = GraceJSONResult.error(status);
        String resultJson = new Gson().toJson(jsonResult);

        byte[] bytes = Objects.requireNonNull(resultJson.getBytes(StandardCharsets.UTF_8));
        DataBuffer wrap = response.bufferFactory().wrap(bytes);

        return response.writeWith(Objects.requireNonNull(Mono.just(wrap)));
    }

    /**
     * 过滤器的执行顺序，值越小，执行越靠前
     */
    @Override
    public int getOrder() {
        return 1;
    }
}
