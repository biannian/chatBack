package com.pgk.chat;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/websocket/{userName}")
@Component
public class CustomWebSocket {
    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的CumWebSocket对象。
     */
    private static Map<String, CustomWebSocket> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 客户端用户名集合
     */
    private static HashSet userNames = new HashSet();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 连接建立成功调用的方法
     *
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("userName") String userName, Session session) {
        this.session = session;
        this.userName = userName;
        addOnlineCount();
        //加入set中
        webSocketMap.put(userName, this);
        HashMap<String, Object> map = new HashMap<>();
        for (Map.Entry<String, CustomWebSocket> entry : webSocketMap.entrySet()) {
            userNames.add(entry.getKey());
        }
        System.out.println(userNames);
        map.put("userNames", userNames);
        map.put("onLineCount", getOnlineCount());
        map.put("userName", userName);
        CustomWebSocket.sendAll(map);
        //添加在线人数
        System.out.println("新连接接入。当前在线人数为：" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从set中删除

        webSocketMap.remove(userName);
        //在线数减1
        subOnlineCount();
        userNames.remove(userName);
        System.out.println("有连接关闭。当前在线人数为：" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) throws Exception {

        System.out.println("客户端发送的消息：" + message);
        JSONObject object = JSONObject.parseObject(message);
        String to = (String) object.get("to");
        if (to.equals("All")){
             CustomWebSocket.sendAll1(object);
        } else {
            CustomWebSocket.sendTo((String) object.get("to"),message);
        }



    }


    /**
     * 私发消息
     *
     * @param userName
     * @param message
     */
    private static void sendTo(String userName, String message) {
        try {
            webSocketMap.get(userName).sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * 群发
     *
     * @param map
     */
    private static void sendAll(HashMap<String, Object> map) {
        for (CustomWebSocket item : webSocketMap.values()) {
            try {
                item.sendMessage(String.valueOf(JSONObject.toJSON(map)));
            } catch (IOException e) {
                e.printStackTrace();
            }
         //   item.session.getAsyncRemote().sendText(String.valueOf(JSONObject.toJSON(map)));
        }

    }
    private static void sendAll1(JSONObject message) {

        for (CustomWebSocket item : webSocketMap.values()) {
            try {
                item.session.getAsyncRemote().sendText(String.valueOf(message));
            }catch (Exception e ){
                e.printStackTrace();
            }

        }
    }



//        webSocketMap.keySet().forEach(item ->{
//            CustomWebSocket customWebSocket = webSocketMap.get(item);
//            try {
//                customWebSocket.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        Arrays.asList(webSocketSet.toArray()).forEach(item -> {
//            CustomWebSocket customWebSocket = (CustomWebSocket) item;
//            //群发
//            try {
//                customWebSocket.sendMessage(message);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    //);


    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("----websocket-------有异常啦");
        error.printStackTrace();
    }

    /**
     * 减少在线人数
     */
    private void subOnlineCount() {
        CustomWebSocket.onlineCount--;
    }

    /**
     * 添加在线人数
     */
    private void addOnlineCount() {
        CustomWebSocket.onlineCount++;
    }

    /**
     * 当前在线人数
     *
     * @return
     */
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    /**
     * 发送信息
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

}