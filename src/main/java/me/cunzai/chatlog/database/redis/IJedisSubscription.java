package me.cunzai.chatlog.database.redis;

import com.google.gson.JsonObject;

public interface IJedisSubscription {

    /**
     * @param payload
     * @param data
     */
    void handleMessage(String payload, JsonObject data);

    /**
     * @return
     */
    String[] subscriptionChannels();

}
