import React from 'react';
import { Play, Sparkles, RotateCcw, ShieldCheck, Scale, Award } from 'lucide-react';
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
    { role: 'BUSINESS_OWNER', label: 'Business Owner', icon: <Scale className="w-3.5 h-3.5" /> },
    { role: 'INSPECTOR', label: 'Inspector', icon: <ShieldCheck className="w-3.5 h-3.5" /> },
    { role: 'ADMIN', label: 'Central Admin', icon: <Award className="w-3.5 h-3.5" /> },
    { role: 'PUBLIC', label: 'Public Portal', icon: <Sparkles className="w-3.5 h-3.5" /> }
  ];

  return (
    <header className="sticky top-0 z-40 bg-white/95 backdrop-blur-md border-b border-slate-200/80 shadow-xs">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-3.5">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
          
          {/* Brand & Landing Link */}
          <div className="flex items-center justify-between">
            <button
              onClick={() => setActiveScreen('LANDING')}
              className="flex items-center gap-3 text-left group hover:opacity-90 transition-opacity"
            >
              <div className="w-10 h-10 rounded-xl bg-slate-900 flex items-center justify-center text-cyan-400 shadow-sm border border-slate-800">
                <Scale className="w-5 h-5" />
              </div>
              <div>
                <div className="flex items-center gap-1.5">
                  <span className="text-[10px] font-extrabold tracking-widest text-cyan-700 uppercase">
                    GovVerify System
                  </span>
                  <span className="inline-block w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
                  <span className="text-[10px] font-semibold text-emerald-700">ISO 17025 Certified</span>
                </div>
                <h1 className="text-lg font-black text-slate-900 tracking-tight flex items-center gap-1.5">
                  Legal Metrology Verification
                </h1>
              </div>
            </button>

            {/* Mobile Actions */}
            <div className="flex items-center gap-2 sm:hidden">
              <button
                onClick={() => runDemoFlow()}
                className="inline-flex items-center gap-1 px-3 py-1.5 bg-cyan-600 text-white rounded-lg text-xs font-bold shadow-xs hover:bg-cyan-700"
              >
                <Play className="w-3 h-3 fill-current" />
                Demo
              </button>
            </div>
          </div>

          {/* Desktop Right Bar: 1-Click Demo, Reset Data, Role Selector & User Badge */}
          <div className="flex flex-wrap items-center justify-between sm:justify-end gap-2.5">
            
            {/* Quick Demo & Reset Buttons */}
            <div className="hidden sm:flex items-center gap-2">
              <button
                onClick={() => runDemoFlow()}
                title="Run end-to-end statutory verification workflow"
                className="inline-flex items-center gap-1.5 px-3.5 py-1.5 bg-slate-900 text-white rounded-xl text-xs font-bold shadow-xs hover:bg-slate-800 border border-slate-800 transition-all active:scale-95"
              >
                <Play className="w-3.5 h-3.5 text-cyan-400 fill-cyan-400" />
                Run Full Demo
              </button>

              <button
                onClick={resetData}
                title="Reset local state to defaults"
                className="p-1.5 text-slate-500 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors"
              >
                <RotateCcw className="w-4 h-4" />
              </button>
            </div>

            {/* Role Switcher Pill Group */}
            <div className="flex items-center p-1 bg-slate-100/90 rounded-xl border border-slate-200/70">
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
                    className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-lg text-xs font-bold transition-all ${
                      isActive
                        ? 'bg-white text-slate-900 shadow-xs border border-slate-200/80'
                        : 'text-slate-600 hover:text-slate-900'
                    }`}
                  >
                    <span className={isActive ? 'text-cyan-600' : 'text-slate-500'}>
                      {opt.icon}
                    </span>
                    <span className="hidden md:inline">{opt.label}</span>
                    <span className="md:hidden">{opt.label.split(' ')[0]}</span>
                  </button>
                );
              })}
            </div>

            {/* User Profile Pill */}
            <div className="hidden lg:flex items-center gap-2 pl-2 border-l border-slate-200">
              <div className="w-8 h-8 rounded-full bg-slate-800 text-cyan-300 font-bold text-xs flex items-center justify-center shadow-xs">
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
