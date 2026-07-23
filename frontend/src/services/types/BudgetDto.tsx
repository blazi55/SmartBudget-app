export type BudgetDto = {
  limitAmount: number;
  period: "WEEK" | "MONTH" | "YEAR" | "MONTHLY" | "DAILY" | "WEEKLY" | "YEARLY";
  startDate: string;
};
