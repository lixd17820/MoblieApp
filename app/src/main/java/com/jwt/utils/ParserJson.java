package com.jwt.utils;

import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParserJson {


    public static final int ARRAY = 0;
    public static final int MUTIARRAY = 1;
    public static final int CLASS = 2;
    public static final int NORMAL = 3;
    public static final int LIST = 4;
    public static final int DATE = 5;
    public static final int STRING = 6;
    public static final int LONG = 7;
    public static final int INT = 8;
    public static final int BOOLEAN = 9;

    private static SimpleDateFormat sdf_l = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdf_m = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm");
    private static SimpleDateFormat sdf_h = new SimpleDateFormat(
            "yyyy-MM-dd HH");
    private static SimpleDateFormat sdf_s = new SimpleDateFormat(
            "yyyy-MM-dd");

    private static int getClassType(Class<?> childType) {
        if (childType.isArray()) {
            if (childType.getComponentType().isArray())
                return MUTIARRAY;
            return ARRAY;
        } else if (childType.isPrimitive()) {
            if (childType.getName().equals("long")) {
                return LONG;
            } else if (childType.getName().equals("int")) {
                return INT;
            } else if (childType.getName().equals("boolean")) {
                return BOOLEAN;
            }
            return NORMAL;

        } else {
            if (childType == Date.class || childType == java.sql.Date.class) {
                // 日期型数据
                // result += sdf.format(obj);
                return DATE;
            } else if (childType == java.lang.Long.class) {
                return LONG;
            } else if (childType == java.lang.Integer.class) {
                return INT;
            } else if (childType == java.lang.Boolean.class) {
                return BOOLEAN;
            } else if (childType == java.util.List.class
                    || childType == java.util.ArrayList.class) {
                return LIST;
            } else if (childType == java.lang.String.class) {
                return STRING;
            }
        }
        return CLASS;

    }

    private static String getObjClassName(Class<?> classType) {
        String className = classType.getSimpleName().substring(0, 1)
                .toLowerCase()
                + classType.getSimpleName().substring(1);
        return className;
    }

    private static <T> T[] parseJsonToArrayObject(JSONArray array,
                                                  Class<T> classType) throws Exception {
        if (array == null || array.length() == 0)
            return null;
        int len = array.length();
        Object newArray = Array.newInstance(classType, len);
        for (int i = 0; i < len; i++) {
            JSONObject obj = array.getJSONObject(i);
            T newObj = parseJsonToObj(obj, classType);
            Array.set(newArray, i, newObj);
        }
        return (T[]) newArray;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] parseJsonToArray(String str, Class<T> classType)
            throws Exception {
        JSONArray arr = null;
        if (str.startsWith("{")) {
            JSONObject obj = new JSONObject(str);
            if (obj == null)
                return null;
            arr = new JSONArray();
            Object o = obj.opt(getObjClassName(classType));
            if (o == null)
                return null;
            if (o instanceof JSONObject)
                arr.put(o);
            else
                arr = (JSONArray) o;
            if (arr == null)
                return null;
        } else if (str.startsWith("[")) {
            arr = new JSONArray(str);
        }
        return parseJsonToArrayObject(arr, classType);
    }

    public static <T> T parseJsonToObj(String str, Class<T> classType)
            throws Exception {
        return parseJsonToObj(new JSONObject(str), classType);
    }

    public static <T> T parseJsonToObj(JSONObject jsonObj, Class<T> classType)
            throws Exception {
        T objectCopy = classType.getConstructor(new Class[]{}).newInstance(
                new Object[]{});
        Field[] fields = classType.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            int type = getClassType(field.getType());
            if (type == ARRAY) {
                Object o = jsonObj.opt(name);
                if (o != null) {
                    JSONArray array = new JSONArray();
                    if (o instanceof JSONObject)
                        array.put(o);
                    else
                        array = (JSONArray) o;
                    Object ar = parseJsonToArrayObject(array, field.getType()
                            .getComponentType());
                    field.set(objectCopy, ar);
                }
            } else if (type == MUTIARRAY) {

            } else if (type == CLASS) {
                JSONObject obj = jsonObj.optJSONObject(name);
                if (obj != null) {
                    Object newObj = parseJsonToObj(obj, field.getType());
                    field.set(objectCopy, newObj);
                }
            } else if (type == STRING) {
                // 将来完善
                field.set(objectCopy, jsonObj.optString(name, ""));
            } else if (type == LONG) {
                field.set(objectCopy, jsonObj.optLong(name, 0));
            } else if (type == INT) {
                field.set(objectCopy, jsonObj.optInt(name, 0));
            } else if (type == BOOLEAN) {
                field.set(objectCopy, jsonObj.optBoolean(name, false));
            } else if (type == LIST) {

            } else if (type == DATE) {
                String s = jsonObj.optString(name);
                Log.e("parseJson", s);
                if (s != null && !"".equals(s)) {
                    Date d = null;
                    if (s.trim().length() == 19)
                        d = sdf_l.parse(s);
                    else if (s.trim().length() == 16)
                        d = sdf_m.parse(s);
                    else if (s.trim().length() == 13)
                        d = sdf_h.parse(s);
                    else if (s.trim().length() == 10)
                        d = sdf_s.parse(s);
                    if (d != null)
                        field.set(objectCopy, d);
                    Log.e("parseJson", sdf_l.format(d));
                }
            }
        }
        return objectCopy;
    }

    public static JSONArray arrayToJsonArray(Object[] array) {
        JSONArray ar = new JSONArray();
        for (Object obj : array) {
            JSONObject j = objToJson(obj);
            ar.put(j);
        }
        return ar;
    }

    public static JSONArray arrayToJsonArray(List array) {
        JSONArray ar = new JSONArray();
        for (Object obj : array) {
            JSONObject j = objToJson(obj);
            ar.put(j);
        }
        return ar;
    }

    public static JSONObject objToJson(Object pojo) {
        JSONObject obj = new JSONObject();
        Class<?> classType = pojo.getClass();
        Field[] fields = classType.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            // 结果集对象字段属性名称如 getString
            String rsGetMethod = "get"
                    + fieldName.substring(0, 1).toUpperCase()
                    + fieldName.substring(1);
            if (rsGetMethod.indexOf("$") > -1 || rsGetMethod.indexOf("getSerialVersionUID") > -1)
                continue;
            Method rsGetMe = null;
            try {
                rsGetMe = classType.getMethod(rsGetMethod, new Class[]{});
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (rsGetMe == null)
                continue;

            Object value = null;
            try {
                value = rsGetMe.invoke(pojo, new Object[]{});
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            if (value != null) {
                try {
                    if (field.getType().isArray()) {
                        JSONArray array = arrayToJsonArray((Object[]) value);
                        obj.put(fieldName, array);
                    } else {
                        obj.put(fieldName, value);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return obj;
    }

    public static JSONObject arrayToObj(JSONArray array, String key, String val) {
        JSONObject obj = new JSONObject();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject temp = array.getJSONObject(i);
                String name = temp.optString(key);
                Object o = temp.opt(val);
                if (o instanceof Integer) {
                    obj.put(name, (int) o);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    public static JSONObject getJsonObject(String s) {
        if (!TextUtils.isEmpty(s) && s.startsWith("{") && s.endsWith("}")) {
            try {
                JSONObject obj = new JSONObject(s);
                return obj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new JSONObject();
    }

    public static JSONArray getJsonArray(String s) {
        if (!TextUtils.isEmpty(s) && s.startsWith("[") && s.endsWith("]")) {
            try {
                JSONArray obj = new JSONArray(s);
                return obj;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new JSONArray();
    }

    public static void putJsonVal(JSONObject obj, String key, Object val) {
        try {
            obj.put(key, val);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static JSONArray createArray(JSONObject... objs) {
        JSONArray array = new JSONArray();
        for (JSONObject obj : objs) {
            array.put(obj);
        }
        return array;
    }

    public static JSONArray createArrayByObjs(Object... objs) {
        JSONArray array = new JSONArray();
        for (Object obj : objs) {
            array.put(objToJson(obj));
        }
        return array;
    }


    public static void main(String[] args) throws Exception {
        String str = "{\"cameraIpBean\":[{\"channel\":{\"channel\":\"1\",\"id\":\"1165\",\"ip\":\"172.23.174.122\",\"jkdwmc\":\"IP_CAMERA\",\"lkjc\":\"0\",\"otherName\":\"IP_CAMERA\",\"yxbj\":\"0\"},\"id\":\"360\",\"ip\":\"172.23.174.122\",\"passwd\":\"12345\",\"port\":\"8000\",\"sblx\":\"5\",\"sbmc\":\"IP_CAMERA\",\"username\":\"admin\"},{\"channel\":{\"channel\":\"1\",\"id\":\"1151\",\"ip\":\"172.23.174.132\",\"jkdwmc\":\"通沪大道富锋路西侧北上高架\",\"lkjc\":\"0\",\"otherName\":\"通沪大道富锋路西侧北上高架\",\"yxbj\":\"0\"},\"id\":\"196\",\"ip\":\"172.23.174.132\",\"passwd\":\"12345\",\"port\":\"8000\",\"sblx\":\"5\",\"sbmc\":\"通沪大道富锋路西侧北上高架\",\"username\":\"admin\"}]}";
        JSONObject obj = new JSONObject(str);
        Object ar = obj.opt("cameraIpBean");
        System.out.println(ar instanceof JSONObject);
        // for (int i = 0; i < ar.length(); i++) {
        // JSONArray o = ar.optJSONArray(i);
        // System.out.println(o);
        // }
        // System.out.println(obj);
    }


}
