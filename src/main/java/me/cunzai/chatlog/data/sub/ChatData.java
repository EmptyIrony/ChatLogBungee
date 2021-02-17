package me.cunzai.chatlog.data.sub;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: EmptyIrony
 * @Date: 2021/2/8 21:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatData {
    private long timestamp;
    private String message;
    private ChatType chatType;

    public static enum ChatType{
        CHAT,
        SHOUT,
        PRIVATE,
        PARTY,
        GUILD,
        GUILD_OFFICIAL,
        COMMAND
    }
}
