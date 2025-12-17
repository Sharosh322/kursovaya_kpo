import React from "react";
import { useAuth } from "../auth/AuthContext";
import { Link } from "react-router-dom";

export default function ProfilePage() {
  const { user, isAdmin } = useAuth();

  if (!user) return null;

  return (
    <div style={{ maxWidth: 720, display: "grid", gap: 12 }}>
      <h2>Личный кабинет</h2>

      <div style={{ border: "1px solid #ddd", borderRadius: 10, padding: 14 }}>
        <div style={{ display: "grid", gap: 6 }}>
          <div><b>Email:</b> {user.email || "-"}</div>
          <div><b>ФИО:</b> {user.fullName || "-"}</div>
          <div>
            <b>Роль:</b>{" "}
            <span style={{ padding: "2px 8px", borderRadius: 999, border: "1px solid #ddd", fontSize: 12 }}>
              {user.role}
            </span>
          </div>
          {isAdmin && <div style={{ color: "green" }}><b>Вы администратор</b></div>}
        </div>
      </div>

      <div style={{ border: "1px solid #ddd", borderRadius: 10, padding: 14 }}>
        <h3 style={{ marginTop: 0 }}>Быстрые действия</h3>
        <div style={{ display: "flex", flexWrap: "wrap", gap: 10 }}>
          <Link to="/vacancies">Вакансии</Link>
          <Link to="/candidates">Кандидаты</Link>
          <Link to="/reviews">Отзывы</Link>
          {isAdmin && <Link to="/decisions">Решения (ADMIN)</Link>}
        </div>
      </div>
    </div>
  );
}
