import React, { useMemo } from 'react';
import { ShieldCheck } from 'lucide-react';

interface MetrologyQrCodeProps {
  data: string;
  size?: number;
  showEmblem?: boolean;
  className?: string;
}

export const MetrologyQrCode: React.FC<MetrologyQrCodeProps> = ({
  data,
  size = 140,
  showEmblem = true,
  className = ''
}) => {
  const matrixSize = 25;

  const matrix = useMemo(() => {
    const grid: boolean[][] = Array.from({ length: matrixSize }, () =>
      Array(matrixSize).fill(false)
    );

    // Deterministic hash based on data string
    let hash = 0;
    for (let i = 0; i < data.length; i++) {
      hash = (hash << 5) - hash + data.charCodeAt(i);
      hash |= 0;
    }
    hash = Math.abs(hash);

    // 1. Finder patterns (Top-Left, Top-Right, Bottom-Left)
    const drawFinder = (startR: number, startC: number) => {
      for (let r = 0; r < 7; r++) {
        for (let c = 0; c < 7; c++) {
          const isOuter = r === 0 || r === 6 || c === 0 || c === 6;
          const isInner = r >= 2 && r <= 4 && c >= 2 && c <= 4;
          grid[startR + r][startC + c] = isOuter || isInner;
        }
      }
    };

    drawFinder(0, 0);
    drawFinder(0, matrixSize - 7);
    drawFinder(matrixSize - 7, 0);

    // 2. Timing patterns
    for (let i = 7; i < matrixSize - 7; i++) {
      grid[6][i] = i % 2 === 0;
      grid[i][6] = i % 2 === 0;
    }

    // 3. Fill pseudo-random cells
    for (let r = 0; r < matrixSize; r++) {
      for (let c = 0; c < matrixSize; c++) {
        const inFinder1 = r <= 7 && c <= 7;
        const inFinder2 = r <= 7 && c >= matrixSize - 8;
        const inFinder3 = r >= matrixSize - 8 && c <= 7;
        const isTiming = r === 6 || c === 6;

        if (!inFinder1 && !inFinder2 && !inFinder3 && !isTiming) {
          const charIndex = (r * matrixSize + c) % (data.length || 1);
          const charVal = data.charCodeAt(charIndex) || 0;
          const pseudoRand = ((hash * (r + 1) + c * 31 + charVal) % 100);
          grid[r][c] = pseudoRand > 48;
        }
      }
    }

    return grid;
  }, [data]);

  return (
    <div
      className={`relative inline-flex flex-col items-center justify-center p-2.5 bg-white rounded-xl border border-slate-200 shadow-sm ${className}`}
      style={{ width: size, height: size }}
    >
      <svg
        viewBox={`0 0 ${matrixSize} ${matrixSize}`}
        className="w-full h-full rounded"
      >
        {matrix.map((row, r) =>
          row.map((cell, c) => {
            // Skip center if emblem is shown
            if (showEmblem && r >= 10 && r <= 14 && c >= 10 && c <= 14) {
              return null;
            }
            if (!cell) return null;
            return (
              <rect
                key={`${r}-${c}`}
                x={c}
                y={r}
                width={1}
                height={1}
                fill="#0f172a"
              />
            );
          })
        )}
      </svg>

      {showEmblem && (
        <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
          <div className="w-7 h-7 bg-slate-900 rounded-md border border-cyan-400/50 shadow-md flex items-center justify-center">
            <ShieldCheck className="w-4 h-4 text-cyan-400" />
          </div>
        </div>
      )}
    </div>
  );
};
