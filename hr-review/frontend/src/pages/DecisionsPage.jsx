import React, { useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api/api";
import { Card, Button, Input, Select, Alert } from "../ui/but";

const STATUSES = ["NEW", "INTERVIEW", "OFFER", "REJECTED", "HIRED"];

export default function DecisionsPage() {
  const [candidateId, setCandidateId] = useState("");
  const [candidate, setCandidate] = useState(null);

  const [newStatus, setNewStatus] = useState("INTERVIEW");

  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");

  const loadCandidate = async () => {
    setErr("");
    setMsg("");
    setCandidate(null);

    const id = Number(candidateId);
    if (!id || id <= 0) {
      setErr("Введите корректный ID кандидата.");
      return;
    }

    setLoading(true);
    try {
      const res = await api.get(`/api/candidates/${id}`);
      setCandidate(res.data);
      if (res.data?.status && STATUSES.includes(res.data.status)) setNewStatus(res.data.status);
    } catch (e) {
      setErr(e?.response?.data?.message || "Не удалось загрузить кандидата.");
    } finally {
      setLoading(false);
    }
  };

  const updateStatus = async () => {
    setErr("");
    setMsg("");

    const id = Number(candidateId);
    if (!id || id <= 0) {
      setErr("Введите корректный ID кандидата.");
      return;
    }

    setLoading(true);
    try {
      await api.patch(`/api/candidates/${id}/status`, { status: newStatus });
      const res = await api.get(`/api/candidates/${id}`);
      setCandidate(res.data);
      setMsg("Статус обновлён ✅");
    } catch (e) {
      setErr(e?.response?.data?.message || "Не удалось обновить статус.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <Card title="Решения" sub="Измени статус кандидата (доступно ADMIN)">
        <div className="row" style={{ marginTop: 8 }}>
          <div style={{ flex: 1, minWidth: 220 }}>
            <Input
              placeholder="ID кандидата (например 1)"
              value={candidateId}
              onChange={(e) => setCandidateId(e.target.value)}
            />
          </div>

          <Button variant="primary" onClick={loadCandidate} disabled={loading}>
            {loading ? "Загрузка..." : "Загрузить"}
          </Button>

          {candidate?.id && (
            <Link className="muted" to={`/candidates/${candidate.id}`}>
              Открыть кандидата →
            </Link>
          )}
        </div>

        <div className="spacer" />
        {err && <Alert type="error">{err}</Alert>}
        {msg && <Alert type="ok">{msg}</Alert>}

        {!candidate && !loading && (
          <div className="muted" style={{ marginTop: 10 }}>
            Введите <b>ID кандидата</b> и нажмите <b>Загрузить</b>.
          </div>
        )}

        {candidate && (
          <>
            <div className="spacer" />

            <Card
              title={`Кандидат #${candidate.id}`}
              sub={`${candidate.name} • текущий статус: ${candidate.status}`}
            >
              <div className="row">
                <div style={{ flex: 1, minWidth: 220 }}>
                  <Select value={newStatus} onChange={(e) => setNewStatus(e.target.value)}>
                    {STATUSES.map((s) => (
                      <option key={s} value={s}>
                        {s}
                      </option>
                    ))}
                  </Select>
                </div>

                <Button variant="primary" onClick={updateStatus} disabled={loading}>
                  Обновить статус
                </Button>
              </div>
            </Card>
          </>
        )}
      </Card>
    </div>
  );
}
