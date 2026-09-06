"use client";

type Props = {
  price?: number;
  zScore?: number;
  volume?: number;
  relative?: number;
  score: number;
};

export default function ExplanationPanel({
  price,
  zScore,
  volume,
  relative,
  score,
}: Props) {
  const move = price ? `${(price * 100).toFixed(2)}%` : "an unusual move";
  const anomaly = zScore ? `${zScore.toFixed(1)}σ` : "an unusual level";
  const vol = volume ? `${volume.toFixed(2)}×` : "elevated";
  const divergence = relative
    ? `${(relative * 100).toFixed(2)}%`
    : "materially";

  return (
    <div className="rounded-2xl border border-zinc-200 bg-zinc-50 p-5">
      <div className="flex items-center justify-between">
        <p className="text-xs font-semibold tracking-wide text-zinc-500">
          PULSE EXPLANATION
        </p>
        <span className="text-xs text-zinc-400">
          Evidence-based
        </span>
      </div>

      <p className="mt-4 text-lg font-medium leading-7">
        HDFC Bank is showing a {move} move that is highly unusual
        at {anomaly}. Trading volume is {vol} normal, while the
        stock is underperforming the market by {divergence}.
      </p>

      <p className="mt-3 text-sm leading-6 text-zinc-500">
        Because price, volume and relative-performance signals
        agree, this is materially different from a routine
        fluctuation. Pulse assigned an attention score of{" "}
        <span className="font-semibold text-zinc-700">{score}</span>.
      </p>

      <div className="mt-4 flex items-center gap-2 text-xs text-zinc-400">
        <span>✓ Price signal</span>
        <span>·</span>
        <span>✓ Volume signal</span>
        <span>·</span>
        <span>✓ Relative signal</span>
      </div>
    </div>
  );
}