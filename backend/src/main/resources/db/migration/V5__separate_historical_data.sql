UPDATE market_snapshots
SET source = 'HISTORICAL'
WHERE timestamp < '2026-09-05 00:00:00+00';

UPDATE market_snapshots
SET source = 'DEMO'
WHERE timestamp >= '2026-09-05 00:00:00+00';