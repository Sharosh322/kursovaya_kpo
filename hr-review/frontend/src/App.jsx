import { BrowserRouter, Routes, Route } from "react-router-dom";
import { VacanciesPage } from "./pages/VacanciesPage";
import { CandidatesPage } from "./pages/CandidatesPage";
import { CandidateDetailsPage } from "./pages/CandidateDetailsPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<VacanciesPage />} />
        <Route path="/vacancies/:vacancyId" element={<CandidatesPage />} />
        <Route path="/candidates/:candidateId" element={<CandidateDetailsPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
