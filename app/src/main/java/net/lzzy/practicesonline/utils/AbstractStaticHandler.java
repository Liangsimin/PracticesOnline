package net.lzzy.practicesonline.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * Created by lzzy_gxy on 2019/4/12.
 * Description:
 */
public abstract class AbstractStaticHandler<T> extends Handler {
    private final WeakReference<T> context;

    public AbstractStaticHandler(T context){
        this.context = new WeakReference<>(context);
    }

    protected AbstractStaticHandler(WeakReference<T> context) {
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        T t = context.get();
        handleMessage(msg,t);
    }

    /**
     * 处理消息业务逻辑
     * @param msg
     * @param t Activity or Fragment对象
     */
    public abstract  void  handleMessage(Message msg,T t);


    }

