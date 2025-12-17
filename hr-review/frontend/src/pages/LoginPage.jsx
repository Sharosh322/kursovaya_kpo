import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { Card, Button, Input, Alert } from "../ui/but";

export default function LoginPage() {
  const nav = useNavigate();
  const { login } = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);

  const onSubmit = async (e) => {
    e.preventDefault();
    setErr("");

    if (!email.trim() || !password) {
      setErr("Введите email и пароль.");
      return;
    }

    setLoading(true);
    try {
      await login(email.trim(), password);
      nav("/vacancies");
    } catch (e2) {
      setErr(e2?.response?.data?.message || "Не удалось войти.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <Card
        title="Вход"
        sub="Введите email и пароль, чтобы продолжить"
        right={
          <Link className="muted" to="/register">
            Нет аккаунта?
          </Link>
        }
        className="page"
      >
        {err && <Alert type="error">{err}</Alert>}

        <form onSubmit={onSubmit} style={{ display: "grid", gap: 10, marginTop: 12 }}>
          <label>
            Email
            <Input
              placeholder="user@mail.ru"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              autoComplete="email"
            />
          </label>

          <label>
            Пароль
            <Input
              placeholder="••••••••"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
            />
          </label>

          <div className="row">
            <Button variant="primary" type="submit" disabled={loading}>
              {loading ? "Входим..." : "Войти"}
            </Button>
            <Link className="muted" to="/register">
              Регистрация →
            </Link>
          </div>
        </form>
      </Card>
    </div>
  );
}
