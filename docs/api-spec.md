
---

## docs/api-spec.md

```md
# API 명세서 (요약)

> 상세 OpenAPI는 `(./docs/openapi.yaml)`에서 확인 해주세요.


## 인증 & 대기열
### 로그인
- `POST /api/v1/auth/login`
  - Req: `{ "loginId": "user@example.com", "password": "P@ssw0rd!" }`
  - Res: `{ "accessToken": "...", "refreshToken": "..." }`

### 대기열 토큰 발급/상태
- `POST /api/v1/queue/tokens`
  - Res: `{ "token": "qtk_...", "position": 12, "status": "WAITING|ACTIVE", "etaSeconds": 300 }`
- `GET /api/v1/queue/status` (헤더 `X-Queue-Token`)
  - Res: `{ "position": 0, "status": "ACTIVE", "activeTtlSeconds": 280 }`

> **주의:** Reservation/Payment/Wallet API는 `Authorization: Bearer ...` + `X-Queue-Token`(ACTIVE) 둘 다 필요.

## 콘서트/공연/좌석
- `GET /api/v1/concerts` : 콘서트 목록(페이지)
- `GET /api/v1/concerts/{concertId}` : 콘서트 상세(+공연 요약)
- `GET /api/v1/concerts/{concertId}/shows` : 공연(회차) 목록
- `GET /api/v1/reservations/available/{concertId}/{showId}` : 좌석 가용 목록  
  - Res 예:  
    ```json
    { "seats": [
      { "seatId": 31005, "seatLabel": "A-12", "price": 150000, "available": true }
    ] }
    ```

## 예약(임시배정)
- `POST /api/v1/reservations`
  - Req: `{ "showId": 202, "seatIds": [31005,31006] }`
  - Res: `{ "reservationId": 5001, "status": "HELD", "expiresAt": "2025-09-01T20:00:00+09:00", "totalAmount": 300000 }`
  - 에러: `409 SEAT_HELD_BY_OTHERS`, `404 NOT_FOUND`, `400 VALIDATION_ERROR`
- `GET /api/v1/reservations` : 내 예약 목록
- `GET /api/v1/reservations/{reservationId}` : 예약 상세
- `POST /api/v1/reservations/{reservationId}/cancel` : 환불 요청(정책에 따라 상태/금액)

## 월렛(포인트)
- `GET /api/v1/users/me/points` : 잔액 조회  
  - Res: `{ "balance": 180000, "asOf": "2025-09-01T19:10:00+09:00" }`
- `POST /api/v1/users/me/points/charges` : 충전(멱등성 권장: `Idempotency-Key`)

## 결제
- `POST /api/v1/payments` (헤더 `Idempotency-Key`)
  - Req: `{ "reservationId": 5001, "amount": 300000 }`
  - 흐름: hold 유효성 → 잔액 차감 → 좌석 SOLD/예약 CONFIRMED → 대기열 토큰 만료
  - 에러: `409 HOLD_EXPIRED|INSUFFICIENT_BALANCE|ALREADY_CONFIRMED`

---

## 상태/정책 (합의안)
- **Queue.status**: `WAITING | ACTIVE | EXPIRED`
- **Reservation.status**: `HELD | CONFIRMED | CANCELED | EXPIRED`
- **Seat.status**: `AVAILABLE | SOLD` (가시성 레이어에서 `HELD_BY_SELF/OTHERS` 표기)
- **Hold TTL**: 기본 5분(정책값)
- **멱등성**: 결제/충전 요청 헤더 `Idempotency-Key` 필수

---

## 에러 표준 (예시)
- 포맷: `application/problem+json`
```json
{
  "type": "https://errors.example.com/seat-conflict",
  "title": "Seat already held",
  "status": 409,
  "detail": "Seat A-12 is temporarily held by another user.",
  "instance": "/api/v1/reservations"
}
