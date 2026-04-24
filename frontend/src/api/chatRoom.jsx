import api from "./axios";

export const getChatRooms = () => api.get("/chat-rooms");

export const createChatRoom = (name) => api.post("/chat-rooms", { name });

export const getMessages = (chatRoomId, page = 0, size = 10) =>
  api.get(`/chat-rooms/${chatRoomId}/messages?page=${page}&size=${size}`);

export const searchUser = (username, chatRoomId) =>
  api.get(`/users/search?username=${username}&chatRoomId=${chatRoomId}`);

export const inviteUser = (chatRoomId, userId) =>
  api.post(`/chat-rooms/${chatRoomId}/members`, { userId });

export const deleteChatRoom = (chatRoomId) =>
  api.delete(`/chat-rooms/${chatRoomId}`);