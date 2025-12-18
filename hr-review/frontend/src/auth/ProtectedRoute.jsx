import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "./AuthContext";

export default function ProtectedRoute({ children }) {
  const { isAuthed, loadingMe } = useAuth();

  if (loadingMe) return <div>Loading...</div>;
  if (!isAuthed) return <Navigate to="/login" replace />;

  return children;
}
