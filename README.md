# 动态数据源

## 简要说明

用于动态数据源，多数据源处理。
使用方法参数动态切换数据源，支持动态添加数据源。

[动态数据源demo](https://gitee.com/yuanheqiuye/multi-datasource-demo)

## 安装

```xml
<dependency>
    <groupId>com.github.yhqy</groupId>
    <artifactId>multi-datasource-spring-boot-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```

## 先看一下如何使用？（配置见下一节）

此处以mybatis为例，其他类似

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
- 2：使用@Did标记，使用datasourceId.toString()后对应的数据源;
- 3：使用@Did标记，使用user.getDataSourceId()方法获得的值所对应的数据源（通过DataSourceFactory.key()方法获得key所对应的get方法）;
- 4：使用@Did标记，使用map.get("dataSourceId")获得的值对应的数据源;
- 5：使用@Did标记，使用List.get(0)后获得的第一个User,然后使用通过user.getDataSourceId()获得的key对应的数据源;
- 6：使用@Did标记，使用List.get(0)后获得的第一个map,然后使用map.get("dataSourceId")获得的值对应的数据源;

## 配置数据源工厂

```java
@Component
public class DemoDataSourceFactory implements DataSourceFactory {

    /**
    * 默认数据源
    */
    @Override
    public DataSource createDefaultDataSource() {
        return DataSourceBuilder.create().username("root").password("password")
                .url("jdbc:mysql://localhost:3306/test_common?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useSSL=false&serverTimezone=GMT%2B8")
                .build();
    }

    /**
    * 根据key创建数据源（后面根据key进行切库）
    */
    @Override
    public DataSource createDataSource(String key) {
        if("1".equals(key)){
            return DataSourceBuilder.create().username("username1").password("password2").url("jdbc:mysql://xxxx").build();
        } else if("2".equals(key)) {
            return DataSourceBuilder.create().username("username1").password("password2").url("jdbc:mysql://xxxx").build();
        }
        
    }

    /**
    * 从bean，map, collection中获取key值时的值
    */
    @Override
    public String key() {
        return "dataSourceId";
    }
}
```

