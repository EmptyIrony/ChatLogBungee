package me.cunzai.chatlog.database.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class JedisCredentials {

    private final String address, password;
    private final int port;

    /**
     * @return
     */
    public boolean isAuth() {
        return password != null && !password.isEmpty();
    }

}
