package com.example.caching_proxy.DTO;


import lombok.Getter;
import lombok.Setter;

import java.awt.*;

public class Server {
    @Getter
    @Setter
    int port;
    @Setter
    @Getter
    String origin;
    private static Server mInstance = null;
    Server(){}
    public static Server getInstance() {
        if (null == mInstance) {
            mInstance = new Server();
            return mInstance;
        }
        return mInstance;
    }

}
