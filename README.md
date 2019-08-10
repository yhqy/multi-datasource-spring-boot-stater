# 动态数据源

## 简要说明

用于动态数据源，多数据源处理。
使用方法参数动态切换数据源，支持动态添加数据源。

- [动态数据源demo](https://gitee.com/yuanheqiuye/multi-datasource-demo)
- [《完全理解spring动态数据源原理》](https://blog.csdn.net/weixin_43002640/article/details/98989716)

## 安装

```xml
<dependency>
    <groupId>com.github.yhqy</groupId>
    <artifactId>multi-datasource-spring-boot-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```

## 如何使用？（以mybatis为例）

### 表结构
假设我们有三个库，db_common, db_1 , db_2.

- db_common作为默认数据源。有表 datasource:

|id|url|drive|username|password|
|----|----|----|----|----|
|1|jdbc:mysql://localhost:3306/db_1|com.mysql.jdbc.Driver|root|password|
|2|jdbc:mysql://localhost:3306/db_2|com.mysql.jdbc.Driver|root|password|

- db_1有表: user

|id|username|
|----|----|
|1|db1_user|

- db_2有表: user

|id|username|
|----|----|
|1|db2_user|

### 实体类
```java
@Data
public class DataSourceDO {
    private int id;
    private String url;
    private String drive;
    private String username;
    private String password;
}

@Data
public class User {
    private int id;
    private String username;
}
```

### DAO
```java
@Mapper
public interface TestMapper extends DAO {

    @Select(" select * from datasource where id = #{id} ")
    DataSourceDO queryDataSourceById(@Param("id") Integer id);

    @Select(" select * from user limit 1")
    User queryUser(@Did Integer datasourceId);

}
```
注意：此处必须实现DAO接口，DAO接口只是一个标识类，不包含任何方法

### 数据源配置
```java
@Component
public class DemoDataSourceFactory implements DataSourceFactory {

    @Autowired
    private TestMapper testMapper;

    @Override
    public DataSource createDefaultDataSource() {
        return DataSourceBuilder.create().username("root").password("password")
                .url("jdbc:mysql://localhost:3306/db_common")
                .build();
    }

    @Override
    public DataSource createDataSource(String key) {
        DataSourceDO dataSourceDO = testMapper.queryDataSourceById(Integer.parseInt(key));
        return DataSourceBuilder.create().username(dataSourceDO.getUsername()).password(dataSourceDO.getPassword()).url(dataSourceDO.getUrl()).build();
    }

    @Override
    public String key() {
        return "id";
    }
}
```

### 测试
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Autowired
    private TestMapper testMapper;

    @Test
    public void contextLoads() {
        User user1 = testMapper.queryUser(1);
        Assert.assertEquals("db1_user", user1.getUsername());
        User user2 = testMapper.queryUser(2);
        Assert.assertEquals("db2_user", user2.getUsername());
    }

}
```

以上test类中，当testMapper.queryUser(1)时，发现当前系统没有key=1所对应的数据源，于是会调用我们配置的DataSourceFactory.createDataSource(key)方法创建，
此时会通过testMapper.queryDataSourceById(key)到datasource表中查询数据源配置信息（因为queryDataSourceById()方法参数中没有使用@Did注解，所以会使用默认数据源），
获得数据源配置信息后，构建datasource并返回。系统会缓存此datasource,后续使用到此datasource时无需再次创建。

### @Did 支持的注解参数类型
```java
/**
* 实现DAO接口（DAO接口是一个标志接口，无任何需要实现的方法）
*/
@Mapper
public interface TestMapper extends DAO {

    //1
    @Select(" select * from datasource where id = #{id} ")
    DataSourceDO getById(@Param("id") Integer id);

    //2
    @Select(" select * from user limit 1")
    User queryUser(@Did Integer datasourceId);

    //3
    @Select(" select * from user limit 1")
    User queryUser(@Did User user);

    //4
    @Select(" select * from user limit 1")
    User queryUser(@Did Map map);

    //5
    @Select(" select * from user limit 1")
    User queryUser(@Did List<User> user);
    
    //6
    @Select(" select * from user limit 1")
    User queryUser(@Did List<Map> map);

}
```

说明：
- 1: 未使用@Did标记，使用默认数据源;
- 2：使用@Did标记，使用datasourceId.toString()后对应的数据源,此处支持基本数据类型及string类型;
- 3：使用@Did标记，使用user.getDataSourceId()方法获得的值所对应的数据源（bean类型，通过DataSourceFactory.key()方法获得key所对应的get方法）;
- 4：使用@Did标记，使用map.get("dataSourceId")获得的值对应的数据源;
- 5：使用@Did标记，使用List.get(0)后获得的第一个User,然后使用通过user.getDataSourceId()获得的key对应的数据源;
- 6：使用@Did标记，使用List.get(0)后获得的第一个map,然后使用map.get("dataSourceId")获得的值对应的数据源;

