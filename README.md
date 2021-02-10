# riverland
微服务Spring Cloud Demo
## 限流
### 1s 20 calls
ab -n 20 -c 10 localhost:8080/box/process20
ab -n 21 -c 10 localhost:8080/box/process20

### 5s 10 calls
ab -n 10 -c 10 localhost:8080/box/process/10
ab -n 11 -c 10 localhost:8080/box/process/10

## 熔断
### case
- 调用成功
curl http://localhost:8080/box/breaker/success
- 打开熔断
ab -n 10 http://localhost:8080/box/breaker/down
- 查看熔断状态(Open)
curl http://localhost:8080/actuator/health
- 调用失败
curl http://localhost:8080/box/breaker/success
- 待Half_Open状态, 关闭熔断
ab -n 10 http://localhost:8080/box/breaker/success
- 查看熔断状态(Close)
curl http://localhost:8080/actuator/health

## 重试
### 重试三次
- 正常返回，无重试
curl http://localhost:8080/box/retry/success
- 重试3次, 间隔1秒, 后台日志输出三次调用时间
curl http://localhost:8080/box/retry/failure

## 健康检查
### Probes
- Livenessprobe
http://localhost:8080/actuator/health/liveness
- Readinessprobe
http://localhost:8080/actuator/health/readiness

### 停止Mysql
Liveness -> Down

### 停止Redis
Readiness -> Down