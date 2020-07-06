package com.flappy.flutterflappyvideoproxy;

import com.flappygo.proxyserver.Interface.ProxyCacheListener;

import java.util.HashMap;

import io.flutter.plugin.common.EventChannel;

//返回
public class RetProxyCacheListener implements ProxyCacheListener {

    //当前的eventSink
    private EventChannel.EventSink eventSink;

    //唯一ID
    private String backid;

    //创建
    public RetProxyCacheListener(EventChannel.EventSink eventSink, String unique) {
        this.eventSink = eventSink;
        this.backid = unique;
    }

    @Override
    public void cachedProgress(int progress) {
        if (this.eventSink != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("event", "cachedProgress");
            map.put("backid", backid);
            map.put("progress", Integer.toString(progress));
            this.eventSink.success(map);
        }
    }

    @Override
    public void cachedSuccess() {
        if (this.eventSink != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("event", "cachedSuccess");
            map.put("backid", backid);
            this.eventSink.success(map);
        }
    }

    @Override
    public void cachedStoped() {
        if (this.eventSink != null) {
            HashMap<String, String> map = new HashMap<>();
            map.put("event", "cachedStoped");
            map.put("backid", backid);
            this.eventSink.success(map);
        }
    }

}
