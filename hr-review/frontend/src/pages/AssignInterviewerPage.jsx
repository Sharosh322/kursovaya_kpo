import React, { useEffect, useMemo, useState } from "react";
import { api } from "../api/api";
import { Card, Input, Select, Button, Alert } from "../ui/but";
import { Link } from "react-router-dom";

export default function AssignInterviewerPage() {
  const [candidateId, setCandidateId] = useState("");
  const [candidate, setCandidate] = useState(null);

  const [interviewers, setInterviewers] = useState([]);
  const [chosenEmail, setChosenEmail] = useState("");

  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");

  const parsedId = useMemo(() => {
    const n = Number(candidateId);
    return n && n > 0 ? n : null;
  }, [candidateId]);

  const loadInterviewers = async () => {
    const res = await api.get("/api/admin/interviewers");
    const list = res.data || [];
    setInterviewers(list);
    if (!chosenEmail && list.length) setChosenEmail(list[0].email);
  };

  useEffect(() => {
    loadInterviewers().catch(() => setErr("Не удалось загрузить интервьюеров"));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const loadCandidate = async () => {
    setErr("");
    setMsg("");
    setCandidate(null);

    if (!parsedId) {
      setErr("Введите корректный ID кандидата");
      return;
    }

    setLoading(true);
    try {
      const res = await api.get(`/api/candidates/${parsedId}`);
      setCandidate(res.data);

      // если уже назначен — поставим в select
      const assignedEmail = res.data?.assignedInterviewerEmail;
      if (assignedEmail) setChosenEmail(assignedEmail);
    } catch (e) {
      if (e?.response?.status === 403) setErr("Нет прав (403). Эта страница только для ADMIN.");
      else setErr(e?.response?.data?.message || "Не удалось загрузить кандидата");
    } finally {
      setLoading(false);
    }
  };

  const assign = async () => {
    setErr("");
    setMsg("");

    if (!parsedId) {
      setErr("Введите корректный ID кандидата");
      return;
    }
    if (!chosenEmail) {
      setErr("Выберите интервьюера");
      return;
    }

    setLoading(true);
    try {
      await api.post(`/api/candidates/${parsedId}/assign`, { interviewerEmail: chosenEmail });
      setMsg("Интервьюер назначен ✅");
      await loadCandidate();
    } catch (e) {
      if (e?.response?.status === 403) setErr("Нет прав (403). Назначать может только ADMIN.");
      else setErr(e?.response?.data?.message || "Не удалось назначить интервьюера");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page" style={{ display: "grid", gap: 14 }}>
      <div className="pageHeader">
        <div>
          <div className="pageTitle">Назначение интервьюера</div>
          <div className="pageSub">Админ назначает кандидата пользователю (USER).</div>
        </div>
      </div>

      {err && <Alert type="error">{err}</Alert>}
      {msg && <Alert type="ok">{msg}</Alert>}

      <Card title="1) Найти кандидата" sub="Введите ID кандидата и загрузите карточку">
        <div className="row">
          <div className="field" style={{ width: 260 }}>
            <div className="label">ID кандидата</div>
            <Input
              value={candidateId}
              onChange={(e) => setCandidateId(e.target.value)}
              placeholder="например: 1"
            />
          </div>

          <Button variant="primary" onClick={loadCandidate} disabled={loading}>
            Загрузить
          </Button>

          {candidate?.id ? (
            <Link style={{ marginLeft: 8 }} to={`/candidates/${candidate.id}`}>
              Открыть кандидата →
            </Link>
          ) : null}
        </div>
      </Card>

      <Card title="2) Назначить интервьюера" sub="Выберите пользователя и примените назначение">
        <div className="row">
          <div className="field" style={{ minWidth: 320, flex: 1 }}>
            <div className="label">Интервьюер (USER)</div>
            <Select value={chosenEmail} onChange={(e) => setChosenEmail(e.target.value)}>
              {interviewers.map((u) => (
                <option key={u.id} value={u.email}>
                  {u.fullName ? `${u.fullName} — ` : ""}{u.email}
                </option>
              ))}
            </Select>
          </div>

          <Button variant="primary" onClick={assign} disabled={loading || !candidate}>
            Назначить
          </Button>
        </div>

        <div className="spacer12" />

        {!candidate ? (
          <div className="muted">Сначала загрузите кандидата.</div>
        ) : (
          <div className="card" style={{ marginTop: 6 }}>
            <div className="cardInner">
              <div style={{ display: "grid", gap: 6 }}>
                <div><b>ID:</b> {candidate.id}</div>
                <div><b>Имя:</b> {candidate.name}</div>
                <div><b>Статус:</b> <span className="badge">{candidate.status}</span></div>
                <div>
                  <b>Назначен:</b>{" "}
                  {candidate.assignedInterviewerEmail
                    ? `${candidate.assignedInterviewerFullName || ""} ${candidate.assignedInterviewerEmail}`.trim()
                    : <span className="muted">пока не назначен</span>}
                </div>
              </div>
            </div>
          </div>
        )}
      </Card>
    </div>
  );
}
