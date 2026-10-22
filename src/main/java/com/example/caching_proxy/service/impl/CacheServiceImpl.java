package com.example.caching_proxy.service.impl;

import com.example.caching_proxy.CachingProxyApplication;
import com.example.caching_proxy.DTO.CachedResponse;
import com.example.caching_proxy.DTO.Server;
import com.example.caching_proxy.service.CacheService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CacheServiceImpl implements CacheService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    RedisTemplate<String, CachedResponse> redisTemplate;
    @Autowired
    ChannelTopic channelTopic;
    @Override
    public ResponseEntity<?> handleGetRequests(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String origin = Server.getInstance().getOrigin() + uri + ((query!=null)?"?"+query:"");

        String cacheKey = "GET:"+uri;

        CachedResponse cacheResponse = redisTemplate.opsForValue().get(cacheKey);
        /*
        * If cache contains the data fetch from cache and send as response */
        if(null != cacheResponse){
            System.out.println("Found Entry in cache");
            HttpHeaders headers = new HttpHeaders();
            headers.putAll(cacheResponse.getHeaders());
            headers.set("X-Cache", "HIT");
            return ResponseEntity.status(cacheResponse.getStatus_code()).headers(headers).body(cacheResponse.getBody());
        }
        /*
        * Fetch the data from origin with the provided URI, store it in cache and return it*/
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        HttpHeaders reqHeaders = new HttpHeaders();
        Collections.list(request.getHeaderNames()).forEach(h->reqHeaders.add(h, request.getHeader(h)));
        HttpEntity<String> entity = new HttpEntity<>(null, reqHeaders);
        ResponseEntity<byte[]> response = restTemplate.exchange(origin, method, entity, byte[].class);

        /*
        * Store the data inside cache*/
        if(response.hasBody() && response.getStatusCode().is2xxSuccessful()) {
            CachedResponse cachedResponse = new CachedResponse(response.getStatusCode().value(), response.getHeaders(), response.getBody());
            redisTemplate.opsForValue().set(cacheKey, cachedResponse);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.putAll(response.getHeaders());
        headers.set("X-Cache","MISS");
        System.out.println("No entry found in cache, Getting entry from the origin...");
        return ResponseEntity.status(response.getStatusCode()).headers(headers).body(response.getBody());
    }

    @Override
    public ResponseEntity<?> handlePutRequests(HttpServletRequest request) {
        String body;
        try{
            body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        }catch (IOException e){
            throw new RuntimeException("Request body missing");
        }
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String origin = Server.getInstance().getOrigin() + uri + ((query!=null)?"?"+query:"");
        HttpMethod httpMethod = HttpMethod.valueOf(request.getMethod());
        HttpHeaders reqHeaders = new HttpHeaders();
        Collections.list(request.getHeaderNames())
                .forEach(h -> reqHeaders.add(h, request.getHeader(h)));
        HttpEntity<String> entity = new HttpEntity<>(body, reqHeaders);
        ResponseEntity<byte[]> response = restTemplate.exchange(origin, httpMethod, entity, byte[].class);
        /*
        If Response is fine then publish the post event to redis pub-sub
         */
        redisTemplate.convertAndSend(channelTopic.getTopic(), origin);
        /*
        Form the response from the request and send back to client
         */
        HttpHeaders resHeaders = new HttpHeaders();
        resHeaders.putAll(response.getHeaders());
        return ResponseEntity.status(response.getStatusCode()).headers(resHeaders).body(response.getBody());
    }

    @Override
    public ResponseEntity<?> handlePostRequests(HttpServletRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<?> handleDeleteRequests(HttpServletRequest request) {
        return null;
    }
}
