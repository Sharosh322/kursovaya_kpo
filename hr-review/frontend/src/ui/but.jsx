import React from "react";

export function Card({ title, sub, right, children, className = "", ...props }) {
  return (
    <div className={`card ${className}`} {...props}>
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

export function Button({ variant = "default", className = "", testId, ...props }) {
  const cls =
    variant === "primary"
      ? "btn btnPrimary"
      : variant === "danger"
      ? "btn btnDanger"
      : variant === "ghost"
      ? "btn btnGhost"
      : "btn";
  return <button data-testid={testId} className={`${cls} ${className}`} {...props} />;
}

export function Input({ className = "", testId, ...props }) {
  return <input data-testid={testId} className={`input ${className}`} {...props} />;
}

export function Select({ className = "", testId, ...props }) {
  return <select data-testid={testId} className={`select ${className}`} {...props} />;
}

export function Textarea({ className = "", testId, ...props }) {
  return <textarea data-testid={testId} className={`textarea ${className}`} {...props} />;
}

export function Badge({ kind = "user", children, testId }) {
  const cls = kind === "admin" ? "badge badgeAdmin" : "badge badgeUser";
  return <span data-testid={testId} className={cls}>{children}</span>;
}

export function Alert({ type = "info", children, testId }) {
  const cls =
    type === "error" ? "alert alertErr" : type === "ok" ? "alert alertOk" : "alert";
  return <div data-testid={testId} className={cls}>{children}</div>;
}
