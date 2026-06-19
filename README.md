# 🏓 PingPong - 실시간 채팅 서비스

채팅을 Ping-Pong처럼 주고 받는 웹 애플리케이션입니다.
<br/>
SSR 기반의 이전 프로젝트(Blink)와 달리, **SPA + REST API + WebSocket** 구조로 설계하고<br/>
JWT 인증, OAuth2 소셜 로그인, 실시간 통신, 예외처리까지 백엔드 핵심 기술을 학습한 개인 프로젝트입니다.

### 프로젝트 개요

- **개발 기간**: 2026.03.03 ~ 2026.05.04
- **개발 인원**: 1인 개발
- **개발 환경**: Spring Boot, React, H2 Database

### 주요 성과

- **JWT 인증 흐름 이해** 
- **OAuth2(Google) 소셜 로그인 흐름 이해**와 JWT 인증 설계
- 실시간 통신을 위한 Polling, Long Polling, **WebSocket(STOMP)** 차이 학습
- **WebSocket(STOMP)** 기반 실시간 메시지 송수신 구현
- Filter, DispatcherServlet 내부, STOMP 연결 전/후 **예외 발생 위치별 처리**
- 공통 응답 포맷, 전역 예외 처리 등 **일관된 API 설계 원칙** 적용

---

### 🏗 아키텍처

### 시스템 구조
```
┌─────────────┐
│   브라우저   │
│   (React)   │
└──────┬──────┘
       │ REST API (JSON) / WebSocket (STOMP)
       ▼
┌──────────────────────────┐
│   Spring Boot Server     │
│  ┌─────────────────────┐ │
│  │  Security Filter     │ │
│  │  (JWT / OAuth2)       │ │
│  └─────────┬───────────┘ │
│             ▼              │
│  Controller → Service     │
│             ▼              │
│        Repository          │
└─────────────┬──────────────┘
              ▼
        ┌──────────┐
        │    H2     │
        └──────────┘
```

### ERD

| 테이블 | 설명 |
|--------|------|
| users | 회원 정보 (일반/소셜 로그인 통합) |
| chat_room | 채팅방 |
| chat_room_member | 채팅방 참여 멤버 |
| message | 채팅 메시지 |
| refresh_token | Refresh Token 저장 |

---

## 실행영상
[실행 영상](https://github.com/user-attachments/assets/2d44ef0d-18c6-46c8-a507-d0d855da8c2f)

---

## [주요 기능]

### 1. 인증
- 이메일/비밀번호 회원가입 및 로그인
- Google OAuth2 소셜 로그인
- JWT Access Token + Refresh Token 발급 및 자동 재발급
- Access Token 만료 시 axios 인터셉터를 통한 재인증

### 2. 채팅
- 채팅방 생성 / 목록 조회 / 삭제
- 유저 검색 및 채팅방 초대
- WebSocket(STOMP) 기반 실시간 메시지 송수신
- 메시지 히스토리 페이징 조회 (최신순, 스크롤 시 추가 로드)

### 3. 공통 설계
- 공통 응답 포맷(`ApiResponse<T>`) 및 전역 예외 처리
- 에러 코드 Enum화로 일관된 에러 응답 관리
- 요청/응답 로깅 인터셉터

---

## [기술 선택의 이유]

### 인증 방식 (JWT)
#### 1. 대규모 동시 접속 환경에서의 서버 메모리 과부하 방지
* **세션 방식**: 사용자의 로그인 정보를 서버 메모리에 저장하므로 과부하가 발생합니다. 
* **JWT 이점**: 사용자 정보를 **클라이언트가 관리**하므로 사용자가 많아짐에도 서버 메모리 부담이 없습니다.<br/>
#### 2. 웹소켓(WebSocket/STOMP)과 시너지
* STOMP는 HTTP와 유사한 Native Header를 지원하므로 웹소켓 인터셉터에서 JWT를 추출, 검증하기 수월합니다.<br/>
#### 3. 서버 확장성 유리
* 세션 방식으로 서버를 확장할 경우 세션 동기화나 Redis같은 복잡한 인프라가 추가됩니다.<br/>
* 반면 JWT는 어떤 서버로 요청이나 웹소켓 연결이 들어와도 비밀키만 있으면 자체 검증이 가능하여 서버 확장에도 유리합니다.

### 웹/도메인 계층 분리
- 도메인 엔티티가 웹 계층(DTO, 인증 객체 등)을 알지 못하도록 하여,
  웹 구현이 바뀌어도 도메인이 영향받지 않게 설계했습니다.

### WebSocket 인증 — ChannelInterceptor
HTTP 요청과 WebSocket 연결은 프로토콜이 다르기 때문에, 동일한 JWT 필터로 처리할 수 없습니다.<br/>
HTTP 요청은 `JwtAuthenticationFilter`가, WebSocket의 STOMP CONNECT 시점 인증은   `ChannelInterceptor`가 각각 담당하도록 역할을 분리했습니다.

### 엔티티 연관관계 — 단방향 우선
모든 연관관계는 단방향으로 설계하고, cascade 등 양방향이 실제로 필요한 경우에만 적용했습니다.<br/>
불필요한 양방향 관계로 인한 복잡도 증가를 피하고자 했습니다.

### OAuth2 + JWT 통합
소셜 로그인 성공 후에도 자체 JWT를 발급하여, 이후 모든 API 요청의 인증 방식을 JWT 하나로 일원화했습니다.<br/>
일반 가입과 소셜 가입 유저를 `provider` 정보로 구분하여 동일한 이메일로 인한 계정 충돌을 방지했습니다.

---

## [트러블슈팅 경험]

### JWT 만료 시 Refresh Token 무한 재발급 루프
Access Token 만료 시 axios 인터셉터가 자동으로 `/auth/refresh`를 호출하도록 구성했는데,<br/>
Refresh Token까지 만료된 경우 재발급 요청 자체가 401을 반환하며 무한 루프에 빠지는 문제가 발생했습니다.<br/>
재발급 실패 응답을 403으로 분리하고, 인터셉터에서 재발급 요청 자체의 실패는 즉시 로그아웃 처리하도록 수정해 해결했습니다.

### OAuth2 로그인 후 인증 상태 유지 문제
**소셜 로그인 성공 후** 프론트 콜백 페이지로 리다이렉트했지만, React 상태가 **새로고침으로 초기화되며** 로그인 페이지로 되돌아가는 문제가 있었습니다.<br/>
앱 시작 시 토큰 유효성을 서버에 검증하는 `/auth/me` API를 추가하여, 새로고침 후에도 인증 상태가 유지되도록 해결했습니다.

### WebSocket 인증 실패 시 무한 재연결
STOMP CONNECT 시점에 JWT 만료를 단순 예외로 던지자, 클라이언트가 연결 실패 후 즉시 재연결을 시도하며 무한 반복되었습니다.<br/>
`ChannelInterceptor`에서 직접 STOMP ERROR 프레임을 생성해 에러 코드를 전달하고,<br/>
클라이언트가 이를 식별해 재연결 대신 로그아웃하도록 흐름을 변경했습니다.

### WebSocket 연결 후 예외 발생 시 처리
WebSocket은 DispatcherServlet과 HttpServletRequest를 거치치 않아<br/>
GlobalExceptionHandler나 response.getWriter().write()로 에러를 전달할 수 없었습니다.<br/>
`@ControllerAdvice + @MessageExceptionHandler`로 STOMP 컨트롤러 전역 예외 처리를 구성하고,<br/>
`SimpMessagingTemplate.convertAndSendToUser()`로 예외 발생한 유저에게만 메세지를 전달해 해결하였습니다.

## [학습하면서 궁금했던 점]
### HTTP와 WebSocket에서 사용자 식별 방식의 차이
**HTTP 요청**은 매 요청마다 필터에서 토큰 검증 후 `SecurityContextHolder`에 인증객체(Authentication)을 저장하여<br/>
응답하기 전까지 요청을 보낸이가 누군지 파악할 수 있었습니다.

**WebSocket(STOMP)** 에서는 CONNECT 시점에 `ChannelInterceptor`에서 토큰을 검증하고, 인증객체(Authentication)을 만들어<br/>
StompHeaderAccessor의 setUser(authentication)로 sessionId와 Principal을 등록합니다.<br/>
이후 sessionId에 해당하는 Principal로 사용자를 식별할 수 있습니다.

---

## [기술 스택]

**Backend**: Java 21, Spring Boot 3, Spring Security, Spring Data JPA, WebSocket(STOMP), JWT, OAuth2 Client, H2
**Frontend**: React, React Router, Axios, SockJS, STOMP.js

---

## [앞으로 보완하고 싶은 점]

- 테스트 코드 작성법 학습
- 채팅방 멤버 권한(방장/일반) 구분
- 메세지 읽기/쓰기 성능 향상 방법
