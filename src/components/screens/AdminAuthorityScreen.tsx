import React, { useState } from 'react';
import {
  Award,
  ShieldCheck,
  AlertTriangle,
  History,
  Users,
  Search,
  CheckCircle2,
  Clock,
  Sparkles,
  BarChart3,
  Scale
} from 'lucide-react';
import { useMetrology } from '../../context/MetrologyContext';
import { INITIAL_USERS } from '../../data/seedData';
import { RiskScoreBadge } from '../common/StatusBadge';

export const AdminAuthorityScreen: React.FC = () => {
  const { auditLogs, instruments, certificates } = useMetrology();
  const [logFilter, setLogFilter] = useState('');

  const filteredLogs = auditLogs.filter(log =>
    logFilter.trim() === '' ||
    log.action.toLowerCase().includes(logFilter.toLowerCase()) ||
    log.performedBy.toLowerCase().includes(logFilter.toLowerCase()) ||
    log.instrumentId.toLowerCase().includes(logFilter.toLowerCase()) ||
    log.details.toLowerCase().includes(logFilter.toLowerCase())
  );

  const highRiskCount = instruments.filter(i => i.riskScore === 'HIGH').length;
  const mediumRiskCount = instruments.filter(i => i.riskScore === 'MEDIUM').length;
  const lowRiskCount = instruments.filter(i => i.riskScore === 'LOW').length;

  return (
    <div className="space-y-6">
      
      {/* Top Directorate Banner */}
      <div className="p-6 bg-slate-900 text-white rounded-3xl shadow-md border border-slate-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div className="space-y-1">
          <div className="flex items-center gap-2">
            <span className="px-2 py-0.5 bg-cyan-500/20 text-cyan-400 font-mono text-[10px] font-bold rounded uppercase">
              Regulatory Board
            </span>
            <span className="text-xs text-slate-400">National Metrology Directorate</span>
          </div>
          <h2 className="text-lg sm:text-xl font-black text-white tracking-tight">
            Central Authority & Legal Metrology Oversight
          </h2>
          <p className="text-xs text-slate-300">
            Real-time compliance monitoring, statutory audit integrity, and predictive risk distribution.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <div className="px-4 py-2 bg-slate-800/80 rounded-2xl border border-slate-700 text-center">
            <span className="text-[10px] font-bold uppercase text-slate-400 block">Total Audits</span>
            <span className="text-lg font-black text-white">{auditLogs.length}</span>
          </div>
          <div className="px-4 py-2 bg-slate-800/80 rounded-2xl border border-slate-700 text-center">
            <span className="text-[10px] font-bold uppercase text-cyan-400 block">Certificates</span>
            <span className="text-lg font-black text-cyan-400">{certificates.length}</span>
          </div>
        </div>
      </div>

      {/* AI Risk Radar & Cadre Row */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-5">
        
        {/* AI Predictive Risk Radar */}
        <div className="p-5 bg-white rounded-3xl border border-slate-200/90 shadow-xs space-y-4 lg:col-span-1">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-extrabold text-slate-900 flex items-center gap-2">
              <Sparkles className="w-4 h-4 text-cyan-600" />
              AI Risk Radar Distribution
            </h3>
            <span className="text-[10px] font-bold text-slate-500 bg-slate-100 px-2 py-0.5 rounded">
              Predictive
            </span>
          </div>

          <div className="space-y-3">
            {/* Low Risk */}
            <div>
              <div className="flex justify-between text-xs font-bold mb-1">
                <span className="text-emerald-700 flex items-center gap-1.5">
                  <span className="w-2 h-2 rounded-full bg-emerald-500"></span>
                  Low Risk (Nominal)
                </span>
                <span className="text-slate-800">{lowRiskCount} devices ({Math.round((lowRiskCount / (instruments.length || 1)) * 100)}%)</span>
              </div>
              <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                <div
                  className="h-full bg-emerald-500 transition-all duration-500"
                  style={{ width: `${(lowRiskCount / (instruments.length || 1)) * 100}%` }}
                />
              </div>
            </div>

            {/* Medium Risk */}
            <div>
              <div className="flex justify-between text-xs font-bold mb-1">
                <span className="text-amber-700 flex items-center gap-1.5">
                  <span className="w-2 h-2 rounded-full bg-amber-500"></span>
                  Medium Risk (Expiring / Drift)
                </span>
                <span className="text-slate-800">{mediumRiskCount} devices ({Math.round((mediumRiskCount / (instruments.length || 1)) * 100)}%)</span>
              </div>
              <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                <div
                  className="h-full bg-amber-500 transition-all duration-500"
                  style={{ width: `${(mediumRiskCount / (instruments.length || 1)) * 100}%` }}
                />
              </div>
            </div>

            {/* High Risk */}
            <div>
              <div className="flex justify-between text-xs font-bold mb-1">
                <span className="text-rose-700 flex items-center gap-1.5">
                  <span className="w-2 h-2 rounded-full bg-rose-500"></span>
                  High Risk (Failed / Out-of-Tolerance)
                </span>
                <span className="text-slate-800">{highRiskCount} devices ({Math.round((highRiskCount / (instruments.length || 1)) * 100)}%)</span>
              </div>
              <div className="h-2 bg-slate-100 rounded-full overflow-hidden">
                <div
                  className="h-full bg-rose-500 transition-all duration-500"
                  style={{ width: `${(highRiskCount / (instruments.length || 1)) * 100}%` }}
                />
              </div>
            </div>
          </div>

          <p className="text-[11px] text-slate-500 bg-slate-50 p-3 rounded-2xl border border-slate-100 leading-relaxed">
            Statutory AI engine continuously evaluates verification cadence, load cell strain profiles, and tolerance drift to prioritize field inspections.
          </p>
        </div>

        {/* Authorized Metrologist Cadre */}
        <div className="p-5 bg-white rounded-3xl border border-slate-200/90 shadow-xs space-y-4 lg:col-span-2">
          <div className="flex items-center justify-between">
            <h3 className="text-sm font-extrabold text-slate-900 flex items-center gap-2">
              <Users className="w-4 h-4 text-indigo-600" />
              Authorized Legal Metrology Officers
            </h3>
            <span className="text-xs text-indigo-600 font-bold">
              Zone 1 & 2 Directory
            </span>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            {INITIAL_USERS.filter(u => u.role === 'INSPECTOR' || u.role === 'ADMIN').map(officer => (
              <div
                key={officer.userId}
                className="p-3.5 bg-slate-50 rounded-2xl border border-slate-200/80 flex items-start gap-3"
              >
                <div className="w-10 h-10 rounded-xl bg-slate-900 text-cyan-300 font-bold text-xs flex items-center justify-center shrink-0">
                  {officer.name.split(' ').map(n => n[0]).join('').slice(0, 2)}
                </div>
                <div className="space-y-0.5 text-xs">
                  <div className="font-bold text-slate-900">{officer.name}</div>
                  <div className="text-slate-500 font-mono text-[10px]">{officer.licenseNumber}</div>
                  <div className="text-slate-600 text-[11px]">{officer.businessOrDepartment}</div>
                  <span className="inline-block mt-1 px-2 py-0.5 bg-emerald-50 text-emerald-700 font-bold text-[10px] rounded">
                    Active Authority
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>

      </div>

      {/* Immutable Statutory Audit Trail */}
      <div className="p-5 sm:p-6 bg-white rounded-3xl border border-slate-200/90 shadow-xs space-y-4">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          <div>
            <h3 className="text-base font-extrabold text-slate-900 tracking-tight flex items-center gap-2">
              <History className="w-4 h-4 text-cyan-600" />
              Immutable Metrological Audit Trail
            </h3>
            <p className="text-xs text-slate-500">
              Cryptographically timestamped record of every registration, inspection, and certificate issuance
            </p>
          </div>

          {/* Search audit */}
          <div className="relative w-full sm:w-64">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-slate-400" />
            <input
              type="text"
              value={logFilter}
              onChange={e => setLogFilter(e.target.value)}
              placeholder="Search audit trail..."
              className="w-full pl-8 pr-3 py-1.5 bg-slate-50 border border-slate-200 rounded-xl text-xs font-medium focus:bg-white focus:outline-hidden focus:ring-2 focus:ring-cyan-500"
            />
          </div>
        </div>

        {/* Audit Log Table */}
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs border-collapse">
            <thead>
              <tr className="bg-slate-50 border-b border-slate-200 text-slate-600">
                <th className="py-3 px-3 font-bold">Timestamp</th>
                <th className="py-3 px-3 font-bold">Action</th>
                <th className="py-3 px-3 font-bold">Officer / Actor</th>
                <th className="py-3 px-3 font-bold">Target ID</th>
                <th className="py-3 px-3 font-bold">Audit Details</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100 font-medium">
              {filteredLogs.map(log => {
                const dateStr = new Date(log.timestamp).toLocaleString();

                return (
                  <tr key={log.id} className="hover:bg-slate-50/70 transition-colors">
                    <td className="py-3 px-3 text-slate-500 whitespace-nowrap font-mono text-[11px]">
                      {dateStr}
                    </td>
                    <td className="py-3 px-3">
                      <span className="px-2 py-0.5 rounded font-mono font-bold text-[10px] bg-slate-100 text-slate-800">
                        {log.action}
                      </span>
                    </td>
                    <td className="py-3 px-3 text-slate-800 font-semibold">
                      {log.performedBy} ({log.role})
                    </td>
                    <td className="py-3 px-3 font-mono text-cyan-700 font-bold">
                      {log.instrumentId}
                    </td>
                    <td className="py-3 px-3 text-slate-600">
                      {log.details}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>

      </div>

    </div>
  );
};
