import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { api } from "../api/apiClient";

export function CandidateDetailsPage() {
  const { candidateId } = useParams();
  const [candidate, setCandidate] = useState(null);
  const [status, setStatus] = useState("");
  const [reviewText, setReviewText] = useState("");
  const [loading, setLoading] = useState(true);

  const loadCandidate = async () => {
    try {
      const res = await api.get(`/candidates/${candidateId}`);
      setCandidate(res.data);
      setStatus(res.data.status || "");
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCandidate();
  }, [candidateId]);

  const handleStatusChange = async (e) => {
    const newStatus = e.target.value;
    setStatus(newStatus);

    await api.patch(`/candidates/${candidateId}/status`, {
      status: newStatus,
    });

    await loadCandidate();
  };

  const handleAddReview = async (e) => {
    e.preventDefault();
    if (!reviewText.trim()) return;

    await api.post(`/candidates/${candidateId}/reviews`, {
      text: reviewText.trim(),
    });

    setReviewText("");
    await loadCandidate();
  };

  if (loading) return <div style={{ padding: "1.5rem" }}>Загрузка...</div>;
  if (!candidate) return <div style={{ padding: "1.5rem" }}>Кандидат не найден</div>;

  return (
    <div style={{ padding: "1.5rem", maxWidth: "800px", margin: "0 auto" }}>
      <Link to={-1} style={{ display: "inline-block", marginBottom: "1rem" }}>
        ← Назад
      </Link>

      <h2>Кандидат #{candidate.id}</h2>

      <p>
        <strong>Имя:</strong> {candidate.name}
      </p>
      <p>
        <strong>Email:</strong> {candidate.email || "—"}
      </p>
      <p>
        <strong>Телефон:</strong> {candidate.phone || "—"}
      </p>

      <p>
        <strong>Статус:</strong>{" "}
        <select value={status} onChange={handleStatusChange}>
          <option value="Отклик получен">Отклик получен</option>
          <option value="Интервью назначено">Интервью назначено</option>
          <option value="На рассмотрении">На рассмотрении</option>
          <option value="Отказ">Отказ</option>
          <option value="Оффер сделан">Оффер сделан</option>
          <option value="Выход на работу">Выход на работу</option>
        </select>
      </p>

      <h3>Отзывы</h3>
      {candidate.reviews && candidate.reviews.length > 0 ? (
        <ul>
          {candidate.reviews.map((r) => (
            <li key={r.id} style={{ marginBottom: "0.5rem" }}>
              <strong>{r.author}:</strong> {r.text}
            </li>
          ))}
        </ul>
      ) : (
        <div>Пока нет отзывов.</div>
      )}

      <h4 style={{ marginTop: "1.5rem" }}>Добавить отзыв</h4>
      <form
        onSubmit={handleAddReview}
        style={{ display: "flex", flexDirection: "column", gap: "0.5rem" }}
      >
        <textarea
          rows={4}
          value={reviewText}
          onChange={(e) => setReviewText(e.target.value)}
          placeholder="Напишите обратную связь по кандидату..."
          style={{ padding: "0.5rem", resize: "vertical" }}
        />
        <button type="submit" style={{ alignSelf: "flex-start", padding: "0.5rem 1rem" }}>
          Сохранить отзыв
        </button>
      </form>
    </div>
  );
}
