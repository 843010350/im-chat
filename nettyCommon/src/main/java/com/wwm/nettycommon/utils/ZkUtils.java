package com.wwm.nettycommon.utils;

import com.wwm.nettycommon.cache.ServerCache;
import com.wwm.nettycommon.constants.Constants;
import com.wwm.nettycommon.model.ServerInfoDto;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.zookeeper.CreateMode;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ZkUtils {

    @Autowired
    private CuratorFramework curatorFramework;

    @Autowired
    private ServerCache serverCache;
    /**
     * 注册列表
     * @param ip
     * @param nettyServerPort
     * @param httpPort
     * @return
     */
    public  String getRegistryList(String ip,Integer nettyServerPort,Integer httpPort){
        String path = Constants.ZK_ROUTE + "/ip-" + ip + ":" + nettyServerPort + ":" + httpPort;
        return path;
    }

    /**
     * 负载均衡算法实现
     * @param userId
     * @return
     */
    public  String getRibbon(Integer userId, List<String> list){
        //直接返回0 我们就一个服务端，多个服务端自己实现算法
        return list.get(0).split("-")[1] +":"+userId;
    }

    public  ServerInfoDto parse(String target){
        String[] serverInfo = target.split(":");
        ServerInfoDto routeInfo =  new ServerInfoDto(serverInfo[0], Integer.parseInt(serverInfo[1]),Integer.parseInt(serverInfo[2]),Integer.parseInt(serverInfo[3])) ;
        return routeInfo ;
    }

    public void createEphemeral(String registry) throws Exception {
        curatorFramework.create()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(registry);
    }

    public List<String> getChildren(String path) throws Exception {
        List<String> list = curatorFramework.getChildren().forPath(path);
        return list;
    }

    public void subscribeEvent(String path) throws Exception {
        PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, path, true);
        System.out.println("Register zk watcher successfully!");
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        PathChildrenCacheListener childrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                System.out.println("开始进行事件分析:-----");
                ChildData data = event.getData();
               /* switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("CHILD_ADDED : "+ data.getPath() +"  数据:"+ data.getData());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD_REMOVED : "+ data.getPath() +"  数据:"+ data.getData());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD_UPDATED : "+ data.getPath() +"  数据:"+ data.getData());
                        break;
                    default:
                        break;
                }*/
                serverCache.rebuildCacheList();
            }
        };
        childrenCache.getListenable().addListener(childrenCacheListener);


    }
}
