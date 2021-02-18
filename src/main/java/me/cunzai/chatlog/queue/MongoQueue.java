package me.cunzai.chatlog.queue;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import me.cunzai.chatlog.ChatLogBungee;
import me.cunzai.chatlog.data.PlayerData;
import org.bson.Document;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @Author: EmptyIrony
 * @Date: 2021/2/10 10:28
 */

public class MongoQueue implements Runnable{
    @Getter
    private final Queue<PlayerData> saveQueue = new LinkedList<>();


    @Override
    public void run() {
        PlayerData data = saveQueue.poll();
        if (data != null){
            Document document = data.dataToDocument();
            ChatLogBungee.getInstance()
                    .getMongoDB()
                    .getDocuments()
                    .replaceOne(Filters.eq("uuid",data.getUuid().toString()),document,new ReplaceOptions().upsert(true));
        }
    }
}
