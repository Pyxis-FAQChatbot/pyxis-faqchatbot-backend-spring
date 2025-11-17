import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 1,
    iterations: 1,
};

const BASE = 'http://localhost:8081';

export default function () {

    // CookieJar는 반드시 VU 실행 context에서 생성해야 함
    let jar = http.cookieJar();

    // =============== 1) 로그인 ==================
    const loginRes = http.post(`${BASE}/api/v1/login`,
        JSON.stringify({
            loginId: "user000010",
            password: "1234"
        }),
        {
            headers: { 'Content-Type': 'application/json' },
            jar
        }
    );

    check(loginRes, {
        "login success": (r) => r.status === 200,
    });

    // 서버가 쿠키를 자동으로 jar에 저장함 → 따로 set 필요 없음

    // =============== 2) GET 리스트 ==================
    const getEndpoints = [
        "/api/v1/me",
        "/api/v1/community/1",
        "/api/v1/community/posts?page=0&size=10",
        "/api/v1/community/1/comment?page=0&size=20",
        "/api/v1/chatbot/rooms?page=0&size=10",
    ];

    getEndpoints.forEach(url => {
        const res = http.get(`${BASE}${url}`, { jar });

        check(res, {
            [`GET ${url}`]: (r) => r.status === 200,
        });
    });

    // =============== 3) POST API ==================
    const postEndpoints = [
        {
            url: "/api/v1/community",
            body: { title: "테스트 제목", content: "테스트 내용", postType : "DEFAULT" }
        },
        {
            url: "/api/v1/community/1/comment",
            body: { content: "댓글 내용", parentId: null }
        },
        {
            url: "/api/v1/chatbot",
            body: {}
        }
    ];

    postEndpoints.forEach(ep => {
        const res = http.post(`${BASE}${ep.url}`, JSON.stringify(ep.body), {
            headers: { 'Content-Type': 'application/json' },
            jar,
        });

        check(res, {
            [`POST ${ep.url}`]: (r) => r.status === 200 || r.status === 201,
        });
    });

    sleep(1);
}
