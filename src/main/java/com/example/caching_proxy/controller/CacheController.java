package com.example.caching_proxy.controller;

import com.example.caching_proxy.service.CacheService;
import com.example.caching_proxy.service.impl.CacheServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {

    private CacheServiceImpl cacheService;
    public CacheController(CacheServiceImpl cacheService){
        this.cacheService = cacheService;
    }
    @RequestMapping("/**")
    public ResponseEntity<?> handleRequests(HttpServletRequest request){
        if(request.getMethod().equals("GET")){
            return cacheService.handleGetRequests(request);
        }
        if(request.getMethod().equals("PUT")){
            return cacheService.handlePutRequests(request);
        }
        return null;
    }
}
