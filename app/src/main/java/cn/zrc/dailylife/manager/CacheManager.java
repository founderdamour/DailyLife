package cn.zrc.dailylife.manager;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.zrc.dailylife.app.App;
import cn.zrc.dailylife.app.BaseManager;
import cn.zrc.dailylife.util.json.JsonHelper;

/**
 * Created by yangzhizhong
 */

public class CacheManager extends BaseManager {


    @Override
    public void onManagerCreate(App app) {

    }

    private ACache getCache() {
        String accountId = App.getInstance().getManager(UserManager.class).getAccountId();
        return ACache.get(App.getInstance(), "cache_" + accountId);
    }

    /**
     * 把数据放入缓存
     *
     * @param key   键
     * @param value 值
     */
    public void put(String key, String value) {
        getCache().put(key, value);
    }

    /**
     * 把数据放入缓存
     *
     * @param key      键 {@link CacheManager}中定义
     * @param value    值
     * @param saveTime 数据保存时间， 单位为秒
     */
    public void put(String key, String value, int saveTime) {
        getCache().put(key, value, saveTime);
    }

    /**
     * 将对象放入缓存  对象主要支持JSON 使用 @JsonField("JSON")注解
     *
     * @param key    缓存的KEY
     * @param object 需要缓存的对象
     */
    public void putObject(String key, Object object) {
        JSONObject jsonObject = JsonHelper.toJSONObject(object);
        put(key, jsonObject.toString());
    }

    /**
     * 将lis的数据放入缓存中
     *
     * @param key  缓存的KEY
     * @param list 需要缓存的数据
     */
    public <V> void putList(String key, List<V> list) {
        if (list == null) {
            return;
        }
        JSONArray jsonArray = new JSONArray();
        for (Object object : list) {
            JSONObject jsonObject = JsonHelper.toJSONObject(object);
            if (jsonObject != null) {
                jsonArray.put(jsonObject);
            }
        }
        put(key, jsonArray.toString());
    }

    /**
     * 获取缓存的数据
     *
     * @param key 缓存的key
     * @param cls 缓存的数据类型
     * @param <V> 数据类型  建议全部使用｛@link JsonField｝注解
     * @return 返回JSON数据
     */
    @NonNull
    public <V> List<V> getList(String key, Class<V> cls) {
        List<V> list = new ArrayList<>();
        String jsonStr = get(key, "");
        if ("".equals(jsonStr)) {
            return list;
        }

        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            list = JsonHelper.toList(jsonArray, cls);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 从缓存中获取数据
     *
     * @param key 数据的key
     * @param cls 数据类型
     * @param <V> 数据类型
     * @return 返回缓存中存在的数据  如果缓存中没有数据  将返回null
     */
    public <V> V getObject(String key, Class<V> cls) {
        String jsonStr = get(key, "");
        if ("".equals(jsonStr)) {
            return null;
        }
        V v;
        try {
            v = JsonHelper.toObject(new JSONObject(jsonStr), cls);
        } catch (JSONException e) {
            e.printStackTrace();
            v = null;
        }
        return v;
    }

    /**
     * 从缓存中获取数据
     *
     * @param key          键
     * @param defaultValue 如果缓存中没有数据，则返回该值
     */
    public String get(String key, String defaultValue) {
        String result = getCache().getAsString(key);
        if (result == null) {
            result = defaultValue;
        }
        return result;
    }

    /**
     * 移除缓存
     *
     * @param key 键
     */
    public void remove(String key) {
        getCache().remove(key);
    }

    /**
     * 清除缓存
     */
    public void clear() {
        getCache().clear();
    }

}
