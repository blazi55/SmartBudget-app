import { useState, type FC, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { createTransaction, getCategories } from "../../services/smartbudget";
import type { CategoryDto } from "../../services/types/CategoryDto";
import { useEffect } from "react";

type QuickActionsProps = {
  userId?: number;
  onCreated?: () => void;
};

export const QuickActions: FC<QuickActionsProps> = ({
  userId = 1,
  onCreated,
}) => {
  const navigate = useNavigate();
  const [open, setOpen] = useState(false);
  const [categories, setCategories] = useState<CategoryDto[]>([]);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [amount, setAmount] = useState("");
  const [type, setType] = useState<"INCOME" | "EXPENSE">("EXPENSE");
  const [categoryId, setCategoryId] = useState("");
  const [description, setDescription] = useState("");
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10));

  useEffect(() => {
    if (!open) return;
    getCategories()
      .then((cats) => {
        setCategories(cats);
        if (cats[0]) setCategoryId(String(cats[0].id));
      })
      .catch(() => setError("Failed to load categories"));
  }, [open]);

  const submit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);

    try {
      await createTransaction({
        userId,
        categoryId: Number(categoryId),
        amount: Number(amount),
        type,
        date,
        currency: "USD",
        description: description || (type === "INCOME" ? "Income" : "Expense"),
      });
      setOpen(false);
      setAmount("");
      setDescription("");
      onCreated?.();
    } catch {
      setError("Failed to create transaction");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="card">
      <div className="card-header">
        <p className="card-title">Quick Actions</p>
      </div>

      <div className="card-body actions-body">
        <div className="btn-grid">
          <button className="btn green" onClick={() => { setType("EXPENSE"); setOpen(true); }}>
            Expense
          </button>
          <button className="btn blue" onClick={() => { setType("INCOME"); setOpen(true); }}>
            Income
          </button>
          <button className="btn purple" onClick={() => navigate("/reports")}>
            Reports
          </button>
          <button className="btn gray" onClick={() => navigate("/transactions/new")}>
            More
          </button>
        </div>
      </div>

      {open && (
        <div className="modal-backdrop" onClick={() => setOpen(false)}>
          <form
            className="modal card"
            onClick={(e) => e.stopPropagation()}
            onSubmit={submit}
          >
            <div className="card-header">
              <p className="card-title">Add {type === "INCOME" ? "Income" : "Expense"}</p>
            </div>
            <div className="card-body form-grid">
              <label>
                Amount
                <input
                  className="input"
                  type="number"
                  min="0.01"
                  step="0.01"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  required
                />
              </label>
              <label>
                Category
                <select
                  className="select"
                  value={categoryId}
                  onChange={(e) => setCategoryId(e.target.value)}
                  required
                >
                  {categories.map((c) => (
                    <option key={c.id} value={c.id}>{c.name}</option>
                  ))}
                </select>
              </label>
              <label>
                Date
                <input
                  className="input"
                  type="date"
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                  required
                />
              </label>
              <label>
                Description
                <input
                  className="input"
                  type="text"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                />
              </label>
              {error && <p className="form-error">{error}</p>}
              <div className="form-actions">
                <button type="button" className="btn gray" onClick={() => setOpen(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn green" disabled={saving}>
                  {saving ? "Saving..." : "Save"}
                </button>
              </div>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};
