package me.cunzai.chatlog.depend;

import me.cunzai.chatlog.util.dependencies.Dependency;
import me.cunzai.chatlog.util.dependencies.DependencyManager;
import me.cunzai.chatlog.util.dependencies.loaders.LoaderType;
import me.cunzai.chatlog.util.dependencies.loaders.ReflectionClassLoader;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * @Author: EmptyIrony
 * @Date: 2021/2/8 20:41
 */
public class DependencyLoader {
    private final DependencyManager manager;

    public DependencyLoader(Plugin plugin) {
        this.manager = new DependencyManager(plugin,new ReflectionClassLoader(plugin));
    }

    public void load(){
        this.manager.loadDependencies(
                new Dependency(
                        "Apache Http Client",
                        "org.apache.httpcomponents",
                        "httpclient",
                        "4.4",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                    "Pool2",
                        "org.apache.commons",
                        "commons-pool2",
                                "2.6.2",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Apache Http Core",
                        "org.apache.httpcomponents",
                        "httpcore",
                        "4.4",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Apache Logging",
                        "commons-logging",
                        "commons-logging",
                        "1.2",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "MongoDB",
                        "org.mongodb",
                        "mongo-java-driver",
                        "3.12.7",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Jedis",
                        "redis.clients",
                        "jedis",
                        "2.9.0",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "MongoJack",
                        "org.mongojack",
                        "mongojack",
                        "2.10.0",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "JackSon-Annotations",
                        "com.fasterxml.jackson.core",
                        "jackson-annotations",
                        "2.10.3",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "JackSon-Databind",
                        "com.fasterxml.jackson.core",
                        "jackson-databind",
                        "2.10.3",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "JackSon-Core",
                        "com.fasterxml.jackson.core",
                        "jackson-core",
                        "2.10.3",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "JackSon-DataType",
                        "com.fasterxml.jackson.datatype",
                        "jackson-datatype-jsr310",
                        "2.10.3",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Bson4Jackson",
                        "de.undercouch",
                        "bson4jackson",
                        "2.9.2",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Redisson",
                        "org.redisson",
                        "redisson",
                        "3.0.1",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Netty-common",
                        "io.netty",
                        "netty-common",
                        "4.0.42.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "Netty-codec",
                        "io.netty",
                        "netty-codec",
                        "4.0.42.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "netty-buffer",
                        "io.netty",
                        "netty-buffer",
                        "4.0.42.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "netty-transport",
                        "io.netty",
                        "netty-transport",
                        "4.0.42.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "netty-handler",
                        "io.netty",
                        "netty-handler",
                        "4.0.42.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "reactor-core",
                        "io.projectreactor",
                        "reactor-core",
                        "3.3.9.RELEASE",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "rxjava",
                        "io.reactivex.rxjava2",
                        "rxjava",
                        "2.2.19",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "cache-api",
                        "javax.cache",
                        "cache-api",
                        "1.0.0",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "byte-buddy",
                        "net.bytebuddy",
                        "byte-buddy",
                        "1.10.14",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "jboss-marshalling-river",
                        "org.jboss.marshalling",
                        "jboss-marshalling-river",
                        "2.0.9.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "jodd-bean",
                        "org.jodd",
                        "jodd-bean",
                        "5.1.6",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "snakeyaml",
                        "org.yaml",
                        "snakeyaml",
                        "1.26",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "slf4j-api",
                        "org.slf4j",
                        "slf4j-api",
                        "1.7.30",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "jboss-marshalling-river",
                        "org.jboss.marshalling",
                        "jboss-marshalling-river",
                        "2.0.9.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "jboss-marshalling",
                        "org.jboss.marshalling",
                        "jboss-marshalling",
                        "2.0.9.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "jackson-dataformat-yaml",
                        "com.fasterxml.jackson.dataformat",
                        "jackson-dataformat-yaml",
                        "2.11.2",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "netty-all",
                        "io.netty",
                        "netty-all",
                        "4.0.42.Final",
                        LoaderType.REFLECTION
                ),
                new Dependency(
                        "guava",
                        "com.google.guava",
                        "guava",
                        "29.0-jre",
                        LoaderType.REFLECTION
                )

        );
    }
}
