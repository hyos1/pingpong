import { useEffect } from 'react';
import { useAuth } from '../context/AuthContext';

export default function OAuthCallbackPage() {
  const { login } = useAuth();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');
    const refreshToken = params.get('refreshToken');
    const userId = params.get('userId');
    const username = params.get('username');

    if (token && userId) {
      login({ userId: Number(userId), username }, token, refreshToken);
      // navigate 말고 window.location.replace
      // 페이지 새로 로드 → AuthContext useEffect 다시 실행 → token 있으니까 getMe 호출 → user 세팅 → /chat 진입
      window.location.replace('/chat');
    } else {
      window.location.replace('/login');
    }
  }, []);

  return <div style={{ padding: '2rem', textAlign: 'center' }}>로그인 처리 중...</div>;
}