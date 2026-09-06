"use client";

import { useEffect, useState } from "react";

type Stock = {
    symbol: string;
    companyName: string;
    sector: string;
    exchange: string;
};

const API = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

export default function WatchlistPanel() {
    const [stocks, setStocks] = useState<Stock[]>([]);
    const [symbol, setSymbol] = useState("");
    const [loading, setLoading] = useState(true);

    async function load() {
        try {
            const lists = await fetch(`${API}/api/watchlists`).then(r => r.json());
            if (!lists.length) return;
            const data = await fetch(`${API}/api/watchlists/${lists[0].id}/stocks`).then(r => r.json());
            setStocks(data);
        } finally {
            setLoading(false);
        }
    }

    async function addStock() {
        const value = symbol.trim().toUpperCase();
        if (!value) return;

        try {
            const lists = await fetch(`${API}/api/watchlists`).then(r => r.json());
            if (!lists.length) return;

            await fetch(`${API}/api/watchlists/${lists[0].id}/stocks/${value}`, {
                method: "POST",
            });

            setSymbol("");
            await load();
        } catch (e) {
            console.error(e);
        }
    }

    async function removeStock(stock: string) {
        const lists = await fetch(`${API}/api/watchlists`).then(r => r.json());
        if (!lists.length) return;

        await fetch(`${API}/api/watchlists/${lists[0].id}/stocks/${stock}`, {
            method: "DELETE",
        });

        await load();
    }

    useEffect(() => {
        load();
    }, []);

    return (
        <section className="mt-12 rounded-2xl border border-zinc-200 bg-white p-6 shadow-sm">
            <div className="flex items-center justify-between">
                <div>
                    <p className="text-xs font-bold tracking-[0.14em] text-[#008f6a]">
                        WATCHLIST
                    </p>
                    <h2 className="mt-1 text-xl font-bold tracking-tight text-zinc-950">
                        My Watchlist
                    </h2>
                </div>

                <span className="text-sm text-zinc-400">
                    {stocks.length} stocks
                </span>
            </div>

            <div className="mt-5 flex gap-2">
                <input
                    value={symbol}
                    onChange={e => setSymbol(e.target.value)}
                    onKeyDown={e => e.key === "Enter" && addStock()}
                    placeholder="Add symbol e.g. INFY"
                    className="min-w-0 flex-1 rounded-xl border border-zinc-200 px-4 py-2.5 text-sm outline-none focus:border-zinc-500"
                />

                <button
                    onClick={addStock}
                    className="rounded-xl bg-zinc-900 px-5 py-2.5 text-sm font-medium text-white hover:bg-zinc-700"
                >
                    Add
                </button>
            </div>

            <div className="mt-5 divide-y divide-zinc-100">
                {loading ? (
                    <p className="py-4 text-sm text-zinc-400">Loading watchlist...</p>
                ) : (
                    stocks.map(stock => (
                        <div
                            key={stock.symbol}
                            className="flex items-center justify-between border-b border-zinc-100 py-5 transition hover:bg-emerald-50/30"
                        >
                            <div>
                                <p className="font-bold tracking-tight text-zinc-950">
                                    {stock.symbol}
                                </p>
                                <p className="mt-0.5 text-sm text-zinc-500">
                                    {stock.companyName}
                                </p>
                            </div>

                            <button
                                onClick={() => removeStock(stock.symbol)}
                                className="text-sm text-zinc-400 hover:text-zinc-900"
                            >
                                Remove
                            </button>
                        </div>
                    ))
                )}
            </div>
        </section>
    );
}