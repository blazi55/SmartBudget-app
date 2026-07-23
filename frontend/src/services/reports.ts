import { reportApi } from "./api";

export type DailyReport = {
  date: string;
  income: number;
  expenses: number;
  balance: number;
};

export type MonthlyReport = {
  yearMonth: string;
  income: number;
  expenses: number;
  balance: number;
};

export type CategoryReport = {
  categoryName: string;
  yearMonth?: string | null;
  totalAmount: number;
  transactionCount: number;
};

export type TrendsReport = {
  totalIncome: number;
  totalExpenses: number;
  netBalance: number;
  transactionCount: number;
  monthlyTrend: MonthlyReport[];
  topCategories: CategoryReport[];
};

export const getDailyReport = (userId: number, from?: string, to?: string) => {
  const query = new URLSearchParams({ userId: String(userId) });
  if (from) query.append("from", from);
  if (to) query.append("to", to);
  return reportApi.get(`/reports/daily?${query}`) as Promise<DailyReport[]>;
};

export const getMonthlyReport = (userId: number, from?: string, to?: string) => {
  const query = new URLSearchParams({ userId: String(userId) });
  if (from) query.append("from", from);
  if (to) query.append("to", to);
  return reportApi.get(`/reports/monthly?${query}`) as Promise<MonthlyReport[]>;
};

export const getCategoryReport = (userId: number, yearMonth?: string) => {
  const query = new URLSearchParams({ userId: String(userId) });
  if (yearMonth) query.append("yearMonth", yearMonth);
  return reportApi.get(`/reports/categories?${query}`) as Promise<CategoryReport[]>;
};

export const getTrendsReport = (userId: number) =>
  reportApi.get(`/reports/trends?userId=${userId}`) as Promise<TrendsReport>;
