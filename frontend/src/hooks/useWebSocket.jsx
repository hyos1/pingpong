import { useEffect, useRef, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

export default function useWebSocket(chatRoomId, onMessageReceived) {
  const clientRef = useRef(null);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    // chatRoomId 없으면 연결 안 함
    if (!chatRoomId) return;

    // 이전 연결 정리
    if (clientRef.current) {
      clientRef.current.deactivate();
      clientRef.current = null;
      setConnected(false);
    }

    const token = localStorage.getItem('token');

    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      connectHeaders: {
        Authorization: token,  // STOMP connect 시점에 토큰 전달
      },
      onConnect: () => {
        setConnected(true);
        client.subscribe(`/sub/chat/${chatRoomId}`, (frame) => {
          const message = JSON.parse(frame.body);
          onMessageReceived(message);
        });
      },
      onDisconnect: () => setConnected(false),
      onStompError: (frame) => {
        console.error('STOMP 오류:', frame);
        setConnected(false);
      },
    });

    client.activate();
    clientRef.current = client;

    // 언마운트 or chatRoomId 변경 시 연결 끊기
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