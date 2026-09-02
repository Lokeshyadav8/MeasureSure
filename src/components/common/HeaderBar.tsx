import React from 'react';
import { Play, Sparkles, RotateCcw, ShieldCheck, Scale, Award, Download } from 'lucide-react';
import { useMetrology } from '../../context/MetrologyContext';
import { UserRole } from '../../types';

export const HeaderBar: React.FC = () => {
  const {
    userRole,
    currentUser,
    setUserRole,
    runDemoFlow,
    resetData,
    setActiveScreen,
    activeScreen
  } = useMetrology();

  const roleOptions: { role: UserRole; label: string; icon: React.ReactNode }[] = [
    { role: 'BUSINESS_OWNER', label: 'Business Owner', icon: <Scale className="w-3.5 h-3.5 shrink-0" /> },
    { role: 'INSPECTOR', label: 'Inspector', icon: <ShieldCheck className="w-3.5 h-3.5 shrink-0" /> },
    { role: 'ADMIN', label: 'Central Admin', icon: <Award className="w-3.5 h-3.5 shrink-0" /> },
    { role: 'PUBLIC', label: 'Public Portal', icon: <Sparkles className="w-3.5 h-3.5 shrink-0" /> }
  ];

  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-slate-200/80 shadow-xs pt-[env(safe-area-inset-top,0px)]">
      {/* Statutory Top Micro-Banner */}
      <div className="bg-slate-900 text-slate-300 px-4 py-1 text-[10px] font-semibold flex items-center justify-between border-b border-slate-800">
        <div className="flex items-center gap-2 truncate">
          <span className="w-1.5 h-1.5 rounded-full bg-cyan-400 shrink-0"></span>
          <span className="text-cyan-300 font-bold uppercase tracking-wider shrink-0">DoCA Legal Metrology</span>
          <span className="text-slate-600 hidden sm:inline">•</span>
          <span className="hidden sm:inline text-slate-400 truncate">OIML R-76 NAWI Verification | PS SIH26035</span>
        </div>
        <div className="flex items-center gap-2 shrink-0">
          <span className="text-[10px] text-emerald-400 font-mono font-bold">ISO/IEC 17025</span>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-3 sm:px-6 lg:px-8 py-2.5 sm:py-3.5">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2.5">
          
          {/* Brand & Landing Link */}
          <div className="flex items-center justify-between">
            <button
              onClick={() => setActiveScreen('LANDING')}
              className="flex items-center gap-2.5 sm:gap-3 text-left group hover:opacity-90 transition-opacity"
            >
              <div className="w-9 h-9 sm:w-10 sm:h-10 rounded-xl bg-slate-900 flex items-center justify-center text-cyan-400 shadow-sm border border-slate-800 shrink-0">
                <Scale className="w-4 h-4 sm:w-5 sm:h-5" />
              </div>
              <div>
                <div className="flex items-center gap-1.5">
                  <span className="text-[10px] font-extrabold tracking-wider text-cyan-700 uppercase whitespace-nowrap">
                    GovVerify System
                  </span>
                  <span className="inline-block w-1.5 h-1.5 rounded-full bg-emerald-500 shrink-0"></span>
                  <span className="text-[10px] font-semibold text-emerald-700 hidden sm:inline whitespace-nowrap">Statutory Registry</span>
                </div>
                <h1 className="text-sm sm:text-base md:text-lg font-black text-slate-900 tracking-tight whitespace-nowrap">
                  Legal Metrology Verification
                </h1>
              </div>
            </button>

            {/* Mobile Action Buttons */}
            <div className="flex items-center gap-1.5 sm:hidden">
              <a
                href="/api/download-apk"
                download="LegalMetrology-Verification.apk"
                title="Download APK"
                className="inline-flex items-center gap-1 px-2 py-1.5 bg-cyan-50 text-cyan-700 border border-cyan-200 rounded-lg text-xs font-bold whitespace-nowrap active:scale-95"
              >
                <Download className="w-3.5 h-3.5 shrink-0" />
                <span>APK</span>
              </a>

              <button
                onClick={() => runDemoFlow()}
                className="inline-flex items-center gap-1 px-2.5 py-1.5 bg-cyan-600 hover:bg-cyan-700 text-white rounded-lg text-xs font-bold shadow-xs whitespace-nowrap active:scale-95"
              >
                <Play className="w-3 h-3 fill-current shrink-0" />
                Demo
              </button>

              <button
                onClick={resetData}
                title="Reset local state"
                className="p-1.5 text-slate-500 hover:text-slate-800 hover:bg-slate-100 rounded-lg transition-colors"
              >
                <RotateCcw className="w-3.5 h-3.5 shrink-0" />
              </button>
            </div>
          </div>

          {/* Desktop Right Bar: 1-Click Demo, Reset Data, Role Selector & User Badge */}
          <div className="flex flex-wrap items-center justify-between sm:justify-end gap-2">
            
            {/* Quick Demo, APK Download & Reset Buttons */}
            <div className="hidden sm:flex items-center gap-2">
              <a
                href="/api/download-apk"
                download="LegalMetrology-Verification.apk"
                title="Download Signed Android APK (.apk)"
                className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-cyan-50 hover:bg-cyan-100 text-cyan-800 border border-cyan-200/80 rounded-xl text-xs font-bold transition-all active:scale-95 whitespace-nowrap"
              >
                <Download className="w-3.5 h-3.5 text-cyan-600 shrink-0" />
                APK
              </a>

              <button
                onClick={() => runDemoFlow()}
                title="Run end-to-end statutory verification workflow"
                className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-slate-900 text-white rounded-xl text-xs font-bold shadow-xs hover:bg-slate-800 border border-slate-800 transition-all active:scale-95 whitespace-nowrap"
              >
                <Play className="w-3.5 h-3.5 text-cyan-400 fill-cyan-400 shrink-0" />
                Run Demo
              </button>

              <button
                onClick={resetData}
                title="Reset local state to defaults"
                className="p-1.5 text-slate-500 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors shrink-0"
              >
                <RotateCcw className="w-4 h-4" />
              </button>
            </div>

            {/* Role Switcher Pill Group */}
            <div className="flex items-center p-0.5 sm:p-1 bg-slate-100/90 rounded-xl border border-slate-200/70 overflow-x-auto no-scrollbar w-full sm:w-auto">
              {roleOptions.map(opt => {
                const isActive = userRole === opt.role;
                return (
                  <button
                    key={opt.role}
                    onClick={() => {
                      setUserRole(opt.role);
                      if (activeScreen === 'LANDING') {
                        setActiveScreen('DASHBOARD');
                      }
                    }}
                    className={`inline-flex items-center justify-center gap-1 px-2.5 sm:px-3 py-1 rounded-lg text-xs font-bold transition-all whitespace-nowrap flex-1 sm:flex-initial ${
                      isActive
                        ? 'bg-white text-slate-900 shadow-xs border border-slate-200/80'
                        : 'text-slate-600 hover:text-slate-900'
                    }`}
                  >
                    <span className={isActive ? 'text-cyan-600 shrink-0' : 'text-slate-500 shrink-0'}>
                      {opt.icon}
                    </span>
                    <span className="hidden lg:inline">{opt.label}</span>
                    <span className="lg:hidden">{opt.label.split(' ')[0]}</span>
                  </button>
                );
              })}
            </div>

            {/* User Profile Pill */}
            <div className="hidden lg:flex items-center gap-2 pl-2 border-l border-slate-200">
              <div className="w-8 h-8 rounded-full bg-slate-800 text-cyan-300 font-bold text-xs flex items-center justify-center shadow-xs shrink-0">
                {currentUser.name.split(' ').map(n => n[0]).join('').slice(0, 2).toUpperCase()}
              </div>
              <div className="text-left leading-tight">
                <div className="text-xs font-bold text-slate-900 truncate max-w-[130px]">
                  {currentUser.name}
                </div>
                <div className="text-[10px] text-slate-600 font-medium truncate max-w-[130px]">
                  {currentUser.businessOrDepartment}
                </div>
              </div>
            </div>

          </div>
        </div>
      </div>
    </header>
  );
};
