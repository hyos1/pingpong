import axios from "axios";
import { useState } from "react";

function LoginPage() {
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [age, setAge] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post("http://localhost:8080/users/addUser", {
        name,
        password,
        age: Number(age),
      });
      alert(response.data);
    } catch (error) {
      console.error(error);
      alert("회원가입 실패");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        value={name}
        onChange={(e) => setName(e.target.value)}
        placeholder="이름"
      /> <br />
      <input
        type="password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        placeholder="비밀번호"
      /> <br />
      <input
        type="number"
        value={age}
        onChange={(e) => setAge(e.target.value)}
        placeholder="나이"
      /> <br />
      <button type="submit">회원가입</button>
    </form>
  );
}

export default LoginPage;
