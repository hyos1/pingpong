import axios from "axios";
import { reissue } from "./auth";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true,
});

// 요청 나가기 전 토큰 헤더 세팅
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers["Authorization"] = token;
  }
  return config;
});

// 401 응답 시 자동 재발급 시도
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;

    // /auth/refresh 요청 실패 시 무한루프 방지
    if (originalRequest.url.includes('/auth/refresh')) {
      localStorage.clear();
      window.location.href = '/login';
      return Promise.reject(error);
    }

    // 401이고 재시도 안 한 요청이면
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      const refreshToken = localStorage.getItem("refreshToken");
      if (!refreshToken) {
        // Refresh Token도 없으면 로그아웃
        localStorage.clear();
        window.location.href = "/login";
        return Promise.reject(error);
      }

      try {
        const res = await reissue(refreshToken);
        const newToken = res.data.data; // 새 Access Token

        localStorage.setItem("token", newToken);
        originalRequest.headers["Authorization"] = newToken;

        // 원래 요청 재시도
        return api(originalRequest);
      } catch (refreshError) {
        // Refresh Token도 만료 → 로그아웃
        localStorage.clear();
        window.location.href = "/login";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  },
);

export default api;
