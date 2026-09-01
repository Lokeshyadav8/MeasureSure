import React from 'react';
import { X, Send, Eye, ShieldCheck, MapPin, Calendar, Activity, AlertCircle } from 'lucide-react';
import { InstrumentEntity, CertificateEntity } from '../../types';
import { InstrumentStatusBadge, RiskScoreBadge, ExpiryStatusBadge } from '../common/StatusBadge';
import { MetrologyQrCode } from '../common/QrCodeGenerator';
import { formatCapacity, formatTolerance } from '../../utils/formatters';

interface InstrumentDetailsModalProps {
  instrument: InstrumentEntity | null;
  certificate: CertificateEntity | null;
  isOpen: boolean;
  onClose: () => void;
  onRequestVerification: (instrument: InstrumentEntity) => void;
  onViewCertificate: (cert: CertificateEntity) => void;
}

export const InstrumentDetailsModal: React.FC<InstrumentDetailsModalProps> = ({
  instrument,
  certificate,
  isOpen,
  onClose,
  onRequestVerification,
  onViewCertificate
}) => {
  if (!isOpen || !instrument) return null;

  return (
    <div className="fixed inset-0 z-50 overflow-y-auto bg-slate-900/70 backdrop-blur-xs flex items-center justify-center p-3 sm:p-6 animate-in fade-in duration-200">
      <div className="relative w-full max-w-xl bg-white rounded-3xl shadow-2xl border border-slate-200 overflow-hidden flex flex-col max-h-[92vh]">
        
        {/* Top Header */}
        <div className="flex items-center justify-between px-6 py-4 bg-slate-900 text-white">
          <div>
            <div className="text-[10px] font-mono font-bold text-cyan-400">
              {instrument.instrumentId}
            </div>
            <h3 className="text-base font-bold text-white tracking-tight">
              {instrument.name}
            </h3>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 rounded-full hover:bg-slate-800 text-slate-400 hover:text-white transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 overflow-y-auto space-y-5">
          
          {/* Status & Risk Row */}
          <div className="flex flex-wrap items-center justify-between gap-2 p-3 bg-slate-50 rounded-2xl border border-slate-200/80">
            <div className="flex items-center gap-2">
              <InstrumentStatusBadge status={instrument.status} />
              <RiskScoreBadge riskLevel={instrument.riskScore} />
            </div>
            <ExpiryStatusBadge validUntilDate={instrument.nextVerificationDate} />
          </div>

          {/* Technical Specs Table */}
          <div className="bg-white rounded-2xl border border-slate-200 p-4 shadow-xs divide-y divide-slate-100 text-xs">
            <div className="flex justify-between py-2">
              <span className="text-slate-500 font-medium">Type / Category</span>
              <span className="font-semibold text-slate-900">{instrument.type} ({instrument.category})</span>
            </div>
            <div className="flex justify-between py-2">
              <span className="text-slate-500 font-medium">Manufacturer / Model</span>
              <span className="font-semibold text-slate-800">{instrument.manufacturer} {instrument.modelNumber}</span>
            </div>
            <div className="flex justify-between py-2">
              <span className="text-slate-500 font-medium">Serial Number</span>
              <span className="font-mono font-bold text-slate-800">{instrument.serialNumber}</span>
            </div>
            <div className="flex justify-between py-2">
              <span className="text-slate-500 font-medium">Capacity & Unit</span>
              <span className="font-bold text-slate-900">{formatCapacity(instrument.capacity, instrument.unitOfMeasurement)}</span>
            </div>
            <div className="flex justify-between py-2">
              <span className="text-slate-500 font-medium">Permissible Tolerance</span>
              <span className="font-mono font-bold text-slate-900">{formatTolerance(instrument.permissibleTolerance)}</span>
            </div>
            <div className="flex justify-between py-2">
              <span className="text-slate-500 font-medium">Operating Location</span>
              <span className="font-semibold text-slate-800 flex items-center gap-1">
                <MapPin className="w-3.5 h-3.5 text-slate-400" />
                {instrument.location}
              </span>
            </div>
            <div className="flex justify-between py-2">
              <span className="text-slate-500 font-medium">Registered Business</span>
              <span className="font-semibold text-slate-800">{instrument.ownerBusiness}</span>
            </div>
            <div className="flex justify-between py-2">
              <span className="text-slate-500 font-medium">Last Verification Date</span>
              <span className="font-medium text-slate-700">{instrument.lastVerificationDate}</span>
            </div>
            <div className="flex justify-between py-2">
              <span className="text-slate-500 font-medium">Next Verification Due</span>
              <span className="font-bold text-slate-900">{instrument.nextVerificationDate}</span>
            </div>
          </div>

          {/* AI Risk Assessment Reason */}
          <div className="p-3.5 bg-slate-50 rounded-2xl border border-slate-200 space-y-1">
            <div className="flex items-center gap-1.5 text-xs font-bold text-slate-700">
              <Activity className="w-3.5 h-3.5 text-cyan-600" />
              Statutory Risk & Health Intelligence
            </div>
            <p className="text-xs text-slate-600 leading-relaxed">
              {instrument.riskReason}
            </p>
          </div>

          {/* Digital QR Code & Certificate Preview */}
          <div className="flex items-center gap-4 p-4 bg-slate-900 text-white rounded-2xl">
            <div className="shrink-0">
              <MetrologyQrCode
                data={instrument.qrPayload}
                size={84}
                showEmblem={true}
              />
            </div>
            <div className="space-y-1">
              <h4 className="text-xs font-bold text-white flex items-center gap-1.5">
                <ShieldCheck className="w-4 h-4 text-cyan-400" />
                Statutory Digital Seal & QR
              </h4>
              <p className="text-[11px] text-slate-300">
                Authorized tamper seal and verifiable public QR code mapped to this instrument registry.
              </p>
              {certificate && (
                <button
                  type="button"
                  onClick={() => {
                    onClose();
                    onViewCertificate(certificate);
                  }}
                  className="mt-1 inline-flex items-center gap-1 text-xs font-bold text-cyan-300 hover:text-cyan-200 underline"
                >
                  <Eye className="w-3.5 h-3.5" />
                  View Certificate ({certificate.certificateNumber})
                </button>
              )}
            </div>
          </div>

        </div>

        {/* Footer Actions */}
        <div className="px-6 py-4 bg-slate-50 border-t border-slate-200 flex items-center justify-between">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 bg-white text-slate-700 border border-slate-300 rounded-xl text-xs font-bold hover:bg-slate-100 shadow-xs"
          >
            Close
          </button>

          {(instrument.status === 'DRAFT' || instrument.status === 'FAILED') && (
            <button
              type="button"
              onClick={() => {
                onRequestVerification(instrument);
                onClose();
              }}
              className="inline-flex items-center gap-1.5 px-5 py-2 bg-cyan-600 hover:bg-cyan-700 text-white rounded-xl text-xs font-bold shadow-md transition-all active:scale-95"
            >
              <Send className="w-3.5 h-3.5" />
              {instrument.status === 'FAILED' ? 'Request Re-Verification' : 'Request Statutory Verification'}
            </button>
          )}
        </div>

      </div>
    </div>
  );
};
