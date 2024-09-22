# 简介
Java实践项目
# 环境
- JDK 1.8
- Maven
- IntelliJ IDEA
- Lombok
- JUnit5
# 需求
1. 实现一个BeanUtils工具类
- 提供类似于Spring BeanUtils的copyProperties方法，用于实现两个对象之间的属性拷贝。  
  代码位置在org.originit.utils.BeanUtils中,你需要实现copyProperties方法，
  并执行测试用例org.originit.utils.BeanUtilsTest，使得所有测试用例通过。
- 支持枚举  
枚举转string, string转枚举，stirng是枚举的name
- 支持注解配置字段名称  
使用@FieldName注解，只能注在Field上，且进行转换时以注解的value作为字段名称
- 支持注解转换器  
如果存在注解转换器，只能注在Field上，字段的实际值应当经过注解转换器转换后得到
- 实现BeanUtils.convert方法  
不需要传递对象，直接基于class创建对象并返回实例，要求传入的class有无参构造函数，否则抛出IllegalArgumentException，以告知调用方参数错误，fast-failed
# 使用规范

## 开发

fork项目到自己的仓库，然后clone到本地，完成需求后进行测试，测试通过提交代码到自己的仓库。

## 检查

代码开发完毕，请执行`mvn clean package -DskipTests`进行检查，确保代码符合规范。
并运行实现代码相关的测试用例，保证用例通过。(当前测试用例标注了@Disabled注解，mvn不会执行，因此需要通过ide执行)

## 同步新需求

原始仓库会不定期发布新的需求，你需要将原始仓库的代码同步到自己的仓库。github fork提供同步功能,点击sync即可。
