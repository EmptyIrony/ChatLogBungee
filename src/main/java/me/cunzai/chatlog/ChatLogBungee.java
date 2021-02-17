package me.cunzai.chatlog;


import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.cunzai.chatlog.command.ChatLogCommand;
import me.cunzai.chatlog.database.MongoDB;
import me.cunzai.chatlog.database.redis.JedisCredentials;
import me.cunzai.chatlog.database.redis.JedisHelper;
import me.cunzai.chatlog.depend.DependencyLoader;
import me.cunzai.chatlog.listener.ChatListener;
import me.cunzai.chatlog.queue.MongoQueue;
import me.cunzai.chatlog.queue.RedisQueue;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Getter
public final class ChatLogBungee extends Plugin {
    @Getter
    private static ChatLogBungee instance;

    private MongoDB mongoDB;
    private JedisHelper jedisHelper;

    @Override
    public void onEnable() {
        instance = this;

        log.info("downloading dependency");
        new DependencyLoader(this).load();
        log.info("downloaded all dependency");

        log.info("connecting to database");
        this.mongoDB = new MongoDB();
        log.info("connected");

        log.info("connecting to redis");
        this.jedisHelper = new JedisHelper(new JedisCredentials("10.191.171.31", null, 6379));
        log.info("connected");

        log.info("init listeners and commands");
        this.getProxy().getPluginManager()
                .registerListener(this,new ChatListener());

        this.getProxy().getPluginManager()
                .registerCommand(this,new ChatLogCommand());

        log.info("ok");

        log.info("starting queue thread");
        new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(new MongoQueue(),100,100, TimeUnit.MILLISECONDS);
        new ScheduledThreadPoolExecutor(1).scheduleWithFixedDelay(new RedisQueue(),100,10, TimeUnit.MILLISECONDS);


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
