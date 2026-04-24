import api from "./axios";

export const getMe = () => api.get("/auth/me");
export const signup = (username, email, password) =>
  api.post("/users/signup", { username, email, password });
export const login = (email, password) =>
  api.post("/auth/login", { email, password });
