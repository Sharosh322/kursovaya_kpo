import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { api } from "../api/apiClient";

export function VacanciesPage() {
  const [vacancies, setVacancies] = useState([]);
  const [title, setTitle] = useState("");
  const [status, setStatus] = useState("Открыта");

  useEffect(() => {
    api.get("/vacancies").then((res) => setVacancies(res.data));
  }, []);

  const createVacancy = async () => {
    if (!title.trim()) return;
    const res = await api.post("/vacancies", { title, status });
    setVacancies([...vacancies, res.data]);
    setTitle("");
  };

  return (
    <div style={{ padding: "1.5rem" }}>
      <h1>Вакансии</h1>

      <div
        style={{
          marginBottom: "1.5rem",
          padding: "1rem",
          border: "1px solid #ddd",
          borderRadius: "6px",
        }}
      >
        <h3>Создать вакансию</h3>
        <input
          placeholder="Название вакансии"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          style={{ padding: "0.5rem", marginRight: "0.5rem", minWidth: "240px" }}
        />

        <select
          value={status}
          onChange={(e) => setStatus(e.target.value)}
          style={{ padding: "0.5rem", marginRight: "0.5rem" }}
        >
          <option value="Открыта">Открыта</option>
          <option value="Закрыта">Закрыта</option>
          <option value="На паузе">На паузе</option>
        </select>

        <button onClick={createVacancy} style={{ padding: "0.5rem 1rem" }}>
          Создать
        </button>
      </div>

      <h3>Список вакансий</h3>
      {vacancies.length === 0 && <div>Пока нет вакансий.</div>}

      <ul style={{ listStyle: "none", padding: 0 }}>
        {vacancies.map((v) => (
          <li
            key={v.id}
            style={{
              border: "1px solid #ddd",
              borderRadius: "6px",
              padding: "0.75rem 1rem",
              marginBottom: "0.75rem",
            }}
          >
            <div>
              <strong>#{v.id}</strong> — {v.title}
            </div>
            <div style={{ fontSize: "0.9rem", color: "#555" }}>Статус: {v.status}</div>
            <Link to={`/vacancies/${v.id}`}>Перейти к кандидатам</Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
