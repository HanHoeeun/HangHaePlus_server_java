package kr.hhplus.be.server.layered.queue;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class QueueGuard implements HandlerInterceptor {
    private final QueueStore store;
    public QueueGuard(QueueStore store) { this.store = store; }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        // 예약/결제 등 보호가 필요한 경로만 검사
        String path = req.getRequestURI();
        boolean protectedPath = path.startsWith("/api/v1/reservations") || path.startsWith("/api/v1/payments");
        if (!protectedPath) return true;

        String token = req.getHeader("X-Queue-Token");
        if (token == null || !store.isActive(token)) {
            res.setStatus(403);
            res.setContentType("application/json");
            try {
                res.getWriter().write("""
          {"code":"QUEUE_INACTIVE","message":"Active queue token required."}
        """);
            } catch (Exception ignored) {}
            return false;
        }
        return true;
    }
}
