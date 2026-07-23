import type { CategoryDto } from "./CategoryDto";

export type TransactionDto = {
  id?: number;
  userId?: number;
  amount: number;
  type: "INCOME" | "EXPENSE";
  date: string;
  currency: string;
  description: string;
  categoryDto: CategoryDto;
};
