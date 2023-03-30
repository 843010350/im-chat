# im-chat
1、项目中的netty-client已作废代码功能移到nettycommon中
2、项目目前用到的技术有nacos、zookeeper、netty、mybatis、mysql、mybatis-plus、kafka，websocket
nacos用户服务注册中心
zookeeper用于netty服务端注册、监听netty服务端操作
netty用于建立用户长连接进行通讯
websocket用户和前端建立长连接chnnel进行通信
mysql用于存储用户的消息信息（已读和未读）
kafka用于转发用户的消息，进行消息固定存储、以及离线消息的存储（redis未实现，可以自己实现）,以及更新reids缓存和本地缓存信息
redis用户存储用户的离线消息以及用户对应的nettyserver信息以及用户组对应的nettyserver信息
3、目前项目中只做简单的登录token认证以及单聊消息的发送和已读对应前端页面已经写好
4、项目架构流程图后续回附上





# im-chat
