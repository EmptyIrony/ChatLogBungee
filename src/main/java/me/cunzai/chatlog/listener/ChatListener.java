package me.cunzai.chatlog.listener;

import me.cunzai.chatlog.data.PlayerData;
import me.cunzai.chatlog.data.sub.ChatData;
import me.cunzai.chatlog.queue.RedisQueue;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: EmptyIrony
 * @Date: 2021/2/8 22:37
 */
public class ChatListener implements Listener {
    private final ExecutorService executor = new ThreadPoolExecutor(16, 16,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

    @EventHandler
    public void onChat(ChatEvent event){
        if (event.getSender() instanceof ProxiedPlayer){
            ProxiedPlayer sender = (ProxiedPlayer) event.getSender();
            PlayerData data = PlayerData.getPlayerDataFromCache(sender.getUniqueId());
            if (data == null){
                return;
            }

            String message = event.getMessage().toLowerCase();
            ChatData chatData = new ChatData();
            chatData.setTimestamp(System.currentTimeMillis());


            if (event.isCommand()) {
                String substring = message.substring(1);
                String[] args = substring.split(" ");
                if (args.length > 0) {
                    String first = args[0];
                    if (args.length > 1) {
                        int i = message.indexOf(" ");
                        String content = message.substring(i + 1);
                        if (first.equalsIgnoreCase("m") || first.equalsIgnoreCase("r") || first.equalsIgnoreCase("t") || first.equalsIgnoreCase("tell") || message.equalsIgnoreCase("msg")) {
                            chatData.setChatType(ChatData.ChatType.PRIVATE);
                            chatData.setMessage(content);
                        } else if (first.equalsIgnoreCase("gc")) {
                            chatData.setChatType(ChatData.ChatType.GUILD);
                            chatData.setMessage(content);
                        } else if (first.equalsIgnoreCase("oc")) {
                            chatData.setChatType(ChatData.ChatType.GUILD_OFFICIAL);
                            chatData.setMessage(content);
                        } else if (first.equalsIgnoreCase("pc")) {
                            chatData.setChatType(ChatData.ChatType.PARTY);
                            chatData.setMessage(content);
                        } else if (first.equalsIgnoreCase("shout")) {
                            chatData.setChatType(ChatData.ChatType.SHOUT);
                            chatData.setMessage(content);
                        } else {
                            chatData.setChatType(ChatData.ChatType.COMMAND);
                        }
                    } else {
                        chatData.setChatType(ChatData.ChatType.COMMAND);
                        chatData.setMessage(event.getMessage());
                    }
                } else {
                    chatData.setChatType(ChatData.ChatType.COMMAND);
                    chatData.setMessage(event.getMessage());
                }
            } else {
                chatData.setChatType(ChatData.ChatType.CHAT);
                chatData.setMessage(event.getMessage());
            }

            chatData.setCurrentServer(sender.getServer().getInfo().getName());

            data.getChats().add(chatData);
            RedisQueue.getQueue()
                    .getSaveQueue()
                    .add(data);
        }
    }

    @EventHandler
    public void onConnect(PostLoginEvent event){
        this.executor.execute(() -> {
            ProxiedPlayer player = event.getPlayer();
            String name = event.getPlayer().getName().toLowerCase();
            PlayerData data = PlayerData.getDataByName(name);
            if (data == null){
                data = new PlayerData(player.getUniqueId(),player.getName());
            }
            data.handleJoin();
        });
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event){
        this.executor.execute(() -> {
            PlayerData data = PlayerData.getDataByName(event.getPlayer().getName());
            if (data != null){
                data.handleQuit();
            }
        });
    }

}
