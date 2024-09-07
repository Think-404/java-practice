package org.originit.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author 缪林辉
 * @date 2024/9/7 12:02
 * @DESCRIPTION
 */
public class BeanUtils2 {

    /**
     * 定义所有的基本类型及包装类型，或者是String、Date
     */
    private static final List<String> generalType = new ArrayList<String>() {{
        add(Integer.class.getName());
        add(Double.class.getName());
        add(Float.class.getName());
        add(Long.class.getName());
        add(Short.class.getName());
        add(Byte.class.getName());
        add(Boolean.class.getName());
        add(Character.class.getName());
        add(String.class.getName());
        add(Date.class.getName());
        add("int");
        add("double");
        add("float");
        add("long");
        add("short");
        add("byte");
        add("boolean");
        add("char");
    }};

    /**
     * 获取本类及其父类的指定的属性
     * @param clazz 当前类对象
     * @return 字段
     */
    private static final Field getField(Class<?> clazz, String name) {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(name);
            } catch (Exception e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    /**
     * 获取本类及其父类的属性
     * @param clazz 当前类对象
     * @return 字段数组
     */
    private static final Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        Field[] fields = new Field[fieldList.size()];
        return fieldList.toArray(fields);
    }

    /**
     * 用于两个对象属性名相同之间的复制
     * <p>
     * 支持字段类型：
     * 1. 所有基本类型 ；
     * 2. String；
     * 3. Date；
     * 4. List(允许源对象、目标对象类型不一致，以目标对象为主。抽象类默认为ArrayList)；
     * 5. Set(允许源对象、目标对象类型不一致，以目标对象为主。抽象类默认为HashSet)；
     * 6. Map<S,T> 其中S,T为基本类型、String、Date、自定义类型；
     * 7. 自定义对象，包含自己本身，可以实现无限层级复制字段值（最重要的功能）；
     * 8. 支持继承父类，父类属性也可赋值
     * 9. 其他未知
     *
     * @param source 原资源对象
     * @param target 目标资源对象
     * @param <T>    目标类型
     * @param <S>    原资源类型
     * @return
     */
    public static final <T, S> T copyProperties(S source, T target) {
        try {

            Class<?> sClass = source.getClass();
            Class<?> tClass = target.getClass();
            // 如果source为基本类型，直接复制
            if (generalType.contains(sClass.getName())) {
                target = (T) source;
                return target;
            }
            // 获取所有字段及父类字段
            Field[] fields = getAllFields(sClass);
            for (int i = 0; i < fields.length; i++) {
                Field sField = fields[i];
                sField.setAccessible(true);
                // 获取字段Class
                Class<?> sFieldClass = sField.getType();
                // 获取字段名
                String name = sField.getName();
                // 获取字段值
                Object value = sField.get(source);
                if (value == null) {
                    // 值为空，跳过
                    continue;
                }
                // 获取目标类字段
                Field tField;
                try {
                    // 尝试获取属性字段，获取不到不复制
                    tField = getField(tClass, name);
                    tField.setAccessible(true);
                } catch (Exception e) {
                    continue;
                }
                Class<?> tFieldClass = tField.getType();
                if (generalType.contains(sFieldClass.getName())) {
                    // 基本类型 + String + Date,直接复制值
                    /**
                     * 判断源字段属性的类型和目标字段属性类型一致，进行赋值，否则跳过
                     */
                    if (tFieldClass.getName().equals(sFieldClass.getName())) {
                        tField.set(target, value);
                    }
                } else if (tFieldClass.isArray()) {
                    // 数组类型
                    // 获取数组的Class
                    Class<?> componentType = tFieldClass.getComponentType();
                    if (generalType.contains(componentType.getName())) {
                        // 如果为通用类型数组，进行复制
                        tField.set(target, value);
                    } else {
                        // 复杂类型使用该方法复制
                        Object[] arr = (Object[]) value;
                        Object targetArr = Array.newInstance(componentType, arr.length);
                        for (int j = 0; j < arr.length; j++) {
                            Object itemSource = arr[j];
                            // 定义目标对象
                            Object itemTarget = componentType.newInstance();
                            // 复制对象
                            copyProperties(itemSource, itemTarget);
                            // 添加到对应数组
                            Array.set(targetArr, j, itemTarget);
                        }
                        // 复制属性
                        tField.set(target, targetArr);
                    }
                } else if (List.class.isAssignableFrom(tFieldClass)) {
                    // 列表类型
                    // 强转列表值
                    List listValue = (List) value;
                    // 获取列表中的泛型
                    Type genericType = tField.getGenericType();
                    // 获取类型Class
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType typeCls = (ParameterizedType) genericType;
                        // 列表中的泛型类型
                        Class subGenericClass = (Class<?>) typeCls.getActualTypeArguments()[0];
                        // 最终属性值，默认转成了ArrayList
                        List targetList = copyList(listValue, subGenericClass);
                        // 复制属性值
                        // 如果为抽象类，或者目标类型与源类型相同，直接复制
                        if (tFieldClass == List.class || tFieldClass == targetList.getClass()) {
                            // 默认为ArrayList
                            tField.set(target, targetList);
                        } else {
                            // 其他List
                            List otherList = (List) tFieldClass.newInstance();
                            // 转换目标类型列表
                            targetList.stream().forEach(item -> otherList.add(item));
                            // 复制
                            tField.set(target, otherList);
                        }
                    }
                } else if (Set.class.isAssignableFrom(tFieldClass)) {
                    // 列表类型
                    // 强转列表值
                    Set listValue = (Set) value;
                    // 获取列表中的泛型
                    Type genericType = tField.getGenericType();
                    // 获取类型Class
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType typeCls = (ParameterizedType) genericType;
                        // 列表中的泛型类型
                        Class subGenericClass = (Class<?>) typeCls.getActualTypeArguments()[0];
                        // 最终属性值，默认转成了ArrayList
                        Set targetSet = copySet(listValue, subGenericClass);
                        // 复制属性值
                        // 如果为抽象类，或者目标类型与源类型相同，直接复制
                        if (tFieldClass == Set.class || tFieldClass == targetSet.getClass()) {
                            // 默认为ArrayList
                            tField.set(target, targetSet);
                        } else {
                            // 其他List
                            Set otherSet = (Set) tFieldClass.newInstance();
                            // 转换目标类型列表
                            targetSet.stream().forEach(item -> otherSet.add(item));
                            // 复制
                            tField.set(target, otherSet);
                        }
                    }
                } else if (Map.class.isAssignableFrom(tFieldClass)) {
                    // 集合类型
                    // 强转集合
                    Map mapValue = (Map) value;
                    // key值集合
                    List keys = new ArrayList(mapValue.keySet());
                    // value值集合
                    List values = new ArrayList(mapValue.values());
                    // 获取列表中的泛型
                    Type genericType = tField.getGenericType();
                    // 获取类型Class
                    if (genericType instanceof ParameterizedType) {
                        ParameterizedType typeCls = (ParameterizedType) genericType;
                        // 获取key的类型
                        Class keyClass = (Class) typeCls.getActualTypeArguments()[0];
                        // 获取value的类型
                        Class valueClass = (Class) typeCls.getActualTypeArguments()[1];
                        // 转换keys集合
                        List targetKeysList = copyList(keys, keyClass);
                        // 转换values集合
                        List targetValuesList = copyList(values, valueClass);
                        // 实例化map对象
                        Map targetValue;
                        try {
                            // 属性可能使用具体类
                            targetValue = (Map) tFieldClass.newInstance();
                        } catch (Exception e) {
                            // 如果使用抽象类，则使用value值的类型
                            targetValue = (Map) value.getClass().newInstance();
                        }
                        // 转换目标Map集合
                        for (int j = 0; j < targetKeysList.size(); j++) {
                            Object targetMapKey = targetKeysList.get(j);
                            Object targetMapValue = targetValuesList.get(j);
                            targetValue.put(targetMapKey, targetMapValue);
                        }
                        // 复制属性
                        tField.set(target, targetValue);
                    }
                } else {
                    // 自定义对象类型
                    // 定义目标对象
                    Object targetValue = tFieldClass.newInstance();
                    // 复制子属性值
                    copyProperties(value, targetValue);
                    // 复制属性值
                    tField.set(target, targetValue);
                }
            }
            return target;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 复制集合
     *
     * @param source
     * @param target
     * @param targetClass
     * @param <T>
     * @param <S>
     */
    private static final <T, S> void copyCollection(Collection<S> source, Collection<T> target, Class<T> targetClass) {
        source.stream().forEach(item -> {
            try {
                T t;
                // 如果是基本类型
                if (generalType.contains(targetClass.getName())) {
                    target.add((T) item);
                } else {
                    t = targetClass.newInstance();
                    copyProperties(item, t);
                    target.add(t);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 实体类与VO对象列表的copy
     * 转换后以源列表为主，默认为ArrayList
     *
     * @param source      数据源列表
     * @param targetClass 目标列表中对象类Class
     * @param <T>         目标列表类型
     * @param <S>         数据源列表类型
     * @return
     */
    public static final <T, S> List<T> copyList(List<S> source, Class<T> targetClass) {
        List<T> target;
        try {
            if (source.getClass() == List.class || source.getClass() == ArrayList.class) {
                target = new ArrayList<>();
            } else {
                target = source.getClass().newInstance();
            }
            List<T> finalTarget = target;
            copyCollection(source, target, targetClass);
            return finalTarget;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 实体类与VO对象Set列表的copy
     * 转换后的集合类型以源类型为主，默认为HashSet
     *
     * @param source      数据源列表
     * @param targetClass 目标列表中对象类Class
     * @param <T>         目标列表类型
     * @param <S>         数据源列表类型
     * @return
     */
    public static final <T, S> Set<T> copySet(Set<S> source, Class<T> targetClass) {
        Set<T> target;
        try {
            if (source.getClass() == Set.class || source.getClass() == HashSet.class) {
                target = new HashSet<>();
            } else {
                target = source.getClass().newInstance();
            }

            Set<T> finalTarget = target;
            copyCollection(source, target, targetClass);
            return finalTarget;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
