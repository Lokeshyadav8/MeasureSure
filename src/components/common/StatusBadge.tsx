import React from 'react';
import {
  FileText,
  Clock,
  UserCheck,
  Calendar,
  FlaskConical,
  CheckCircle2,
  XCircle,
  Award,
  AlertTriangle,
  Hourglass
} from 'lucide-react';
import { InstrumentStatus, RiskLevel } from '../../types';

interface StatusBadgeProps {
  status: InstrumentStatus;
  className?: string;
}

export const InstrumentStatusBadge: React.FC<StatusBadgeProps> = ({ status, className = '' }) => {
  switch (status) {
    case 'DRAFT':
      return (
        <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-slate-100 text-slate-700 border border-slate-200 ${className}`}>
          <FileText className="w-3.5 h-3.5 text-slate-500" />
          Draft
        </span>
      );
    case 'SUBMITTED':
      return (
        <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-sky-50 text-sky-700 border border-sky-200 ${className}`}>
          <Clock className="w-3.5 h-3.5 text-sky-600" />
          Submitted
        </span>
      );
    case 'ASSIGNED':
      return (
        <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-indigo-50 text-indigo-700 border border-indigo-200 ${className}`}>
          <UserCheck className="w-3.5 h-3.5 text-indigo-600" />
          Inspector Assigned
        </span>
      );
    case 'INSPECTION_SCHEDULED':
      return (
        <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-amber-50 text-amber-700 border border-amber-200 ${className}`}>
          <Calendar className="w-3.5 h-3.5 text-amber-600" />
          Inspection Scheduled
        </span>
      );
    case 'UNDER_INSPECTION':
      return (
        <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-amber-50 text-amber-800 border border-amber-300 animate-pulse ${className}`}>
          <FlaskConical className="w-3.5 h-3.5 text-amber-600" />
          Under Inspection
        </span>
      );
    case 'PASSED':
      return (
        <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200 ${className}`}>
          <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600" />
          Passed Verification
        </span>
      );
    case 'FAILED':
      return (
        <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-rose-50 text-rose-700 border border-rose-200 ${className}`}>
          <XCircle className="w-3.5 h-3.5 text-rose-600" />
          Verification Failed
        </span>
      );
    case 'CERTIFICATE_GENERATED':
      return (
        <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-800 border border-emerald-300 ${className}`}>
          <Award className="w-3.5 h-3.5 text-emerald-600" />
          Certified & Verified
        </span>
      );
    default:
      return null;
  }
};

export const RiskScoreBadge: React.FC<{ riskLevel: RiskLevel; className?: string }> = ({ riskLevel, className = '' }) => {
  switch (riskLevel) {
    case 'LOW':
      return (
        <span className={`inline-flex items-center gap-1.5 px-2 py-0.5 rounded-md text-[11px] font-bold tracking-wide bg-emerald-50 text-emerald-700 border border-emerald-200 ${className}`}>
          <span className="w-1.5 h-1.5 rounded-full bg-emerald-600"></span>
          LOW RISK
        </span>
      );
    case 'MEDIUM':
      return (
        <span className={`inline-flex items-center gap-1.5 px-2 py-0.5 rounded-md text-[11px] font-bold tracking-wide bg-amber-50 text-amber-700 border border-amber-200 ${className}`}>
          <span className="w-1.5 h-1.5 rounded-full bg-amber-600"></span>
          MED RISK
        </span>
      );
    case 'HIGH':
      return (
        <span className={`inline-flex items-center gap-1.5 px-2 py-0.5 rounded-md text-[11px] font-bold tracking-wide bg-rose-50 text-rose-700 border border-rose-200 ${className}`}>
          <span className="w-1.5 h-1.5 rounded-full bg-rose-600"></span>
          HIGH RISK
        </span>
      );
  }
};

export const ExpiryStatusBadge: React.FC<{ validUntilDate: string; className?: string }> = ({ validUntilDate, className = '' }) => {
  const isExpired = validUntilDate.toLowerCase().includes('failed') || validUntilDate.startsWith('2024') || validUntilDate.startsWith('2025-06');
  const isExpiringSoon = validUntilDate.startsWith('2026-09') || validUntilDate.startsWith('2026-08') || validUntilDate.startsWith('2026-10');

  if (isExpired) {
    return (
      <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[11px] font-semibold bg-rose-50 text-rose-700 border border-rose-200 ${className}`}>
        <AlertTriangle className="w-3 h-3 text-rose-600" />
        Expired
      </span>
    );
  }

  if (isExpiringSoon) {
    return (
      <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[11px] font-semibold bg-amber-50 text-amber-800 border border-amber-300 ${className}`}>
        <Hourglass className="w-3 h-3 text-amber-600" />
        Expiring Soon
      </span>
    );
  }

  return (
    <span className={`inline-flex items-center gap-1 px-2 py-0.5 rounded-md text-[11px] font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200 ${className}`}>
      <CheckCircle2 className="w-3 h-3 text-emerald-600" />
      Valid & Active
    </span>
  );
};
