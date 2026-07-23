import { useEffect, useState, type FC, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { DashboardLayout } from "../components/layout/DashboardLayout";
import {
  createTransaction,
  getCategories,
} from "../services/smartbudget";
import type { CategoryDto } from "../services/types/CategoryDto";

const USER_ID = 1;

export const NewTransactionPage: FC = () => {
  const navigate = useNavigate();
  const [categories, setCategories] = useState<CategoryDto[]>([]);
  const [amount, setAmount] = useState("");
  const [type, setType] = useState<"INCOME" | "EXPENSE">("EXPENSE");
  const [categoryId, setCategoryId] = useState("");
  const [description, setDescription] = useState("");
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10));
  const [currency, setCurrency] = useState("USD");
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    getCategories()
      .then((cats) => {
        setCategories(cats);
        if (cats[0]) setCategoryId(String(cats[0].id));
      })
      .catch(() => setError("Failed to load categories"));
  }, []);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);

    try {
      await createTransaction({
        userId: USER_ID,
        categoryId: Number(categoryId),
        amount: Number(amount),
        type,
        date,
        currency,
        description: description || type,
      });
      navigate("/");
    } catch {
      setError("Failed to create transaction. Is the budget service running?");
    } finally {
      setSaving(false);
    }
  };

  return (
    <DashboardLayout>
      <div className="card" style={{ maxWidth: 520 }}>
        <div className="card-header">
          <h2 className="section-title">New Transaction</h2>
        </div>
        <form className="card-body form-grid" onSubmit={submit}>
          <label>
            Type
            <select className="select" value={type} onChange={(e) => setType(e.target.value as "INCOME" | "EXPENSE")}>
              <option value="EXPENSE">Expense</option>
              <option value="INCOME">Income</option>
            </select>
          </label>
          <label>
            Amount
            <input className="input" type="number" min="0.01" step="0.01" value={amount} onChange={(e) => setAmount(e.target.value)} required />
          </label>
          <label>
            Category
            <select className="select" value={categoryId} onChange={(e) => setCategoryId(e.target.value)} required>
              {categories.map((c) => (
                <option key={c.id} value={c.id}>{c.name}</option>
              ))}
            </select>
          </label>
          <label>
            Date
            <input className="input" type="date" value={date} onChange={(e) => setDate(e.target.value)} required />
          </label>
          <label>
            Currency
            <select className="select" value={currency} onChange={(e) => setCurrency(e.target.value)}>
              <option value="USD">USD</option>
              <option value="EUR">EUR</option>
              <option value="PLN">PLN</option>
            </select>
          </label>
          <label>
            Description
            <input className="input" type="text" value={description} onChange={(e) => setDescription(e.target.value)} />
          </label>
          {error && <p className="form-error">{error}</p>}
          <div className="form-actions">
            <button type="button" className="btn gray" onClick={() => navigate("/")}>Cancel</button>
            <button type="submit" className="btn green" disabled={saving}>
              {saving ? "Saving..." : "Create"}
            </button>
          </div>
        </form>
      </div>
    </DashboardLayout>
  );
};
