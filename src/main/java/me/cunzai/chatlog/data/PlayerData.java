package me.cunzai.chatlog.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.ToString;
import me.cunzai.chatlog.ChatLogBungee;
import me.cunzai.chatlog.data.sub.ChatData;
import org.bson.Document;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: EmptyIrony
 * @Date: 2021/2/8 20:55
 */

@Getter
@ToString
public class PlayerData {
    private final static JsonParser parser = new JsonParser();
    private final static Map<UUID, PlayerData> dataUuidMap = new HashMap<>();
    private final static Map<String, PlayerData> dataNameMap = new HashMap<>();
    private final List<ChatData> chats;
    private UUID uuid;
    private String name;

    public PlayerData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;

        this.chats = new ArrayList<>();
    }

    public PlayerData() {
        this.chats = new ArrayList<>();
    }


    public void handleJoin(){
        System.out.println("login: " + this.uuid.toString());
        System.out.println("login: " + this.name);
        dataUuidMap.put(this.uuid,this);
        dataNameMap.put(this.name.toLowerCase(),this);

        try(Jedis jedis = ChatLogBungee.getInstance().getJedisHelper().getPool().getResource()){
            jedis.hset("uuid-to-name",this.uuid.toString(),this.name);
            jedis.hset("chatLogData",name.toLowerCase(),this.dataToString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void updateRedisCache(){
        try(Jedis jedis = ChatLogBungee.getInstance().getJedisHelper().getPool().getResource()){
            jedis.hset("chatLogData",name.toLowerCase(),this.dataToString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void handleQuit(){
        dataUuidMap.remove(this.uuid);
        dataNameMap.remove(this.name.toLowerCase());

        try(Jedis jedis = ChatLogBungee.getInstance().getJedisHelper().getPool().getResource()){
            jedis.hdel("uuid-to-name",this.uuid.toString());
            jedis.hdel("chatLogData",name.toLowerCase());
        }catch (Exception e){
            e.printStackTrace();
        }

        List<ChatData> shouldRemove = this.chats.stream()
                .filter(chatData -> System.currentTimeMillis() - chatData.getTimestamp() >= 30 * 24 * 60 * 60 * 1000L)
                .collect(Collectors.toList());

        this.chats.removeAll(shouldRemove);

        ChatLogBungee.getInstance()
                .getMongoDB()
                .getDocuments()
                .replaceOne(Filters.eq("uuid",this.uuid.toString()),dataToDocument(),new ReplaceOptions().upsert(true));
    }

    public static PlayerData getPlayerDataFromCache(UUID uuid){
        return dataUuidMap.get(uuid);
    }

    public static PlayerData getDataByUuid(UUID uuid) {
        PlayerData playerData = dataUuidMap.get(uuid);
        if (playerData != null) {
            return playerData;
        }

        playerData = getDataByUuidFromRedis(uuid);
        if (playerData != null) {
            return playerData;
        }

        playerData = getDataByUuidFromMongo(uuid);

        return playerData;
    }

    public static PlayerData getDataByName(String name) {
        PlayerData playerData = dataNameMap.get(name.toLowerCase());
        if (playerData != null) {
            System.out.println("从缓存中拿数据成功");
            return playerData;
        }

        playerData = getDataByNameFromRedis(name.toLowerCase());
        if (playerData != null) {
            System.out.println("从Redis中拿数据成功");
            return playerData;
        }

        playerData = getDataByNameFromMongo(name.toLowerCase());
        System.out.println("从Mongo中拿数据成功");

        return playerData;
    }

    private static PlayerData getDataByUuidFromMongo(UUID uuid) {
        Document document = ChatLogBungee.getInstance()
                .getMongoDB()
                .getDocuments()
                .find(Filters.eq("uuid", uuid.toString()))
                .first();

        if (document != null) {
            PlayerData playerData = new PlayerData();
            playerData.loadFromDocument(document);
            return playerData;
        }

        return null;
    }

    private static PlayerData getDataByNameFromMongo(String name) {
        Document document = ChatLogBungee.getInstance()
                .getMongoDB()
                .getDocuments()
                .find(Filters.eq("name", name))
                .first();

        if (document != null) {
            PlayerData playerData = new PlayerData();
            playerData.loadFromDocument(document);
            return playerData;
        }

        return null;
    }

    private static PlayerData getDataByNameFromRedis(String name) {
        try (Jedis jedis = ChatLogBungee.getInstance().getJedisHelper().getPool().getResource()) {
            String chatLogData = jedis.hget("chatLogData", name.toLowerCase());
            if (chatLogData == null) {
                return null;
            }

            JsonObject jsonObject = parser.parse(chatLogData).getAsJsonObject();
            PlayerData playerData = new PlayerData();
            playerData.loadFromJson(jsonObject);

            return playerData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static PlayerData getDataByUuidFromRedis(UUID uuid) {
        boolean online = RedisBungee.getApi()
                .isPlayerOnline(uuid);

        if (online) {
            try (Jedis jedis = ChatLogBungee.getInstance().getJedisHelper().getPool().getResource()) {
                String name = jedis.hget("uuid-to-name", uuid.toString());
                if (name == null) {
                    return null;
                }
                name = name.toLowerCase();
                return getDataByNameFromRedis(name);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    public String dataToString() {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", uuid.toString());
        json.addProperty("name", name.toLowerCase());

        JsonArray array = new JsonArray();
        for (ChatData chat : this.chats) {
            JsonObject chatJson = new JsonObject();
            chatJson.addProperty("timestamp", chat.getTimestamp());
            chatJson.addProperty("message", chat.getMessage());
            chatJson.addProperty("chatType", chat.getChatType().toString());

            array.add(chatJson);
        }
        json.add("chatLog", array);

        return json.toString();
    }

    public Document dataToDocument(){
        Document document = new Document();
        document.put("uuid",this.uuid.toString());
        document.put("name",name.toLowerCase());
        List<Document> documents = new ArrayList<>();
        for (ChatData chat : chats) {
            Document chatDoc = new Document();
            chatDoc.put("timestamp",chat.getTimestamp());
            chatDoc.put("message",chat.getMessage());
            chatDoc.put("chatType",chat.getChatType().toString());

            documents.add(chatDoc);
        }
        document.put("chatLog",documents);

        return document;
    }


    public void loadFromJson(JsonObject json) {
        this.uuid = UUID.fromString(json.get("uuid").getAsString());
        this.name = json.get("name").getAsString().toLowerCase();

        JsonArray chatLog = json.get("chatLog").getAsJsonArray();
        this.chats.clear();
        if (chatLog.size() > 0) {
            for (JsonElement element : chatLog) {
                JsonObject logJson = element.getAsJsonObject();

                ChatData data = new ChatData();
                data.setTimestamp(logJson.get("timestamp").getAsLong());
                data.setMessage(logJson.get("message").getAsString());
                data.setChatType(ChatData.ChatType.valueOf(logJson.get("chatType").getAsString()));

                this.chats.add(data);
            }
        }
    }

    public void loadFromDocument(Document document) {
        this.uuid = UUID.fromString(document.getString("uuid"));
        this.name = document.getString("name").toLowerCase();

        try {
            List<Document> chatLog = document.getList("chatLog", Document.class);
            this.chats.clear();
            if (chatLog.size() > 0) {
                for (Document logDocument : chatLog) {
                    ChatData chatData = new ChatData();
                    chatData.setTimestamp(logDocument.getLong("timestamp"));
                    chatData.setMessage(logDocument.getString("message"));
                    chatData.setChatType(ChatData.ChatType.valueOf(logDocument.getString("chatType")));

                    this.chats.add(chatData);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
