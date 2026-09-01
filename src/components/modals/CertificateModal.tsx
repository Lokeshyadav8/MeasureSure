import React from 'react';
import {
  X,
  Award,
  Download,
  Building2,
  CheckCircle2,
  AlertTriangle,
  Printer,
  FileCheck
} from 'lucide-react';
import { CertificateEntity } from '../../types';
import { MetrologyQrCode } from '../common/QrCodeGenerator';

interface CertificateModalProps {
  certificate: CertificateEntity | null;
  isOpen: boolean;
  onClose: () => void;
}

export const CertificateModal: React.FC<CertificateModalProps> = ({
  certificate,
  isOpen,
  onClose
}) => {
  if (!isOpen || !certificate) return null;

  const isExpired = certificate.status === 'EXPIRED';

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/70 backdrop-blur-xs flex items-center justify-center p-3 sm:p-6 animate-in fade-in duration-200">
      <div className="relative w-full max-w-2xl bg-white rounded-3xl shadow-2xl border border-slate-200 overflow-hidden flex flex-col max-h-[92vh]">
        
        {/* Top Dialog Bar */}
        <div className="flex items-center justify-between px-6 py-4 bg-slate-900 text-white">
          <div className="flex items-center gap-2.5">
            <Award className="w-5 h-5 text-cyan-400" />
            <h3 className="text-sm sm:text-base font-bold text-white tracking-tight">
              Legal Metrology Digital Certificate
            </h3>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-full hover:bg-slate-800 text-slate-400 hover:text-white transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Certificate Body Container */}
        <div className="p-4 sm:p-6 overflow-y-auto space-y-4">
          
          {/* Statutory Gold Border Certificate Document */}
          <div className="relative bg-gradient-to-b from-[#fefdfb] to-[#faf8f3] border-4 border-[#d4af37] rounded-2xl p-5 sm:p-8 shadow-sm">
            
            {/* Header / National Seal */}
            <div className="text-center space-y-1.5 pb-4 border-b border-amber-200/80">
              <div className="w-14 h-14 mx-auto rounded-full bg-slate-900 border-2 border-[#d4af37] flex items-center justify-center shadow-md">
                <Building2 className="w-7 h-7 text-amber-400" />
              </div>
              <h2 className="text-xs font-black tracking-widest text-slate-900 uppercase">
                Directorate of Legal Metrology
              </h2>
              <p className="text-[10px] font-bold tracking-wider text-slate-500 uppercase">
                Central Weights & Measures Statutory Verification Authority
              </p>
              <h1 className="text-lg sm:text-xl font-black tracking-tight text-slate-900 pt-1">
                CERTIFICATE OF VERIFICATION
              </h1>
              <p className="text-[11px] text-slate-600 font-medium">
                Issued under the Statutory Metrology & Measurement Standards Act
              </p>

              <div className="pt-2">
                <span className="inline-block px-4 py-1 rounded-lg bg-slate-900 text-cyan-300 font-mono font-bold text-xs sm:text-sm tracking-wider shadow-xs">
                  No: {certificate.certificateNumber}
                </span>
              </div>
            </div>

            {/* Certificate Details Table */}
            <div className="my-5 bg-white rounded-xl border border-slate-200 p-4 shadow-xs divide-y divide-slate-100 text-xs">
              <div className="flex justify-between py-2">
                <span className="text-slate-500 font-medium">Instrument ID</span>
                <span className="font-mono font-bold text-slate-900">{certificate.instrumentId}</span>
              </div>
              <div className="flex justify-between py-2">
                <span className="text-slate-500 font-medium">Instrument Name / Type</span>
                <span className="font-semibold text-slate-900">{certificate.instrumentName} ({certificate.instrumentType})</span>
              </div>
              <div className="flex justify-between py-2">
                <span className="text-slate-500 font-medium">Manufacturer / Model</span>
                <span className="font-semibold text-slate-800">{certificate.manufacturer} / {certificate.modelNumber}</span>
              </div>
              <div className="flex justify-between py-2">
                <span className="text-slate-500 font-medium">Serial Number</span>
                <span className="font-mono font-bold text-slate-800">{certificate.serialNumber}</span>
              </div>
              <div className="flex justify-between py-2">
                <span className="text-slate-500 font-medium">Capacity / Unit</span>
                <span className="font-bold text-slate-900">{certificate.capacity} {certificate.unit}</span>
              </div>
              <div className="flex justify-between py-2">
                <span className="text-slate-500 font-medium">Registered Business</span>
                <span className="font-semibold text-slate-800">{certificate.ownerBusiness}</span>
              </div>
              <div className="flex justify-between py-2">
                <span className="text-slate-500 font-medium">Operating Location</span>
                <span className="text-slate-700">{certificate.location}</span>
              </div>
              <div className="flex justify-between py-2">
                <span className="text-slate-500 font-medium">Verification Standard</span>
                <span className="font-semibold text-slate-800">{certificate.standardCode}</span>
              </div>
              <div className="flex justify-between py-2">
                <span className="text-slate-500 font-medium">Date of Verification</span>
                <span className="font-semibold text-slate-800">{certificate.verificationDate}</span>
              </div>
              <div className="flex justify-between py-2 bg-emerald-50/50 -mx-4 px-4 rounded-b-xl">
                <span className="text-slate-700 font-bold">Valid Until</span>
                <span className="font-extrabold text-emerald-800">{certificate.validUntil}</span>
              </div>
            </div>

            {/* Verification Status & QR Code Stamp */}
            <div className={`p-4 rounded-xl border flex flex-col sm:flex-row items-center justify-between gap-4 ${
              isExpired
                ? 'bg-rose-50 border-rose-200 text-rose-900'
                : 'bg-emerald-50 border-emerald-200 text-emerald-950'
            }`}>
              <div className="space-y-1 text-center sm:text-left">
                <div className="flex items-center justify-center sm:justify-start gap-2">
                  {isExpired ? (
                    <AlertTriangle className="w-5 h-5 text-rose-600" />
                  ) : (
                    <CheckCircle2 className="w-5 h-5 text-emerald-600" />
                  )}
                  <span className="font-extrabold text-xs sm:text-sm uppercase tracking-wide">
                    {isExpired ? 'STATUS: EXPIRED' : 'STATUS: OFFICIALLY VERIFIED & COMPLIANT'}
                  </span>
                </div>
                <p className="text-[11px] text-slate-600">
                  Inspected & verified by <strong className="text-slate-900">{certificate.inspectorName}</strong>
                </p>
                <p className="text-[10px] text-slate-500">
                  Holographic tamper seal applied: <code className="font-mono text-slate-700">{certificate.tamperSealNumber}</code>
                </p>
              </div>

              <div className="shrink-0">
                <MetrologyQrCode
                  data={certificate.qrVerificationUrl}
                  size={90}
                  showEmblem={true}
                />
              </div>
            </div>

            {/* Signatures & Seal Footer */}
            <div className="mt-6 pt-4 border-t border-slate-200 flex items-center justify-between text-xs">
              <div className="text-center sm:text-left">
                <div className="font-bold text-slate-900">{certificate.inspectorName}</div>
                <div className="text-[10px] text-slate-500">Authorized Legal Metrologist</div>
              </div>
              <div className="text-center sm:text-right">
                <div className="font-extrabold text-[#d4af37] tracking-wider uppercase">ELECTRONIC SEAL</div>
                <div className="text-[10px] text-slate-500 font-mono">Secured via SHA-256</div>
              </div>
            </div>

          </div>
        </div>

        {/* Footer Actions */}
        <div className="px-6 py-4 bg-slate-50 border-t border-slate-200 flex items-center justify-end gap-3">
          <button
            onClick={handlePrint}
            className="inline-flex items-center gap-1.5 px-4 py-2 bg-white text-slate-700 border border-slate-300 rounded-xl text-xs font-bold hover:bg-slate-50 shadow-xs"
          >
            <Printer className="w-4 h-4" />
            Print
          </button>
          <button
            onClick={onClose}
            className="inline-flex items-center gap-1.5 px-5 py-2 bg-slate-900 text-white rounded-xl text-xs font-bold hover:bg-slate-800 shadow-xs"
          >
            <FileCheck className="w-4 h-4 text-cyan-400" />
            Done
          </button>
        </div>

      </div>
    </div>
  );
};
