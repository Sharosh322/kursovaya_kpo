import React from "react";
import { NavLink, Outlet } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { Badge, Button } from "../ui/but";

function N({ to, children }) {
  return (
    <NavLink
      to={to}
      className={({ isActive }) => `navLink ${isActive ? "active" : ""}`}
    >
      {children}
    </NavLink>
  );
}

export default function Layout() {
  const { isAuthed, logout, user, isAdmin } = useAuth();

  return (
    <div className="appShell">
      <aside className="sidebar">
        {/* TOP */}
        <div className="sbTop">
          <div className="sbBrandRow">
            <div className="sbBrand">HR Review</div>

            {isAuthed && (
              <Badge
                testId="role-badge"
                kind={isAdmin ? "admin" : "user"}
              >
                {isAdmin ? "ADMIN" : "USER"}
              </Badge>
            )}
          </div>

          <div className="sbSub">
            {isAuthed ? "Панель управления" : "Вход в систему"}
          </div>
        </div>

        {/* PROFILE */}
        {isAuthed && (
          <div className="card sbProfile">
            <div className="cardInner">
              <div className="sbProfileGrid">
                <div className="sbProfileLeft">
                  <div className="sbName">{user?.fullName || "Пользователь"}</div>
                  <div className="sbEmail">{user?.email || ""}</div>
                </div>

                <div className="sbProfileRight">
                  <div className="sbHint">Роль</div>
                  <div className="sbRole">
                    {isAdmin ? "Режим администратора" : "Пользователь"}
                  </div>
                </div>
              </div>

              <div className="sbProfileLinkRow">
                <NavLink className="sbProfileLink" to="/profile">
                  Профиль →
                </NavLink>
              </div>
            </div>
          </div>
        )}

        {/* NAV */}
        <nav className="nav">
          {!isAuthed ? (
            <>
              <div className="navTitle">Аккаунт</div>
              <N to="/login">Вход</N>
              <N to="/register">Регистрация</N>
            </>
          ) : (
            <>
              <div className="navTitle">Разделы</div>

              <N to="/profile">Личный кабинет</N>
              <N to="/vacancies">Вакансии</N>
              <N to="/candidates">Все кандидаты</N>
              <N to="/reviews">Отзывы</N>

              {isAdmin ? <N to="/decisions">Решения</N> : null}
              {isAdmin ? <N to="/assign">Назначения</N> : null}

              <div className="spacer12" />

              <Button testId="logout-btn" variant="danger" onClick={logout}>
                Выйти
              </Button>
            </>
          )}
        </nav>
      </aside>

      <main className="main">
        <Outlet />
      </main>
    </div>
  );
}
