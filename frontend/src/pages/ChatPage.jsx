import { useEffect, useState, useRef, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import {
  getChatRooms,
  createChatRoom,
  getMessages,
  searchUser,
  inviteUser,
  deleteChatRoom,
} from "../api/chatRoom";
import useWebSocket from "../hooks/useWebSocket";
import "./ChatPage.css";

export default function ChatPage() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const [rooms, setRooms] = useState([]);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState("");
  const [newRoomName, setNewRoomName] = useState("");
  const [showCreateInput, setShowCreateInput] = useState(false);
  const [searchKeyword, setSearchKeyword] = useState("");
  const [searchResult, setSearchResult] = useState(null);
  const [searchLoading, setSearchLoading] = useState(false);

  // 페이징 관련
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);

  const messagesEndRef = useRef(null);
  const messagesTopRef = useRef(null);
  const messageListRef = useRef(null);

  useEffect(() => {
    if (!user) return;
    getChatRooms()
      .then((res) => setRooms(res.data.data))
      .catch((err) => console.error(err));
  }, [user]);

  // 채팅방 선택 시 초기화 + 첫 페이지 로드
  useEffect(() => {
    if (!selectedRoom) return;
    setMessages([]);
    setPage(0);
    setHasMore(true);
    setSearchKeyword("");
    setSearchResult(null);

    getMessages(selectedRoom.chatRoomId, 0, 10)
      .then((res) => {
        const data = res.data.data;
        setMessages([...data].reverse()); // DESC → ASC로 뒤집기
        if (data.length < 10) setHasMore(false);
      })
      .catch((err) => console.error(err));
  }, [selectedRoom]);

  // 스크롤 올리면 이전 메시지 로드
  const handleScroll = useCallback(async () => {
    if (!messageListRef.current) return;
    if (messageListRef.current.scrollTop !== 0) return;
    if (!hasMore || loadingMore) return;

    setLoadingMore(true);
    const nextPage = page + 1;

    try {
      const res = await getMessages(selectedRoom.chatRoomId, nextPage, 10);
      const older = res.data.data;

      if (older.length === 0) {
        setHasMore(false);
        return;
      }

      const prevHeight = messageListRef.current.scrollHeight;
      setMessages((prev) => [...older].reverse().concat(prev)); // DESC → ASC 뒤집어서 앞에 붙이기
      setPage(nextPage);

      requestAnimationFrame(() => {
        if (messageListRef.current) {
          messageListRef.current.scrollTop =
            messageListRef.current.scrollHeight - prevHeight;
        }
      });

      if (older.length < 10) setHasMore(false);
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingMore(false);
    }
  }, [hasMore, loadingMore, page, selectedRoom]);

  useEffect(() => {
    const el = messageListRef.current;
    if (!el) return;
    el.addEventListener("scroll", handleScroll);
    return () => el.removeEventListener("scroll", handleScroll);
  }, [handleScroll]);

  const handleMessageReceived = (message) => {
    setMessages((prev) => [...prev, message]);
  };

  const { sendMessage, connected } = useWebSocket(
    selectedRoom?.chatRoomId,
    handleMessageReceived,
  );

  const handleSend = () => {
    if (!inputText.trim() || !connected) return;
    sendMessage(inputText.trim());
    setInputText("");
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter") handleSend();
  };

  const handleCreateRoom = async () => {
    if (!newRoomName.trim()) return;
    try {
      await createChatRoom(newRoomName.trim());
      const res = await getChatRooms();
      setRooms(res.data.data);
      setNewRoomName("");
      setShowCreateInput(false);
    } catch (err) {
      console.error(err);
    }
  };

  const handleLogout = () => {
    setSelectedRoom(null);
    setMessages([]);
    setRooms([]);
    logout();
    navigate("/login");
  };

  const handleDeleteRoom = async () => {
    if (!window.confirm(`"${selectedRoom.name}" 채팅방을 삭제할까요?`)) return;
    try {
      await deleteChatRoom(selectedRoom.chatRoomId);
      setSelectedRoom(null);
      setMessages([]);
      const res = await getChatRooms();
      setRooms(res.data.data);
    } catch (err) {
      console.error(err);
    }
  };

  // 새 메시지 오면 맨 아래로
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages.length]);

  const handleSearch = async (keyword) => {
    setSearchKeyword(keyword);
    setSearchResult(null);
    if (!keyword.trim()) return;
    setSearchLoading(true);
    try {
      const res = await searchUser(keyword, selectedRoom.chatRoomId);
      setSearchResult(res.data.data);
    } catch (err) {
      setSearchResult(null);
    } finally {
      setSearchLoading(false);
    }
  };

  const handleInvite = async (userId) => {
    try {
      await inviteUser(selectedRoom.chatRoomId, userId);
      setSearchResult((prev) => ({ ...prev, alreadyJoined: true }));
      const res = await getChatRooms();
      setRooms(res.data.data);
    } catch (err) {
      console.error(err);
    }
  };

  const getInitial = (name) => name?.charAt(0).toUpperCase() ?? "?";
  const avatarColors = ["#d4e6f9", "#f9d4e6", "#faecd4", "#e6f9d4"];
  const avatarTextColors = ["#1e5f9e", "#9e1e5f", "#9e5f1e", "#2e7d3e"];

  const getAvatarStyle = (id) => ({
    background: avatarColors[id % 4],
    color: avatarTextColors[id % 4],
  });

  const getSenderAvatarStyle = (username) => {
    const hash = username ? username.charCodeAt(0) % 4 : 0;
    return { background: avatarColors[hash], color: avatarTextColors[hash] };
  };

  return (
    <div className="chat-root">
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

        {selectedRoom && (
          <div className="search-box">
            <input
              className="search-input"
              placeholder="유저 검색 후 초대..."
              value={searchKeyword}
              onChange={(e) => handleSearch(e.target.value)}
            />
          </div>
        )}

        {selectedRoom && searchKeyword.trim() ? (
          <div className="search-results">
            <div className="result-label">검색 결과</div>
            {searchLoading && <div className="result-empty">검색 중...</div>}
            {!searchLoading && !searchResult && (
              <div className="result-empty">유저를 찾을 수 없습니다.</div>
            )}
            {!searchLoading && searchResult && (
              <div className="result-item">
                <div
                  className="r-ava"
                  style={getSenderAvatarStyle(searchResult.username)}
                >
                  {getInitial(searchResult.username)}
                </div>
                <span className="r-name">{searchResult.username}</span>
                {searchResult.alreadyJoined ? (
                  <span className="invite-btn done">참여 중</span>
                ) : (
                  <button
                    className="invite-btn"
                    onClick={() => handleInvite(searchResult.userId)}
                  >
                    초대
                  </button>
                )}
              </div>
            )}
          </div>
        ) : (
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
        )}

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
              <div style={{ marginLeft: "auto" }}>
                <button
                  className="icon-btn"
                  onClick={handleDeleteRoom}
                  title="채팅방 삭제"
                  style={{ color: "#e05050" }}
                >
                  🗑
                </button>
              </div>
            </div>

            <div className="messages" ref={messageListRef}>
              {loadingMore && (
                <div className="load-more-indicator">
                  이전 메시지 불러오는 중...
                </div>
              )}
              {!hasMore && messages.length > 0 && (
                <div className="load-more-indicator">처음 메시지입니다.</div>
              )}

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
                          : getSenderAvatarStyle(msg.senderUsername)
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
