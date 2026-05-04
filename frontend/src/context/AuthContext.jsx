import { createContext, useContext, useState, useEffect } from 'react';
import { getMe, logoutApi } from '../api/auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [authLoading, setAuthLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      setAuthLoading(false);
      return;
    }
    getMe()
      .then((res) => {
        const { userId, username, email } = res.data.data;
        // getMe 응답으로 온 새 토큰도 갱신
        if (res.data.data.token) {
          localStorage.setItem('token', res.data.data.token);
        }
        setUser({ userId, username, email });
      })
      .catch(() => {
        localStorage.clear();
        setUser(null);
      })
      .finally(() => setAuthLoading(false));
  }, []);

  const login = (userData, token, refreshToken) => {
    localStorage.setItem('user', JSON.stringify(userData));
    localStorage.setItem('token', token);
    if (refreshToken) localStorage.setItem('refreshToken', refreshToken);
    setUser(userData);  // 이게 실행되면 PrivateRoute가 통과시켜줌
  };

  const logout = async () => {
    try {
      await logoutApi();
    } catch (e) {}
    localStorage.clear();
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout, authLoading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}