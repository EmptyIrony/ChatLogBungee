package me.cunzai.chatlog.queue;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import me.cunzai.chatlog.ChatLogBungee;
import me.cunzai.chatlog.data.PlayerData;
import org.bson.Document;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: EmptyIrony
 * @Date: 2021/2/10 10:29
 */
public class RedisQueue implements Runnable{
    private final ExecutorService executor = new ThreadPoolExecutor(16, 16,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());
    @Getter
    private final Queue<PlayerData> saveQueue = new LinkedList<>();


    @Override
    public void run() {
        PlayerData data = saveQueue.poll();
        if (data != null){
            data.updateRedisCache();
        }
    }
}
