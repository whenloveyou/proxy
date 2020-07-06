package com.flappy.flutterflappyvideoproxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.flappygo.proxyserver.FlappyProxyServer;
import com.flappygo.proxyserver.Interface.ProxyCacheListener;
import com.flappygo.proxyserver.Tools.ToolString;

import java.util.List;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterflappyvideoproxyPlugin
 */
public class FlutterflappyvideoproxyPlugin implements FlutterPlugin, MethodCallHandler, EventChannel.StreamHandler {


    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private MethodChannel channel;

    //当前的channel
    private EventChannel eventChannel;

    //上下文activity
    private Context context;

    //用于所有回调
    private EventChannel.EventSink meventSink;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {

        context = flutterPluginBinding.getApplicationContext();

        channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "flutterflappyvideoproxy");
        channel.setMethodCallHandler(this);

        eventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "flutterflappyvideoproxy_event");
        eventChannel.setStreamHandler(this);

    }


    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutterflappyvideoproxy");

        FlutterflappyvideoproxyPlugin plugin = new FlutterflappyvideoproxyPlugin();
        plugin.context = registrar.activity().getApplicationContext();

        plugin.eventChannel = new EventChannel(registrar.messenger(), "flutterflappyvideoproxy_event");
        plugin.eventChannel.setStreamHandler(plugin);

        channel.setMethodCallHandler(plugin);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull final Result result) {

        //获取缓存地址
        if (call.method.equals("getCacheDictionary")) {
            //当前的路径
            String path = FlappyProxyServer.getInstance(context).getCacheDictionary();
            //成功
            result.success(path);
        }
        //开始代理
        else if (call.method.equals("proxyInit")) {
            //请求
            final String port = call.argument("port");
            //唯一值
            final String poolSize = call.argument("poolSize");
            //进行初始化
            FlappyProxyServer.init(Integer.parseInt(port), Integer.parseInt(poolSize));
            //成功了
            result.success("1");
        }
        //开始代理
        else if (call.method.equals("proxyStart")) {
            //请求
            final String url = call.argument("url");
            //唯一值
            final String unique = call.argument("unique");
            //创建handler
            final Handler handler = new Handler() {
                public void handleMessage(Message message) {
                    //成功
                    result.success(message.obj);
                }
            };
            //进行代理
            new Thread() {
                public void run() {
                    //代理后的地址
                    String path = FlappyProxyServer.getInstance(context).proxyStart(url, unique);
                    //成功
                    Message message = handler.obtainMessage(1, path);
                    //发送成功的消息
                    handler.sendMessage(message);
                }
            }.start();
        }
        //停止代理
        else if (call.method.equals("proxyStop")) {
            //请求
            final String url = call.argument("url");
            //唯一值
            final String unique = call.argument("unique");
            //当前的handler
            final Handler handler = new Handler() {
                public void handleMessage(Message message) {
                    result.success(message.obj);
                }
            };
            //进行代理
            new Thread() {
                public void run() {
                    boolean flag = FlappyProxyServer.getInstance(context).proxyStop(url, unique);
                    //成功
                    if (flag) {
                        //成功
                        Message message = handler.obtainMessage(1, "1");
                        //发送成功的消息
                        handler.sendMessage(message);
                    } else {
                        //成功
                        Message message = handler.obtainMessage(1, "0");
                        //发送成功的消息
                        handler.sendMessage(message);
                    }
                }
            }.start();
        }
        //回调
        else if (call.method.equals("proxyCacheStart")) {
            //请求
            final String url = call.argument("url");
            //标志唯一值的backid
            final String backid = call.argument("backid");
            //当前的handler
            final Handler handler = new Handler() {
                public void handleMessage(Message message) {
                    result.success(message.obj);
                }
            };
            //进行代理
            new Thread() {
                public void run() {
                    //当前的路径
                    String path = FlappyProxyServer.getInstance(context).proxyCacheStart(url, new RetProxyCacheListener(meventSink, backid));
                    //成功
                    Message message = handler.obtainMessage(1, path);
                    //发送成功的消息
                    handler.sendMessage(message);
                }
            }.start();
        }

        //回调
        else if (call.method.equals("proxyCacheStop")) {
            //请求
            final String url = call.argument("url");
            //当前的handler
            final Handler handler = new Handler() {
                public void handleMessage(Message message) {
                    result.success(message.obj);
                }
            };
            //进行代理
            new Thread() {
                public void run() {
                    //当前的路径
                    boolean flag = FlappyProxyServer.getInstance(context).proxyCacheStop(url);
                    //成功
                    if (flag) {
                        //成功
                        Message message = handler.obtainMessage(1, "1");
                        //发送成功的消息
                        handler.sendMessage(message);
                    } else {
                        //成功
                        Message message = handler.obtainMessage(1, "0");
                        //发送成功的消息
                        handler.sendMessage(message);
                    }
                }
            }.start();
        }
        //清理
        else if (call.method.equals("cleanProxy")) {
            //请求
            String url = call.argument("url");
            //当前的路径
            boolean flag = FlappyProxyServer.getInstance(context).cleanProxy(url);
            //成功
            if (flag) {
                result.success("1");
            } else {
                result.success("0");
            }
        }
        //清理
        else if (call.method.equals("cleanAll")) {
            //当前的路径
            boolean flag = FlappyProxyServer.getInstance(context).cleanAll();
            //成功
            if (flag) {
                result.success("1");
            } else {
                result.success("0");
            }
        }
        //清理
        else if (call.method.equals("getProxyUrls")) {
            //返回
            List<String> strs = FlappyProxyServer.getInstance(context).getProxyUrls();
            //成功
            result.success(ToolString.strListToStr(strs, ","));
        }
        //没有实现的
        else {
            result.notImplemented();
        }
    }


    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        //设置
        channel.setMethodCallHandler(null);
        eventChannel.setStreamHandler(null);
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        //当前的
        meventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        //取消
        meventSink = null;
    }


}
