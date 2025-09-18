-- 활성(HELD/CONFIRMED)만 중복 금지
ALTER TABLE reservation
  ADD COLUMN active_flag TINYINT(1)
  AS (CASE WHEN status IN ('HELD','CONFIRMED') THEN 1 ELSE 0 END)
  STORED;

CREATE UNIQUE INDEX IF NOT EXISTS ux_reservation_active
  ON reservation (schedule_id, seat_number, active_flag);

CREATE UNIQUE INDEX IF NOT EXISTS ux_seat_unique
  ON seat (schedule_id, seat_number);

ALTER TABLE payment
  ADD UNIQUE KEY IF NOT EXISTS ux_payment_idem (user_id, idempotency_key);

ALTER TABLE wallet_ledger
  ADD UNIQUE KEY IF NOT EXISTS ux_wallet_ledger_idem (wallet_id, idempotency_key);
