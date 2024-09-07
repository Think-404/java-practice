package org.originit.util;

import lombok.Data;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.originit.annotation.FieldName;
import org.originit.annotation.ValueConverter;
import org.originit.converter.Converter;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class BeanUtilsTest {


    @Test
    void copyProperties_sameTypeProperties() {
        User user = new User();
        user.setName("John");
        user.setAge(30);

        UserDTO target = new UserDTO();
        BeanUtils.copyProperties(user, target);

        assertEquals("John", target.getName());
        assertEquals(30, target.getAge());
    }

    @Test
    void copyProperties_nullSource() {
        UserDTO target = new UserDTO();
        BeanUtils.copyProperties(null, target);
        assertNull(target.getName());
        assertEquals(0, target.getAge());
    }

    @Test
    void copyProperties_nullTarget() {
        User user = new User();
        assertThrows(IllegalArgumentException.class, () -> BeanUtils.copyProperties(user, null));
    }

    @Test
    void copyProperties_differentTypeProperties() {
        User user = new User();
        user.setName("John");
        user.setAge(30);

        UserVO target = new UserVO();
        BeanUtils.copyProperties(user, target);

        assertEquals("John", target.getName());
        assertEquals(30, target.getAge());
    }

    @Test
    void copyProperties_emptySource() {
        User user = new User();
        UserDTO target = new UserDTO();
        BeanUtils.copyProperties(user, target);

        assertNull(target.getName());
        assertEquals(0, target.getAge());
    }

    @Test
    void copyProperties_lessProperties() {
        User user = new User();
        user.setName("John");
        user.setAge(30);

        UserVO2 target = new UserVO2();
        BeanUtils.copyProperties(user, target);
        assertEquals("John", target.getName());
    }

    @Test
    void copyProperties_moreProperties() {
        User user = new User();
        user.setName("John");
        user.setAge(30);

        UserVO3 target = new UserVO3();
        target.setName("Alice");
        target.setSchool("MIT");
        BeanUtils.copyProperties(user, target);
        assertEquals("John", target.getName());
        assertEquals("MIT", target.getSchool());
    }

    @Test
    void copyProperties_boxTypeProperties() {
        User user = new User();
        user.setName("John");
        user.setAge(30);

        UserVO4 target = new UserVO4();
        BeanUtils.copyProperties(user, target);

        assertEquals("John", target.getName());
        assertEquals(30, target.getAge());
    }

    @Test
    void copyProperties_unboxTypeProperties() {
        UserVO4 user = new UserVO4();
        user.setName("John");
        user.setAge(30);

        User target = new User();
        BeanUtils.copyProperties(user, target);

        assertEquals("John", target.getName());
        assertEquals(30, target.getAge());

        user.setAge(null);
        BeanUtils.copyProperties(user, target);
        assertEquals("John", target.getName());
        assertEquals(0, target.getAge());
    }

    @Test
    void copyProperties_convertEnum() {
        UserEntity u = new UserEntity();
        u.setName("John");
        u.setSex(Sex.WOMAN);
        UserDO userDO = BeanUtils.convert(u, UserDO.class);
        assertEquals("John", userDO.getName());
        assertEquals(userDO.getSex(), "WOMAN");
        u.setSex(null);
        userDO = BeanUtils.convert(u, UserDO.class);
        assertEquals("John", userDO.getName());
        assertNull(userDO.getSex());
        UserDO1 convert = BeanUtils.convert(u, UserDO1.class);
        assertEquals("John", convert.getName());
        assertEquals(0, convert.getSex());
    }

    @Test
    void copyProperties_convertWithFieldName() {
        User user = new User();
        user.setAge(30);
        user.setName("John");
        OldUser oldUser = BeanUtils.convert(user, OldUser.class);
        assertEquals("John", oldUser.getName());
        assertEquals(30, oldUser.getEga());
        User user1 = BeanUtils.convert(oldUser, User.class);
        assertEquals("John", user1.getName());
        assertEquals(30, user1.getAge());
    }

    @Test
    void copyProperties_convertWithConverter() {
        User2 u = new User2();
        u.setName(" John ");
        User user = BeanUtils.convert(u, User.class);
        assertEquals("John", user.getName());
    }

    /**
     * Data这种注解是Lombok的，不了解可以搜一下，很好用
     */
    @Data
    static class User {
        private String name;
        private int age;
    }

    @Data
    static class UserDTO {
        private String name;
        private int age;
    }

    @Data
    static class UserVO {
        private String name;
        private long age;
    }

    @Data
    static class UserVO2 {
        private String name;
    }

    @Data
    static class UserVO3 {
        private String name;
        private String school;
    }

    @Data
    static class UserVO4 {
        private String name;
        private Integer age;
    }

    enum Sex {

        MAN,
        WOMAN
    }


    @Data
    static class UserEntity {
        private String name;
        private Sex sex;
    }

    @Data
    static class UserDO {
        private String name;
        private String sex;
    }

    @Data
    static class UserDO1 {
        private String name;
        private int sex;
    }

    @Data
    static class OldUser {
        private String name;
        @FieldName("age")
        private int ega;
    }

    static class TrimConverter implements Converter {

        @Override
        public Object convert(Object value) {
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                return ((String) value).trim();
            }
            return value;
        }
    }

    @Data
    static class User2 {
        @ValueConverter(TrimConverter.class)
        private String name;
    }
}