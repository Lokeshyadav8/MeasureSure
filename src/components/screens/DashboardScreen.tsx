import React from 'react';
import {
  Scale,
  ClipboardList,
  Award,
  QrCode,
  FlaskConical,
  Building,
  UserCheck
} from 'lucide-react';
import { useMetrology } from '../../context/MetrologyContext';
import { InstrumentsScreen } from './InstrumentsScreen';
import { VerificationRequestsScreen } from './VerificationRequestsScreen';
import { AdminAuthorityScreen } from './AdminAuthorityScreen';
import { PublicQrVerificationScreen } from './PublicQrVerificationScreen';
import { InspectionWorkspaceScreen } from './InspectionWorkspaceScreen';

export const DashboardScreen: React.FC = () => {
  const {
    userRole,
    selectedTab,
    setSelectedTab,
    activeScreen,
    instruments,
    requests
  } = useMetrology();

  if (activeScreen === 'INSPECTION_WORKSPACE') {
    return <InspectionWorkspaceScreen />;
  }

  const tabs: { id: 'INSTRUMENTS' | 'REQUESTS' | 'ADMIN' | 'PUBLIC_VERIFY'; label: string; icon: React.ReactNode; count?: number }[] = [
    {
      id: 'INSTRUMENTS',
      label: 'Measuring Instruments',
      icon: <Scale className="w-4 h-4" />,
      count: instruments.length
    },
    {
      id: 'REQUESTS',
      label: 'Verification Pipeline',
      icon: <ClipboardList className="w-4 h-4" />,
      count: requests.filter(r => r.status !== 'CERTIFICATE_GENERATED').length
    },
    {
      id: 'ADMIN',
      label: 'Directorate Oversight',
      icon: <Award className="w-4 h-4" />
    },
    {
      id: 'PUBLIC_VERIFY',
      label: 'Public QR Seal Verification',
      icon: <QrCode className="w-4 h-4" />
    }
  ];

  return (
    <div className="space-y-6">
      
      {/* Navigation Tabs Bar (Desktop & Tablet) */}
      <div className="bg-white rounded-3xl p-1.5 border border-slate-300 shadow-xs flex items-center gap-1.5 overflow-x-auto no-scrollbar">
        {tabs.map(tab => {
          const isActive = selectedTab === tab.id;

          return (
            <button
              key={tab.id}
              onClick={() => setSelectedTab(tab.id)}
              className={`inline-flex items-center gap-2 px-4 py-2.5 rounded-2xl text-xs font-black transition-all whitespace-nowrap shrink-0 ${
                isActive
                  ? 'bg-slate-950 text-white shadow-sm'
                  : 'text-slate-800 hover:text-black hover:bg-slate-100'
              }`}
            >
              <span className={isActive ? 'text-cyan-400 shrink-0' : 'text-slate-600 shrink-0'}>
                {tab.icon}
              </span>
              <span>{tab.label}</span>
              {tab.count !== undefined && tab.count > 0 && (
                <span
                  className={`px-2 py-0.5 rounded-full text-[10px] font-black shrink-0 ${
                    isActive
                      ? 'bg-cyan-500/30 text-cyan-300'
                      : 'bg-slate-200 text-slate-900'
                  }`}
                >
                  {tab.count}
                </span>
              )}
            </button>
          );
        })}
      </div>

      {/* Main Tab Screen Content */}
      <div className="animate-in fade-in duration-200 pb-20 sm:pb-0">
        {selectedTab === 'INSTRUMENTS' && <InstrumentsScreen />}
        {selectedTab === 'REQUESTS' && <VerificationRequestsScreen />}
        {selectedTab === 'ADMIN' && <AdminAuthorityScreen />}
        {selectedTab === 'PUBLIC_VERIFY' && <PublicQrVerificationScreen />}
      </div>

      {/* Mobile Bottom Navigation Bar (App Bar with safe area) */}
      <div className="fixed bottom-0 inset-x-0 z-40 bg-white/98 backdrop-blur-md border-t border-slate-300 shadow-xl sm:hidden pb-[max(env(safe-area-inset-bottom,0px),0.5rem)]">
        <div className="grid grid-cols-4 px-2 py-2">
          {tabs.map(tab => {
            const isActive = selectedTab === tab.id;
            const shortLabel = tab.id === 'INSTRUMENTS' ? 'Devices' : tab.id === 'REQUESTS' ? 'Requests' : tab.id === 'ADMIN' ? 'Oversight' : 'QR Verify';

            return (
              <button
                key={tab.id}
                onClick={() => setSelectedTab(tab.id)}
                className={`flex flex-col items-center justify-center py-1.5 px-1 rounded-xl transition-all relative ${
                  isActive ? 'text-cyan-950 font-black' : 'text-slate-700 font-bold'
                }`}
              >
                <div className={`p-1.5 rounded-lg ${isActive ? 'bg-cyan-100 text-cyan-900' : 'text-slate-600'}`}>
                  {tab.icon}
                </div>
                <span className="text-[11px] mt-0.5 tracking-tight truncate max-w-full font-bold">{shortLabel}</span>
                {tab.count !== undefined && tab.count > 0 && (
                  <span className="absolute top-1 right-2.5 w-4 h-4 bg-cyan-600 text-white rounded-full text-[9px] font-black flex items-center justify-center shadow-xs">
                    {tab.count}
                  </span>
                )}
              </button>
            );
          })}
        </div>
      </div>

    </div>
  );
};
