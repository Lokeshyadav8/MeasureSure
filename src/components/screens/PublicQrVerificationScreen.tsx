import React, { useState } from 'react';
import {
  QrCode,
  Search,
  CheckCircle2,
  AlertTriangle,
  Award,
  Eye,
  Building,
  Sparkles,
  Camera,
  Scan,
  ShieldCheck
} from 'lucide-react';
import { useMetrology } from '../../context/MetrologyContext';
import { MetrologyQrCode } from '../common/QrCodeGenerator';

export const PublicQrVerificationScreen: React.FC = () => {
  const {
    certificates,
    publicSearchQuery,
    publicSearchResult,
    publicHasSearched,
    searchPublicCertificate,
    setSelectedCertificate,
    setShowCertificateModal
  } = useMetrology();

  const [inputVal, setInputVal] = useState(publicSearchQuery || 'CERT-2026-NLM-0841');
  const [isScanningSim, setIsScanningSim] = useState(false);

  const sampleCerts = [
    { code: 'CERT-2026-NLM-0841', label: 'Apex Heavy Weighbridge (Valid Active)' },
    { code: 'CERT-2025-NLM-0219', label: 'PetroMax Fuel Pump #4 (Expiring Soon)' },
    { code: 'CERT-2024-NLM-0782', label: 'Crane Scale (Expired/Failed)' }
  ];

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    searchPublicCertificate(inputVal);
  };

  const handleQuickSelect = (code: string) => {
    setInputVal(code);
    setIsScanningSim(true);
    setTimeout(() => {
      setIsScanningSim(false);
      searchPublicCertificate(code);
    }, 600);
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      
      {/* Header Banner */}
      <div className="text-center space-y-2 max-w-2xl mx-auto">
        <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs font-bold shadow-xs">
          <ShieldCheck className="w-4 h-4 text-emerald-600" />
          <span>Public Legal Metrology Verification Portal</span>
        </div>
        <h2 className="text-2xl sm:text-3xl font-black text-slate-900 tracking-tight">
          Verify Official Holographic Seal & QR Code
        </h2>
        <p className="text-xs sm:text-sm text-slate-600 font-medium">
          Scan the QR sticker affixed to any commercial scale, fuel dispenser, or weighbridge to verify its statutory legal calibration.
        </p>
      </div>

      {/* Scanner Box & Input Form */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6 items-center">
        
        {/* Animated Scanner Viewfinder */}
        <div className="relative aspect-square max-w-xs mx-auto w-full bg-slate-950 rounded-3xl p-4 shadow-xl border-4 border-slate-800 flex flex-col items-center justify-between overflow-hidden">
          
          <div className="w-full flex items-center justify-between text-[11px] text-cyan-400 font-mono font-bold z-10">
            <span className="flex items-center gap-1.5">
              <Camera className="w-3.5 h-3.5" />
              VIEWFINDER ACTIVE
            </span>
            <span className="w-2 h-2 rounded-full bg-cyan-400 animate-ping"></span>
          </div>

          {/* Scanner Target Box with Corner Reticles */}
          <div className="relative w-48 h-48 border border-cyan-500/30 rounded-2xl flex items-center justify-center">
            
            {/* 4 Corner brackets */}
            <div className="absolute -top-1 -left-1 w-5 h-5 border-t-2 border-l-2 border-cyan-400"></div>
            <div className="absolute -top-1 -right-1 w-5 h-5 border-t-2 border-r-2 border-cyan-400"></div>
            <div className="absolute -bottom-1 -left-1 w-5 h-5 border-b-2 border-l-2 border-cyan-400"></div>
            <div className="absolute -bottom-1 -right-1 w-5 h-5 border-b-2 border-r-2 border-cyan-400"></div>

            {/* Simulated QR Pattern inside */}
            <MetrologyQrCode
              data={inputVal || 'https://metrology.gov.verify'}
              size={140}
              showEmblem={true}
            />

            {/* Laser scanning beam */}
            <div className="absolute inset-x-0 top-0 h-1 bg-gradient-to-r from-transparent via-cyan-400 to-transparent shadow-lg shadow-cyan-400 animate-[scan_2s_ease-in-out_infinite]"></div>
          </div>

          <div className="text-[11px] text-slate-400 text-center font-medium z-10">
            {isScanningSim ? 'Simulating Optical QR Scan...' : 'Point camera at statutory seal on device'}
          </div>
        </div>

        {/* Search & Demo Chips Panel */}
        <div className="p-6 bg-white rounded-3xl border border-slate-200/90 shadow-xs space-y-4">
          <h3 className="text-sm font-extrabold text-slate-900">
            Search Certificate or Instrument ID
          </h3>

          <form onSubmit={handleSearch} className="space-y-3">
            <div className="relative">
              <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
              <input
                type="text"
                value={inputVal}
                onChange={e => setInputVal(e.target.value)}
                placeholder="e.g. CERT-2026-NLM-0841 or INST-WB-8801"
                className="w-full pl-10 pr-4 py-3 bg-slate-50 border border-slate-200 rounded-2xl text-xs sm:text-sm font-mono font-medium focus:bg-white focus:outline-hidden focus:ring-2 focus:ring-emerald-500 transition-all"
              />
            </div>

            <button
              type="submit"
              className="w-full py-3 bg-emerald-600 hover:bg-emerald-700 text-white rounded-2xl text-xs font-black shadow-md transition-all active:scale-95 flex items-center justify-center gap-2"
            >
              <CheckCircle2 className="w-4 h-4" />
              Verify Authenticity in National Registry
            </button>
          </form>

          {/* Quick Select Sample Chips */}
          <div className="space-y-2 pt-2 border-t border-slate-100">
            <span className="text-[11px] font-bold text-slate-500 uppercase tracking-wider block">
              Quick Test Sample Codes:
            </span>
            <div className="space-y-1.5">
              {sampleCerts.map(sample => (
                <button
                  key={sample.code}
                  type="button"
                  onClick={() => handleQuickSelect(sample.code)}
                  className="w-full text-left p-2.5 rounded-xl bg-slate-50 hover:bg-slate-100 border border-slate-200/80 transition-colors flex items-center justify-between text-xs group"
                >
                  <span className="font-mono font-bold text-slate-800 group-hover:text-emerald-700">
                    {sample.code}
                  </span>
                  <span className="text-[11px] text-slate-500 font-medium truncate max-w-[180px]">
                    {sample.label}
                  </span>
                </button>
              ))}
            </div>
          </div>

        </div>

      </div>

      {/* Verification Result Display */}
      {publicHasSearched && (
        <div className="animate-in fade-in slide-in-from-bottom-3 duration-300">
          {publicSearchResult ? (
            <div className={`p-6 rounded-3xl border shadow-md space-y-4 ${
              publicSearchResult.status === 'EXPIRED'
                ? 'bg-rose-50/70 border-rose-200'
                : 'bg-emerald-50/70 border-emerald-200'
            }`}>
              
              <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 pb-3 border-b border-slate-200/60">
                <div className="flex items-center gap-3">
                  <div className={`w-12 h-12 rounded-2xl flex items-center justify-center ${
                    publicSearchResult.status === 'EXPIRED'
                      ? 'bg-rose-600 text-white'
                      : 'bg-emerald-600 text-white'
                  }`}>
                    {publicSearchResult.status === 'EXPIRED' ? (
                      <AlertTriangle className="w-6 h-6" />
                    ) : (
                      <CheckCircle2 className="w-6 h-6" />
                    )}
                  </div>
                  <div>
                    <span className="text-[11px] font-bold uppercase tracking-wider text-slate-600">
                      National Registry Status
                    </span>
                    <h3 className="text-base sm:text-lg font-black text-slate-900">
                      {publicSearchResult.status === 'EXPIRED'
                        ? 'EXPIRED / RE-VERIFICATION REQUIRED'
                        : 'OFFICIALLY VERIFIED & ACTIVE'}
                    </h3>
                  </div>
                </div>

                <span className="px-3.5 py-1.5 bg-white font-mono font-bold text-xs rounded-xl shadow-xs border border-slate-200">
                  {publicSearchResult.certificateNumber}
                </span>
              </div>

              {/* Certificate Quick Specs */}
              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3 text-xs">
                <div className="p-3 bg-white rounded-2xl border border-slate-100">
                  <span className="text-slate-500 block text-[10px] uppercase font-bold">Instrument Name</span>
                  <span className="font-bold text-slate-900">{publicSearchResult.instrumentName}</span>
                </div>
                <div className="p-3 bg-white rounded-2xl border border-slate-100">
                  <span className="text-slate-500 block text-[10px] uppercase font-bold">Capacity & Make</span>
                  <span className="font-bold text-slate-900">{publicSearchResult.capacity} {publicSearchResult.unit} ({publicSearchResult.manufacturer})</span>
                </div>
                <div className="p-3 bg-white rounded-2xl border border-slate-100">
                  <span className="text-slate-500 block text-[10px] uppercase font-bold">Verified By</span>
                  <span className="font-bold text-slate-900">{publicSearchResult.inspectorName}</span>
                </div>
                <div className="p-3 bg-white rounded-2xl border border-slate-100">
                  <span className="text-slate-500 block text-[10px] uppercase font-bold">Valid Until</span>
                  <span className={`font-extrabold ${publicSearchResult.status === 'EXPIRED' ? 'text-rose-600' : 'text-emerald-700'}`}>
                    {publicSearchResult.validUntil}
                  </span>
                </div>
              </div>

              {/* Actions Footer */}
              <div className="flex items-center justify-between pt-1">
                <div className="text-[11px] text-slate-600 font-medium">
                  Tamper Seal Hologram: <code className="font-mono font-bold text-slate-900">{publicSearchResult.tamperSealNumber}</code>
                </div>

                <button
                  type="button"
                  onClick={() => {
                    setSelectedCertificate(publicSearchResult);
                    setShowCertificateModal(true);
                  }}
                  className="inline-flex items-center gap-1.5 px-4 py-2 bg-slate-900 text-white rounded-xl text-xs font-bold hover:bg-slate-800 shadow-xs"
                >
                  <Eye className="w-3.5 h-3.5 text-cyan-400" />
                  View Full Official Certificate Document
                </button>
              </div>

            </div>
          ) : (
            <div className="p-8 text-center bg-rose-50 rounded-3xl border border-rose-200 space-y-2">
              <AlertTriangle className="w-10 h-10 text-rose-600 mx-auto" />
              <h3 className="text-base font-bold text-rose-950">
                No Record Found in Legal Metrology Registry
              </h3>
              <p className="text-xs text-rose-800 max-w-md mx-auto">
                No certificate or instrument matching <code className="font-mono font-bold">"{publicSearchQuery}"</code> was found in the official registry. This seal may be unverified or unauthorized.
              </p>
            </div>
          )}
        </div>
      )}

    </div>
  );
};
