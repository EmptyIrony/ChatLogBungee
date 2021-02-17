package me.cunzai.chatlog.command;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import lombok.SneakyThrows;
import me.cunzai.chatlog.data.PlayerData;
import me.cunzai.chatlog.data.sub.ChatData;
import me.cunzai.chatlog.util.chat.CC;
import me.cunzai.chatlog.util.time.Duration;
import me.cunzai.chatlog.util.time.TimeUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import sun.misc.IOUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Author: EmptyIrony
 * @Date: 2021/2/8 22:58
 */
public class ChatLogCommand extends Command {
    private final Gson gson = new Gson();
    private final DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private final ExecutorService service = new ScheduledThreadPoolExecutor(4);

    public ChatLogCommand() {
        super("chatLog");
    }



    @Override
    @SneakyThrows
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 3){
            sender.sendMessage(toMsg("&c用法: /chatLog [玩家名] [种类] [时间]"));
            return;
        }

        long now = System.currentTimeMillis();

        String name = args[0];
        ChatData.ChatType type;
        try{
            type = ChatData.ChatType.valueOf(args[1].toUpperCase());
        }catch (Exception ignore){
            sender.sendMessage(toMsg("&c错误的消息种类格式"));
            return;
        }
        String time = args[2];
        long parseTime = TimeUtil.parseTime(time);
        if (parseTime == -1){
            sender.sendMessage(toMsg("&c错误的时间格式"));
            return;
        }

        Duration duration = new Duration(parseTime);

        sender.sendMessage(toMsg("&a" + name + " 的" + type + "聊天记录 &7(从 " + this.format.format(now - duration.getValue()) + " 到 " + this.format.format(now) + "&a)"));
        sender.sendMessage(toMsg("&a正在生成..."));

        service.execute(() -> {
            try{
            PlayerData data = PlayerData.getDataByName(name);
            if (data == null){
                sender.sendMessage(toMsg("&c没有找到这个人的ChatLog信息..."));
                return;
            }

            List<ChatData> dataList = data.getChats();
            StringBuilder messages = new StringBuilder();

            dataList.stream()
                    .filter(chatData -> chatData.getChatType() == type)
                    .filter(chatData -> now - chatData.getTimestamp() <= duration.getValue())
                    .forEach(entry -> messages.append("[").append(this.format.format(entry.getTimestamp())).append("] ").append(entry.getMessage()).append("\n"));

            CloseableHttpClient client = HttpClientBuilder.create().build();

            HttpPost postRequest = new HttpPost("https://paste.md-5.net/documents");

            StringEntity userEntity = new StringEntity(messages.toString(), HTTP.UTF_8);
            postRequest.setEntity(userEntity);


                HttpResponse response = client.execute(postRequest);
                if (response.getStatusLine().getStatusCode() == 200) {
                    JsonObject responseObject = gson.fromJson(EntityUtils.toString(response.getEntity()), JsonObject.class);
                    String key = responseObject.get("key").getAsString();
                    sender.sendMessage(toMsg("&a生成成功! 链接: "),toUrl("https://paste.md-5.net/" + key));
                }else {
                    sender.sendMessage(toMsg("&c返回错误"));
                }
            }catch (Exception e){
                e.printStackTrace();
                sender.sendMessage(toMsg("&c与Paste网站连接失败，请稍后再试"));
            }

        });

    }

    private BaseComponent toMsg(String str){
        return new TextComponent(ChatColor.translateAlternateColorCodes('&',str));
    }

    private BaseComponent toUrl(String str){
        TextComponent text = new TextComponent(ChatColor.translateAlternateColorCodes('&', str));
        text.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL,str));
        return text;
    }



    @Override
    public String getPermission() {
        return "domcer.staff";
    }


}
