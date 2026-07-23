const BUDGET_API_URL = "http://localhost:8080/api";
const REPORT_API_URL = "http://localhost:8081/api";

async function request(baseUrl: string, url: string, options?: RequestInit) {
  const res = await fetch(`${baseUrl}${url}`, options);

  if (!res.ok) {
    const text = await res.text();
    console.error("API ERROR:", { url: `${baseUrl}${url}`, status: res.status, body: text });
    throw new Error(`${options?.method || "GET"} ${url} failed (${res.status})`);
  }

  if (res.status === 204) {
    return null;
  }

  return res.json();
}

export const api = {
  get: (url: string) => request(BUDGET_API_URL, url),

  post: (url: string, body: unknown) =>
    request(BUDGET_API_URL, url, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    }),

  put: (url: string, body: unknown) =>
    request(BUDGET_API_URL, url, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(body),
    }),

  delete: async (url: string) => {
    await request(BUDGET_API_URL, url, { method: "DELETE" });
  },
};

export const reportApi = {
  get: (url: string) => request(REPORT_API_URL, url),
};
