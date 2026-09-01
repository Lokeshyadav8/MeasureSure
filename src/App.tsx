import React from 'react';
import { MetrologyProvider, useMetrology } from './context/MetrologyContext';
import { HeaderBar } from './components/common/HeaderBar';
import { LandingScreen } from './components/screens/LandingScreen';
import { DashboardScreen } from './components/screens/DashboardScreen';
import { InspectionWorkspaceScreen } from './components/screens/InspectionWorkspaceScreen';
import { CertificateModal } from './components/modals/CertificateModal';
import { RegisterInstrumentModal } from './components/modals/RegisterInstrumentModal';
import { InstrumentDetailsModal } from './components/modals/InstrumentDetailsModal';

const AppContent: React.FC = () => {
  const {
    activeScreen,
    selectedCertificate,
    showCertificateModal,
    setShowCertificateModal,
    showRegisterModal,
    setShowRegisterModal,
    selectedInstrument,
    setSelectedInstrument,
    requestVerification,
    setSelectedCertificate,
    certificates
  } = useMetrology();

  const matchingCert = selectedInstrument
    ? certificates.find(c => c.instrumentId === selectedInstrument.instrumentId || c.certificateNumber === selectedInstrument.certificateId) || null
    : null;

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 flex flex-col selection:bg-cyan-500 selection:text-white font-sans antialiased">
      <HeaderBar />

      <main className="flex-1 max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6">
        {activeScreen === 'LANDING' && <LandingScreen />}
        {activeScreen === 'DASHBOARD' && <DashboardScreen />}
        {activeScreen === 'INSPECTION_WORKSPACE' && <InspectionWorkspaceScreen />}
      </main>

      {/* Global Modals */}
      <CertificateModal
        isOpen={showCertificateModal}
        certificate={selectedCertificate}
        onClose={() => setShowCertificateModal(false)}
      />

      <RegisterInstrumentModal
        isOpen={showRegisterModal}
        onClose={() => setShowRegisterModal(false)}
      />

      <InstrumentDetailsModal
        isOpen={!!selectedInstrument}
        instrument={selectedInstrument}
        certificate={matchingCert}
        onClose={() => setSelectedInstrument(null)}
        onRequestVerification={requestVerification}
        onViewCertificate={(cert) => {
          setSelectedCertificate(cert);
          setShowCertificateModal(true);
        }}
      />
    </div>
  );
};

export function App() {
  return (
    <MetrologyProvider>
      <AppContent />
    </MetrologyProvider>
  );
}

export default App;
