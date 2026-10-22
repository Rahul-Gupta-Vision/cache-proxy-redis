package com.example.caching_proxy.DTO;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class CachedResponse implements Serializable {
    int status_code;
    Map<String, List<String>> headers;
    byte[] body;
    public CachedResponse() {}
    public CachedResponse(int status_code, Map<String, List<String>> headers, byte[] body){
        this.status_code = status_code;
        this.headers = headers;
        this.body = body;
    }
}
