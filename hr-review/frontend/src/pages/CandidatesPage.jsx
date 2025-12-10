import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { api } from "../api/apiClient";

export function CandidatesPage() {
  const { vacancyId } = useParams();
  const [candidates, setCandidates] = useState([]);
  const [name, setName] = useState("");

  useEffect(() => {
    api
      .get(`/vacancies/${vacancyId}/candidates`)
      .then((res) => setCandidates(res.data));
  }, [vacancyId]);

  const createCandidate = async () => {
    if (!name.trim()) return;

    const res = await api.post(`/vacancies/${vacancyId}/candidates`, {
      name,
      email: "",
      phone: "",
      status: "Отклик получен",
    });

    setCandidates([...candidates, res.data]);
    setName("");
  };

  return (
    <div style={{ padding: "1.5rem" }}>
      <h2>Кандидаты по вакансии #{vacancyId}</h2>

      <div
        style={{
          marginBottom: "1.5rem",
          padding: "1rem",
          border: "1px solid #ddd",
          borderRadius: "6px",
        }}
      >
        <h3>Добавить кандидата</h3>
        <input
          placeholder="Имя кандидата"
          value={name}
          onChange={(e) => setName(e.target.value)}
          style={{ padding: "0.5rem", marginRight: "0.5rem", minWidth: "240px" }}
        />
        <button onClick={createCandidate} style={{ padding: "0.5rem 1rem" }}>
          Добавить
        </button>
      </div>

      <h3>Список кандидатов</h3>
      {candidates.length === 0 && <div>Пока нет кандидатов.</div>}

      <ul style={{ listStyle: "none", padding: 0 }}>
        {candidates.map((c) => (
          <li
            key={c.id}
            style={{
              border: "1px solid #ddd",
              borderRadius: "6px",
              padding: "0.75rem 1rem",
              marginBottom: "0.75rem",
            }}
          >
            <div>
              <strong>#{c.id}</strong> — {c.name}
            </div>
            <div style={{ fontSize: "0.9rem", color: "#555" }}>
              Статус: {c.status}
            </div>
            <Link to={`/candidates/${c.id}`}>Открыть карточку</Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
