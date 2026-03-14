import urllib.request
import json
import random

u = 'pytest_' + str(random.randint(1000, 9999))
print(f'Testing with user {u}')

def req(url, data, token=None):
    req = urllib.request.Request(url, data=json.dumps(data).encode('utf-8'), method='POST')
    req.add_header('Content-Type', 'application/json')
    if token:
        req.add_header('Authorization', 'Bearer ' + token)
    try:
        with urllib.request.urlopen(req) as response:
            return json.loads(response.read().decode('utf-8'))
    except urllib.error.HTTPError as e:
        print(f'HTTP Error {e.code}: {e.read().decode("utf-8")}')
        return None

# Register
res = req('http://localhost:8080/api/auth/signup', {'username': u, 'email': f'{u}@test.com', 'password': 'TestPass123'})
print('Register:', res)

# Login
res = req('http://localhost:8080/api/auth/signin', {'username': u, 'password': 'TestPass123'})
print('Login:', 'Token acquired' if res else 'Failed')
token = res['token'] if res else None

if token:
    # Start
    res = req('http://localhost:8080/api/game/start', {'playerName': 'PythonHero'}, token)
    print('Start:', res['profile']['status'])
    
    # Action
    res = req('http://localhost:8080/api/game/action', {'actionId': 'study'}, token)
    print('Action Money:', res['stats']['money'], 'Action Stress:', res['stats']['stress'])
