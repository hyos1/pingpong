import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  withCredentials: true,
});

// 요청 나가기 전 헤더에 토큰 값 세팅
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers["Authorization"] = token;
  }
  return config;
});

export default api;
