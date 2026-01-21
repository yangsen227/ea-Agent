目标是实现一个get_trace_info(interface)功能，
用于获取任意一个API接口（interface）最近一次被调用时的追踪ID（msgno）和处理该请求的机器IP。
为了实现这个目标，我们将采用以下技术方案：
1.请求拦截与追踪ID生成：使用Spring MVC的HandlerInterceptor来拦截所有进入应用的API请求。在请求开始时，为每个请求生成一个全局唯一的追踪ID（msgno）。
2.MDC（Mapped Diagnostic Context）：将生成的msgno存入SLF4J的MDC中。MDC是线程隔离的，可以确保在整个请求处理链路中（Controller, Service, DAO等），我们都能在日志中自动打印出这个msgno，而无需手动传递参数。
3.信息注册与存储：在请求处理完毕后，将该请求的interface（接口名）、msgno（追踪ID）和IP（服务器IP）信息记录到一个全局的、内存中的注册表（Registry）里。为了简化，这个注册表将只保存每个接口最近一次的调用信息。
4.API接口暴露：创建一个新的@RestController，提供一个/get_trace_info的HTTP GET接口。
该接口接收一个interface名称作为参数，然后从注册表中查询并返回对应的msgno和IP。这个方案无侵入性，不需要修改现有的业务代码，并且遵循了业界日志追踪的最佳实践。