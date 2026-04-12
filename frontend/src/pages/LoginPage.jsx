import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { signup, login } from "../api/auth";
import "./LoginPage.css";

export default function LoginPage() {
  const navigate = useNavigate();
  const [tab, setTab] = useState("login");
  const [form, setForm] = useState({
    username: "",
    email: "",
    password: "",
    confirm: "",
  });
  const [errorMsg, setErrorMsg] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setErrorMsg("");
  };

  const handleTabSwitch = (t) => {
    setTab(t);
    setForm({ username: "", email: "", password: "", confirm: "" });
    setErrorMsg("");
  };

const handleSubmit = async () => {
  setErrorMsg('');

  // 빈 값 체크
  if (!form.email || !form.password) {
    setErrorMsg('이메일과 비밀번호를 입력해주세요.');
    return;
  }
  if (tab === 'signup' && !form.username) {
    setErrorMsg('닉네임을 입력해주세요.');
    return;
  }
  if (tab === 'signup' && form.password !== form.confirm) {
    setErrorMsg('비밀번호가 일치하지 않습니다.');
    return;
  }

  setLoading(true);
  try {
    if (tab === 'signup') {
      await signup(form.username, form.email, form.password);
      alert('회원가입 완료! 로그인해주세요.');
      handleTabSwitch('login');
    } else {
      const res = await login(form.email, form.password);
      localStorage.setItem('user', JSON.stringify(res.data.data));
      navigate('/chat');
    }
  } catch (err) {
    setErrorMsg(err.response?.data?.message || '오류가 발생했습니다.');
  } finally {
    setLoading(false);
  }
};

  const handleKeyDown = (e) => {
    if (e.key === "Enter") handleSubmit();
  };

  return (
    <div className="login-root">
      <div className="login-card">
        <div className="logo-row">
          <div className="logo-icon" />
          <span className="logo-text">
            Ping<span>Pong</span>
          </span>
        </div>

        <div className="tab-row">
          <button
            className={`tab-btn ${tab === "login" ? "active" : ""}`}
            onClick={() => handleTabSwitch("login")}
          >
            로그인
          </button>
          <button
            className={`tab-btn ${tab === "signup" ? "active" : ""}`}
            onClick={() => handleTabSwitch("signup")}
          >
            회원가입
          </button>
        </div>

        {tab === "signup" && (
          <div className="form-group">
            <label className="form-label">닉네임</label>
            <input
              className="form-input"
              name="username"
              value={form.username}
              onChange={handleChange}
              onKeyDown={handleKeyDown}
              placeholder="핑퐁 닉네임"
              autoFocus
            />
          </div>
        )}

        <div className="form-group">
          <label className="form-label">이메일</label>
          <input
            className="form-input"
            name="email"
            type="email"
            value={form.email}
            onChange={handleChange}
            onKeyDown={handleKeyDown}
            placeholder="hello@pingpong.com"
          />
        </div>

        <div className="form-group">
          <label className="form-label">비밀번호</label>
          <input
            className="form-input"
            name="password"
            type="password"
            value={form.password}
            onChange={handleChange}
            onKeyDown={handleKeyDown}
            placeholder="••••••••"
          />
        </div>

        {tab === "signup" && (
          <div className="form-group">
            <label className="form-label">비밀번호 확인</label>
            <input
              className="form-input"
              name="confirm"
              type="password"
              value={form.confirm}
              onChange={handleChange}
              onKeyDown={handleKeyDown}
              placeholder="비밀번호 재입력"
            />
          </div>
        )}

        {errorMsg && <p className="error-msg">{errorMsg}</p>}

        <button
          className="submit-btn"
          onClick={handleSubmit}
          disabled={loading}
        >
          {loading ? "처리 중..." : tab === "login" ? "로그인" : "시작하기"}
        </button>
      </div>
    </div>
  );
}
