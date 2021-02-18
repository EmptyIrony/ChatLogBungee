# ChatLogBungee - 记录聊天的Bungee插件
该插件用于记录玩家的全部发言，指令，以及其他（可以自行修改）
支持多BC，利用Redis实现跨BC查询

当玩家登录时才会从MongoDB中获取玩家数据，当玩家退出时才会存入MongoDB
当玩家查询时，首先查询本BungeeCord中有无缓存，如果没有，则向Redis获取，如果没有，最后从Mongo获取
每次玩家发言将会队列更新玩家数据，以保证跨BC查询的数据是最新的

自用插件，无配置文件，需要自行修改Mongo IP和Redis IP

最终展示使用的md_5的HasteBin，您也可以自行搭建
https://paste.md-5.net/
