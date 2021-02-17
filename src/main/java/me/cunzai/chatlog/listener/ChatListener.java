package me.cunzai.chatlog.listener;

import com.imaginarycode.minecraft.redisbungee.events.PlayerJoinedNetworkEvent;
import me.cunzai.chatlog.data.PlayerData;
import me.cunzai.chatlog.data.sub.ChatData;
import net.md_5.bungee.api.connection.ConnectedPlayer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
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
            chatData.setMessage(event.getMessage());

            if (event.isCommand()){
                if (message.startsWith("/m") || message.startsWith("/r") || message.startsWith("/t") || message.startsWith("/tell") || message.startsWith("/msg")){
                    chatData.setChatType(ChatData.ChatType.PRIVATE);
                }else if (message.startsWith("/gc")){
                    chatData.setChatType(ChatData.ChatType.GUILD);
                }else if (message.startsWith("/oc")){
                    chatData.setChatType(ChatData.ChatType.GUILD_OFFICIAL);
                }else if (message.startsWith("/pc")){
                    chatData.setChatType(ChatData.ChatType.PARTY);
                }else if (message.startsWith("/shout")){
                    chatData.setChatType(ChatData.ChatType.SHOUT);
                }else {
                    chatData.setChatType(ChatData.ChatType.COMMAND);
                }
            }else {
                chatData.setChatType(ChatData.ChatType.CHAT);
            }

            data.getChats().add(chatData);
            data.updateRedisCache();
        }
    }

    @EventHandler
    public void onConnect(PostLoginEvent event){
        this.executor.execute(() -> {
            ProxiedPlayer player = event.getPlayer();
            String name = event.getPlayer().getName();
            PlayerData data = PlayerData.getDataByName(name);
            if (data == null){
                data = new PlayerData(player.getUniqueId(),player.getName());
            }else {
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
