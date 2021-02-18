package me.cunzai.chatlog.queue;

import lombok.Getter;
import me.cunzai.chatlog.data.PlayerData;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @Author: EmptyIrony
 * @Date: 2021/2/10 10:29
 */
public class RedisQueue implements Runnable{
    @Getter
    private final Queue<PlayerData> saveQueue = new LinkedList<>();


    @Override
    public void run() {
        //save 5000 numbers pre second.
        for (int i = 0; i < 5000; i++) {
            PlayerData data = saveQueue.poll();
            if (data != null){
                data.updateRedisCache();
            }else {
                return;
            }
        }
    }
}
