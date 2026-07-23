import type { FC } from "react";
import { Routes, Route } from "react-router-dom";
import { Dashboard } from "./pages/Dashboard";
import { ReportsPage } from "./pages/ReportsPage";
import { NewTransactionPage } from "./pages/NewTransactionPage";

const App: FC = () => {
  return (
    <Routes>
      <Route path="/" element={<Dashboard />} />
      <Route path="/reports" element={<ReportsPage />} />
      <Route path="/transactions/new" element={<NewTransactionPage />} />
    </Routes>
  );
};

export default App;
