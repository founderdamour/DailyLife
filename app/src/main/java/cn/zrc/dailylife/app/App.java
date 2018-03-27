package cn.zrc.dailylife.app;

import android.app.Application;
import android.support.annotation.UiThread;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.zrc.dailylife.manager.CacheManager;
import cn.zrc.dailylife.manager.UserManager;
import cn.zrc.dailylife.util.CommonUtils;

/**
 * Created by yangzhizhong
 */

public class App extends Application {
    private static final String TAG = "App";
    /**
     * 应用上下文
     */
    private static App instance;

    /**
     * 所有manager
     */
    private HashMap<String, BaseManager> managers = new HashMap<>();

    /**
     * 获取应用上下文
     *
     * @return 应用上下文
     */
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (instance != null) {
            instance = this;
            return;
        }
        instance = this;

        String pidName = CommonUtils.getUIPName(this);
        if (pidName.equals(getPackageName())) {
            initManager();
        }
    }


    /**
     * 初始化manager
     */
    private void initManager() {
        List<BaseManager> managerList = new ArrayList<>();
        registerManager(managerList);
        for (BaseManager baseManager : managerList) {
            long time = System.currentTimeMillis();
            injectManager(baseManager);
            baseManager.onManagerCreate(this);
            Class<? extends BaseManager> baseManagerClass = baseManager.getClass();
            String name = baseManagerClass.getName();
            managers.put(name, baseManager);
            // 打印初始化耗时
            long spendTime = System.currentTimeMillis() - time;
        }

        for (Map.Entry<String, BaseManager> entry : managers.entrySet()) {
            entry.getValue().onAllManagerCreated();
        }
    }

    // 添加管理器  注意添加的顺序
    protected void registerManager(List<BaseManager> lists) {
        lists.add(new UserManager());
        lists.add(new CacheManager());
    }

    /**
     * 注解manager
     *
     * @param object 需要注解的object
     */
    public void injectManager(Object object) {
        Class<?> aClass = object.getClass();

        while (aClass != BaseManager.class && aClass != Object.class) {
            Field[] declaredFields = aClass.getDeclaredFields();
            if (declaredFields != null && declaredFields.length > 0) {
                for (Field field : declaredFields) {
                    int modifiers = field.getModifiers();
                    if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers)) {
                        // 忽略掉static 和 final 修饰的变量
                        continue;
                    }

                    if (!field.isAnnotationPresent(Manager.class)) {
                        continue;
                    }

                    Class<?> type = field.getType();
                    if (!BaseManager.class.isAssignableFrom(type)) {
                        throw new RuntimeException("@Manager 注解只能应用到BaseManager的子类");
                    }

                    BaseManager baseManager = getManager((Class<? extends BaseManager>) type);

                    if (baseManager == null) {
                        throw new RuntimeException(type.getSimpleName() + " 管理类还未初始化！");
                    }

                    if (!field.isAccessible())
                        field.setAccessible(true);

                    try {
                        field.set(object, baseManager);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            aClass = aClass.getSuperclass();
        }
    }

    @UiThread
    public <V extends BaseManager> V getManager(Class<V> cls) {
        return (V) managers.get(cls.getName());
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface Manager {

    }
}
