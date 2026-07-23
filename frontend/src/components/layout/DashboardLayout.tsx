import type { FC, ReactNode } from "react";
import { Sidebar } from "./Sidebar";
import { TopBar } from "./TopBar";

type DashboardLayoutProps = {
  children: ReactNode;
};

export const DashboardLayout: FC<DashboardLayoutProps> = ({ children }) => {
  return (
    <div className="app">
      <Sidebar />
      <div className="main">
        <TopBar />
        {children}
      </div>
    </div>
  );
};
