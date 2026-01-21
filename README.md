异常分析 Agent
目标：日常 SOP（标准作业程序） 变成 Agent 的 Workflow
核心工具：
query_error_platform(code)：直接对接错误码平台 API，获取错误归属和文案。
get_trace_info(interface)：模拟在日志系统里“挑一个接口”的行为，获取 msgno 和对应的机器 IP。analyze_remote_log(ip, msgno, query_time)：这是最关键的一步。 
Agent 内部执行逻辑完全复刻我的操作：先远程执行 ls -rtl 获取文件列表，
根据 query_time 定位到具体的 applog 文件，再根据 msgno 或者时间窗口抓取堆栈。


这是Agent的核心工作。你拿到堆栈信息后，需要构造一个强有力的 Prompt。
我的 Prompt 结构通常是这样的：
Role: 你是一个资深的Java后端架构师，擅长排查线上故障。  
Context:  接口：com.tencent.xxx.OrderService
错误码：10023 (含义：订单状态校验失败)
报错机器：10.12.xx.xxEvidence 
(堆栈信息):  java.lang.NullPointerException    at com.tencent.xxx.OrderService.validate(OrderService.java:45)    ... 
 Task:  分析NPE的可能原因。
关键一步：
如果我有代码库权限，Agent可以调用 read_code(file="OrderService.java", lines="40-50") 获取源码。
（这个一般是可以开权限给机器人api的）给出具体的修复建议代码。判断这是一个“配置问题”、“代码逻辑Bug”还是“脏数据”。

流程大于模型：做垂直场景的 Agent，梳理清楚 SOP（比如那个精髓的 ls -rtl）比调整 Prompt 更重要。
数据串联是核心：Agent 的威力在于把监控数据、错误码定义、服务器日志这三个原本孤立的数据源，通过 msgno 串起来了。
容错处理：线上环境很复杂，有时候日志打印有延迟，Agent 里的 sleep 和重试机制要写好，就像我们人工查日志找不到时也会等两秒再查一样。
