package kr.hhplus.be.server.config;

import kr.hhplus.be.server.layered.queue.QueueGuard;
import kr.hhplus.be.server.layered.queue.QueueStore;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final QueueStore queueStore;
    public WebConfig(QueueStore queueStore) { this.queueStore = queueStore; }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new QueueGuard(queueStore)).addPathPatterns("/api/v1/**");
    }
}
