import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import sys
import os

# ── Style ─────────────────────────────────────────────────────
plt.style.use("dark_background")
plt.rcParams["figure.facecolor"] = "#13141a"
plt.rcParams["axes.facecolor"]   = "#1c1e2a"
plt.rcParams["axes.edgecolor"]   = "#2a2d3e"
plt.rcParams["axes.labelcolor"]  = "#9ba3be"
plt.rcParams["axes.titlecolor"]  = "#e8eaf0"
plt.rcParams["xtick.color"]      = "#9ba3be"
plt.rcParams["ytick.color"]      = "#9ba3be"
plt.rcParams["text.color"]       = "#e8eaf0"
plt.rcParams["grid.color"]       = "#2a2d3e"

COLORS = {
    "BubbleSort":    "#ff5c6a",
    "InsertionSort": "#ffb347",
    "SelectionSort": "#f7c948",
    "MergeSort":     "#4f8ef7",
    "QuickSort":     "#00d4aa",
    "HeapSort":      "#b48ef7",
}

# ── Load ───────────────────────────────────────────────────────
path = sys.argv[1] if len(sys.argv) > 1 else "results.csv"
if not os.path.exists(path):
    print(f"File not found: {path}")
    sys.exit(1)

df = pd.read_csv(path)
algos = sorted(df["Algorithm"].unique())
sizes = sorted(df["ArraySize"].unique())
colors = [COLORS.get(a, "#9ba3be") for a in algos]

# Pick largest size for single-size charts
big = df[df["ArraySize"] == sizes[-1]]
avg = big.groupby("Algorithm")["AvgRuntimeNs"].mean()
mn  = big.groupby("Algorithm")["MinRuntimeNs"].mean()
mx  = big.groupby("Algorithm")["MaxRuntimeNs"].mean()
cmp = big.groupby("Algorithm")["Comparisons"].mean()
swp = big.groupby("Algorithm")["Interchanges"].mean()

fig, axes = plt.subplots(2, 2, figsize=(14, 9))
fig.suptitle("Sorting Algorithm Analysis", fontsize=15, fontweight="bold")
x = np.arange(len(algos))

# ── Graph 1 — Avg Runtime ──────────────────────────────────────
ax = axes[0, 0]
ax.bar(x, avg.reindex(algos), color=colors, alpha=0.85, edgecolor="#000", linewidth=0.5)
ax.set_yscale("log")
ax.set_title("Average Runtime (Log Scale)")
ax.set_xticks(x); ax.set_xticklabels(algos, rotation=15, ha="right")
ax.set_ylabel("ns (log scale)"); ax.grid(axis="y", alpha=0.3, which="both")

# ── Graph 2 — Min / Avg / Max ──────────────────────────────────
ax = axes[0, 1]
w = 0.25
ax.bar(x - w, mn.reindex(algos),  w*0.9, label="Min", color="#00d4aa", alpha=0.85)
ax.bar(x,     avg.reindex(algos), w*0.9, label="Avg", color="#4f8ef7", alpha=0.85)
ax.bar(x + w, mx.reindex(algos),  w*0.9, label="Max", color="#ff5c6a", alpha=0.85)
ax.set_yscale("log")
ax.set_title("Min / Avg / Max Runtime (Log Scale)")
ax.set_xticks(x); ax.set_xticklabels(algos, rotation=15, ha="right")
ax.set_ylabel("ns (log scale)"); ax.legend(); ax.grid(axis="y", alpha=0.3, which="both")

# ── Graph 3 — Comparisons vs Interchanges ─────────────────────
ax = axes[1, 0]
ax.bar(x - 0.2, cmp.reindex(algos), 0.38, label="Comparisons",  color="#4f8ef7", alpha=0.85)
ax.bar(x + 0.2, swp.reindex(algos), 0.38, label="Interchanges", color="#00d4aa", alpha=0.85)
ax.set_title("Comparisons vs Interchanges")
ax.set_xticks(x); ax.set_xticklabels(algos, rotation=15, ha="right")
ax.set_ylabel("Count"); ax.legend(); ax.grid(axis="y", alpha=0.3)

# ── Graph 4 — Scaling ──────────────────────────────────────────
ax = axes[1, 1]
if len(sizes) < 2:
    ax.text(0.5, 0.5, "Need multiple sizes\nfor scaling chart",
            ha="center", va="center", transform=ax.transAxes, color="#9ba3be")
else:
    data = df[df["ArrayType"] == "RANDOM"] if "RANDOM" in df["ArrayType"].values else df
    for algo in algos:
        subset = data[data["Algorithm"] == algo].groupby("ArraySize")["AvgRuntimeNs"].mean()
        ax.plot(subset.index, subset.values, marker="o", linewidth=2,
                markersize=5, label=algo, color=COLORS.get(algo, "#9ba3be"))
    ax.set_yscale("log")  # ← this one line fixes everything
    ax.legend(fontsize=8); ax.grid(alpha=0.3, which="both")

ax.set_title("Runtime vs Array Size (Scaling — Log)")
ax.set_xlabel("Array Size"); ax.set_ylabel("ns (log scale)")

plt.tight_layout()
plt.show() 