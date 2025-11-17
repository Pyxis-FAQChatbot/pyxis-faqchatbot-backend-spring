import http, { CookieJar } from "k6/http";
import { check, sleep, group } from "k6";
import { Trend } from "k6/metrics";

const BASE = "http://localhost:8081";
const jar = new CookieJar();

// 🔧 API별 latency 메트릭 생성
const metrics = {};
[
    "login",
    "getMe",
    "getPost",
    "getPosts",
    "getComments",
    "getChatbotRooms",
    "createPost",
    "createComment",
    "createBotchat",
].forEach((m) => (metrics[m] = new Trend(`${m}_latency`)));

// 🔥 JSON 파싱 오류 방지 (핵심!)
function safeJson(res) {
    try {
        return res.json();
    } catch (e) {
        return {}; // JSON 아닌 응답이면 빈 객체 반환
    }
}

export const options = {
    stages: [
        { duration: "30s", target: 10 },
        { duration: "40s", target: 50 },
        { duration: "30s", target: 100 },
        { duration: "20s", target: 0 },
    ],
};

// 📌 공통 요청 함수
function smartRequest(method, url, body, name) {
    const res = http.request(method, `${BASE}${url}`, body, {
        headers: { "Content-Type": "application/json" },
        jar,
    });

    // latency 기록
    metrics[name].add(res.timings.duration);

    // 안전 JSON 파싱
    const json = safeJson(res);

    // 성공 조건 자동 정의
    const successRules = {
        login:          () => res.status === 200,
        getMe:          () => res.status === 200,
        getPost:        () => res.status === 200,
        getPosts:       () => res.status === 200,
        getComments:    () => res.status === 200,
        getChatbotRooms:() => res.status === 200,

        // 생성 계열 API
        createPost:     () => res.status === 201,
        createComment:  () => res.status === 200 && json.commentId,
        createBotchat:  () => res.status === 200 && json.botChatId,
    };

    const ok = successRules[name] ? successRules[name]() : res.status === 200;

    check(res, { [`${name} success`]: () => ok });

    return res;
}

// 📌 테스트 시나리오
export default function () {
    group("로그인", () =>
        smartRequest(
            "POST",
            "/api/v1/login",
            JSON.stringify({ loginId: "user000010", password: "1234" }),
            "login"
        )
    );

    sleep(0.2);

    group("유저 정보", () =>
        smartRequest("GET", "/api/v1/me", null, "getMe")
    );

    group("게시글 상세 조회", () =>
        smartRequest("GET", "/api/v1/community/1", null, "getPost")
    );

    group("게시글 목록 조회", () =>
        smartRequest("GET", "/api/v1/community/posts?page=0&size=10", null, "getPosts")
    );

    group("댓글 조회", () =>
        smartRequest("GET", "/api/v1/community/1/comment?page=0&size=20", null, "getComments")
    );

    group("챗봇방 조회", () =>
        smartRequest("GET", "/api/v1/chatbot/rooms?page=0&size=10", null, "getChatbotRooms")
    );

    // 🔥 생성 계열 API
    group("게시글 생성", () =>
        smartRequest(
            "POST",
            "/api/v1/community",
            JSON.stringify({ title: "테스트", content: "테스트", postType: "DEFAULT" }),
            "createPost"
        )
    );

    group("댓글 생성", () =>
        smartRequest(
            "POST",
            "/api/v1/community/1/comment",
            JSON.stringify({ content: "테스트 댓글" }),
            "createComment"
        )
    );

    group("챗봇 생성", () =>
        smartRequest(
            "POST",
            "/api/v1/chatbot",
            JSON.stringify({}),
            "createBotchat"
        )
    );

    sleep(1);
}
