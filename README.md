# 🎫 콘서트 예약 서비스

> 대기열 + 좌석 임시배정 + 포인트 충전식 결제 기반의 콘서트 예약 서비스.

## 🗂️ 문서
- [API 명세서](./docs/openapi.yaml)
- [API spec](./docs/api-spec.md)
- [ERD](./docs/erd.md)
- [인프라 구성도](./docs/infra.md)
- [시퀀스 다이어그램](./docs/sequence-diagram.md)
- [infrastructure layer](./docs/InfrastructureLayer.md)

## 🎯 목표 시나리오 (선정)
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
## 🛠 Infrastructure Layer 구조
```bash
clean/{domain}/adapter
├── in/
│   └── web/                         # API Controller
├── out/
│   ├── persistence/                # JPA 구현체
│   │   ├── *RepositoryAdapter.java
│   │   └── *Mapper.java
│   ├── lock/
│   │   └── RedisSeatLockAdapter.java   # 좌석 락 처리
│   └── external/
│       └── WalletServiceAdapter.java   # 외부 결제 연동 등



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

## ✅ 핵심 설계 포인트
| 항목        | 설명                                             |
| --------- | ---------------------------------------------- |
| 동시성 제어    | SeatLockPort + RedisSeatLockAdapter 로 예약 중복 방지 |
| 대기열 관리    | 상태(WAITING → ACTIVE → EXPIRED) 전환 + 순번 기반 처리   |
| 클린 아키텍처   | 예약/결제는 port in/out 기반으로 책임 분리 및 테스트 용이         |
| 레이어드 아키텍처 | Catalog(조회)는 단순 Service-Repository 구조          |
| 테스트 전략    | 단위(Mock 기반) + 통합 테스트(Testcontainers 기반) 병행     |


## 🧪 테스트 구조
```bash
src/test/java/kr/hhplus/be/server
├── clean/queue
│   ├── application/service/QueueServiceTest.java
│   └── integration/QueueTokenConcurrencyTest.java
├── clean/reservation
│   ├── application/service/
│   │   ├── ReserveSeatServiceTest.java
│   │   ├── ConfirmReservationServiceTest.java
│   │   └── ReservationQueryServiceTest.java
│   └── integration/
│       ├── ReservationFlowIntegrationTest.java
│       ├── ReservationExpireIntegrationTest.java
│       ├── ReservationExpireWithTimeProviderTest.java
│       ├── ReservationConcurrencyTest.java
│       ├── ReservationPaymentIdempotencyTest.java
│       └── SeatExpirationIntegrationTest.java
├── clean/wallet
│   ├── application/service/WalletServiceTest.java
│   └── integration/WalletPaymentConcurrencyTest.java
└── layered/catalog
    ├── CatalogControllerTest.java
    └── CatalogServiceTest.java
```

## 🧪 주요 통합 테스트
- ReservationFlowIntegrationTest → 유저 전체 플로우 (토큰 → 예약 → 결제 → 잔액 확인)
- ReservationExpireIntegrationTest → TTL 만료 후 예약 가능 여부
- ReservationPaymentIdempotencyTest → 중복 결제 방지 (멱등성)
- WalletPaymentConcurrencyTest → 결제 동시성 테스트 (잔액 음수 방지)
- QueueTokenConcurrencyTest → 대기열 토큰 중복 방지


