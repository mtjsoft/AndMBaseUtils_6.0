package com.huahan.hhbaseutils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huahan.hhbaseutils.constant.HHConstantParam;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * 解析Model的工具类，限制数据的格式是json。解析出来的Model必须有一个不带参数的默认的构造函数<br/>
 * 1：如果json的形式包含Model套Model，则里边的Model必须实现接口{@link com.huahan.hhbaseutils.imp HHInstanceModelImp}才能被正确的解析<br/>
 * 2：如果定义的Model中的字段不需要解析，需要为字段添加注解{@link com.huahan.hhbaseutils.imp Ignore},例如<br/>
 * <code>
 * <pre>
 *        {@code @Ignore}
 * 		private String xxx;
 * </pre>
 * </code><br/>
 * 或者字段的名称以Ignore结尾；
 *
 * @author yuan
 */
public class HHModelUtils {
    private static final String tag = HHModelUtils.class.getName();

    /**
     * 判断当前的字段在解析的时候是否应该被忽略
     *
     * @param field 需要解析的字段
     * @return true，在解析的时候该字段会被忽略掉
     */
    private static boolean isIgnore(Field field) {
        if (field.getName().endsWith("Ignore")) {
            return true;
        }
        return isAnnotationAvalible(field, HHConstantParam.ANNOTATION_IGNORE);
    }

    /**
     * 判断一个字段是否需要按照解析Model的形式解析
     *
     * @param field 需要解析的字段
     * @return 如果需要按照解析Model的形式来解析数据的话，返回true
     */
    private static boolean isInstanceModel(Field field) {
        return isAnnotationAvalible(field, HHConstantParam.ANNOTATION_INSTANCE_MODEL);
    }

    /**
     * 判断一个字段时候还有指定的注解
     *
     * @param field          字段
     * @param annotationName 注解的全程。包含包名
     * @return 如果字段包含了某个注解，返回true
     */
    private static boolean isAnnotationAvalible(Field field, String annotationName) {
        Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
        for (Annotation annotation : declaredAnnotations) {
            if (annotation.toString().contains(annotationName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 解析数据到对应的Model
     *
     * @param clazz      解析到的Model的Class对象
     * @param jsonObject 需要解析的数据
     * @param isEnstry   数据时候加密
     * @return 如果jsonObject为null，返回null，否则的话返回解析出来的Model对象
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    public static <T> T setModelValues(Class<T> clazz, JSONObject jsonObject, boolean isEnstry) throws Exception {
        T object = null;
        if (jsonObject != null) {
            object = clazz.newInstance();
            //getDeclaredFields()返回所有字段；getFields()返回公共字段
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (isIgnore(field)) {
                    continue;
                }
                String fieldName = field.getName();
                if (field.getType().equals(ArrayList.class)) {
                    ParameterizedType pType = (ParameterizedType) field.getGenericType();
                    Class typeClass = (Class) pType.getActualTypeArguments()[0];
                    HHLog.i(tag, fieldName + " is ArrayList");
                    @SuppressWarnings("unchecked")
                    ArrayList list = setModelValues(typeClass, jsonObject.optJSONArray(fieldName), isEnstry);
                    field.set(object, list);
                } else if (isInstanceModel(field)) {
                    HHLog.i(tag, fieldName + " is Model");
                    Object setModelValues = setModelValues(field.getType(), jsonObject.optJSONObject(fieldName), isEnstry);
                    field.set(object, setModelValues);
                } else {
                    String value = jsonObject.optString(fieldName);
                    Log.i(tag, "name:" + fieldName + ",value:" + value);
                    field.set(object, isEnstry ? HHEncryptUtils.decodeBase64(value) : value);
                }
            }
        }
        return object;
    }

    /**
     * 解析数据到对应的Model,默认数据是不加密的
     *
     * @param clazz      解析到的对象的Class
     * @param jsonObject 需要解析的数据
     * @return 如果jsonObject为null，返回null；否则的话返回解析出来的Model
     * @throws Exception
     */
    public static <T> T setModelValues(Class<T> clazz, JSONObject jsonObject) throws Exception {
        return setModelValues(clazz, jsonObject, false);
    }

    /**
     * 解析数据到对应的Model,默认数据是不加密的
     *
     * @param clazz            解析到的对象的Class
     * @param jsonObjectString 需要解析的数据
     * @return 如果jsonObjectString为null，返回null；否则的话返回解析出来的Model
     * @throws Exception
     */
    public static <T> T setModelValues(Class<T> clazz, String jsonObjectString) throws Exception {
        return setModelValues(clazz, jsonObjectString, false);
    }

    /**
     * 解析数据到对应的Model
     *
     * @param clazz            解析到的对象的Class
     * @param jsonObjectString 需要解析的数据
     * @param isEnstry         数据是否加密
     * @return 如果jsonObjectString为null，返回null；否则的话返回解析出来的Model
     * @throws Exception
     */
    public static <T> T setModelValues(Class<T> clazz, String jsonObjectString, boolean isEnstry) throws Exception {
        JSONObject jsonObject = new JSONObject(jsonObjectString);
        return setModelValues(clazz, jsonObject, isEnstry);
    }

    /**
     * 解析一个JsonArray到一个对应的Model的集合
     *
     * @param clazz     解析出来的Model
     * @param jsonArray 需要解析的数据
     * @param isEnstry  是否加密
     * @return 如果JsonArray为null，则返回null，否则的话返回Model的集合
     * @throws Exception
     */
    public static <T> ArrayList<T> setModelValues(Class<T> clazz, JSONArray jsonArray, boolean isEnstry) throws Exception {
        ArrayList<T> list = null;
        if (jsonArray != null) {
            list = new ArrayList<T>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsob = jsonArray.optJSONObject(i);
                HHLog.i(tag, "item" + i + ":" + jsob.toString());
                list.add(setModelValues(clazz, jsob, isEnstry));
            }
        }
        return list;
    }

    /**
     * 解析一个JsonArray到一个对应的Model的集合，默认数据是不加密的
     *
     * @param clazz     解析出来的Model
     * @param jsonArray 需要解析的数据
     * @return 如果JsonArray为null，则返回null，否则的话返回Model的集合
     * @throws Exception
     */
    public static <T> ArrayList<T> setModelValues(Class<T> clazz, JSONArray jsonArray) throws Exception {
        return setModelValues(clazz, jsonArray, false);
    }

    /**
     * 解析数据到一个Model中
     *
     * @param codeName  code对应的名称
     * @param codeValue code的值，表示在什么值得时候去解析数据部分，一般情况下是100
     * @param dataName  数据对应的值
     * @param clazz     解析出来的Model
     * @param data      需要解析的数据
     * @param isEnstry  是否加密
     * @return 如果数据为null，返回null；否则的话返回解析出来的Model
     */
    public static <T> T getModel(String codeName, String codeValue, String dataName, Class<T> clazz, String data, boolean isEnstry) {
        T t = null;
        if (data != null) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                t = clazz.newInstance();
                if (codeValue.equals(jsonObject.optString(codeName))) {
                    jsonObject = jsonObject.optJSONObject(dataName);
                    t = setModelValues(clazz, jsonObject, isEnstry);
                }
            } catch (Exception e) {
                HHLog.i(tag, "getModel", e);
            }
        }
        return t;
    }

    /**
     * 解析数据到一个Model中,用Gosn解析方式
     *
     * @param codeName  状态code对应的名称
     * @param codeValue 状态code的值，表示在什么值得时候去解析数据部分，一般情况下是0
     * @param dataName  数据对应的值
     * @param clazz     解析出来的Model
     * @param data      需要解析的数据
     * @return 如果数据为null，返回null；否则的话返回解析出来的Model
     */
    public static <T> T getModel2Gson(String codeName, String codeValue, String dataName, Class<T> clazz, String data) {
        T t = null;
        if (data != null) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                if (codeValue.equals(jsonObject.optString(codeName))) {
                    Gson gson = new Gson();
                    t = gson.fromJson(jsonObject.optString(dataName), clazz);
                }
            } catch (Exception e) {
                HHLog.i(tag, "getModel2Gson", e);
            }
        }
        return t;
    }


    /**
     * 解析数据到一个集合,用Gosn解析方式
     *
     * @param codeValue code的值，表示在什么值得时候去解析数据部分，一般情况下是0
     * @param data      需要解析的数据
     * @param codeName  code 对应的名称
     * @param dataName  数据对应的名称
     * @return 如果需要解析的数据是null，返回null；否则返回一个类型为clazz的实例的集合
     */
    public static <T> List<T> getModelList2Gson(String codeName, String codeValue, String dataName, Class<T> clzss, String data) {
        List<T> list = null;
        if (data != null) {
            try {
                list = new ArrayList<T>();
                JSONObject jsonObject = new JSONObject(data);
                if (codeValue.equals(jsonObject.optString(codeName))) {
                    Gson gson = new Gson();
                    list = gson.fromJson(jsonObject.optString(dataName), TypeToken.getParameterized(ArrayList.class, clzss).getType());
                }
            } catch (Exception e) {
                HHLog.i(tag, "getModel2GsonList", e);
            }
        }
        return list;
    }

    /**
     * 解析数据到一个Model中
     *
     * @param codeName code的名称
     * @param dataName 数据对应的名称
     * @param clazz    解析出来的model的class对象
     * @param data     需要解析的数据
     * @param isEnstry 数据是否加密
     * @return 如果数据为null，返回null；否则的话返回解析出来的Model
     */
    public static <T> T getModel(String codeName, String dataName, Class<T> clazz, String data, boolean isEnstry) {
        return getModel(codeName, "100", dataName, clazz, data, isEnstry);
    }

    /**
     * 解析数据到一个Model中
     *
     * @param codeName code的名称
     * @param dataName 数据对应的名称
     * @param clazz    解析出来的model的class对象
     * @param data     需要解析的数据
     * @return 如果数据为null，返回null；否则的话返回解析出来的Model
     */
    public static <T> T getModel(String codeName, String dataName, Class<T> clazz, String data) {
        return getModel(codeName, dataName, clazz, data, false);
    }

    /**
     * 解析数据到一个集合
     *
     * @param clazz     实例的类型
     * @param codeValue code的值，表示在什么值得时候去解析数据部分，一般情况下是100
     * @param data      需要解析的数据
     * @param codeName  code 对应的名称
     * @param dataName  数据对应的名称
     * @param isEnstry  数据是否加密
     * @return 如果需要解析的数据是null，返回null；否则返回一个类型为clazz的实例的集合
     */
    public static <T> List<T> getModelList(String codeName, String codeValue, String dataName, Class<T> clazz, String data, boolean isEnstry) {
        List<T> list = null;
        if (data != null) {
            try {
                list = new ArrayList<T>();
                JSONObject jsonObject = new JSONObject(data);
                if (codeValue.equals(jsonObject.optString(codeName))) {
                    JSONArray array = jsonObject.optJSONArray(dataName);
                    list = setModelValues(clazz, array, isEnstry);
                }
            } catch (Exception e) {
                HHLog.i(tag, "getModelList", e);
            }
        }
        return list;
    }

    /**
     * 解析数据到一个集合
     *
     * @param clazz    实例的类型
     * @param data     需要解析的数据
     * @param codeName code 对应的名称
     * @param dataName 数据对应的名称
     * @param isEnstry 数据是否加密
     * @return 如果需要解析的数据是null，返回null；否则返回一个类型为clazz的实例的集合
     */
    public static <T> List<T> getModelList(String codeName, String dataName, Class<T> clazz, String data, boolean isEnstry) {
        return getModelList(codeName, "100", dataName, clazz, data, isEnstry);
    }

    /**
     * 解析数据到一个集合
     *
     * @param codeName code 对应的名称
     * @param dataName 数据对应的名称
     * @param clazz    实例的类型
     * @param data     需要解析的数据
     * @return 如果需要解析的数据是null，返回null；否则返回一个类型为clazz的实例的集合
     */
    public static <T> List<T> getModelList(String codeName, String dataName, Class<T> clazz, String data) {
        return getModelList(codeName, dataName, clazz, data, false);
    }
}
