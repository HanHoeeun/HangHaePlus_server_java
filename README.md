# 콘서트 예약 서비스

> 대기열 + 좌석 임시배정 + 포인트 충전식 결제 기반의 콘서트 예약 서비스.

## 문서
- [API 명세서](./docs/openapi.yaml)
- [API spec](./docs/api-spec.md)
- [ERD](./docs/erd.md)
- [인프라 구성도](./docs/infra.md)
- [시퀀스 다이어그램](./docs/sequence-diagram.md)

## 목표 시나리오 (선정)
- **대기열 기반 콘서트 예약**  
  1) 사용자는 로그인 후 대기열에 진입해 토큰을 발급받는다.  
  2) 활성(Active) 상태의 사용자만 좌석 조회/예약/결제 가능.  
  3) 좌석 예약 시 **임시배정(ex> 5분 TTL)** 이 설정되어 타 사용자가 접근 불가.  
  4) 임시배정 내 결제가 완료되면 확정, 아니면 만료되어 재판매 가능.  
  5) 결제 수단은 **포인트 충전식 결제** 를 사용.

## ⚙기술 스택
- Java 21, Spring Boot 3, JPA (PostgreSQL)
- Redis (Redisson) → 대기열/락 관리
- JWT → 인증 & 대기열 검증
- 테스트: JUnit5 + Mockito + Testcontainers

## 📂 프로젝트 구조

```bash
src/main/java/kr/hhplus/be/server
├── clean
│   ├── reservation   # ✅ 예약/결제 (클린 아키텍처)
│   ├── queue         # ✅ 대기열 (클린 아키텍처)
│   └── wallet        # ✅ 지갑/포인트 (클린 아키텍처)
└── layered
    └── catalog       # ✅ 조회 전용 (레이어드 아키텍처)

```
## 🚀 API 요약
1️⃣ 대기열 (Queue)
- POST /api/v1/queue/tokens → 토큰 발급
- GET /api/v1/queue/status?token=... → 토큰 상태 확인

2️⃣ 예약 가능 조회 (Catalog)
- GET /api/v1/catalog/shows/{concertId} → 공연 회차별 예약 가능 좌석 수
- GET /api/v1/catalog/seats/{scheduleId} → 특정 회차 좌석 상세 가용성

3️⃣ 예약 (Reservation)
- POST /api/v1/reservations → 좌석 임시 배정 (HOLD)

4️⃣ 지갑 (Wallet)
- POST /api/v1/wallet/charge → 포인트 충전
- GET /api/v1/wallet/{userId} → 잔액 조회

5️⃣ 결제 (ConfirmReservation)
- POST /api/v1/reservations/confirm → 결제 확정 + 좌석 소유권 부여 + 대기열 토큰 만료

## 🧪 테스트 구조
```bash
src/test/java/kr/hhplus/be/server
├── clean/queue/application/service
│   └── QueueServiceTest.java
├── clean/reservation/application/service
│   ├── ConfirmReservationServiceTest.java
│   ├── ReservationQueryServiceTest.java
│   └── ReserveSeatServiceTest.java
└── application
    └── ReservationServiceTest.java
```

- QueueServiceTest → 토큰 발급 및 상태 관리 검증
- ReserveSeatServiceTest → 좌석 HOLD 처리 검증
- ConfirmReservationServiceTest → 결제 시 RESERVED 전환 검증
- ReservationQueryServiceTest → 예약 가능 조회 검증

👉 모든 테스트는 Mockito 기반 단위 테스트로 작성되어 DB/외부 의존성 제거

## ✅ Key Point

- 동시성 제어 : SeatLockPort + RedisSeatLockAdapter 로 중복 예약 방지
- 대기열 관리 : 순번 기반 FIFO, 상태(WAITING, ACTIVE, EXPIRED) 관리
- 클린 아키텍처 : 예약/결제는 port in/out 구조로 책임 분리 → 테스트 용이
- 레이어드 아키텍처 : 조회 전용(Catalog) 단순 Service-Repository 구조
- 테스트 : 핵심 로직은 모두 Mock 기반 단위 테스트로 커버
