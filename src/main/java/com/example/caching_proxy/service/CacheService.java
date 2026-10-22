package com.example.caching_proxy.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface CacheService {
    ResponseEntity<?> handleGetRequests(HttpServletRequest request);
    ResponseEntity<?> handlePutRequests(HttpServletRequest request);
    ResponseEntity<?> handlePostRequests(HttpServletRequest request);
    ResponseEntity<?> handleDeleteRequests(HttpServletRequest request);
}
