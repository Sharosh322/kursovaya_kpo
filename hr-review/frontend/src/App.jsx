import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./auth/AuthContext";
import ProtectedRoute from "./auth/ProtectedRoute";

import Layout from "./ui/Layout";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";

import VacanciesPage from "./pages/VacanciesPage";
import CandidatesPage from "./pages/CandidatesPage";
import CandidatesListPage from "./pages/CandidatesListPage";
import ReviewsPage from "./pages/ReviewsPage";
import DecisionsPage from "./pages/DecisionsPage";
import CandidateDetailsPage from "./pages/CandidateDetailsPage";
import ProfilePage from "./pages/ProfilePage";
import AssignInterviewerPage from "./pages/AssignInterviewerPage";


// только для ADMIN
function AdminOnly({ children }) {
  const { isAdmin } = useAuth();
  if (!isAdmin) return <Navigate to="/vacancies" replace />;
  return children;
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route element={<Layout />}>
            <Route path="/" element={<Navigate to="/vacancies" replace />} />

            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            <Route
              path="/vacancies"
              element={
                <ProtectedRoute>
                  <VacanciesPage />
                </ProtectedRoute>
              }
            />
            <Route
              path="/assign"
              element={
                <ProtectedRoute>
                  <AdminOnly>
                   <AssignInterviewerPage />
                  </AdminOnly>
                </ProtectedRoute>
               }
/>

            <Route
              path="/profile"
              element={
                <ProtectedRoute>
                  <ProfilePage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/vacancies/:vacancyId/candidates"
              element={
                <ProtectedRoute>
                  <CandidatesPage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/candidates"
              element={
                <ProtectedRoute>
                  <CandidatesListPage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/candidates/:candidateId"
              element={
                <ProtectedRoute>
                  <CandidateDetailsPage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/reviews"
              element={
                <ProtectedRoute>
                  <ReviewsPage />
                </ProtectedRoute>
              }
            />

            {/* Decisions только ADMIN */}
            <Route
              path="/decisions"
              element={
                <ProtectedRoute>
                  <AdminOnly>
                    <DecisionsPage />
                  </AdminOnly>
                </ProtectedRoute>
              }
            />
          </Route>
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
