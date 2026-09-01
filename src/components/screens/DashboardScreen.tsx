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
      
      {/* Navigation Tabs Bar */}
      <div className="bg-white rounded-3xl p-1.5 border border-slate-200/80 shadow-xs flex items-center gap-1.5 overflow-x-auto">
        {tabs.map(tab => {
          const isActive = selectedTab === tab.id;

          return (
            <button
              key={tab.id}
              onClick={() => setSelectedTab(tab.id)}
              className={`inline-flex items-center gap-2 px-4 py-2.5 rounded-2xl text-xs font-extrabold transition-all whitespace-nowrap ${
                isActive
                  ? 'bg-slate-900 text-white shadow-sm'
                  : 'text-slate-600 hover:text-slate-900 hover:bg-slate-50'
              }`}
            >
              <span className={isActive ? 'text-cyan-400' : 'text-slate-400'}>
                {tab.icon}
              </span>
              <span>{tab.label}</span>
              {tab.count !== undefined && tab.count > 0 && (
                <span
                  className={`px-2 py-0.5 rounded-full text-[10px] font-black ${
                    isActive
                      ? 'bg-cyan-500/20 text-cyan-300'
                      : 'bg-slate-100 text-slate-600'
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
      <div className="animate-in fade-in duration-200">
        {selectedTab === 'INSTRUMENTS' && <InstrumentsScreen />}
        {selectedTab === 'REQUESTS' && <VerificationRequestsScreen />}
        {selectedTab === 'ADMIN' && <AdminAuthorityScreen />}
        {selectedTab === 'PUBLIC_VERIFY' && <PublicQrVerificationScreen />}
      </div>

    </div>
  );
};
