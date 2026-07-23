import type { FC } from "react";

export const TopBar: FC = () => {
  const today = new Date().toLocaleDateString("en-US", {
    weekday: "long",
    year: "numeric",
    month: "long",
    day: "numeric",
  });

  return (
    <div className="topbar">
      <div className="topbar-left">
        <div className="tab active">Smart Budget</div>
      </div>

      <div className="topbar-date">{today}</div>

      <div className="topbar-right">
        <div className="user-info">
          <p className="user-name">Demo User</p>
          <p className="user-role">Personal finance</p>
        </div>
        <div className="avatar" />
      </div>
    </div>
  );
};
