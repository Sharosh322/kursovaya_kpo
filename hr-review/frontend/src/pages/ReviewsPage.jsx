import React, { useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api/api";
import { Card, Button, Input, Textarea, Alert } from "../ui/but";

export default function ReviewsPage() {
  const [candidateId, setCandidateId] = useState("");
  const [candidate, setCandidate] = useState(null);

  const [text, setText] = useState("");
  const [author, setAuthor] = useState("");

  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");

  const parseId = () => {
    const id = Number(candidateId);
    return id && id > 0 ? id : null;
  };

  const loadCandidate = async () => {
    setErr("");
    setMsg("");
    setCandidate(null);

    const id = parseId();
    if (!id) {
      setErr("Введите корректный ID кандидата.");
      return;
    }

    setLoading(true);
    try {
      const res = await api.get(`/api/candidates/${id}`);
      setCandidate(res.data);
    } catch (e) {
      setErr(e?.response?.data?.message || "Не удалось загрузить кандидата.");
    } finally {
      setLoading(false);
    }
  };

  const addReview = async (e) => {
    e.preventDefault();
    setErr("");
    setMsg("");

    const id = parseId();
    if (!id) {
      setErr("Введите корректный ID кандидата.");
      return;
    }
    if (!text.trim()) {
      setErr("Текст отзыва не должен быть пустым.");
      return;
    }

    setLoading(true);
    try {
      await api.post(
        `/api/candidates/${id}/reviews`,
        { text: text.trim() },
        author.trim() ? { headers: { "X-Author": author.trim() } } : undefined
      );

      setText("");
      setAuthor("");

      const res = await api.get(`/api/candidates/${id}`);
      setCandidate(res.data);

      setMsg("Отзыв добавлен ✅");
    } catch (e) {
      setErr(e?.response?.data?.message || "Не удалось добавить отзыв.");
    } finally {
      setLoading(false);
    }
  };

  const reviews = candidate?.reviews ?? [];

  return (
    <div className="page">
      <Card
        title="Отзывы"
        sub="Загрузи кандидата по ID и добавляй отзывы"
        right={candidate?.id ? <Link to={`/candidates/${candidate.id}`}>Открыть кандидата →</Link> : null}
      >
        <div className="row" style={{ marginTop: 8 }}>
          <div style={{ flex: 1, minWidth: 220 }}>
            <Input
              placeholder="ID кандидата (например 1)"
              value={candidateId}
              onChange={(e) => setCandidateId(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") loadCandidate();
              }}
            />
          </div>

          <Button variant="primary" onClick={loadCandidate} disabled={loading}>
            {loading ? "Загрузка..." : "Загрузить"}
          </Button>
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
              sub={`${candidate.name} • ${candidate.status}`}
              right={<span className="muted">{candidate.email || "—"}</span>}
            >
              <div className="row">
                <div className="muted">Телефон: {candidate.phone || "—"}</div>
              </div>
            </Card>

            <div className="spacer" />

            <Card title="Добавить отзыв" sub="Можно указать автора (необязательно)">
              <form onSubmit={addReview} style={{ display: "grid", gap: 10 }}>
                <label>
                  Автор (опционально)
                  <Input
                    placeholder="author@mail.ru"
                    value={author}
                    onChange={(e) => setAuthor(e.target.value)}
                  />
                </label>

                <label>
                  Текст отзыва
                  <Textarea
                    rows={4}
                    placeholder="Напиши отзыв..."
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                  />
                </label>

                <div className="row">
                  <Button variant="primary" type="submit" disabled={loading}>
                    Добавить отзыв
                  </Button>
                </div>
              </form>
            </Card>

            <div className="spacer" />

            <Card title="Список отзывов" sub={reviews.length ? `Всего: ${reviews.length}` : "Пока отзывов нет"}>
              {reviews.length === 0 ? (
                <div className="muted">Нет отзывов.</div>
              ) : (
                <table className="table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Автор</th>
                      <th>Текст</th>
                    </tr>
                  </thead>
                  <tbody>
                    {reviews.map((r) => (
                      <tr key={r.id}>
                        <td>{r.id}</td>
                        <td>{r.author || "—"}</td>
                        <td>{r.text}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </Card>
          </>
        )}
      </Card>
    </div>
  );
}
