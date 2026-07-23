import { useCallback, useEffect, useState } from "react";
import {
  getTransactions,
  getCategories,
  getBudgets,
} from "../services/smartbudget";
import type { BudgetDto } from "../services/types/BudgetDto";
import type { CategoryDto } from "../services/types/CategoryDto";
import type { TransactionDto } from "../services/types/TransactionDto";

export const useDashboard = (userId: number = 1) => {
  const [transactions, setTransactions] = useState<TransactionDto[]>([]);
  const [categories, setCategories] = useState<CategoryDto[]>([]);
  const [budgets, setBudgets] = useState<BudgetDto[]>([]);
  const [filter, setFilter] = useState<string>("ALL");
  const [loading, setLoading] = useState(true);

  const reload = useCallback(() => {
    setLoading(true);
    return Promise.all([
      getTransactions({ userId }),
      getCategories(),
      getBudgets(userId),
    ])
      .then(([tx, cat, bud]) => {
        setTransactions(tx);
        setCategories(cat);
        setBudgets(bud);
      })
      .finally(() => setLoading(false));
  }, [userId]);

  useEffect(() => {
    reload();
  }, [reload]);

  const filteredTransactions =
    filter === "ALL"
      ? transactions
      : transactions.filter((t) => t.categoryDto?.name === filter);

  const balance = filteredTransactions.reduce((sum, t) => {
    return t.type === "INCOME" ? sum + Number(t.amount) : sum - Number(t.amount);
  }, 0);

  const income = transactions
    .filter((t) => t.type === "INCOME")
    .reduce((sum, t) => sum + Number(t.amount), 0);

  const expenses = transactions
    .filter((t) => t.type === "EXPENSE")
    .reduce((sum, t) => sum + Math.abs(Number(t.amount)), 0);

  const spending = filteredTransactions
    .filter((t) => t.type === "EXPENSE")
    .map((t) => ({ value: Math.abs(Number(t.amount)) }));

  const notifications = transactions.slice(0, 5).map((t) => ({
    id: `${t.id ?? t.date}-${t.amount}`,
    title: t.type === "INCOME" ? "Payment received" : "Expense",
    amount: Number(t.amount),
    time: t.date,
    type: t.type,
  }));

  const breakdownMap = transactions
    .filter((t) => t.type === "EXPENSE")
    .reduce((acc, t) => {
      const key = t.categoryDto?.name || "Other";
      if (!acc[key]) acc[key] = 0;
      acc[key] += Math.abs(Number(t.amount));
      return acc;
    }, {} as Record<string, number>);

  const spendingBreakdown = Object.entries(breakdownMap).map(([label, value]) => ({
    label,
    value,
  }));

  const petExpenses = transactions
    .filter((t) => t.categoryDto?.name === "Pets")
    .map((t) => ({
      label: t.description || "Pet expense",
      value: Math.abs(Number(t.amount)),
    }));

  const assetsMap = transactions.reduce((acc, t) => {
    const key = t.categoryDto?.name || "Other";
    if (!acc[key]) acc[key] = 0;
    acc[key] += Math.abs(Number(t.amount));
    return acc;
  }, {} as Record<string, number>);

  const assets = Object.entries(assetsMap).map(([name, value]) => ({
    name,
    value,
  }));

  const incomeGoal =
    budgets.find((b) =>
      ["MONTH", "MONTHLY", "YEAR", "YEARLY"].includes(b.period)
    )?.limitAmount ?? Math.max(income * 1.2, 1000);

  const netChange = income - expenses;

  return {
    balance,
    income,
    expenses,
    incomeGoal,
    netChange,
    transactions: filteredTransactions,
    categories,
    budgets,
    spending,
    assets,
    notifications,
    spendingBreakdown,
    petExpenses,
    setFilter,
    loading,
    reload,
  };
};
