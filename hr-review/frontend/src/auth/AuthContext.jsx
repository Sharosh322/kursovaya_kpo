import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import { api } from "../api/api";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loadingMe, setLoadingMe] = useState(true);

  const refreshMe = async () => {
    try {
      const res = await api.get("/api/auth/me");
      setUser(res.data);
    } catch {
      setUser(null);
    } finally {
      setLoadingMe(false);
    }
  };

  useEffect(() => {
    refreshMe();
  }, []);

  const login = async (email, password) => {
    await api.post("/api/auth/login", { email, password });
    await refreshMe();
  };

  const logout = async () => {
    await api.post("/api/auth/logout");
    setUser(null);
  };

  const isAuthed = !!user;
  const isAdmin = user?.role === "ADMIN";

  const value = useMemo(
    () => ({ user, isAuthed, isAdmin, login, logout, refreshMe, loadingMe }),
    [user, isAuthed, isAdmin, loadingMe]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
