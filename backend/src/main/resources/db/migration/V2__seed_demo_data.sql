-- Demo user
INSERT INTO users (email, name)
VALUES ('demo@pulse.app', 'Demo User')
ON CONFLICT (email) DO NOTHING;

-- Stock master
INSERT INTO stock_master (symbol, company_name, sector, exchange)
VALUES
    ('HDFCBANK', 'HDFC Bank', 'Banking', 'NSE'),
    ('RELIANCE', 'Reliance Industries', 'Energy', 'NSE'),
    ('INFY', 'Infosys', 'IT', 'NSE'),
    ('TCS', 'Tata Consultancy Services', 'IT', 'NSE'),
    ('ICICIBANK', 'ICICI Bank', 'Banking', 'NSE'),
    ('SBIN', 'State Bank of India', 'Banking', 'NSE')
ON CONFLICT (symbol) DO NOTHING;

-- Default watchlist
INSERT INTO watchlists (user_id, name)
SELECT id, 'My Watchlist'
FROM users
WHERE email = 'demo@pulse.app'
  AND NOT EXISTS (
      SELECT 1
      FROM watchlists w
      WHERE w.user_id = users.id
        AND w.name = 'My Watchlist'
  );

-- Add initial stocks
INSERT INTO watchlist_stocks (watchlist_id, symbol)
SELECT w.id, s.symbol
FROM watchlists w
CROSS JOIN stock_master s
WHERE w.name = 'My Watchlist'
  AND w.user_id = (
      SELECT id
      FROM users
      WHERE email = 'demo@pulse.app'
  )
  AND s.symbol IN (
      'HDFCBANK',
      'RELIANCE',
      'INFY',
      'TCS',
      'ICICIBANK',
      'SBIN'
  )
ON CONFLICT (watchlist_id, symbol) DO NOTHING;

-- Default checkpoint
INSERT INTO user_checkpoints (user_id)
SELECT id
FROM users
WHERE email = 'demo@pulse.app'
ON CONFLICT (user_id) DO NOTHING;

-- Default preferences
INSERT INTO user_preferences (user_id)
SELECT id
FROM users
WHERE email = 'demo@pulse.app'
ON CONFLICT (user_id) DO NOTHING;