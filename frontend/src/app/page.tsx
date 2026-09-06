"use client";

import { useEffect, useState } from "react";
import WatchlistPanel from "@/components/WatchlistPanel";
import ExplanationPanel from "@/components/ExplanationPanel";


const API = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

type Attention = {
  id: number;
  symbol: string;
  attentionScore: number;
  status: string;
};

type Pulse = {
  lastCheckedAt: string;
  hoursSinceLastCheck: number;
  meaningfulChangeCount: number;
  attentionItems: Attention[];
  unremarkableCount: number;
};

type Signal = {
  signalType: string;
  value: number;
  score: number;
  metadata: string;
};

export default function Home() {
  const [pulse, setPulse] = useState<Pulse | null>(null);
  const [signals, setSignals] = useState<Signal[]>([]);
  const [loading, setLoading] = useState(true);
  const [showDetails, setShowDetails] = useState(false);
  const [reviewed, setReviewed] = useState(false);
  const [replaying, setReplaying] = useState(false);
  const [replayStep, setReplayStep] = useState("");

  async function load() {
    try {
      const p = await fetch(`${API}/api/pulse`, {
        cache: "no-store",
      }).then(r => r.json());

      setPulse(p);

      if (p.attentionItems?.length) {
        const symbol = p.attentionItems[0].symbol;
        const s = await fetch(
          `${API}/api/signals/${symbol}/signals`,
          { cache: "no-store" }
        ).then(r => r.json());

        setSignals(s);
      } else {
        setSignals([]);
      }
    } catch (e) {
      console.error("Pulse load failed:", e);
    } finally {
      setLoading(false);
    }
  }

  async function check() {
    await fetch(`${API}/api/pulse/check`, {
      method: "POST",
    });
    setReviewed(false);
    await load();
  }


  async function resetDemo() {
    await fetch(`${API}/api/attention/reset`, { method: "POST" });
    await fetch(`${API}/api/significance/demo-minute/0`, { method: "POST" });
    setReviewed(false);
    setShowDetails(false);
    setReplayStep("");
    await load();
  }

  async function replay() {
    if (replaying) return;

    setReplaying(true);
    setShowDetails(false);

    await fetch(`${API}/api/pulse/reset`, {
      method: "POST",
    });


    const steps: [number, string][] = [
      [0, "09:30 · Market opens normally"],
      [10, "10:30 · Unusual movement begins"],
      [20, "11:00 · Volume starts accelerating"],
      [40, "12:00 · Signals converge"],
    ];

    try {
      for (const [minute, label] of steps) {
        setReplayStep(label);

        await fetch(
          `${API}/api/significance/demo-minute/${minute}`,
          { method: "POST" }
        );

        await fetch(
          `${API}/api/significance/evaluate/HDFCBANK`,
          { method: "POST" }
        );

        await new Promise(r => setTimeout(r, 1600));
        await load();
      }

      setReplayStep("Replay complete · Pulse updated");
      await load();
    } finally {
      setReplaying(false);
    }
  }

  async function review() {
    const item = pulse?.attentionItems?.[0];
    if (!item) return;

    await fetch(
      `${API}/api/attention/${item.id}/review`,
      { method: "POST" }
    );

    setReviewed(true);
    setShowDetails(false);
    await load();
  }

  useEffect(() => {
    load();
  }, []);

  if (loading) {
    return (
      <main className="min-h-screen bg-[#f7f7f5] p-10 text-zinc-900">
        Loading Pulse...
      </main>
    );
  }

  if (!pulse) {
    return (
      <main className="min-h-screen bg-[#f7f7f5] p-10 text-zinc-900">
        Unable to load Pulse.
      </main>
    );
  }

  const item = pulse?.attentionItems?.[0];

  const price = signals.find(
    s => s.signalType === "PRICE_ANOMALY"
  );

  const volume = signals.find(
    s => s.signalType === "VOLUME_ANOMALY"
  );

  const relative = signals.find(
    s => s.signalType === "RELATIVE_PERFORMANCE"
  );

  const metadata = (s?: Signal) => {
    try {
      return s ? JSON.parse(s.metadata) : {};
    } catch {
      return {};
    }
  };

  const pm = metadata(price);

  return (
    <main className="min-h-screen bg-[#f7f7f5] text-zinc-900">
      <div className="mx-auto max-w-6xl px-6 py-8">

        {/* HEADER */}
        <header className="pulse-grid relative overflow-hidden rounded-3xl border border-zinc-200 bg-white px-7 py-6 shadow-sm">
          <div className="absolute right-0 top-0 h-40 w-40 rounded-full bg-emerald-100/50 blur-3xl" />

          <div className="relative flex items-center justify-between gap-6">
            <div>
              <div className="flex items-center gap-3">
                <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-[#00b386] text-lg font-bold text-white">
                  P
                </div>
                <div>
                  <div className="flex items-center gap-2">
                    <h1 className="text-2xl font-bold tracking-tight text-zinc-950">
                      PULSE
                    </h1>
                    <span className="pulse-dot" />
                  </div>
                  <p className="mt-0.5 text-sm text-zinc-500">
                    Your watchlist watches the market.
                  </p>
                </div>
              </div>

              <div className="mt-5 flex items-center gap-2 text-xs text-zinc-500">
                <span className="font-medium text-[#008f6a]">● MARKET WATCH</span>
                <span>•</span>
                <span>Focused on what matters</span>
              </div>
            </div>

            <div className="relative flex items-center gap-2">
              <button
                onClick={replay}
                disabled={replaying}
                className="rounded-xl border border-zinc-200 bg-white px-4 py-2.5 text-sm font-medium text-zinc-800 shadow-sm transition hover:border-zinc-300 hover:bg-zinc-50 disabled:opacity-50"
              >
                ▶ Replay market
              </button>

              <button
                onClick={resetDemo}
                disabled={replaying}
                className="rounded-xl border border-zinc-200 bg-white px-4 py-2.5 text-sm font-medium text-zinc-600 shadow-sm transition hover:bg-zinc-50 disabled:opacity-50"
              >
                Reset demo
              </button>

              <button
                onClick={check}
                disabled={loading}
                className="rounded-xl bg-[#00b386] px-5 py-2.5 text-sm font-semibold text-white shadow-sm transition hover:bg-[#008f6a] disabled:opacity-50"
              >
                Check market
              </button>
            </div>
          </div>
        </header>

        {/* REPLAY STATUS */}
        {replaying && (
          <div className="mt-5 rounded-xl border border-zinc-200 bg-white px-5 py-4 shadow-sm">
            <div className="flex items-center gap-3">
              <span className="h-2 w-2 animate-pulse rounded-full bg-zinc-900" />
              <span className="text-xs font-semibold tracking-wide text-zinc-500">
                LIVE REPLAY
              </span>
              <span className="text-sm text-zinc-700">
                {replayStep}
              </span>
            </div>
          </div>
        )}

        {/* HERO */}
        <section className="mt-10">
          <p className="text-xs font-bold tracking-[0.16em] text-[#008f6a]">
            PULSE · ATTENTION CENTER
          </p>

          <h2 className="mt-3 text-4xl font-bold tracking-tight text-zinc-950">
            What deserves your attention?
          </h2>

          <p className="mt-2 text-base text-zinc-500">
            You were last here {pulse?.hoursSinceLastCheck ?? 0}h ago.
          </p>
        </section>

        {/* ATTENTION */}
        <section className="mt-10">

          <p className="text-sm font-medium text-zinc-500">
            WHAT CHANGED
          </p>

          <div className="mt-6 flex items-end gap-3">
            <span className="text-5xl font-bold tracking-tight text-zinc-950">
              {pulse.meaningfulChangeCount}
            </span>
            <span className="pb-1 text-lg text-zinc-500">
              meaningful change
              {pulse.meaningfulChangeCount === 1 ? "" : "s"}
            </span>
          </div>

          {item && (
  <div className="mt-4 overflow-hidden rounded-3xl border border-emerald-200 bg-white shadow-[0_8px_30px_rgba(0,179,134,0.08)]">

    <div className="h-1.5 bg-[#00b386]" />

    <div className="p-7">
      <div className="flex items-start justify-between gap-6">

        <div className="min-w-0 flex-1">

          <div className="flex items-center gap-3">
            <div>
              <h2 className="text-2xl font-bold tracking-tight text-zinc-950">
                HDFC Bank
              </h2>
              <p className="mt-1 text-sm text-zinc-500">
                {item.symbol}
              </p>
            </div>

                    <span className="rounded-full bg-emerald-50 px-3 py-1.5 text-xs font-bold tracking-wide text-[#008f6a] ring-1 ring-emerald-200">
              SIGNIFICANT
            </span>
          </div>

                  {/* <p className="mt-2 text-sm font-medium text-zinc-500">
                    {item.symbol}
                  </p> */}

                  {/* SIGNALS */}
          <div className="mt-7 grid grid-cols-2 gap-3 sm:grid-cols-4">

            <div className="rounded-2xl border border-zinc-100 bg-zinc-50 p-4">
              <p className="text-[11px] font-semibold tracking-wide text-zinc-400">
                PRICE MOVE
              </p>
              <p className="mt-2 text-xl font-bold text-zinc-950">
                {price?.value
                  ? `${(price.value * 100).toFixed(2)}%`
                  : "—"}
              </p>
            </div>

                    <div className="rounded-2xl border border-zinc-100 bg-zinc-50 p-4">
              <p className="text-[11px] font-semibold tracking-wide text-zinc-400">
                ANOMALY
              </p>
              <p className="mt-2 text-xl font-bold text-zinc-950">
                {pm?.zScore
                  ? `${Number(pm.zScore).toFixed(1)}σ`
                  : "—"}
              </p>
            </div>

                    <div className="rounded-2xl border border-zinc-100 bg-zinc-50 p-4">
              <p className="text-[11px] font-semibold tracking-wide text-zinc-400">
                VOLUME
              </p>
              <p className="mt-2 text-xl font-bold text-zinc-950">
                {volume?.value
                  ? `${Number(volume.value).toFixed(2)}×`
                  : "—"}
              </p>
            </div>

                    <div className="rounded-2xl border border-zinc-100 bg-zinc-50 p-4">
              <p className="text-[11px] font-semibold tracking-wide text-zinc-400">
                VS MARKET
              </p>
              <p className="mt-2 text-xl font-bold text-zinc-950">
                {relative?.value
                  ? `${(Number(relative.value) * 100).toFixed(2)}%`
                  : "—"}
              </p>
            </div>

          </div>

                  {/* WHY IT MATTERS */}
          <div className="mt-7 rounded-2xl bg-emerald-50/70 p-5">
            <div className="flex items-center gap-2">
              <span className="pulse-dot" />
              <p className="text-xs font-bold tracking-[0.12em] text-[#008f6a]">
                WHY IT MATTERS
              </p>
            </div>

            <p className="mt-3 text-lg font-semibold text-zinc-900">
              Price moved unusually while volume accelerated.
            </p>

            <p className="mt-1 text-sm leading-6 text-zinc-600">
              Three independent signals agree this deserves attention.
            </p>
          </div>

        </div>

                {/* SCORE */}
        <div className="shrink-0 rounded-2xl bg-zinc-950 px-5 py-4 text-center">
          <p className="text-[10px] font-semibold tracking-[0.12em] text-zinc-400">
            ATTENTION
          </p>
          <p className="mt-1 text-3xl font-bold text-white">
            {item.attentionScore}
          </p>
          <p className="text-[10px] text-emerald-400">
            / 100
          </p>
        </div>

      </div>

              {/* ACTIONS */}
              <div className="mt-7 flex gap-3 border-t border-zinc-100 pt-5">

                <button
                  onClick={() => setShowDetails(true)}
                  className="rounded-xl bg-zinc-900 px-5 py-2.5 text-sm font-medium text-white hover:bg-zinc-700"
                >
                  Why this matters →
                </button>

                <button
                  onClick={review}
                  className="rounded-xl border border-zinc-200 px-5 py-2.5 text-sm font-medium hover:bg-zinc-50"
                >
                  {reviewed ? "Reviewed ✓" : "Review"}
                </button>

              </div>
            </div>
            </div>
          )}

          {/* NOTHING MATERIAL */}
          <div className="mt-8 border-t border-zinc-200 pt-6">
            <p className="text-sm text-zinc-500">
              {pulse.unremarkableCount} other stocks — Nothing material detected.
            </p>
          </div>

        </section>

        <WatchlistPanel />

      </div>

      {/* STOCK INTELLIGENCE MODAL */}
      {showDetails && item && (
        <div
          className="fixed inset-0 z-50 overflow-y-auto bg-black/40 p-4 sm:p-6"
          onClick={() => setShowDetails(false)}
        >
          <div
            className="mx-auto my-8 max-w-3xl rounded-3xl bg-white p-6 shadow-2xl sm:p-8"
            onClick={e => e.stopPropagation()}
          >

            {/* MODAL HEADER */}
            <div className="flex items-start justify-between">
              <div>
                <p className="text-xs font-semibold tracking-wide text-zinc-500">
                  STOCK INTELLIGENCE
                </p>

                <h2 className="mt-2 text-3xl font-semibold">
                  HDFC Bank
                </h2>

                <p className="mt-1 text-sm text-zinc-500">
                  HDFCBANK · Why this matters
                </p>
              </div>

              <button
                onClick={() => setShowDetails(false)}
                className="rounded-full px-2 text-2xl text-zinc-400 hover:bg-zinc-100"
              >
                ×
              </button>
            </div>

            {/* WHAT CHANGED */}
            <div className="mt-8">
              <p className="text-xs font-semibold tracking-wide text-zinc-500">
                WHAT CHANGED
              </p>

              <p className="mt-3 text-xl font-medium leading-8">
                HDFC Bank is showing an unusually large move,
                elevated trading activity, and material
                underperformance versus the market.
              </p>
            </div>

            {/* KEY METRICS */}
            <div className="mt-7 grid gap-3 sm:grid-cols-3">

              <div className="rounded-2xl bg-zinc-100 p-5">
                <p className="text-xs text-zinc-500">
                  PRICE ANOMALY
                </p>
                <p className="mt-2 text-2xl font-semibold">
                  {pm?.zScore
                    ? `${Number(pm.zScore).toFixed(1)}σ`
                    : "—"}
                </p>
              </div>

              <div className="rounded-2xl bg-zinc-100 p-5">
                <p className="text-xs text-zinc-500">
                  VOLUME
                </p>
                <p className="mt-2 text-2xl font-semibold">
                  {volume?.value
                    ? `${Number(volume.value).toFixed(2)}×`
                    : "—"}
                </p>
              </div>

              <div className="rounded-2xl bg-zinc-100 p-5">
                <p className="text-xs text-zinc-500">
                  VS MARKET
                </p>
                <p className="mt-2 text-2xl font-semibold">
                  {relative?.value
                    ? `${(Number(relative.value) * 100).toFixed(2)}%`
                    : "—"}
                </p>
              </div>

            </div>

            <div className="mt-5 rounded-2xl border border-zinc-200 bg-zinc-50 p-5">
              <div className="flex items-center justify-between">
                <p className="text-xs font-semibold tracking-wide text-zinc-500">
                  MARKET CONTEXT
                </p>
                <span className="text-xs text-zinc-400">
                  Benchmark +0.50%
                </span>
              </div>

              <div className="mt-4 grid grid-cols-3 gap-3">
                <div>
                  <p className="text-xs text-zinc-400">HDFC BANK</p>
                  <p className="mt-1 text-lg font-semibold">
                    {price?.value ? `${(price.value * 100).toFixed(2)}%` : "—"}
                  </p>
                </div>

                <div>
                  <p className="text-xs text-zinc-400">MARKET</p>
                  <p className="mt-1 text-lg font-semibold">+0.50%</p>
                </div>

                <div>
                  <p className="text-xs text-zinc-400">DIVERGENCE</p>
                  <p className="mt-1 text-lg font-semibold">
                    {relative?.value
                      ? `${(Number(relative.value) * 100).toFixed(2)}%`
                      : "—"}
                  </p>
                </div>
              </div>

              <p className="mt-4 text-sm text-zinc-500">
                HDFC Bank is moving materially differently from the broader market.
              </p>
            </div>

            {/* WHY */}
            <div className="mt-8 border-t border-zinc-200 pt-6">
              <p className="text-xs font-semibold tracking-wide text-zinc-500">
                WHY IT MATTERS
              </p>

              <p className="mt-3 leading-7 text-zinc-600">
                Three independent signals agree: the price move is
                highly unusual, trading volume is elevated, and
                HDFC Bank is materially underperforming the market.
                Together, the convergence makes this less likely
                to be a routine fluctuation.
              </p>
            </div>

            <ExplanationPanel
              price={price?.value}
              zScore={pm?.zScore ? Number(pm.zScore) : undefined}
              volume={volume?.value ? Number(volume.value) : undefined}
              relative={relative?.value ? Number(relative.value) : undefined}
              score={item.attentionScore}
            />

            {/* EVIDENCE */}
            <div className="mt-7 rounded-2xl border border-zinc-200 p-5">
              <p className="text-xs font-semibold tracking-wide text-zinc-500">
                EVIDENCE
              </p>

              <div className="mt-4 grid gap-3">

                <div className="flex items-center justify-between rounded-xl bg-zinc-50 p-4">
                  <span className="text-sm">
                    Price anomaly
                  </span>
                  <span className="font-semibold">
                    {pm?.zScore
                      ? `${Number(pm.zScore).toFixed(1)}σ`
                      : "—"}
                  </span>
                </div>

                <div className="flex items-center justify-between rounded-xl bg-zinc-50 p-4">
                  <span className="text-sm">
                    Volume anomaly
                  </span>
                  <span className="font-semibold">
                    {volume?.value
                      ? `${Number(volume.value).toFixed(2)}× normal`
                      : "—"}
                  </span>
                </div>

                <div className="flex items-center justify-between rounded-xl bg-zinc-50 p-4">
                  <span className="text-sm">
                    Relative performance
                  </span>
                  <span className="font-semibold">
                    {relative?.value
                      ? `${(Number(relative.value) * 100).toFixed(2)}%`
                      : "—"}
                  </span>
                </div>

              </div>
            </div>

            {/* CHANGE REPLAY */}
            <div className="mt-7 border-t border-zinc-200 pt-6">

              <p className="text-xs font-semibold tracking-wide text-zinc-500">
                CHANGE REPLAY
              </p>

              <div className="mt-6 space-y-6">

                {[
                  ["09:30", "Market opens normally", "No unusual activity detected."],
                  ["10:30", "Unusual movement begins", "Price anomaly crosses the watch threshold."],
                  ["11:00", "Volume accelerates", "Trading activity rises significantly above normal."],
                  ["12:00", "Signals converge", "Price, volume and relative performance become significant."],
                ].map(([time, title, description]) => (
                  <div key={time} className="flex gap-4">

                    <div className="w-14 shrink-0 pt-0.5 text-sm font-medium text-zinc-500">
                      {time}
                    </div>

                    <div className="relative border-l border-zinc-200 pl-6">
                      <div className="absolute -left-1.5 top-1 h-3 w-3 rounded-full bg-zinc-900" />

                      <p className="font-medium">
                        {title}
                      </p>

                      <p className="mt-1 text-sm leading-6 text-zinc-500">
                        {description}
                      </p>
                    </div>

                  </div>
                ))}

              </div>
            </div>

            {/* CLOSE */}
            <div className="mt-8 flex justify-end">
              <button
                onClick={() => setShowDetails(false)}
                className="rounded-xl bg-zinc-900 px-5 py-2.5 text-sm font-medium text-white hover:bg-zinc-700"
              >
                Done
              </button>
            </div>

          </div>
        </div>
      )}
    </main>
  );
}