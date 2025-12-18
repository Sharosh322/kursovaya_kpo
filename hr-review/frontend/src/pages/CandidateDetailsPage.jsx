import React, { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { api } from "../api/api";
import { useAuth } from "../auth/AuthContext";
import { Card, Button, Input, Select, Textarea, Alert, Badge } from "../ui/but";

const STATUSES = ["NEW", "INTERVIEW", "OFFER", "REJECTED", "HIRED"];

export default function CandidateDetailsPage() {
  const { candidateId } = useParams();
  const { isAdmin } = useAuth();

  const [data, setData] = useState(null);
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");

  const [newStatus, setNewStatus] = useState("NEW");
  const [reviewText, setReviewText] = useState("");
  const [author, setAuthor] = useState("");

  const load = async () => {
    const res = await api.get(`/api/candidates/${candidateId}`);
    setData(res.data);
    setNewStatus(res.data?.status || "NEW");
  };

  useEffect(() => {
    setErr("");
    setMsg("");
    load().catch((e) => {
      if (e?.response?.status === 403) setErr("Нет доступа к кандидату.");
      else setErr("Не удалось загрузить кандидата.");
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [candidateId]);

  const updateStatus = async () => {
    setErr("");
    setMsg("");
    try {
      await api.patch(`/api/candidates/${candidateId}/status`, { status: newStatus });
      setMsg("Статус обновлён ✅");
      await load();
    } catch (e) {
      if (e?.response?.status === 403) setErr("Менять статус может только ADMIN.");
      else setErr(e?.response?.data?.message || "Не удалось обновить статус");
    }
  };

  const addReview = async (e) => {
    e.preventDefault();
    setErr("");
    setMsg("");

    if (!reviewText.trim()) {
      setErr("Текст отзыва не должен быть пустым.");
      return;
    }

    try {
      await api.post(
        `/api/candidates/${candidateId}/reviews`,
        { text: reviewText.trim() },
        author.trim() ? { headers: { "X-Author": author.trim() } } : undefined
      );
      setReviewText("");
      setAuthor("");
      setMsg("Отзыв добавлен ✅");
      await load();
    } catch (e2) {
      setErr(e2?.response?.data?.message || "Не удалось добавить отзыв");
    }
  };

  if (!data) {
    return (
      <div className="page">
        <Card title="Кандидат" sub="Загрузка данных…">
          <div className="muted">Подождите…</div>
        </Card>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="pageHeader">
        <div>
          <div className="pageTitle">Кандидат #{data.id}</div>
          <div className="pageSub">Данные кандидата, статусы и отзывы.</div>
        </div>
        <div className="rowRight">
          <Link to="/candidates">← Назад</Link>
        </div>
      </div>

      {err && <Alert type="error">{err}</Alert>}
      {msg && <Alert type="ok">{msg}</Alert>}

      <Card title="Данные кандидата" sub="Основная информация">
        <div className="grid2">
          <div>
            <div className="muted">Имя</div>
            <div style={{ fontWeight: 800 }}>{data.name}</div>
          </div>
          <div>
            <div className="muted">Статус</div>
            <div>
              <Badge kind="user">{data.status}</Badge>
            </div>
          </div>
          <div>
            <div className="muted">Email</div>
            <div>{data.email || "—"}</div>
          </div>
          <div>
            <div className="muted">Телефон</div>
            <div>{data.phone || "—"}</div>
          </div>
          <div style={{ gridColumn: "1 / -1" }}>
            <div className="muted">Заметки</div>
            <div>{data.notes || "—"}</div>
          </div>
        </div>
      </Card>

      <Card title="Изменить статус" sub={isAdmin ? "Доступно администратору" : "Только ADMIN"}>
        {isAdmin ? (
          <div className="row">
            <div style={{ flex: 1, minWidth: 260 }}>
              <Select
                testId="cand-status-select"
                value={newStatus}
                onChange={(e) => setNewStatus(e.target.value)}
              >
                {STATUSES.map((s) => (
                  <option key={s} value={s}>
                    {s}
                  </option>
                ))}
              </Select>
            </div>
            <Button testId="cand-status-save" variant="primary" onClick={updateStatus}>
              Сохранить
            </Button>
          </div>
        ) : (
          <Alert type="info">
            Менять статус может только <b>ADMIN</b>.
          </Alert>
        )}
      </Card>

      <Card title="Отзывы" sub="Добавляй и смотри историю отзывов">
        <form onSubmit={addReview} className="form">
          <label>
            Автор (необязательно)
            <Input
              testId="review-author"
              placeholder="author@mail.ru"
              value={author}
              onChange={(e) => setAuthor(e.target.value)}
            />
          </label>

          <label>
            Текст отзыва
            <Textarea
              testId="review-text"
              placeholder="Напиши отзыв…"
              value={reviewText}
              onChange={(e) => setReviewText(e.target.value)}
              rows={4}
            />
          </label>

          <div className="rowRight">
            <Button testId="review-submit" variant="primary" type="submit" disabled={!reviewText.trim()}>
              Добавить отзыв
            </Button>
          </div>
        </form>

        <div className="spacer" />

        {!data.reviews || data.reviews.length === 0 ? (
          <div className="muted">Отзывов пока нет.</div>
        ) : (
          <table className="table">
            <thead>
              <tr>
                <th style={{ width: 90 }}>ID</th>
                <th style={{ width: 260 }}>Автор</th>
                <th>Текст</th>
              </tr>
            </thead>
            <tbody>
              {data.reviews.map((r) => (
                <tr key={r.id}>
                  <td>{r.id}</td>
                  <td style={{ fontWeight: 800 }}>{r.author || "Аноним"}</td>
                  <td>{r.text}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Card>
    </div>
  );
}
