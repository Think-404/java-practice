package org.originit.util;

import lombok.Data;

import java.lang.reflect.Field;
import java.util.HashSet;

public class BeanUtils {

    private static final HashSet<String> baseType = new HashSet<String>() {{
        add("int");
        add("double");
        add("float");
        add("long");
        add("short");
        add("byte");
        add("boolean");
        add("char");
    }};

    //给基础数据类型字段设置默认值
    private static void setBaseType(Field field,Object object) throws IllegalAccessException {
        switch (field.getType().getName()){
            case "boolean":
                field.set(object, false);
                break;
            case "byte":
                field.set(object, (byte) 0);
                break;
            case "char":
                field.set(object, '\u0000');
                break;
            case "short":
                field.set(object, (short) 0);
                break;
            case "int":
                field.set(object, 0);
                break;
            case "long":
                field.set(object, 0L);
                break;
            case "float":
                field.set(object, 0.0f);
                break;
            case "double":
                field.set(object, 0.0d);
                break;
            default:
                // 处理非基本类型的情况
                break;
        }
    }

    /**
     * 将源对象的属性拷贝到目标对象中，支持同名同类型属性的拷贝
     * 需要跑通单元测试
     */
    public static void copyProperties(Object source, Object target) {
        // 需求
        // 两个类 有相同属性进行赋值
        // 如果源为null name为空  age为默认值0
        // 如果目标为null  抛出 IllegalArgumentException.class
        if (target == null) {
            throw new IllegalArgumentException();
        }
        if (source == null) {
            return;
        }
        try {
            Class<?> sourceClass = source.getClass();
            Class<?> targetClass = target.getClass();
            Field[] sourceFields = sourceClass.getDeclaredFields();
            for (Field sFiled : sourceFields) {
                sFiled.setAccessible(true);
                // 获取该字段类型的class对象
                Class<?> sFiledType = sFiled.getType();
                // 获取该字段名
                String sFiledName = sFiled.getName();
                // 获取该字段的值
                Object sFiledValue = sFiled.get(source);
                //查询target中是否有该字段
                Field tField;
                try {
                    tField = targetClass.getDeclaredField(sFiledName);
                    tField.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    //如果没有对应字段，则continue
                    continue;
                }
                if (sFiledValue == null) {
                    if(baseType.contains(tField.getType().getName())){//如果tField是基础类型
                        setBaseType(tField,target);
                    }else{
                        tField.set(target, null);
                    }
                } else {
                    tField.set(target, sFiledValue);
                }
                // //有的话，判断字段类型是否相同
                // if(tField.getType().getName().equals(sFiledType.getName())){
                //     //字段类型也相同的话，则赋值
                //     tField.set(target,sFiledValue);
                // }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convert(Object source, Class<T> targetClass) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
@Data
class Test1{
    Integer integer1;
    char char1;
    boolean boolean1;
    long long1;
    int int1;
    float float1;
    double double1;
    byte byte1;
    short short1;
    //getType().getName()
    // java.lang.Integer
    // char
    // boolean
    // long
    // int
    // float
    // double
    // byte
    // short
    // null
    //boolean：默认值为 false
    // byte：默认值为 0
    // char：默认值为 '\u0000'（null 字符）
    // short：默认值为 0
    // int：默认值为 0
    // long：默认值为 0L
    // float：默认值为 0.0f
    // double：默认值为 0.0d
}