import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { api } from "../api/api";
import { Card, Button, Input, Select, Alert } from "../ui/but";

export default function RegisterPage() {
  const nav = useNavigate();

  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("ADMIN"); // по умолчанию как у тебя

  const [err, setErr] = useState("");
  const [ok, setOk] = useState("");
  const [loading, setLoading] = useState(false);

  const submit = async (e) => {
    e.preventDefault();
    setErr("");
    setOk("");

    if (!fullName.trim() || !email.trim() || !password) {
      setErr("Заполни: ФИО, Email и пароль.");
      return;
    }

    setLoading(true);
    try {
      await api.post("/api/auth/register", {
        fullName: fullName.trim(),
        email: email.trim(),
        password,
        role,
      });

      setOk("Аккаунт создан ✅ Перенаправляю на вход...");
      setTimeout(() => nav("/login"), 700);
    } catch (e2) {
      setErr(e2?.response?.data?.message || "Не удалось зарегистрироваться.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <Card
        title="Регистрация"
        sub="Создай аккаунт для работы с системой"
        right={
          <Link className="muted" to="/login">
            Уже есть аккаунт?
          </Link>
        }
        className="page"
      >
        {err && <Alert type="error">{err}</Alert>}
        {ok && <Alert type="ok">{ok}</Alert>}

        <form onSubmit={submit} style={{ display: "grid", gap: 10, marginTop: 12 }}>
          <label>
            ФИО
            <Input
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              placeholder="Иванов Иван"
              autoComplete="name"
            />
          </label>

          <label>
            Email
            <Input
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="ivanov@mail.ru"
              autoComplete="email"
            />
          </label>

          <label>
            Пароль
            <Input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              autoComplete="new-password"
            />
          </label>

          <label>
            Роль
            <Select value={role} onChange={(e) => setRole(e.target.value)}>
              <option value="ADMIN">ADMIN</option>
              <option value="USER">USER</option>
            </Select>
          </label>

          <div className="row">
            <Button variant="primary" type="submit" disabled={loading}>
              {loading ? "Создаю..." : "Создать аккаунт"}
            </Button>
            <Link className="muted" to="/login">
              Войти →
            </Link>
          </div>
        </form>
      </Card>
    </div>
  );
}
