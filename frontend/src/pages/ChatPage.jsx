import { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { getChatRooms, createChatRoom, getMessages } from "../api/chatRoom";
import useWebSocket from "../hooks/useWebSocket";
import "./ChatPage.css";

export default function ChatPage() {
  const navigate = useNavigate();
  const user = JSON.parse(localStorage.getItem("user"));

  const [rooms, setRooms] = useState([]);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState("");
  const [newRoomName, setNewRoomName] = useState("");
  const [showCreateInput, setShowCreateInput] = useState(false);
  const messagesEndRef = useRef(null);

  // 로그인 안 된 경우 튕겨내기
  useEffect(() => {
    if (!user) navigate("/login");
  }, []);

  // 채팅방 목록 불러오기
  useEffect(() => {
    getChatRooms(user.userId)
      .then((res) => setRooms(res.data.data))
      .catch((err) => console.error(err));
  }, []);

  // 채팅방 선택 시 메시지 히스토리 불러오기
  useEffect(() => {
    if (!selectedRoom) return;
    getMessages(selectedRoom.chatRoomId)
      .then((res) => setMessages(res.data.data))
      .catch((err) => console.error(err));
  }, [selectedRoom]);

  // 메시지 수신 콜백
  const handleMessageReceived = (message) => {
    setMessages((prev) => [...prev, message]);
  };

  const { sendMessage, connected } = useWebSocket(
    selectedRoom?.chatRoomId,
    handleMessageReceived,
  );

  // 메시지 전송
  const handleSend = () => {
    if (!inputText.trim() || !connected) return;
    sendMessage(inputText.trim(), user.username);
    setInputText("");
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") handleSend();
  };

  // 채팅방 생성
  const handleCreateRoom = async () => {
    if (!newRoomName.trim()) return;
    try {
      await createChatRoom(newRoomName.trim(), user.userId);
      const res = await getChatRooms(user.userId);
      setRooms(res.data.data);
      setNewRoomName("");
      setShowCreateInput(false);
    } catch (err) {
      console.error(err);
    }
  };

  // 메시지 추가될 때마다 스크롤 아래로
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // 로그아웃
  const handleLogout = () => {
    localStorage.removeItem("user");
    navigate("/login");
  };

  const getInitial = (name) => name?.charAt(0).toUpperCase() ?? "?";

  const avatarColors = ["#d4e6f9", "#f9d4e6", "#faecd4", "#e6f9d4"];
  const avatarTextColors = ["#1e5f9e", "#9e1e5f", "#9e5f1e", "#2e7d3e"];
  const getAvatarStyle = (id) => ({
    background: avatarColors[id % 4],
    color: avatarTextColors[id % 4],
  });

  return (
    <div className="chat-root">
      {/* ── Sidebar ── */}
      <div className="sidebar">
        <div className="sidebar-top">
          <div className="logo-text">
            Ping<span>Pong</span>
          </div>
          <button
            className="icon-btn"
            onClick={() => setShowCreateInput((v) => !v)}
          >
            ＋
          </button>
        </div>

        {showCreateInput && (
          <div className="create-room-box">
            <input
              className="create-room-input"
              placeholder="채팅방 이름"
              value={newRoomName}
              onChange={(e) => setNewRoomName(e.target.value)}
              onKeyDown={(e) => e.key === "Enter" && handleCreateRoom()}
            />
            <button className="create-room-btn" onClick={handleCreateRoom}>
              만들기
            </button>
          </div>
        )}

        <div className="sidebar-section">
          <div className="sidebar-label">채팅방</div>
          {rooms.map((room) => (
            <div
              key={room.chatRoomId}
              className={`room-item ${selectedRoom?.chatRoomId === room.chatRoomId ? "active" : ""}`}
              onClick={() => setSelectedRoom(room)}
            >
              <div
                className="room-avatar"
                style={
                  selectedRoom?.chatRoomId === room.chatRoomId
                    ? { background: "rgba(255,255,255,0.25)", color: "#fff" }
                    : getAvatarStyle(room.chatRoomId)
                }
              >
                {getInitial(room.name)}
              </div>
              <span className="room-name">{room.name}</span>
              <span className="room-meta">{room.memberCount}명</span>
            </div>
          ))}
        </div>

        <div className="sidebar-user">
          <div className="user-dot">{getInitial(user?.username)}</div>
          <div className="user-info">
            <div className="user-name">{user?.username}</div>
            <div className="user-status">
              {connected ? "온라인" : "오프라인"}
            </div>
          </div>
          <button className="icon-btn" onClick={handleLogout} title="로그아웃">
            ⎋
          </button>
        </div>
      </div>

      {/* ── Main ── */}
      <div className="main">
        {selectedRoom ? (
          <>
            <div className="chat-header">
              <div
                className="header-avatar"
                style={getAvatarStyle(selectedRoom.chatRoomId)}
              >
                {getInitial(selectedRoom.name)}
              </div>
              <span className="header-name">{selectedRoom.name}</span>
              <span className="header-sub">
                · {selectedRoom.memberCount}명 참여 중
              </span>
            </div>

            <div className="messages">
              {messages.map((msg, idx) => {
                const isMine = msg.senderUsername === user?.username;
                return (
                  <div
                    key={msg.messageId ?? idx}
                    className={`msg-group ${isMine ? "msg-mine" : ""}`}
                  >
                    <div
                      className="msg-ava"
                      style={
                        isMine
                          ? { background: "#5bbd72", color: "#fff" }
                          : getAvatarStyle(msg.messageId ?? idx)
                      }
                    >
                      {getInitial(msg.senderUsername)}
                    </div>
                    <div className="msg-body">
                      <div className="msg-header">
                        <span className="msg-sender">{msg.senderUsername}</span>
                        <span className="msg-time">
                          {msg.sentAt
                            ? new Date(msg.sentAt).toLocaleTimeString("ko-KR", {
                                hour: "2-digit",
                                minute: "2-digit",
                              })
                            : "방금"}
                        </span>
                      </div>
                      {isMine ? (
                        <div className="msg-bubble">{msg.content}</div>
                      ) : (
                        <div className="msg-text">{msg.content}</div>
                      )}
                    </div>
                  </div>
                );
              })}
              <div ref={messagesEndRef} />
            </div>

            <div className="input-area">
              <div className="input-box">
                <input
                  className="input-field"
                  placeholder="메시지를 입력하세요..."
                  value={inputText}
                  onChange={(e) => setInputText(e.target.value)}
                  onKeyDown={handleKeyDown}
                />
                <button className="send-btn" onClick={handleSend}>
                  ↑
                </button>
              </div>
            </div>
          </>
        ) : (
          <div className="empty-state">
            <div className="empty-icon">🏓</div>
            <p className="empty-text">채팅방을 선택하세요</p>
          </div>
        )}
      </div>
    </div>
  );
}
