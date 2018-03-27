package cn.zrc.dailylife.app;

import android.os.Handler;

/**
 * Created by yangzhizhong
 */

public abstract class BaseManager {

    // 全局handler
    protected static final Handler handler = new Handler();

    /**
     * 管理器被初始化的回调，初始化整个管理器
     */
    public abstract void onManagerCreate(App app);

    /**
     * 所有管理器都初始化后执行
     */
    public void onAllManagerCreated() {

    }

    /**
     * 获得应用程序实例
     *
     * @return 应用实例
     */
    public App getApplication() {
        return App.getInstance();
    }

    /**
     * 获得管理器
     *
     * @param manager 管理器类型
     * @param <M>     管理器Class
     * @return 管理器
     */
    public <M extends BaseManager> M getManager(Class<M> manager) {
        return App.getInstance().getManager(manager);
    }
}
