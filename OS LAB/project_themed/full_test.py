"""
LifeLoad Full API Test Suite - ASCII safe for Windows
"""
import urllib.request
import json
import random
import sys

BASE = 'http://localhost:8080'
u = 'test_' + str(random.randint(10000, 99999))
PASS = 'TestPass@123'
token = None
P = 0
F = 0

def chk(label, cond, extra=''):
    global P, F
    status = 'PASS' if cond else 'FAIL'
    msg = f'  [{status}] {label}'
    if extra:
        msg += f' | {extra}'
    print(msg)
    if cond:
        P += 1
    else:
        F += 1

def get(path, t=None):
    req = urllib.request.Request(f'{BASE}{path}')
    req.add_header('Content-Type', 'application/json')
    if t:
        req.add_header('Authorization', 'Bearer ' + t)
    try:
        with urllib.request.urlopen(req) as res:
            return json.loads(res.read().decode())
    except urllib.error.HTTPError as e:
        print(f'    -> HTTP {e.code}: {e.read().decode()[:150]}')
        return None
    except Exception as ex:
        print(f'    -> ERROR: {ex}')
        return None

def post(path, data, t=None):
    req = urllib.request.Request(f'{BASE}{path}', data=json.dumps(data).encode(), method='POST')
    req.add_header('Content-Type', 'application/json')
    if t:
        req.add_header('Authorization', 'Bearer ' + t)
    try:
        with urllib.request.urlopen(req) as res:
            return json.loads(res.read().decode())
    except urllib.error.HTTPError as e:
        print(f'    -> HTTP {e.code}: {e.read().decode()[:150]}')
        return None
    except Exception as ex:
        print(f'    -> ERROR: {ex}')
        return None

print(f'\n{"=" * 52}')
print(f'  LifeLoad API Test Suite  (user: {u})')
print(f'{"=" * 52}\n')

# ============================================
print('--- 1. AUTH TESTS ---')

r = post('/api/auth/signup', {'username': u, 'email': f'{u}@test.com', 'password': PASS})
chk('Register new user', r is not None and 'successfully' in str(r), str(r))

r = post('/api/auth/signup', {'username': u, 'email': f'{u}2@test.com', 'password': PASS})
chk('Duplicate username rejected (expect error)', r is None)

r = post('/api/auth/signin', {'username': u, 'password': PASS})
chk('Login valid credentials', r is not None and 'token' in r)
token = r['token'] if r and 'token' in r else None

r = post('/api/auth/signin', {'username': u, 'password': 'WrongPassword!'})
chk('Wrong password rejected (expect error)', r is None)

if not token:
    print('\nFATAL: No auth token. Aborting.')
    sys.exit(1)

# ============================================
print('\n--- 2. GAME TESTS ---')

r = get('/api/game/load', token)
chk('Load game before start -> 404', r is None)

r = post('/api/game/start', {'playerName': 'TestHero'}, token)
status = r.get('profile', {}).get('status') if r else None
chk('Start game -> ACTIVE', status == 'ACTIVE',
    f'age={r["profile"]["age"]} money={r["stats"]["money"]:.0f}' if r else 'no response')

r = get('/api/game/load', token)
chk('Load game after start -> ok', r is not None and 'profile' in r)

# Actions
print('\n  [Actions]')
for act in ['rest', 'work', 'study', 'gym', 'meditate', 'odd_jobs', 'hustle']:
    r = post('/api/game/action', {'actionId': act}, token)
    if r and 'stats' in r:
        st = r['stats']
        chk(f'Action: {act}', True,
            f'money={st["money"]:.0f} energy={st["energy"]} stress={st["stress"]}')
    else:
        chk(f'Action: {act}', False, str(r)[:80] if r else 'None')

r = post('/api/game/action', {'actionId': 'unknownXYZ'}, token)
chk('Unknown action rejected', r is None)

# Socialize and network need money - do these after some work
for act in ['socialize', 'network']:
    r = post('/api/game/action', {'actionId': act}, token)
    if r and 'stats' in r:
        chk(f'Action: {act}', True, f'money={r["stats"]["money"]:.0f}')
    else:
        chk(f'Action: {act} (may fail if broke)', r is None,
            'Needs funds - INSUFFICIENT_FUNDS is valid')

# Freelance needs knowledge >= 20
r = post('/api/game/action', {'actionId': 'freelance'}, token)
chk('Action: freelance (needs knowledge>=20)', r is not None)

# Daily Reward
print('\n  [Daily Reward]')
r = get('/api/game/daily-reward', token)
chk('Daily reward first claim', r is not None and r.get('claimed') == True,
    str(r)[:80] if r else 'None')

r = get('/api/game/daily-reward', token)
chk('Daily reward blocked same day', r is not None and r.get('claimed') == False,
    str(r)[:80] if r else 'None')

# Timeline
print('\n  [Timeline]')
r = get('/api/game/timeline', token)
chk('Timeline has events', r is not None and len(r) > 0,
    f'{len(r)} events' if r else 'None')

# Rivals
print('\n  [Rivals]')
r = get('/api/game/rivals', token)
chk('Rivals returned', r is not None and len(r) > 0,
    f'{len(r)} rivals' if r else 'None')

# Mini-games
print('\n  [Mini-Games]')
for mg, sc in [('BUDGET_QUIZ', 5), ('PRODUCTIVITY_FLOW', 30), ('MEMORY_MATRIX', 3), ('TYPING_HUSTLE', 50)]:
    r = post('/api/game/minigame', {'type': mg, 'score': sc}, token)
    chk(f'MiniGame {mg} score={sc}', r is not None and 'message' in r,
        r.get('message', '')[:60] if r else 'None')

r = post('/api/game/minigame', {'type': 'INVALID_GAME', 'score': 0}, token)
chk('Invalid minigame type rejected', r is None)

# Event choice with no pending event
print('\n  [Event System]')
r = post('/api/game/event/choice', {'optionIndex': 0}, token)
chk('Event choice with no pending event -> error', r is None)

# ============================================
print('\n--- 3. ECONOMY TESTS ---')

r = get('/api/economy/market', token)
chk('Market state returned', r is not None and 'state' in r, str(r) if r else 'None')

r = post('/api/economy/invest', {'type': 'STOCK', 'name': 'TechCorp', 'amount': 500}, token)
chk('Invest valid amount', r is not None, str(r)[:60] if r else 'None')

r = get('/api/economy/portfolio', token)
chk('Portfolio returned', r is not None,
    f'{len(r)} investments' if isinstance(r, list) else str(r)[:60])
inv_id = r[0]['id'] if r and isinstance(r, list) and len(r) > 0 else None

r = post('/api/economy/invest', {'type': 'STOCK', 'name': 'GoldBar', 'amount': 9999999}, token)
chk('Invest insufficient funds rejected', r is None)

if inv_id:
    r = post(f'/api/economy/sell/{inv_id}', {}, token)
    chk(f'Sell investment id={inv_id}', r is not None, str(r)[:60] if r else 'None')

# ============================================
print('\n--- 4. LEADERBOARD TESTS ---')

r = get('/api/leaderboard/wealth', token)
chk('Wealth leaderboard', r is not None,
    f'{len(r)} entries' if isinstance(r, list) else str(r)[:60])

r = get('/api/leaderboard/balanced', token)
chk('Balanced leaderboard', r is not None,
    f'{len(r)} entries' if isinstance(r, list) else str(r)[:60])

# ============================================
print(f'\n{"=" * 52}')
print(f'  TOTAL: {P + F} tests  |  PASS: {P}  |  FAIL: {F}')
print(f'{"=" * 52}')
if F == 0:
    print('  All tests passed!')
else:
    print(f'  {F} test(s) FAILED. Review output above.')
print()
