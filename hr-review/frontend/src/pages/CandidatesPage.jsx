import React, { useEffect, useMemo, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { api } from "../api/api";
import { useAuth } from "../auth/AuthContext";
import { Card, Button, Input, Select, Alert } from "../ui/but";

const STATUSES = ["NEW", "INTERVIEW", "OFFER", "REJECTED", "HIRED"];

export default function CandidatesPage() {
  const { vacancyId } = useParams();
  const { user } = useAuth();

  const isAdmin = useMemo(() => {
    const r = user?.role;
    return r === "ADMIN" || r === "ROLE_ADMIN";
  }, [user]);

  const [items, setItems] = useState([]);
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");
  const [loading, setLoading] = useState(false);

  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [phone, setPhone] = useState("");
  const [status, setStatus] = useState("NEW");

  if (!vacancyId) {
    return (
      <div className="page">
        <Card title="Кандидаты" sub="Открой кандидатов через страницу вакансий.">
          <Alert type="error">vacancyId отсутствует в URL.</Alert>
          <div className="inlineRow">
            <Link to="/vacancies">← Назад к вакансиям</Link>
          </div>
        </Card>
      </div>
    );
  }

  const load = async () => {
    setErr("");
    setMsg("");
    setLoading(true);
    try {
      const res = await api.get(`/api/vacancies/${vacancyId}/candidates`);
      setItems(res.data || []);
    } catch (e) {
      setErr(e?.response?.data?.message || "Не удалось загрузить кандидатов");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load().catch(console.error);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [vacancyId]);

  const create = async (e) => {
    e.preventDefault();
    setErr("");
    setMsg("");

    if (!isAdmin) {
      setErr("Добавлять кандидатов может только ADMIN.");
      return;
    }
    if (!name.trim()) {
      setErr("Имя кандидата обязательно.");
      return;
    }

    try {
      await api.post(`/api/vacancies/${vacancyId}/candidates`, {
        name: name.trim(),
        email: email.trim(),
        phone: phone.trim(),
        status,
      });

      setName("");
      setEmail("");
      setPhone("");
      setStatus("NEW");
      setMsg("Кандидат добавлен ✅");
      await load();
    } catch (e2) {
      if (e2?.response?.status === 403) setErr("Недостаточно прав (только ADMIN).");
      else setErr(e2?.response?.data?.message || "Не удалось добавить кандидата");
    }
  };

  return (
    <div className="page">
      <div className="pageHeader">
        <div>
          <div className="pageTitle">Кандидаты по вакансии #{vacancyId}</div>
          <div className="pageSub">Добавляй и открывай карточки кандидатов.</div>
        </div>
        <div className="pageRight">
          <span className="muted">Показано: {items.length}</span>
          <Link to="/vacancies">← К вакансиям</Link>
        </div>
      </div>

      <Card title="Добавить кандидата" sub={isAdmin ? "Доступно администратору" : "Только ADMIN может добавлять кандидатов"}>
        {!isAdmin ? <Alert type="info">В этом режиме можно только просматривать список.</Alert> : null}

        <form onSubmit={create} className="formRow">
          <label>
            Имя
            <Input
              testId="cand-create-name"
              placeholder="Например: Иванов Иван"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </label>

          <label>
            Статус
            <Select testId="cand-create-status" value={status} onChange={(e) => setStatus(e.target.value)}>
              {STATUSES.map((s) => (
                <option key={s} value={s}>
                  {s}
                </option>
              ))}
            </Select>
          </label>

          <div className="inlineRow" style={{ justifyContent: "flex-end" }}>
            <Button testId="cand-create-submit" variant="primary" type="submit" disabled={!isAdmin}>
              Добавить
            </Button>
          </div>

          <div className="formRow2" style={{ gridColumn: "1 / -1" }}>
            <label>
              Email
              <Input placeholder="ivanov@mail.ru" value={email} onChange={(e) => setEmail(e.target.value)} />
            </label>
            <label>
              Телефон
              <Input placeholder="+7..." value={phone} onChange={(e) => setPhone(e.target.value)} />
            </label>
          </div>
        </form>

        {err && <Alert type="error">{err}</Alert>}
        {msg && <Alert type="ok">{msg}</Alert>}
      </Card>

      <Card title="Список кандидатов" sub={loading ? "Загрузка..." : `Показано: ${items.length}`}>
        {loading ? (
          <div className="muted">Загружаю список…</div>
        ) : items.length === 0 ? (
          <div className="muted">Кандидатов пока нет.</div>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th style={{ width: 90 }}>ID</th>
                <th>Имя</th>
                <th style={{ width: 180 }}>Статус</th>
                <th style={{ width: 140 }}></th>
              </tr>
            </thead>
            <tbody>
              {items.map((c) => (
                <tr key={c.id}>
                  <td>{c.id}</td>
                  <td style={{ fontWeight: 800 }}>{c.name}</td>
                  <td>
                    <span className="pill">{c.status}</span>
                  </td>
                  <td style={{ textAlign: "right" }}>
                    <Link to={`/candidates/${c.id}`}>Открыть</Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>
    </div>
  );
}
