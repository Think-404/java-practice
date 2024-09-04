package org.originit.util;

import lombok.Data;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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
}