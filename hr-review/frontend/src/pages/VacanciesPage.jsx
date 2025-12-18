import React, { useEffect, useMemo, useState } from "react";
import { api } from "../api/api";
import { Link } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { Card, Input, Select, Button, Alert } from "../ui/but";

export default function VacanciesPage() {
  const { isAdmin } = useAuth();

  const [items, setItems] = useState([]);
  const [title, setTitle] = useState("");
  const [status, setStatus] = useState("OPEN");
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");

  // фильтры
  const [q, setQ] = useState("");
  const [statusFilter, setStatusFilter] = useState("ALL");

  const load = async () => {
    const res = await api.get("/api/vacancies");
    setItems(res.data || []);
  };

  useEffect(() => {
    load().catch(console.error);
  }, []);

  const filtered = useMemo(() => {
    const qq = q.trim().toLowerCase();
    return (items || []).filter((v) => {
      const okQ = !qq || (v.title || "").toLowerCase().includes(qq);
      const okS = statusFilter === "ALL" || v.status === statusFilter;
      return okQ && okS;
    });
  }, [items, q, statusFilter]);

  const create = async (e) => {
    e.preventDefault();
    setErr("");
    setMsg("");

    if (!isAdmin) return setErr("Создавать вакансии может только ADMIN.");
    if (!title.trim()) return setErr("Введите название вакансии.");

    try {
      await api.post("/api/vacancies", { title: title.trim(), status });
      setTitle("");
      setStatus("OPEN");
      setMsg("Вакансия создана ✅");
      await load();
    } catch (e2) {
      if (e2?.response?.status === 403) setErr("Недостаточно прав. Только ADMIN.");
      else setErr(e2?.response?.data?.message || "Не удалось создать вакансию");
    }
  };

  return (
    <div style={{ display: "grid", gap: 14 }}>
      <div className="pageHeader">
        <h1 className="h1">Вакансии</h1>
        <span className="muted">Всего: {items.length}</span>
      </div>

      <Card title="Поиск и фильтр" sub="Найди вакансию по названию или статусу">
        <div className="row">
          <div className="field" style={{ flex: 1, minWidth: 220 }}>
            <div className="label">Поиск по названию</div>
            <Input
              testId="vacancy-search"
              value={q}
              onChange={(e) => setQ(e.target.value)}
              placeholder="например: Data Scientist"
            />
          </div>

          <div className="field" style={{ width: 220 }}>
            <div className="label">Статус</div>
            <Select
              testId="vacancy-status-filter"
              value={statusFilter}
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option value="ALL">Все</option>
              <option value="OPEN">OPEN</option>
              <option value="CLOSED">CLOSED</option>
            </Select>
          </div>

          <Button
            testId="vacancy-reset"
            variant="ghost"
            onClick={() => {
              setQ("");
              setStatusFilter("ALL");
            }}
          >
            Сбросить
          </Button>
        </div>
      </Card>

      <Card title="Создание вакансии" sub={isAdmin ? "Доступно администратору" : "Создание доступно только ADMIN"}>
        {!isAdmin ? (
          <Alert type="error">
            Создавать вакансии может только <b>ADMIN</b>.
          </Alert>
        ) : (
          <form onSubmit={create} className="row" style={{ alignItems: "end" }}>
            <div className="field" style={{ flex: 1, minWidth: 240 }}>
              <div className="label">Название</div>
              <Input
                testId="vacancy-create-title"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                placeholder="Название вакансии"
              />
            </div>

            <div className="field" style={{ width: 220 }}>
              <div className="label">Статус</div>
              <Select
                testId="vacancy-create-status"
                value={status}
                onChange={(e) => setStatus(e.target.value)}
              >
                <option value="OPEN">OPEN</option>
                <option value="CLOSED">CLOSED</option>
              </Select>
            </div>

            <Button testId="vacancy-create-submit" variant="primary" type="submit">
              Создать
            </Button>
          </form>
        )}

        <div className="spacer12" />
        {err && <Alert type="error">{err}</Alert>}
        {msg && <Alert type="ok">{msg}</Alert>}
      </Card>

      <Card title="Список вакансий" sub={`Показано: ${filtered.length}`}>
        <div className="tableWrap card">
          <div className="cardInner" style={{ padding: 0 }}>
            <table className="table">
              <thead>
                <tr>
                  <th style={{ width: 70 }}>ID</th>
                  <th>Название</th>
                  <th style={{ width: 120 }}>Статус</th>
                  <th style={{ width: 140 }}>Кандидаты</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((v) => (
                  <tr key={v.id}>
                    <td>{v.id}</td>
                    <td style={{ fontWeight: 800 }}>{v.title}</td>
                    <td>
                      <span className="badge">{v.status}</span>
                    </td>
                    <td>
                      <Link to={`/vacancies/${v.id}/candidates`}>Открыть</Link>
                    </td>
                  </tr>
                ))}
                {filtered.length === 0 && (
                  <tr>
                    <td colSpan={4} className="muted">
                      Ничего не найдено.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </Card>
    </div>
  );
}
