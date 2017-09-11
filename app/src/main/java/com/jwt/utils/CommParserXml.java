package com.jwt.utils;

import java.io.StringReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.text.TextUtils;
import android.util.Log;

public class CommParserXml {

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

    private static SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");

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

    private static String crTag(String field, boolean isStr) {
        if (isStr)
            return "<" + field + ">";
        else
            return "</" + field + ">";
    }

    private static String getObjClassName(Object obj) {
        Class<?> classType = obj.getClass();
        String className = classType.getSimpleName().substring(0, 1)
                .toLowerCase()
                + classType.getSimpleName().substring(1);
        return className;
    }

    public static String objToXml(Object pojo) throws Exception {
        String result = "";// "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
        result = fieldToXml(null, pojo, result, true);
        return result;
    }

    public static String mapToXml(Map<String, String> map) {
        String re = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";
        re += "<root>";
        Set<Entry<String, String>> set = map.entrySet();
        for (Entry<String, String> entry : set) {
            re += "<" + entry.getKey() + ">" + entry.getValue() + "</"
                    + entry.getKey() + ">";
        }
        re += "</root>";
        return re;
    }

    public static Map<String, String> xmlToMap(String xml) throws Exception {
        Map<String, String> map = new HashMap<String, String>();
        XmlPullParser parser = getParser(xml);
        int event = parser.getEventType();// 产生第一个事件
        String key = null, value = null;
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT: {
                }
                break;
                case XmlPullParser.START_TAG:
                    String tagName = parser.getName();
                    if (!TextUtils.isEmpty(tagName)
                            && !TextUtils.equals(tagName, "root"))
                        key = parser.getName();
                    Log.e("CommParser", "start tag: " + tagName);
                    // 返回错误描述
                    break;
                case XmlPullParser.END_TAG: {
                    if (TextUtils.equals(parser.getName(), "root"))
                        return map;
                    if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)
                            && TextUtils.equals(parser.getName(), key)) {
                        map.put(key, value);
                    }
                    key = "";
                    value = "";
                    Log.e("CommParser", "end tag: " + parser.getName());
                }
                break;
                case XmlPullParser.TEXT: {
                    value = parser.getText();
                }
                break;
            }
            event = parser.next();// 进入下一个元素并触发相应事件
        }
        return map;
    }

    /**
     * 字段转为XML文本
     *
     * @param field
     * @param obj
     * @param result
     * @param isLast
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static String fieldToXml(Field field, Object obj, String result,
                                     boolean isLast) throws IllegalArgumentException,
            IllegalAccessException {
        if (obj == null)
            return result;
        // String result = "";
        // XML的根标签内容
        String root = "";
        if (field == null) {
            // 顶层对象，还没有取其中的字段
            root = getObjClassName(obj);
        } else {
            if (isLast)
                root = field.getName();
            else
                root = "item";
        }
        // 开始标签

        Class<?> type = obj.getClass();
        if (type.isArray()) {
            // 数组类型，第一层数组为字段名，类数组为类名，其他数组为item
            for (int i = 0; i < Array.getLength(obj); i++) {
                Object childArrayObj = Array.get(obj, i);
                if (childArrayObj.getClass().isArray()) {
                    //
                    result += crTag(field.getName(), true);
                    result = fieldToXml(field, childArrayObj, result, false);
                    result += crTag(field.getName(), false);
                } else {
                    result = fieldToXml(field, childArrayObj, result, isLast);
                }
            }
        } else if (type.getPackage() != null
                && type.getPackage().getName().equals("java.util")
                && type.getSimpleName().equals("ArrayList")) {
            List list = (List) obj;
            Object[] objArr = new Object[list.size()];
            objArr = list.toArray(objArr);
            // for (int i = 0; i < list.size(); i++) {
            // Object childListObj = list.get(i);
            result = fieldToXml(field, objArr, result, true);
        } else {
            result += crTag(root, true);
            if (type.isPrimitive()
                    || type.getPackage().getName().equals("java.lang")) {
                // 基本类型
                result += obj.toString();
            } else if (type == java.util.Date.class
                    || type == java.sql.Date.class) {
                // 日期型数据
                result += sdf.format(obj);
            } else {
                // 其他类，需要调用字段的列表，并递归找出数据
                Field[] fields = type.getDeclaredFields();
                for (Field f : fields) {
                    f.setAccessible(true);
                    Object childObj = f.get(obj);
                    result = fieldToXml(f, childObj, result, true);
                }
                // System.out.println(field.getName() + " is other class");
            }
            result += crTag(root, false);
        }
        // 结束标签

        return result;
    }

    private static Object createArray(Field field, Object obj) throws Exception {
        Object oldArray = field.get(obj);// 数组
        Object newArray;
        int arrLen = 0;
        if (oldArray != null)
            arrLen = Array.getLength(oldArray);
        newArray = Array.newInstance(field.getType().getComponentType(),
                arrLen + 1);
        // 将老数组的内容拷贝到新数组中
        for (int i = 0; i < arrLen; i++) {
            Array.set(newArray, i, Array.get(oldArray, i));
        }
        Object lastArrayObj = field.getType().getComponentType()
                .getConstructor(new Class[]{}).newInstance(new Object[]{});
        Array.set(newArray, arrLen, lastArrayObj);
        field.set(obj, newArray);
        return lastArrayObj;
    }

    public static void setArrayValue(Field field, Object parentObj, Object value)
            throws IllegalArgumentException, IllegalAccessException {
        Object oldArray = field.get(parentObj);
        Object newArray;
        int arrLen = 0;
        if (oldArray != null) {
            // 数组还没有初始化
            arrLen = Array.getLength(oldArray);
        }
        newArray = Array.newInstance(field.getType().getComponentType(),
                arrLen + 1);
        // 将老数组的内容拷贝到新数组中
        for (int i = 0; i < arrLen; i++) {
            Array.set(newArray, i, Array.get(oldArray, i));
        }
        Array.set(newArray, arrLen, value);
        field.set(parentObj, newArray);
    }

    private static XmlPullParser getParser(String xml)
            throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser parser = factory.newPullParser();
        StringReader sr = new StringReader(xml);
        parser.setInput(sr);
        return parser;
    }

    private static String getClassName(Class<?> classType) {
        String className = classType.getSimpleName().substring(0, 1)
                .toLowerCase()
                + classType.getSimpleName().substring(1);
        return className;
    }

    public static <T> List<T> ParseXmlToListObj(String xml, Class<T> classType)
            throws Exception {
        List<T> list = null;
        String cn = getClassName(classType);
        XmlPullParser parser = getParser(xml);
        int event = parser.getEventType();// 产生第一个事件
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT: {
                }
                break;
                case XmlPullParser.START_TAG:
                    String tagName = parser.getName();
                    if (tagName.equals(cn + "s")) {
                        // 位于最上层数组
                        list = new ArrayList<T>();
                    } else {
                        T objectCopy = classType.getConstructor(new Class[]{})
                                .newInstance(new Object[]{});
                        parserObj(parser, objectCopy);
                        list.add(objectCopy);
                    }
                    break;
                case XmlPullParser.END_TAG: {
                }
                break;
            }
            event = parser.next();// 进入下一个元素并触发相应事件
        }
        return list;
    }

    public static <T> T parseXmlToObj(String xml, Class<T> classType)
            throws Exception {
        T objectCopy = classType.getConstructor(new Class[]{}).newInstance(
                new Object[]{});
        XmlPullParser parser = getParser(xml);
        int event = parser.getEventType();// 产生第一个事件
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT: {
                }
                break;
                case XmlPullParser.START_TAG:
                    parserObj(parser, objectCopy);
                    // 返回错误描述
                    break;
                case XmlPullParser.END_TAG: {
                }
                break;
            }
            event = parser.next();// 进入下一个元素并触发相应事件
        }
        return objectCopy;
    }

    private static void parserObj(XmlPullParser parser, Object obj)
            throws Exception {
        int event = parser.getEventType();
        String tag = parser.getName();
        String tagName = "";
        Field field = null;
        do {
            event = parser.next();
            switch (event) {
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();
                    if (tagName.equals("item")) {
                        // 取得一个两维数组
                        Object oldArray = field.get(obj);
                        // 取得两维数组中最后一个一维数组
                        Object oneDemArray = Array.get(oldArray,
                                Array.getLength(oldArray) - 1);
                        // 一维数组加一个空值
                        oneDemArray = addOneDemArray(oneDemArray);
                        // 对最后一个位置赋值
                        Array.set(oneDemArray, Array.getLength(oneDemArray) - 1,
                                parser.nextText());
                        // 重新写回到两维数组中
                        Array.set(oldArray, Array.getLength(oldArray) - 1,
                                oneDemArray);
                    } else {
                        field = obj.getClass().getDeclaredField(tagName);
                        field.setAccessible(true);
                        if (getClassType(field.getType()) == ARRAY) {
                            // 创建数组中最后一个对象
                            if (getClassType(field.getType().getComponentType()) == CLASS) {
                                Object lastArrayObj = createArray(field, obj);
                                parserObj(parser, lastArrayObj);
                            } else if (getClassType(field.getType()
                                    .getComponentType()) == MUTIARRAY) {
                            }
                        } else if (getClassType(field.getType()) == CLASS) {
                            // 包含有字段的对象
                            Object tempObj = field.getType()
                                    .getConstructor(new Class[]{})
                                    .newInstance(new Object[]{});
                            field.set(obj, tempObj);
                            parserObj(parser, tempObj);
                        } else if (getClassType(field.getType()) == MUTIARRAY) {
                            // newArray,oldArray都是二维数组
                            // 将老数组的内容拷贝到新数组中
                            // 将新数组的容量加上一
                            Object oldArray = field.get(obj);
                            Object newArray;
                            int arrLen = 0;
                            if (oldArray != null)
                                arrLen = Array.getLength(oldArray);
                            Object oa = Array.newInstance(field.getType()
                                    .getComponentType().getComponentType(), 0);
                            newArray = Array.newInstance(oa.getClass(), arrLen + 1);

                            for (int i = 0; i < arrLen; i++) {
                                Array.set(newArray, i, Array.get(oldArray, i));
                            }
                            Array.set(newArray, Array.getLength(newArray) - 1, oa);
                            field.set(obj, newArray);
                        }
                    }
                    break;
                case XmlPullParser.TEXT:
                    if (tagName.equals("item")) {
                    } else {
                        field.setAccessible(true);
                        if (getClassType(field.getType()) == ARRAY) {
                            setArrayValue(field, obj, parser.getText());
                        } else if (getClassType(field.getType()) == LONG) {

                        } else {
                            field.set(obj, parser.getText());
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (tag.equals(parser.getName()))
                        break;
                    break;
                default:
                    break;
            }
        } while (!(event == XmlPullParser.END_TAG && tag.equals(parser
                .getName())));
    }

    private static Object addOneDemArray(Object oneDemArray) {
        // oneDemArray.getClass().
        int arrLen = 0;
        if (oneDemArray != null)
            arrLen = Array.getLength(oneDemArray);

        Object newArray = Array.newInstance(oneDemArray.getClass()
                .getComponentType(), arrLen + 1);
        for (int i = 0; i < arrLen; i++) {
            Array.set(newArray, i, Array.get(oneDemArray, i));
        }
        return newArray;
    }

    public static Map<String, String> objToMap(Object pojo) {
        Map<String, String> map = new HashMap<String, String>();
        Class<?> classType = pojo.getClass();
        Field[] fields = classType.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            String fieldName = field.getName();
            // 结果集对象字段属性名称如 getString
            String rsGetMethod = "get"
                    + fieldName.substring(0, 1).toUpperCase()
                    + fieldName.substring(1);
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
                if ("java.util.Date".equals(field.getType().getName())) {
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    map.put(fieldName.toUpperCase(),
                            sdf.format((java.util.Date) value));
                } else {
                    map.put(fieldName.toUpperCase(), value.toString());
                }
            } else {
                map.put(fieldName.toUpperCase(), "");
            }
        }
        return map;
    }


    public static void main(String[] args) {

    }
}
