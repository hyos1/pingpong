import { useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export default function useWebSocket(chatRoomId, onMessageReceived) {
  const clientRef = useRef(null);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    if (!chatRoomId) return;

    if (clientRef.current) {
      clientRef.current.deactivate();
      clientRef.current = null;
      setConnected(false);
    }

    const token = localStorage.getItem('token');

    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: {
        Authorization: token,
      },
      onConnect: () => {
        setConnected(true);
        client.subscribe(`/sub/chat/${chatRoomId}`, (frame) => {
          const message = JSON.parse(frame.body);
          onMessageReceived(message);
        });
      },
      onStompError: (frame) => {
        const errorCode = frame.headers['errorCode'];
        console.error('STOMP 오류:', errorCode, frame);

        if (errorCode === 'TOKEN_EXPIRED' || errorCode === 'REFRESH_TOKEN_EXPIRED') {
          // 토큰 만료 → 로그아웃
          localStorage.clear();
          window.location.replace('/login');
          return;
        }

        if (errorCode === 'INVALID_TOKEN' || errorCode === 'TOKEN_NOT_FOUND') {
          // 토큰 자체가 잘못됨 → 로그아웃
          localStorage.clear();
          window.location.replace('/login');
          return;
        }

        if (errorCode === 'INTERNAL_SERVER_ERROR') {
          // 서버 오류 → 연결만 끊기
          setConnected(false);
          return;
        }

        // 그 외 알 수 없는 오류
        setConnected(false);
      },
      onDisconnect: () => setConnected(false),
    });

    client.activate();
    clientRef.current = client;

    return () => {
      client.deactivate();
      clientRef.current = null;
      setConnected(false);
    };
  }, [chatRoomId]);

  const sendMessage = (content) => {
    if (!clientRef.current?.connected) return;
    clientRef.current.publish({
      destination: `/app/chat/${chatRoomId}`,
      body: JSON.stringify({ content }),
    });
  };

  return { sendMessage, connected };
}