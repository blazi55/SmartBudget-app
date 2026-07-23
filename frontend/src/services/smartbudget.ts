import { api } from "./api";
import type { BudgetDto } from "./types/BudgetDto";
import type { CategoryDto } from "./types/CategoryDto";
import type { TransactionDto } from "./types/TransactionDto";

export type CreateTransactionPayload = {
  userId: number;
  categoryId: number;
  amount: number;
  type: "INCOME" | "EXPENSE";
  date: string;
  currency: string;
  description: string;
};

export const getUsers = () => api.get("/users");
export const getUser = (id: number) => api.get(`/users/${id}`);

export const getTransactions = (params?: {
  userId?: number;
  categoryId?: number;
}) => {
  const query = new URLSearchParams();

  if (params?.userId) query.append("userId", String(params.userId));
  if (params?.categoryId) query.append("categoryId", String(params.categoryId));

  return api.get(`/transactions?${query.toString()}`) as Promise<TransactionDto[]>;
};

export const createTransaction = (payload: CreateTransactionPayload) =>
  api.post("/transactions", payload) as Promise<TransactionDto>;

export const deleteTransaction = (id: number) => api.delete(`/transactions/${id}`);

export const republishTransactionEvents = () =>
  api.post("/transactions/republish-events", {}) as Promise<{ republished: number }>;

export const getCategories = () =>
  api.get("/categories") as Promise<CategoryDto[]>;

export const getBudgets = (userId: number) =>
  api.get(`/budgets?userId=${userId}`) as Promise<BudgetDto[]>;
