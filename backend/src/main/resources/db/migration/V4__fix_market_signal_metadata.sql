ALTER TABLE market_signals
ALTER COLUMN metadata TYPE TEXT
USING metadata::text;