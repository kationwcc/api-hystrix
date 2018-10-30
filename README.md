api断路器 
------

* 注意:
* 1：依赖spring aop 实现api断路功能实现
* 2：实现断路开启时的服务降级策略
* 3：可动态获取各个api的断路相关状态
* 4：通过切面监听api服务状态
* 5：通过配置定义错误数，错误时间段，断路尝试恢复时间
* 6：不负责具体的接口调用，建议与feign+hystrix联合使用


## 示例
接口定义 :
```java
@Server(server="user-server", fallback = UserServiceFallback.class)
public interface UserService {

    /**
     * 获取用户信息
     * @param uid
     * @return
     * @throws BreakerException
     */
    @API(api = "/user/{uid}", requestMethod = APIRequestMethod.GET)
    RespModel<User> getUser(String uid) throws BreakerException;
}
```

实现类 :
```java
@Component("userService")
public class UserServiceImpl implements UserService{

    /**
     * 获取用户信息
     * @param uid
     * @return
     * @throws BreakerException
     */
    RespModel<User> getUser(String uid) throws BreakerException{
        //相关业务逻辑
        return new User;
    }
}
```

服务降级定义 :
```java
@Component("userServiceFallback")
public class UserServiceFallback implements UserService{

    private final Logger logger = LogManager.getLogger(this.getClass());
    /**
     * 获取用户信息
     * @param uid
     * @return
     * @throws BreakerException
     */
    RespModel<User> getUser(String uid) throws BreakerException{
        //服务降级相关逻辑
        logger.info("服务降级相关逻辑");
        return new User;
    }
}
```


获取接口熔断相关状态信息 :
```java
@Controller
@RequestMapping("/api")
public class APIAction {

    @Autowired
    private APIHystrixExecutor apiHystrixExecutor;

    @GetMapping("/state")
    public RespModel<Map<String, APIServer>> getAPIState(){
        return RespModel.success(apiHystrixExecutor.getApiTable());
    }
}

```