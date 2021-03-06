package com.jihf.facedetect.http;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.lzy.okgo.model.HttpParams;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Func：
 * Desc:
 * Author：JHF
 * Data：2017-06-20 14:30
 * Mail：jihaifeng@raiyi.com
 */

public class OkGoHelper {
  private static final String TAG = OkGoHelper.class.getSimpleName().trim();
  private static OkGoHelper instance;

  public static OkGoHelper getInstance() {
    if (null == instance) {
      synchronized (OkGoHelper.class) {
        if (null == instance) {
          instance = new OkGoHelper();
        }
      }
    }
    return instance;
  }

  public void init(Application application) {

    //必须调用初始化
    OkGo.init(application);

    //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
    //好处是全局参数统一,特定请求可以特别定制参数
    try {
      //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
      OkGo.getInstance()
          //打开该调试开关,控制台会使用 红色error 级别打印log,并不是错误,是为了显眼,不需要就不要加入该行
          .debug("OkGo")
          //如果使用默认的 60秒,以下三行也不需要传
          .setConnectTimeout(OkGo.DEFAULT_MILLISECONDS)  //全局的连接超时时间
          .setReadTimeOut(OkGo.DEFAULT_MILLISECONDS)     //全局的读取超时时间
          .setWriteTimeOut(OkGo.DEFAULT_MILLISECONDS)    //全局的写入超时时间
          //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
          .setCacheMode(CacheMode.NO_CACHE)
          //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
          .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
          //如果不想让框架管理cookie,以下不需要
          //.setCookieStore(new MemoryCookieStore())      //cookie使用内存缓存（app退出后，cookie消失）
          .setCookieStore(new PersistentCookieStore())    //cookie持久化存储，如果cookie不过期，则一直有效
          .setCertificates();  //方法一：信任所有证书
    } catch (Exception e) {
      e.printStackTrace();
      Log.i(TAG, "e：" + e.getMessage());
    }
  }

  public void post(String url, HttpParams params, final HttpListener listener) {
    OkGo.post(url).params(params).execute(new StringCallback() {
      @Override public void onSuccess(String s, Call call, Response response) {
        if (null == listener) {
          return;
        }
        if (TextUtils.isEmpty(s)) {
          listener.onDataError("数据异常！");
          return;
        }
        listener.onSuccess(s);
      }

      @Override public void onError(Call call, Response response, Exception e) {
        super.onError(call, response, e);
        if (null == listener) {
          return;
        }
        listener.onSuccess(e.getMessage());
      }
    });
  }
}