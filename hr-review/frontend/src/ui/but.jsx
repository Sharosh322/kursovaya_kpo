import React from "react";

export function Card({ title, sub, right, children, className = "" }) {
  return (
    <div className={`card ${className}`}>
      <div className="cardInner">
        {(title || right) && (
          <div className="cardHeaderRow">
            <div>
              {title && <div className="cardTitle">{title}</div>}
              {sub && <div className="cardSub">{sub}</div>}
            </div>
            {right}
          </div>
        )}
        {children}
      </div>
    </div>
  );
}

export function Button({ variant = "default", className = "", ...props }) {
  const cls =
    variant === "primary"
      ? "btn btnPrimary"
      : variant === "danger"
      ? "btn btnDanger"
      : variant === "ghost"
      ? "btn btnGhost"
      : "btn";
  return <button className={`${cls} ${className}`} {...props} />;
}

export function Input({ className = "", ...props }) {
  return <input className={`input ${className}`} {...props} />;
}

export function Select({ className = "", ...props }) {
  return <select className={`select ${className}`} {...props} />;
}

export function Textarea({ className = "", ...props }) {
  return <textarea className={`textarea ${className}`} {...props} />;
}

export function Badge({ kind = "user", children }) {
  const cls = kind === "admin" ? "badge badgeAdmin" : "badge badgeUser";
  return <span className={cls}>{children}</span>;
}

export function Alert({ type = "info", children }) {
  const cls =
    type === "error" ? "alert alertErr" : type === "ok" ? "alert alertOk" : "alert";
  return <div className={cls}>{children}</div>;
}
