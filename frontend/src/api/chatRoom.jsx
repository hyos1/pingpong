import api from './axios';

export const getChatRooms = (userId) =>
  api.get(`/chat-rooms?userId=${userId}`);

export const createChatRoom = (name, userId) =>
  api.post(`/chat-rooms?userId=${userId}`, { name });

export const getMessages = (chatRoomId) =>
  api.get(`/chat-rooms/${chatRoomId}/messages`);