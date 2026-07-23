import type { FC } from "react";
import { NavLink } from "react-router-dom";
import { BarChart3, LayoutDashboard, Plus } from "lucide-react";

export const Sidebar: FC = () => {
  return (
    <div className="sidebar">
      <div className="logo">SB</div>

      <div className="menu">
        <NavLink
          to="/"
          className={({ isActive }) => `menu-item ${isActive ? "active" : ""}`}
          title="Dashboard"
        >
          <LayoutDashboard size={18} />
        </NavLink>

        <NavLink
          to="/reports"
          className={({ isActive }) => `menu-item ${isActive ? "active" : ""}`}
          title="Reports"
        >
          <BarChart3 size={18} />
        </NavLink>

        <NavLink
          to="/transactions/new"
          className={({ isActive }) => `menu-item ${isActive ? "active" : ""}`}
          title="Add transaction"
        >
          <Plus size={18} />
        </NavLink>
      </div>
    </div>
  );
};
