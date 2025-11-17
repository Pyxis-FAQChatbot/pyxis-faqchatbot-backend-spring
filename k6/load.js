import http from 'k6/http';
import { check, sleep } from 'k6';

// 🟦 10 → 50 → 100 VU 점진 증가
export const options = {
    stages: [
        { duration: '30s', target: 10 },   // 10명으로 증가
        { duration: '30s', target: 50 },   // 50명으로 증가
        { duration: '30s', target: 100 },  // 100명까지 증가
        { duration: '30s', target: 0 },    // 종료
    ],
};

const BASE_URL = 'http://localhost:8081';

// 로그인 수행 후 쿠키 저장
export function setup() {
    const loginPayload = JSON.stringify({
        loginId: "user000010",
        password: "1234"
    });

    const loginHeaders = { 'Content-Type': 'application/json' };

    const res = http.post(`${BASE_URL}/api/v1/login`, loginPayload, {
        headers: loginHeaders,
    });

    check(res, { "login success": (r) => r.status === 200 });

    // 서버가 반환한 Set-Cookie 추출
    const cookie = res.headers['Set-Cookie'];
    return { cookie };
}

// 각 VU(가상 유저)가 실행할 함수
export default function (data) {
    const jar = http.cookieJar();
    jar.set(BASE_URL, "JSESSIONID", data.cookie.split("JSESSIONID=")[1].split(";")[0]);

    const endpoints = [
        '/api/v1/me',
        '/api/v1/community/1',
        '/api/v1/community/posts?page=0&size=10',
        '/api/v1/community/1/comment?page=0&size=20',
        '/api/v1/chatbot/rooms?page=0&size=10'
    ];

    for (const url of endpoints) {
        const res = http.get(`${BASE_URL}${url}`);
        check(res, { "status 200": (r) => r.status === 200 });
    }

    sleep(1);
}
