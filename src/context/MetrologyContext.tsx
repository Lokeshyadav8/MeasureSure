import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import {
  UserRole,
  InstrumentEntity,
  VerificationRequestEntity,
  InspectionEntity,
  CertificateEntity,
  AuditLogEntity,
  TestReading,
  AnomalyDetectionResult,
  OcrExtractionResult,
  UserEntity
} from '../types';
import {
  INITIAL_USERS,
  INITIAL_INSTRUMENTS,
  INITIAL_REQUESTS,
  INITIAL_CERTIFICATES,
  INITIAL_AUDIT_LOGS
} from '../data/seedData';
import { GeminiMetrologyService } from '../services/geminiService';
import confetti from 'canvas-confetti';

interface MetrologyContextType {
  userRole: UserRole;
  currentUser: UserEntity;
  activeScreen: 'LANDING' | 'DASHBOARD' | 'INSPECTION_WORKSPACE';
  selectedTab: 'INSTRUMENTS' | 'REQUESTS' | 'ADMIN' | 'PUBLIC_VERIFY';
  instruments: InstrumentEntity[];
  requests: VerificationRequestEntity[];
  inspections: InspectionEntity[];
  certificates: CertificateEntity[];
  auditLogs: AuditLogEntity[];
  
  // Inspection Workspace
  activeInspectionRequest: VerificationRequestEntity | null;
  activeInspectionInstrument: InstrumentEntity | null;
  testReadings: TestReading[];
  aiAnomalyResult: AnomalyDetectionResult | null;
  isAiAnalyzing: boolean;

  // Selected details
  selectedInstrument: InstrumentEntity | null;
  selectedCertificate: CertificateEntity | null;
  showCertificateModal: boolean;
  showRegisterModal: boolean;

  // OCR
  ocrResult: OcrExtractionResult | null;
  isOcrScanning: boolean;

  // Search & Filters
  searchQuery: string;
  statusFilter: string;
  categoryFilter: string;

  // Public Verify
  publicSearchQuery: string;
  publicSearchResult: CertificateEntity | null;
  publicHasSearched: boolean;

  // Actions
  setUserRole: (role: UserRole) => void;
  setActiveScreen: (screen: 'LANDING' | 'DASHBOARD' | 'INSPECTION_WORKSPACE') => void;
  setSelectedTab: (tab: 'INSTRUMENTS' | 'REQUESTS' | 'ADMIN' | 'PUBLIC_VERIFY') => void;
  setSearchQuery: (query: string) => void;
  setStatusFilter: (filter: string) => void;
  setCategoryFilter: (category: string) => void;
  setSelectedInstrument: (inst: InstrumentEntity | null) => void;
  setSelectedCertificate: (cert: CertificateEntity | null) => void;
  setShowCertificateModal: (show: boolean) => void;
  setShowRegisterModal: (show: boolean) => void;
  
  requestVerification: (instrument: InstrumentEntity) => void;
  registerInstrument: (data: {
    name: string;
    type: string;
    category: string;
    manufacturer: string;
    modelNumber: string;
    serialNumber: string;
    capacity: string;
    unitOfMeasurement: string;
    location: string;
    permissibleTolerance: number;
  }) => void;

  openInspectionWorkspace: (request: VerificationRequestEntity) => void;
  addTestReading: (reading: Omit<TestReading, 'id' | 'errorPercentage' | 'passed'>) => void;
  updateTestReading: (id: string, actualReading: number) => void;
  removeTestReading: (id: string) => void;
  runAiAnomalyAnalysis: () => Promise<void>;
  completeInspection: (params: {
    isPassed: boolean;
    inspectorNotes: string;
    tamperSealNumber: string;
    environmentTempC?: number;
    environmentHumidityPercent?: number;
  }) => Promise<void>;
  
  runAiOcrScan: () => Promise<void>;
  searchPublicCertificate: (query: string) => void;
  runDemoFlow: () => Promise<void>;
  resetData: () => void;
}

const MetrologyContext = createContext<MetrologyContextType | undefined>(undefined);

const STORAGE_KEY = 'LEGAL_METROLOGY_STATE_V1';

export const MetrologyProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  // Load initial from localStorage or defaults
  const loadSaved = () => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) {
        return JSON.parse(saved);
      }
    } catch (e) {
      console.warn('Failed to load from localStorage:', e);
    }
    return null;
  };

  const initialData = loadSaved();

  const [userRole, setUserRoleState] = useState<UserRole>('BUSINESS_OWNER');
  const [activeScreen, setActiveScreen] = useState<'LANDING' | 'DASHBOARD' | 'INSPECTION_WORKSPACE'>('LANDING');
  const [selectedTab, setSelectedTab] = useState<'INSTRUMENTS' | 'REQUESTS' | 'ADMIN' | 'PUBLIC_VERIFY'>('INSTRUMENTS');

  const [instruments, setInstruments] = useState<InstrumentEntity[]>(initialData?.instruments || INITIAL_INSTRUMENTS);
  const [requests, setRequests] = useState<VerificationRequestEntity[]>(initialData?.requests || INITIAL_REQUESTS);
  const [inspections, setInspections] = useState<InspectionEntity[]>(initialData?.inspections || []);
  const [certificates, setCertificates] = useState<CertificateEntity[]>(initialData?.certificates || INITIAL_CERTIFICATES);
  const [auditLogs, setAuditLogs] = useState<AuditLogEntity[]>(initialData?.auditLogs || INITIAL_AUDIT_LOGS);

  // Modals & Active Workspace state
  const [selectedInstrument, setSelectedInstrument] = useState<InstrumentEntity | null>(null);
  const [selectedCertificate, setSelectedCertificate] = useState<CertificateEntity | null>(null);
  const [showCertificateModal, setShowCertificateModal] = useState<boolean>(false);
  const [showRegisterModal, setShowRegisterModal] = useState<boolean>(false);

  const [activeInspectionRequest, setActiveInspectionRequest] = useState<VerificationRequestEntity | null>(null);
  const [testReadings, setTestReadings] = useState<TestReading[]>([]);
  const [aiAnomalyResult, setAiAnomalyResult] = useState<AnomalyDetectionResult | null>(null);
  const [isAiAnalyzing, setIsAiAnalyzing] = useState<boolean>(false);

  const [ocrResult, setOcrResult] = useState<OcrExtractionResult | null>(null);
  const [isOcrScanning, setIsOcrScanning] = useState<boolean>(false);

  // Search & Filters
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<string>('ALL');
  const [categoryFilter, setCategoryFilter] = useState<string>('ALL');

  // Public Search
  const [publicSearchQuery, setPublicSearchQuery] = useState<string>('');
  const [publicSearchResult, setPublicSearchResult] = useState<CertificateEntity | null>(null);
  const [publicHasSearched, setPublicHasSearched] = useState<boolean>(false);

  // Current active user
  const currentUser = INITIAL_USERS.find(u => u.role === userRole) || INITIAL_USERS[0];

  // Save to localStorage on state changes
  useEffect(() => {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify({
        instruments,
        requests,
        inspections,
        certificates,
        auditLogs
      }));
    } catch (e) {
      console.warn('Failed to save to localStorage:', e);
    }
  }, [instruments, requests, inspections, certificates, auditLogs]);

  const setUserRole = (role: UserRole) => {
    setUserRoleState(role);
    if (role === 'PUBLIC') {
      setSelectedTab('PUBLIC_VERIFY');
    } else if (role === 'ADMIN') {
      setSelectedTab('ADMIN');
    } else if (role === 'INSPECTOR') {
      setSelectedTab('REQUESTS');
    } else {
      setSelectedTab('INSTRUMENTS');
    }
  };

  const addAuditLog = useCallback((action: string, instrumentId: string, details: string) => {
    const newLog: AuditLogEntity = {
      id: 'log-' + Date.now() + '-' + Math.random().toString(36).substring(2, 5),
      timestamp: Date.now(),
      action,
      performedBy: currentUser.name,
      role: userRole,
      instrumentId,
      details
    };
    setAuditLogs(prev => [newLog, ...prev]);
  }, [currentUser.name, userRole]);

  // Business Owner: Request Verification
  const requestVerification = useCallback((instrument: InstrumentEntity) => {
    const reqId = `REQ-2026-${Math.floor(100 + Math.random() * 900)}`;
    const newReq: VerificationRequestEntity = {
      id: 'req-' + Date.now(),
      requestId: reqId,
      instrumentId: instrument.instrumentId,
      instrumentName: instrument.name,
      instrumentType: instrument.type,
      ownerId: currentUser.userId,
      businessName: instrument.ownerBusiness || currentUser.businessOrDepartment,
      status: 'SUBMITTED',
      submissionDate: new Date().toISOString().split('T')[0],
      scheduledDate: 'Pending Assignment',
      assignedInspectorId: '',
      assignedInspectorName: 'Unassigned',
      notes: `Statutory verification requested for ${instrument.name}. Permissible tolerance: ±${(instrument.permissibleTolerance * 100).toFixed(2)}%.`,
      inspectionFee: 250.0,
      paymentStatus: 'PAID'
    };

    setRequests(prev => [newReq, ...prev]);
    setInstruments(prev => prev.map(inst =>
      inst.id === instrument.id ? { ...inst, status: 'SUBMITTED' } : inst
    ));

    addAuditLog('VERIFICATION_REQUESTED', instrument.instrumentId, `Verification request ${reqId} created by ${currentUser.name}.`);
  }, [addAuditLog, currentUser.businessOrDepartment, currentUser.name, currentUser.userId]);

  // Business Owner: Register new Instrument
  const registerInstrument = useCallback((data: {
    name: string;
    type: string;
    category: string;
    manufacturer: string;
    modelNumber: string;
    serialNumber: string;
    capacity: string;
    unitOfMeasurement: string;
    location: string;
    permissibleTolerance: number;
  }) => {
    const instId = `INST-${data.type.substring(0, 2).toUpperCase()}-${Math.floor(1000 + Math.random() * 9000)}`;
    const newInst: InstrumentEntity = {
      id: 'inst-' + Date.now(),
      instrumentId: instId,
      name: data.name,
      type: data.type,
      category: data.category,
      manufacturer: data.manufacturer,
      modelNumber: data.modelNumber,
      serialNumber: data.serialNumber,
      capacity: data.capacity,
      unitOfMeasurement: data.unitOfMeasurement,
      location: data.location,
      ownerBusiness: currentUser.businessOrDepartment,
      ownerId: currentUser.userId,
      permissibleTolerance: data.permissibleTolerance,
      status: 'DRAFT',
      riskScore: 'LOW',
      riskReason: 'Newly registered instrument. Initial statutory verification pending.',
      lastVerificationDate: 'None',
      nextVerificationDate: 'Pending Initial Verification',
      certificateId: null,
      qrPayload: `https://metrology.gov.verify/inst/${instId}`,
      createdAt: Date.now(),
      updatedAt: Date.now()
    };

    setInstruments(prev => [newInst, ...prev]);
    addAuditLog('INSTRUMENT_REGISTERED', instId, `Registered ${data.name} (${instId}) in legal metrology directory.`);
  }, [addAuditLog, currentUser.businessOrDepartment, currentUser.userId]);

  // Inspector: Open workspace
  const openInspectionWorkspace = useCallback((request: VerificationRequestEntity) => {
    const instrument = instruments.find(i => i.instrumentId === request.instrumentId);
    setActiveInspectionRequest(request);

    // Populate initial default calibration test points based on instrument capacity
    const capNum = parseFloat(instrument?.capacity || '50') || 50;
    const tol = instrument?.permissibleTolerance || 0.05;

    const points = [0.1, 0.25, 0.5, 0.75, 1.0].map((frac, idx) => {
      const standard = Math.round(capNum * frac * 100) / 100;
      // Normal slight reading variation (within ±0.01%)
      const variation = (Math.random() * 0.0004 - 0.0002) * standard;
      const actual = Math.round((standard + variation) * 1000) / 1000;
      const errPct = standard > 0 ? (actual - standard) / standard : 0;
      return {
        id: 'reading-' + (idx + 1),
        standardWeight: standard,
        actualReading: actual,
        errorPercentage: errPct,
        toleranceLimit: tol,
        passed: Math.abs(errPct) <= tol,
        notes: `Test Point ${idx + 1} (${Math.round(frac * 100)}% capacity)`
      };
    });

    setTestReadings(points);
    setAiAnomalyResult(null);
    setActiveScreen('INSPECTION_WORKSPACE');

    // Update request status to UNDER_INSPECTION
    setRequests(prev => prev.map(r => r.id === request.id ? { ...r, status: 'UNDER_INSPECTION' } : r));
    setInstruments(prev => prev.map(i => i.instrumentId === request.instrumentId ? { ...i, status: 'UNDER_INSPECTION' } : i));
    addAuditLog('INSPECTION_STARTED', request.instrumentId, `Officer ${currentUser.name} initiated calibration testing for ${request.requestId}.`);
  }, [addAuditLog, currentUser.name, instruments]);

  const activeInspectionInstrument = activeInspectionRequest
    ? instruments.find(i => i.instrumentId === activeInspectionRequest.instrumentId) || null
    : null;

  // Add Test Reading
  const addTestReading = useCallback((reading: Omit<TestReading, 'id' | 'errorPercentage' | 'passed'>) => {
    const tol = activeInspectionInstrument?.permissibleTolerance || 0.05;
    const errPct = reading.standardWeight > 0 ? (reading.actualReading - reading.standardWeight) / reading.standardWeight : 0;
    const newReading: TestReading = {
      ...reading,
      id: 'reading-' + Date.now(),
      errorPercentage: errPct,
      toleranceLimit: tol,
      passed: Math.abs(errPct) <= tol
    };
    setTestReadings(prev => [...prev, newReading]);
  }, [activeInspectionInstrument]);

  // Update Test Reading
  const updateTestReading = useCallback((id: string, actualReading: number) => {
    setTestReadings(prev => prev.map(r => {
      if (r.id === id) {
        const errPct = r.standardWeight > 0 ? (actualReading - r.standardWeight) / r.standardWeight : 0;
        return {
          ...r,
          actualReading,
          errorPercentage: errPct,
          passed: Math.abs(errPct) <= r.toleranceLimit
        };
      }
      return r;
    }));
  }, []);

  const removeTestReading = useCallback((id: string) => {
    setTestReadings(prev => prev.filter(r => r.id !== id));
  }, []);

  // Run AI Anomaly Analysis
  const runAiAnomalyAnalysis = useCallback(async () => {
    if (!activeInspectionInstrument) return;
    setIsAiAnalyzing(true);
    try {
      const result = await GeminiMetrologyService.analyzeInspection({
        instrumentName: activeInspectionInstrument.name,
        instrumentType: activeInspectionInstrument.type,
        capacity: activeInspectionInstrument.capacity,
        unit: activeInspectionInstrument.unitOfMeasurement,
        tolerancePercent: activeInspectionInstrument.permissibleTolerance,
        readings: testReadings
      });
      setAiAnomalyResult(result);
    } catch (err) {
      console.error('AI Analysis failed:', err);
    } finally {
      setIsAiAnalyzing(false);
    }
  }, [activeInspectionInstrument, testReadings]);

  // Complete Inspection and issue certificate
  const completeInspection = useCallback(async (params: {
    isPassed: boolean;
    inspectorNotes: string;
    tamperSealNumber: string;
    environmentTempC?: number;
    environmentHumidityPercent?: number;
  }) => {
    if (!activeInspectionRequest || !activeInspectionInstrument) return;

    const inspectionId = `INSP-2026-${Math.floor(100 + Math.random() * 900)}`;
    const newInspection: InspectionEntity = {
      id: 'insp-' + Date.now(),
      inspectionId,
      requestId: activeInspectionRequest.requestId,
      instrumentId: activeInspectionInstrument.instrumentId,
      inspectorId: currentUser.userId,
      inspectorName: currentUser.name,
      inspectionDate: new Date().toISOString().split('T')[0],
      environmentTempC: params.environmentTempC || 21.5,
      environmentHumidityPercent: params.environmentHumidityPercent || 48,
      readingsJson: JSON.stringify(testReadings),
      isPassed: params.isPassed,
      tamperSealApplied: params.isPassed,
      tamperSealNumber: params.tamperSealNumber || `SEAL-2026-NLM-${Math.floor(10000 + Math.random() * 90000)}`,
      aiAnomalyDetected: aiAnomalyResult?.isAnomaly || false,
      aiRiskScore: aiAnomalyResult?.riskScore || (params.isPassed ? 'LOW' : 'HIGH'),
      aiDiagnosticNotes: aiAnomalyResult?.explanation || (params.isPassed ? 'Readings verified within statutory tolerances.' : 'Tolerance exceeded.'),
      inspectorNotes: params.inspectorNotes
    };

    setInspections(prev => [newInspection, ...prev]);

    if (params.isPassed) {
      // Generate Digital Certificate
      const certNumber = `CERT-2026-NLM-${Math.floor(1000 + Math.random() * 9000)}`;
      const validUntil = new Date(Date.now() + 365 * 86400000).toISOString().split('T')[0];

      const newCert: CertificateEntity = {
        id: 'cert-' + Date.now(),
        certificateNumber: certNumber,
        inspectionId,
        instrumentId: activeInspectionInstrument.instrumentId,
        instrumentName: activeInspectionInstrument.name,
        instrumentType: activeInspectionInstrument.type,
        manufacturer: activeInspectionInstrument.manufacturer,
        modelNumber: activeInspectionInstrument.modelNumber,
        serialNumber: activeInspectionInstrument.serialNumber,
        capacity: activeInspectionInstrument.capacity,
        unit: activeInspectionInstrument.unitOfMeasurement,
        ownerBusiness: activeInspectionInstrument.ownerBusiness,
        location: activeInspectionInstrument.location,
        inspectorName: `${currentUser.name} (${currentUser.licenseNumber})`,
        standardCode: 'ISO/IEC 17025 • OIML R76-1 Statutory Legal Metrology',
        verificationDate: new Date().toISOString().split('T')[0],
        validUntil,
        status: 'ACTIVE',
        qrVerificationUrl: `https://metrology.gov.verify/cert/${certNumber}`,
        tamperSealNumber: newInspection.tamperSealNumber,
        issuedAt: Date.now()
      };

      setCertificates(prev => [newCert, ...prev]);

      // Update instrument status
      setInstruments(prev => prev.map(inst =>
        inst.instrumentId === activeInspectionInstrument.instrumentId
          ? {
              ...inst,
              status: 'CERTIFICATE_GENERATED',
              riskScore: 'LOW',
              riskReason: 'Annual statutory verification current. Tamper holographic seal applied.',
              lastVerificationDate: newCert.verificationDate,
              nextVerificationDate: newCert.validUntil,
              certificateId: certNumber,
              qrPayload: newCert.qrVerificationUrl
            }
          : inst
      ));

      // Update request status
      setRequests(prev => prev.map(r =>
        r.id === activeInspectionRequest.id ? { ...r, status: 'CERTIFICATE_GENERATED' } : r
      ));

      addAuditLog('CERTIFICATE_ISSUED', activeInspectionInstrument.instrumentId, `Certificate ${certNumber} generated for ${activeInspectionInstrument.name}.`);

      // Trigger Celebration Confetti
      try {
        confetti({
          particleCount: 80,
          spread: 70,
          origin: { y: 0.6 }
        });
      } catch (e) {
        // no-op
      }

      setSelectedCertificate(newCert);
      setShowCertificateModal(true);
    } else {
      // Mark Failed
      setInstruments(prev => prev.map(inst =>
        inst.instrumentId === activeInspectionInstrument.instrumentId
          ? {
              ...inst,
              status: 'FAILED',
              riskScore: 'HIGH',
              riskReason: `Failed statutory tolerance limit test. Error observed during calibration.`,
              nextVerificationDate: 'Failed - Recalibration Required'
            }
          : inst
      ));

      setRequests(prev => prev.map(r =>
        r.id === activeInspectionRequest.id ? { ...r, status: 'FAILED' } : r
      ));

      addAuditLog('INSPECTION_FAILED', activeInspectionInstrument.instrumentId, `Inspection failed tolerance checks. Re-verification required.`);
    }

    setActiveScreen('DASHBOARD');
    setSelectedTab('REQUESTS');
  }, [activeInspectionInstrument, activeInspectionRequest, addAuditLog, aiAnomalyResult, currentUser.licenseNumber, currentUser.name, currentUser.userId, testReadings]);

  // Run AI OCR Nameplate Scan
  const runAiOcrScan = useCallback(async () => {
    setIsOcrScanning(true);
    try {
      const result = await GeminiMetrologyService.runOcrScan();
      setOcrResult(result);
    } catch (err) {
      console.error('OCR Scan failed:', err);
    } finally {
      setIsOcrScanning(false);
    }
  }, []);

  // Public Certificate Search
  const searchPublicCertificate = useCallback((query: string) => {
    setPublicSearchQuery(query);
    const cleaned = query.trim().toUpperCase();
    if (!cleaned) {
      setPublicSearchResult(null);
      setPublicHasSearched(false);
      return;
    }

    const found = certificates.find(c =>
      c.certificateNumber.toUpperCase() === cleaned ||
      c.instrumentId.toUpperCase() === cleaned ||
      c.serialNumber.toUpperCase() === cleaned
    );

    setPublicSearchResult(found || null);
    setPublicHasSearched(true);
  }, [certificates]);

  // Run End-to-End Demo Flow (1-click interactive demo for hackathons and judges)
  const runDemoFlow = useCallback(async () => {
    // 1. Switch to Business Owner, register a new instrument
    setUserRole('BUSINESS_OWNER');
    setActiveScreen('DASHBOARD');
    setSelectedTab('INSTRUMENTS');

    const demoSerial = 'SN-APEX-' + Math.floor(10000 + Math.random() * 90000);
    const demoInstId = 'INST-WB-' + Math.floor(1000 + Math.random() * 9000);

    const newInst: InstrumentEntity = {
      id: 'demo-inst-' + Date.now(),
      instrumentId: demoInstId,
      name: 'Apex Automated Heavy Weighbridge Matrix-9',
      type: 'Weighbridge',
      category: 'Industrial',
      manufacturer: 'Avery Weigh-Tronix',
      modelNumber: 'BridgeMaster Super-80T',
      serialNumber: demoSerial,
      capacity: '80000',
      unitOfMeasurement: 'kg',
      location: 'Apex Inland Port Terminal, Gate 3',
      ownerBusiness: 'Apex Logistics & Freight Hub',
      ownerId: 'USR-BIZ-001',
      permissibleTolerance: 0.05,
      status: 'SUBMITTED',
      riskScore: 'LOW',
      riskReason: 'Statutory verification requested. Scheduled with inspector.',
      lastVerificationDate: 'None',
      nextVerificationDate: 'Under Metrology Review',
      certificateId: null,
      qrPayload: `https://metrology.gov.verify/inst/${demoInstId}`,
      createdAt: Date.now(),
      updatedAt: Date.now()
    };

    const demoReqId = `REQ-2026-${Math.floor(100 + Math.random() * 900)}`;
    const newReq: VerificationRequestEntity = {
      id: 'demo-req-' + Date.now(),
      requestId: demoReqId,
      instrumentId: demoInstId,
      instrumentName: newInst.name,
      instrumentType: newInst.type,
      ownerId: 'USR-BIZ-001',
      businessName: 'Apex Logistics & Freight Hub',
      status: 'UNDER_INSPECTION',
      submissionDate: new Date().toISOString().split('T')[0],
      scheduledDate: new Date().toISOString().split('T')[0],
      assignedInspectorId: 'USR-INS-001',
      assignedInspectorName: 'Officer Sarah Jenkins',
      notes: 'End-to-End Demo statutory inspection testing.',
      inspectionFee: 450.0,
      paymentStatus: 'PAID'
    };

    setInstruments(prev => [newInst, ...prev]);
    setRequests(prev => [newReq, ...prev]);
    addAuditLog('DEMO_INITIALIZED', demoInstId, `Automated End-to-End Metrology verification flow started.`);

    // Switch to Inspector role and open workspace
    setUserRole('INSPECTOR');
    setActiveInspectionRequest(newReq);

    const points: TestReading[] = [
      { id: 'dp-1', standardWeight: 10000, actualReading: 10001.2, errorPercentage: 0.00012, toleranceLimit: 0.05, passed: true, notes: 'Zero point & 10-ton load test' },
      { id: 'dp-2', standardWeight: 25000, actualReading: 25002.5, errorPercentage: 0.0001, toleranceLimit: 0.05, passed: true, notes: 'Quarter capacity linearity' },
      { id: 'dp-3', standardWeight: 50000, actualReading: 49998.0, errorPercentage: -0.00004, toleranceLimit: 0.05, passed: true, notes: 'Half capacity load strain' },
      { id: 'dp-4', standardWeight: 80000, actualReading: 80006.4, errorPercentage: 0.00008, toleranceLimit: 0.05, passed: true, notes: 'Full statutory capacity test' }
    ];

    setTestReadings(points);
    setActiveScreen('INSPECTION_WORKSPACE');
  }, [addAuditLog]);

  const resetData = useCallback(() => {
    localStorage.removeItem(STORAGE_KEY);
    setInstruments(INITIAL_INSTRUMENTS);
    setRequests(INITIAL_REQUESTS);
    setInspections([]);
    setCertificates(INITIAL_CERTIFICATES);
    setAuditLogs(INITIAL_AUDIT_LOGS);
    setActiveScreen('LANDING');
    setUserRoleState('BUSINESS_OWNER');
    setSelectedTab('INSTRUMENTS');
  }, []);

  return (
    <MetrologyContext.Provider
      value={{
        userRole,
        currentUser,
        activeScreen,
        selectedTab,
        instruments,
        requests,
        inspections,
        certificates,
        auditLogs,
        activeInspectionRequest,
        activeInspectionInstrument,
        testReadings,
        aiAnomalyResult,
        isAiAnalyzing,
        selectedInstrument,
        selectedCertificate,
        showCertificateModal,
        showRegisterModal,
        ocrResult,
        isOcrScanning,
        searchQuery,
        statusFilter,
        categoryFilter,
        publicSearchQuery,
        publicSearchResult,
        publicHasSearched,
        setUserRole,
        setActiveScreen,
        setSelectedTab,
        setSearchQuery,
        setStatusFilter,
        setCategoryFilter,
        setSelectedInstrument,
        setSelectedCertificate,
        setShowCertificateModal,
        setShowRegisterModal,
        requestVerification,
        registerInstrument,
        openInspectionWorkspace,
        addTestReading,
        updateTestReading,
        removeTestReading,
        runAiAnomalyAnalysis,
        completeInspection,
        runAiOcrScan,
        searchPublicCertificate,
        runDemoFlow,
        resetData
      }}
    >
      {children}
    </MetrologyContext.Provider>
  );
};

export const useMetrology = () => {
  const context = useContext(MetrologyContext);
  if (!context) {
    throw new Error('useMetrology must be used within a MetrologyProvider');
  }
  return context;
};
