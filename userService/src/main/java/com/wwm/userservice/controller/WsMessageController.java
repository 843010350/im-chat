package com.wwm.userservice.controller;

import com.wwm.nettycommon.websocket.WsMessageEndPoint;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author OnCloud9
 * @description
 * @project tt-server
 * @date 2022年11月09日 下午 10:03
 */
@RestController
@RequestMapping("/websocket/sender")
public class WsMessageController {

    @PostMapping("/all")
    public void sendMessageToAllClient(@RequestBody Map<String, String> map) {
        final String text = map.get("text");
        WsMessageEndPoint.sendMessageForAllClient(text);
    }

    @PostMapping("/one")
    public void sendMessageToClient(@RequestBody Map<String, String> map) {
        final String text = map.get("text");
        final String client = map.get("client");
        WsMessageEndPoint.sendMessageForClient(client, text);
    }
}
