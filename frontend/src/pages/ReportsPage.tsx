import { useEffect, useState, type FC } from "react";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { DashboardLayout } from "../components/layout/DashboardLayout";
import {
  getCategoryReport,
  getDailyReport,
  getMonthlyReport,
  getTrendsReport,
  type CategoryReport,
  type DailyReport,
  type MonthlyReport,
  type TrendsReport,
} from "../services/reports";
import { republishTransactionEvents } from "../services/smartbudget";

const USER_ID = 1;

export const ReportsPage: FC = () => {
  const [daily, setDaily] = useState<DailyReport[]>([]);
  const [monthly, setMonthly] = useState<MonthlyReport[]>([]);
  const [categories, setCategories] = useState<CategoryReport[]>([]);
  const [trends, setTrends] = useState<TrendsReport | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [syncing, setSyncing] = useState(false);

  const load = () => {
    setLoading(true);
    setError(null);

    Promise.all([
      getDailyReport(USER_ID),
      getMonthlyReport(USER_ID),
      getCategoryReport(USER_ID),
      getTrendsReport(USER_ID),
    ])
      .then(([d, m, c, t]) => {
        setDaily(d);
        setMonthly(m);
        setCategories(c);
        setTrends(t);
      })
      .catch(() =>
        setError("Report service unavailable. Start backend-report on :8081 and Kafka.")
      )
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  const syncEvents = async () => {
    setSyncing(true);
    try {
      await republishTransactionEvents();
      setTimeout(load, 1500);
    } catch {
      setError("Failed to republish events from budget service.");
    } finally {
      setSyncing(false);
    }
  };

  if (loading) {
    return (
      <DashboardLayout>
        <div style={{ padding: 20 }}>Loading reports...</div>
      </DashboardLayout>
    );
  }

  return (
    <DashboardLayout>
      <div className="reports-header">
        <div>
          <h2 className="section-title">Reports</h2>
          <p className="muted">Analytics from Report Service (CQRS read model)</p>
        </div>
        <button className="btn blue" onClick={syncEvents} disabled={syncing}>
          {syncing ? "Syncing..." : "Sync from Budget Service"}
        </button>
      </div>

      {error && <p className="form-error" style={{ marginBottom: 16 }}>{error}</p>}

      <div className="reports-stats">
        <div className="card stat-card">
          <p className="muted">Total income</p>
          <p className="stat-value">${Number(trends?.totalIncome ?? 0).toLocaleString()}</p>
        </div>
        <div className="card stat-card">
          <p className="muted">Total expenses</p>
          <p className="stat-value">${Number(trends?.totalExpenses ?? 0).toLocaleString()}</p>
        </div>
        <div className="card stat-card">
          <p className="muted">Net balance</p>
          <p className="stat-value">${Number(trends?.netBalance ?? 0).toLocaleString()}</p>
        </div>
        <div className="card stat-card">
          <p className="muted">Transactions</p>
          <p className="stat-value">{trends?.transactionCount ?? 0}</p>
        </div>
      </div>

      <div className="reports-grid">
        <div className="card">
          <div className="card-header">
            <p className="card-title">Daily balance</p>
          </div>
          <div className="card-body chart" style={{ height: 280 }}>
            {daily.length === 0 ? (
              <p className="muted">No daily data yet. Create transactions and sync.</p>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={daily}>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
                  <XAxis dataKey="date" stroke="#94a3b8" />
                  <YAxis stroke="#94a3b8" />
                  <Tooltip />
                  <Legend />
                  <Line type="monotone" dataKey="income" stroke="#22c55e" strokeWidth={2} />
                  <Line type="monotone" dataKey="expenses" stroke="#f97316" strokeWidth={2} />
                  <Line type="monotone" dataKey="balance" stroke="#3b82f6" strokeWidth={2} />
                </LineChart>
              </ResponsiveContainer>
            )}
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <p className="card-title">Monthly summary</p>
          </div>
          <div className="card-body chart" style={{ height: 280 }}>
            {monthly.length === 0 ? (
              <p className="muted">No monthly data yet.</p>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={monthly}>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
                  <XAxis dataKey="yearMonth" stroke="#94a3b8" />
                  <YAxis stroke="#94a3b8" />
                  <Tooltip />
                  <Legend />
                  <Bar dataKey="income" fill="#22c55e" />
                  <Bar dataKey="expenses" fill="#f97316" />
                </BarChart>
              </ResponsiveContainer>
            )}
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <p className="card-title">Categories</p>
          </div>
          <div className="card-body">
            {categories.length === 0 ? (
              <p className="muted">No category totals yet.</p>
            ) : (
              <div className="category-list">
                {categories.map((c) => (
                  <div key={`${c.categoryName}-${c.yearMonth ?? "all"}`} className="category-row">
                    <span>{c.categoryName}</span>
                    <span>${Number(c.totalAmount).toLocaleString()}</span>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <p className="card-title">Top categories (trends)</p>
          </div>
          <div className="card-body chart" style={{ height: 280 }}>
            {(trends?.topCategories?.length ?? 0) === 0 ? (
              <p className="muted">No trend categories yet.</p>
            ) : (
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={trends?.topCategories ?? []} layout="vertical">
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.08)" />
                  <XAxis type="number" stroke="#94a3b8" />
                  <YAxis type="category" dataKey="categoryName" stroke="#94a3b8" width={100} />
                  <Tooltip />
                  <Bar dataKey="totalAmount" fill="#a855f7" />
                </BarChart>
              </ResponsiveContainer>
            )}
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
};
