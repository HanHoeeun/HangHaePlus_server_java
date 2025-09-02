콘서트 예약 서비스 ERD
📊 시스템 개요
구분	개수
전체 Entity	10개
도메인	콘서트/예약/결제/사용자/대기열/월렛
핵심 제약	좌석 중복 방지, Idempotency 보장, TTL 만료, Optimistic 락
🗃️ Entity Relationship Diagram
erDiagram
%% === 사용자 & 인증/대기열 ===
USER ||--|| WALLET : owns
USER ||--o{ WALLET_LEDGER : has
USER ||--o{ QUEUE_TOKEN : has
USER ||--o{ RESERVATION : makes
USER ||--o{ PAYMENT : pays

%% === 콘서트/스케줄/좌석 ===
CONCERT ||--o{ CONCERT_SCHEDULE : has
CONCERT_SCHEDULE ||--o{ SEAT : contains
CONCERT_SCHEDULE ||--o{ RESERVATION : has

%% === 예약/결제 ===
SEAT ||--o{ RESERVATION : reservedOverTime
RESERVATION ||--|| PAYMENT : isSettledBy
PAYMENT ||--o{ PAYMENT_HISTORY : produces
WALLET ||--o{ WALLET_LEDGER : records

%% === 엔티티 ===
USER {
uuid user_id PK "사용자 ID"
string email "이메일"
string name "이름"
timestamp created_at
timestamp updated_at
}

QUEUE_TOKEN {
string token PK "대기열 토큰"
uuid user_id FK
int position "대기 순번"
string status "WAITING|ACTIVE|EXPIRED"
timestamp activated_at
timestamp expires_at
timestamp created_at
timestamp updated_at
}

CONCERT {
uuid concert_id PK
string title
string description
date start_date
date end_date
string status "PUBLISHED|UNPUBLISHED"
timestamp created_at
timestamp updated_at
}

CONCERT_SCHEDULE {
uuid schedule_id PK
uuid concert_id FK
timestamp show_at
int base_price
timestamp created_at
timestamp updated_at
}

SEAT {
uuid seat_id PK
uuid schedule_id FK
int seat_number "1..50"
int price
string status "AVAILABLE|SOLD"
UNIQUE(schedule_id, seat_number)
timestamp created_at
timestamp updated_at
}

RESERVATION {
uuid reservation_id PK
uuid user_id FK
uuid schedule_id FK
int seat_number
string status "HELD|CONFIRMED|CANCELED|EXPIRED"
timestamp hold_expires_at
int amount
int version "optimistic lock"
timestamp created_at
timestamp updated_at
%% 좌석 중복 방지: 활성 상태만 유니크
%% UNIQUE(schedule_id, seat_number) WHERE status IN ('HELD','CONFIRMED')
}

PAYMENT {
uuid payment_id PK
uuid user_id FK
uuid reservation_id FK UNIQUE
string status "CAPTURED|FAILED"
bigint amount
string idempotency_key
timestamp paid_at
timestamp created_at
UNIQUE(user_id, idempotency_key)
}

PAYMENT_HISTORY {
uuid payment_history_id PK
uuid payment_id FK
uuid user_id FK
string status "CAPTURED|FAILED|REFUNDED|CANCELED"
bigint amount
timestamp created_at
}

WALLET {
uuid wallet_id PK
uuid user_id FK UNIQUE
bigint balance
timestamp created_at
timestamp updated_at
}

WALLET_LEDGER {
uuid ledger_id PK
uuid wallet_id FK
string type "CHARGE|DEBIT|REFUND|ADJUST"
bigint amount
string idempotency_key
timestamp created_at
UNIQUE(wallet_id, idempotency_key)
}


모델링 포인트

좌석 자체(SEAT)는 **영구 상태(AVAILABLE/SOLD)**만 가짐.
임시배정(홀드)은 RESERVATION(HELD) 레코드와 hold_expires_at로 표현.

같은 좌석이 시간이 지나 여러 번 예약될 수 있으므로 SEAT 1 : N RESERVATION(시간 흐름 기준).

활성 예약만 중복 금지: UNIQUE(schedule_id, seat_number) WHERE status IN ('HELD','CONFIRMED').

결제/충전 Idempotency: PAYMENT, WALLET_LEDGER의 idempotency_key 유니크.

🏗️ 도메인별 Entity 상세
콘서트 도메인

CONCERT
필드	타입	설명
concert_id (PK)	UUID	콘서트 ID
title	String	콘서트 제목
description	String	설명
start_date / end_date	Date	전시/공연 기간
status	Enum	PUBLISHED/UNPUBLISHED
created_at / updated_at	DateTime	생성/수정
CONCERT_SCHEDULE
필드	타입	설명
schedule_id (PK)	UUID	스케줄 ID
concert_id (FK)	UUID	콘서트 ID
show_at	DateTime	공연 일시
base_price	Integer	기본가
created_at / updated_at	DateTime	생성/수정
SEAT
필드	타입	설명
seat_id (PK)	UUID	좌석 ID
schedule_id (FK)	UUID	스케줄 ID
seat_number	Int	좌석번호(1~50)
price	Int	가격
status	Enum	AVAILABLE/SOLD (가시화에서 HELD_BY_SELF/OTHERS 표기)
created_at / updated_at	DateTime	생성/수정
제약	Unique(schedule_id, seat_number)	회차 내 좌석 고유
👤 사용자/대기열 도메인
USER
필드	타입	설명
user_id (PK)	UUID	사용자 ID
email	String	이메일(Unique 권장)
name	String	이름
created_at / updated_at	DateTime	생성/수정
QUEUE_TOKEN
필드	타입	설명
token (PK)	String	대기열 토큰
user_id (FK)	UUID	사용자 ID
position	Int	대기 순번
status	Enum	WAITING/ACTIVE/EXPIRED
activated_at / expires_at	DateTime	활성/만료 시각
created_at / updated_at	DateTime	생성/수정
📋 예약 도메인
RESERVATION
필드	타입	설명
reservation_id (PK)	UUID	예약 ID
user_id (FK)	UUID	사용자
schedule_id (FK)	UUID	회차
seat_number	Int	좌석 번호
status	Enum	HELD/CONFIRMED/CANCELED/EXPIRED
hold_expires_at	DateTime	임시배정 만료
amount	Int	결제 예정 금액
version	Int	Optimistic 락(스냅샷 경합 방지)
created_at / updated_at	DateTime	생성/수정
제약	Partial Unique	UNIQUE(schedule_id, seat_number) WHERE status IN ('HELD','CONFIRMED')
💳 결제/월렛 도메인
PAYMENT
필드	타입	설명
payment_id (PK)	UUID	결제 ID
user_id (FK)	UUID	사용자
reservation_id (FK, UNIQUE)	UUID	예약 ID
status	Enum	CAPTURED/FAILED
amount	Long	결제 금액
idempotency_key	String	멱등 키(Unique by user)
paid_at / created_at	DateTime	결제 시각/생성
제약	Unique(user_id, idempotency_key)	Idempotency 보장
PAYMENT_HISTORY
필드	타입	설명
payment_history_id (PK)	UUID	이력 ID
payment_id (FK)	UUID	결제 ID
user_id (FK)	UUID	사용자
status	Enum	CAPTURED/FAILED/REFUNDED/CANCELED
amount	Long	금액
created_at	DateTime	생성
WALLET
필드	타입	설명
wallet_id (PK)	UUID	월렛 ID
user_id (FK, UNIQUE)	UUID	사용자별 1지갑
balance	Long	잔액(원)
created_at / updated_at	DateTime	생성/수정
WALLET_LEDGER
필드	타입	설명
ledger_id (PK)	UUID	원장 ID
wallet_id (FK)	UUID	월렛
type	Enum	CHARGE/DEBIT/REFUND/ADJUST
amount	Long	금액(+/-)
idempotency_key	String	멱등 키(Unique by wallet)
created_at	DateTime	생성
제약	Unique(wallet_id, idempotency_key)	Idempotency 보장
🔗 핵심 관계/제약 요약
관계	카디널리티	비고
CONCERT → CONCERT_SCHEDULE	1:N	콘서트-회차
CONCERT_SCHEDULE → SEAT	1:N	회차-좌석(1..50)
CONCERT_SCHEDULE → RESERVATION	1:N	회차-예약
SEAT → RESERVATION	1:N	시간 경과에 따라 여러 예약 가능
USER → RESERVATION / PAYMENT / LEDGER	1:N	사용자 활동
RESERVATION ↔ PAYMENT	1:1	예약 1건은 결제 1건으로 정산
PAYMENT → PAYMENT_HISTORY	1:N	결제 상태 전이 이력
USER ↔ WALLET	1:1	사용자당 1 월렛
WALLET ↔ WALLET_LEDGER	1:N	지갑 트랜잭션 원장
USER ↔ QUEUE_TOKEN	1:N	대기열 진입 이력

부분 유니크 인덱스(강추)

RESERVATION(schedule_id, seat_number) WHERE status IN ('HELD','CONFIRMED')
→ 동시 예약 경쟁에도 좌석 중복 배정 방지.

PAYMENT(user_id, idempotency_key), WALLET_LEDGER(wallet_id, idempotency_key)
→ 중복 호출에도 1회 처리 보장.

락/동시성

애플리케이션 레벨: lock:seat:{schedule}:{no}(Redis 분산락, TTL=hold TTL)

DB 레벨: version(Optimistic 락) + 부분 유니크 인덱스

📋 Enum 정의 (Java 예시)
public enum QueueStatus { WAITING, ACTIVE, EXPIRED }

public enum SeatStatus {
AVAILABLE,  // 판매 가능 (DB 저장)
SOLD        // 판매 완료 (DB 저장)
// 프론트 표시는 HELD_BY_SELF / HELD_BY_OTHERS 로 가공
}

public enum ReservationStatus { HELD, CONFIRMED, CANCELED, EXPIRED }

public enum PaymentStatus { CAPTURED, FAILED }

public enum PaymentHistoryStatus { CAPTURED, FAILED, REFUNDED, CANCELED }

public enum LedgerType { CHARGE, DEBIT, REFUND, ADJUST }

🧭 설계 의도 & 운영 팁

좌석 임시배정은 예약 엔티티로 관리
SEAT에 user_id를 저장하면 한 시점의 점유는 표현되지만, 시간 경과/취소/재판매 이력 관리가 어려워짐.
→ RESERVATION(HELD) + hold_expires_at로 표현하면 만료/결제/취소 흐름이 명확해지고, 감사 추적도 쉬움.

가시성 레이어 분리
클라이언트에선 *“지금 내 홀드인지/남의 홀드인지”*를 보여줘야 함 → API에서 좌석 리스트를 만들 때
SeatStatus(DB) + “현재 HELD 예약 존재 여부”를 조합해 HELD_BY_SELF/HELD_BY_OTHERS로 태깅.

Idempotency 키 강제
결제/충전 API에 Idempotency-Key를 필수로 받아 PAYMENT/WALLET_LEDGER 유니크로 보장.
네트워크 재시도/중복 클릭에도 안전.

TTL 만료 처리
hold_expires_at 기준으로 스케줄러(또는 Redis Keyspace Notifications)로 만료 처리 → RESERVATION.EXPIRED, 좌석 재가용.

Generated: 2025-09-02
Project: HangHae Plus Concert Reservation System