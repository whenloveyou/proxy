import 'dart:async';
import 'dart:io';
import 'package:flutter/services.dart';

//进度
typedef ProxyCacheProgressListener = void Function(int progress);

//成功
typedef ProxyCacheSuccessListener = void Function();

//代理服务器
class Flutterflappyvideoproxy {
  //进度
  static Map<String, ProxyCacheProgressListener> _progressListeners =
      new Map<String, ProxyCacheProgressListener>();

  //成功
  static Map<String, ProxyCacheSuccessListener> _successListeners =
      new Map<String, ProxyCacheSuccessListener>();

  //渠道channel
  static const MethodChannel _channel =
      const MethodChannel('flutterflappyvideoproxy');

  //用于接收回调新的的channel
  static const EventChannel _eventChannel =
      EventChannel('flutterflappyvideoproxy_event');

  //是否处于监听状态
  static bool _isListen = false;

  //添加监听
  static void _addListeners(ProxyCacheProgressListener cacheProgressListener,
      ProxyCacheSuccessListener successListener, String backid) {
    //监听
    if (cacheProgressListener != null)
      _progressListeners[backid] = cacheProgressListener;
    //监听
    if (successListener != null) _successListeners[backid] = successListener;
    //监听
    if (!_isListen) {
      _isListen = true;
      //注册用于和原生代码的持续回调
      Stream<String> stream = _eventChannel.receiveBroadcastStream();
      //数据
      stream.listen((dynamic data) {
        //获取数据
        Map<String, String> map = data;
        //设置
        switch (map['event']) {
          //缓存进度
          case 'cachedProgress':
            {
              //进度
              String progress = map['progress'];
              //唯一值
              String backid = map['backid'];
              //缓存进度
              ProxyCacheProgressListener listener = _progressListeners[backid];
              //缓存进度
              if (listener != null) {
                listener(int.parse(progress));
              }
              break;
            }
          //缓存成功
          case 'cachedSuccess':
            {
              //唯一值
              String backid = map['backid'];
              //缓存进度
              ProxyCacheSuccessListener listener = _successListeners[backid];
              //缓存进度
              if (listener != null) {
                listener();
              }
              _removeListeners(backid);
              break;
            }
          //缓存停止
          case 'cachedStoped':
            {
              //唯一值
              String backid = map['backid'];
              _removeListeners(backid);
              break;
            }
        }
      });
    }
  }

  //移除监听
  static void _removeListeners(String backid) {
    _progressListeners.remove(backid);
    _successListeners.remove(backid);
  }

  //获取缓存地址
  static Future<String> getCacheDictionary() async {
    //如果是android
    if (Platform.isAndroid) {
      final String version = await _channel.invokeMethod('getCacheDictionary');
      return version;
    } else {
      return "";
    }
  }

  //初始化配置
  static Future<void> proxyInit(int port, int poolSize) async {
    await _channel.invokeMethod('proxyInit',
        {"port": port.toString(), "poolSize": poolSize.toString()});
  }

  //开始进行代理
  static Future<String> proxyStart(String url, String unique) async {
    //如果是android
    if (Platform.isAndroid) {
      final String version = await _channel
          .invokeMethod('proxyStart', {"url": url, "unique": unique});
      return version;
    } else {
      return url;
    }
  }

  //停止进行代理
  static Future<bool> proxyStop(String url, String unique) async {
    //如果是android
    if (Platform.isAndroid) {
      final String ret = await _channel
          .invokeMethod('proxyStop', {"url": url, "unique": unique});
      return ret == "0" ? false : true;
    } else {
      return true;
    }
  }

  //缓存并进行
  static Future<String> proxyCacheStart(
      String url,
      ProxyCacheProgressListener progressListener,
      ProxyCacheSuccessListener successListener) async {
    if (Platform.isAndroid) {
      String backid = DateTime.now().microsecondsSinceEpoch.toString();

      _addListeners(progressListener, successListener, backid);

      final String version = await _channel.invokeMethod('proxyCacheStart', {
        "url": url,
        "backid": backid,
      });
      return version;
    } else {
      return url;
    }
  }

  //停止进行代理
  static Future<bool> proxyCacheStop(String url) async {
    //如果是android
    if (Platform.isAndroid) {
      final String ret =
          await _channel.invokeMethod('proxyCacheStop', {"url": url});
      return ret == "0" ? false : true;
    } else {
      return true;
    }
  }

  //清理缓存
  static Future<bool> cleanProxy(String url) async {
    //如果是android
    if (Platform.isAndroid) {
      final String ret =
          await _channel.invokeMethod('cleanProxy', {"url": url});
      return ret == "0" ? false : true;
    } else {
      return true;
    }
  }

  //清空所有的
  static Future<bool> cleanAll() async {
    //如果是android
    if (Platform.isAndroid) {
      final String ret = await _channel.invokeMethod('cleanAll');
      return ret == "0" ? false : true;
    } else {
      return true;
    }
  }

  //获取被代理过的urls
  static Future<List<String>> getProxyUrls() async {
    //如果是android
    if (Platform.isAndroid) {
      //返回
      final String ret = await _channel.invokeMethod('getProxyUrls');
      //裁剪
      List<String> strs = ret.split(",");
      //返回
      return strs;
    } else {
      return new List<String>();
    }
  }
}
