import React, { useEffect, useMemo, useState } from "react";
import { api } from "../api/api";
import { Link } from "react-router-dom";
import { Card, Input, Select, Button, Alert, Badge } from "../ui/but";
import { useAuth } from "../auth/AuthContext";

const STATUSES = ["NEW", "INTERVIEW", "OFFER", "REJECTED", "HIRED"];

export default function CandidatesListPage() {
  const { isAdmin, isAuthed, loadingMe } = useAuth();

  const [items, setItems] = useState([]);
  const [err, setErr] = useState("");

  const [statusFilter, setStatusFilter] = useState("ALL");
  const [vacancyQ, setVacancyQ] = useState("");

  const load = async () => {
    setErr("");
    const url = isAdmin ? "/api/candidates" : "/api/candidates/my";
    const res = await api.get(url);
    setItems(res.data || []);
  };

  useEffect(() => {
    // ✅ ждём пока /api/auth/me завершится
    if (loadingMe) return;

    // ✅ если не авторизован — ничего не грузим (ProtectedRoute обычно не пустит сюда, но на всякий случай)
    if (!isAuthed) return;

    load().catch((e) => {
      const code = e?.response?.status;
      if (code === 401) setErr("Сессия не активна. Перезайди в аккаунт.");
      else setErr("Не удалось загрузить кандидатов");
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [loadingMe, isAuthed, isAdmin]);

  const filtered = useMemo(() => {
    const qq = vacancyQ.trim().toLowerCase();
    return (items || []).filter((c) => {
      const okS = statusFilter === "ALL" || c.status === statusFilter;
      const vt = (c.vacancyTitle || "").toLowerCase();
      const okV = !qq || vt.includes(qq);
      return okS && okV;
    });
  }, [items, statusFilter, vacancyQ]);

  return (
    <div className="page">
      <div className="pageHeader">
        <div>
          <div className="pageTitle">{isAdmin ? "Все кандидаты" : "Мои кандидаты"}</div>
          <div className="pageSub">
            {isAdmin
              ? "Список всех кандидатов в системе."
              : "Кандидаты, назначенные вам как интервьюеру."}
          </div>
        </div>
        <div className="rowRight">
          <span className="muted">
            Показано: <b>{filtered.length}</b>
          </span>
        </div>
      </div>

      <Card title="Фильтры" sub="Фильтр по статусу и поиск по вакансии">
        <div className="row">
          <div style={{ width: 220 }}>
            <div className="label">Статус</div>
            <Select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
              <option value="ALL">Все</option>
              {STATUSES.map((s) => (
                <option key={s} value={s}>{s}</option>
              ))}
            </Select>
          </div>

          <div style={{ flex: 1, minWidth: 240 }}>
            <div className="label">Поиск по вакансии</div>
            <Input
              value={vacancyQ}
              onChange={(e) => setVacancyQ(e.target.value)}
              placeholder="например: Data Scientist"
            />
          </div>

          <Button
            variant="ghost"
            onClick={() => {
              setStatusFilter("ALL");
              setVacancyQ("");
            }}
          >
            Сбросить
          </Button>
        </div>

        <div className="spacer" />
        {err && <Alert type="error">{err}</Alert>}
      </Card>

      <Card title="Список" sub="Открывай карточку кандидата и связанную вакансию">
        <div className="tableWrap">
          <table className="table">
            <thead>
              <tr>
                <th style={{ width: 70 }}>ID</th>
                <th>Имя</th>
                <th style={{ width: 160 }}>Статус</th>
                <th>Вакансия</th>
                <th style={{ width: 110 }} />
              </tr>
            </thead>
            <tbody>
              {filtered.map((c) => (
                <tr key={c.id}>
                  <td>{c.id}</td>
                  <td style={{ fontWeight: 800 }}>{c.name}</td>
                  <td>
                    <Badge kind="user">{c.status}</Badge>
                  </td>
                  <td>
                    {c.vacancyId ? (
                      <Link to={`/vacancies/${c.vacancyId}/candidates`}>
                        {c.vacancyTitle || `Вакансия #${c.vacancyId}`}
                      </Link>
                    ) : (
                      <span className="muted">—</span>
                    )}
                  </td>
                  <td>
                    <Link to={`/candidates/${c.id}`}>Открыть</Link>
                  </td>
                </tr>
              ))}
              {filtered.length === 0 && (
                <tr>
                  <td colSpan={5} className="muted">
                    Нет кандидатов по выбранным фильтрам.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
}
