import type { FC } from "react";
import { DashboardLayout } from "../components/layout/DashboardLayout";

import { BalanceCard } from "../components/cards/BalanceCard";
import { NetWorthCard } from "../components/cards/NetWorthCard";

import { SpendingChart } from "../components/charts/SpendingChart";
import { AssetsChart } from "../components/charts/AssetsChart";

import { TransactionsList } from "../components/widgets/TransactionsList";
import { Notifications } from "../components/widgets/Notifications";
import { QuickActions } from "../components/widgets/QuickActions";
import { IncomeGoal } from "../components/widgets/IncomeGoal";
import { SpendingBreakdown } from "../components/widgets/SpendingBreakdown";
import { PetExpenses } from "../components/widgets/PetExpenses";

import { useDashboard } from "../hooks/useDashboard";

export const Dashboard: FC = () => {
  const {
    balance,
    transactions,
    notifications,
    assets,
    spending,
    categories,
    petExpenses,
    setFilter,
    loading,
    income,
    incomeGoal,
    netChange,
    reload,
  } = useDashboard(1);

  if (loading) {
    return <div style={{ padding: 20 }}>Loading...</div>;
  }

  return (
    <DashboardLayout>
      <div className="grid">
        <div className="balance">
          <BalanceCard amount={balance} />
        </div>

        <div className="networth">
          <NetWorthCard amount={balance} change={netChange} />
        </div>

        <div className="actions">
          <QuickActions onCreated={reload} />
        </div>

        <div className="spending">
          <div className="card">
            <div className="card-header">
              <p className="card-title">Spending</p>
            </div>
            <div className="card-body chart">
              <SpendingChart data={spending} />
            </div>
          </div>
        </div>

        <div className="goal">
          <IncomeGoal current={income} goal={Number(incomeGoal)} />
        </div>

        <div className="notifications">
          <Notifications data={notifications} />
        </div>

        <div className="breakdown">
          <SpendingBreakdown data={transactions} />
        </div>

        <div className="pets">
          <PetExpenses data={petExpenses} />
        </div>

        <div className="assets">
          <AssetsChart data={assets} />
        </div>

        <div className="transactions">
          <div className="card">
            <div className="card-header">
              <h2 className="section-title">Transactions</h2>
              <select
                className="select"
                onChange={(e) => setFilter(e.target.value)}
              >
                <option value="ALL">All Categories</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.name}>
                    {c.name}
                  </option>
                ))}
              </select>
            </div>
            <div className="card-body">
              <TransactionsList data={transactions} />
            </div>
          </div>
        </div>
      </div>
    </DashboardLayout>
  );
};
