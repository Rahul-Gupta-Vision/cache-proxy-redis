package com.example.caching_proxy.messageListener;

import com.example.caching_proxy.DTO.CachedResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Set;


@Component
public class MessageListener implements org.springframework.data.redis.connection.MessageListener {
    @Lazy
    @Autowired
    RedisTemplate<String, CachedResponse> redisTemplate;
    @Override
    public void onMessage(Message message, byte[] pattern) {
        /*
         * Get the URI from message change and get the affected GET keys and update the value in redis cache
         */

        try {
            String origin = new String(message.getBody(), StandardCharsets.UTF_8);
            origin = origin.replace("\"", "");
            URI uri = new URI(origin);
            String path = uri.getPath();
            System.out.println("Received the change in path: "+path);

            String exactKey = "GET:"+path+"*";

            Set<String> keysToDelete = redisTemplate.keys(exactKey);

            if(keysToDelete == null || keysToDelete.isEmpty()){
                System.out.println("No related keys found in cache");
            }else {
                redisTemplate.delete(keysToDelete);
            }

            // Deleting the parent level paths if available

            String[] parent = path.split("/");
            if(parent.length > 1){
                String parentPath = "/"+parent[1];
                String parentKeys = "GET:"+parentPath+"*";

                Set<String> parentKeySet = redisTemplate.keys(parentKeys);

                if(parentKeySet == null || parentKeySet.isEmpty()){
                    System.out.println("No Parent key found");
                }else{
                    redisTemplate.delete(parentKeySet);
                }
            }

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }
}
