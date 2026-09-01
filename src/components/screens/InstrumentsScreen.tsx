import React, { useMemo } from 'react';
import {
  Search,
  Plus,
  Scale,
  MapPin,
  Send,
  Eye,
  Award,
  Sparkles,
  Filter,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';
import { useMetrology } from '../../context/MetrologyContext';
import { InstrumentEntity } from '../../types';
import { InstrumentStatusBadge, RiskScoreBadge, ExpiryStatusBadge } from '../common/StatusBadge';
import { MetricStatCard, PassRateGaugeCard } from '../common/MetricCards';

export const InstrumentsScreen: React.FC = () => {
  const {
    instruments,
    searchQuery,
    setSearchQuery,
    statusFilter,
    setStatusFilter,
    categoryFilter,
    setCategoryFilter,
    setSelectedInstrument,
    setShowRegisterModal,
    requestVerification,
    setSelectedCertificate,
    setShowCertificateModal,
    certificates,
    userRole
  } = useMetrology();

  const categories = ['ALL', 'Industrial', 'Petroleum', 'Laboratory', 'Retail', 'Commercial'];
  const statusOptions = ['ALL', 'CERTIFICATE_GENERATED', 'SUBMITTED', 'UNDER_INSPECTION', 'FAILED', 'DRAFT'];

  const filteredInstruments = useMemo(() => {
    return instruments.filter(inst => {
      const matchesSearch =
        searchQuery.trim() === '' ||
        inst.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        inst.instrumentId.toLowerCase().includes(searchQuery.toLowerCase()) ||
        inst.serialNumber.toLowerCase().includes(searchQuery.toLowerCase()) ||
        inst.manufacturer.toLowerCase().includes(searchQuery.toLowerCase()) ||
        inst.location.toLowerCase().includes(searchQuery.toLowerCase());

      const matchesStatus = statusFilter === 'ALL' || inst.status === statusFilter;
      const matchesCategory = categoryFilter === 'ALL' || inst.category === categoryFilter;

      return matchesSearch && matchesStatus && matchesCategory;
    });
  }, [instruments, searchQuery, statusFilter, categoryFilter]);

  // Metrics summary
  const totalCount = instruments.length;
  const certifiedCount = instruments.filter(i => i.status === 'CERTIFICATE_GENERATED').length;
  const failedCount = instruments.filter(i => i.status === 'FAILED').length;
  const passRate = totalCount > 0 ? Math.round((certifiedCount / (certifiedCount + failedCount || 1)) * 100) : 100;

  return (
    <div className="space-y-6">
      
      {/* Top Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <PassRateGaugeCard
          passRatePercentage={passRate}
          totalVerified={certifiedCount}
          totalFailed={failedCount}
        />
        <MetricStatCard
          title="Registered Instruments"
          value={totalCount}
          subtitle="Statutory metrology devices"
          icon={Scale}
          accentColor="bg-cyan-50 text-cyan-600"
        />
        <MetricStatCard
          title="Compliant & Certified"
          value={certifiedCount}
          subtitle="Active legal seals"
          icon={Award}
          accentColor="bg-emerald-50 text-emerald-600"
        />
        <MetricStatCard
          title="Action Required"
          value={instruments.filter(i => i.status === 'FAILED' || i.status === 'DRAFT').length}
          subtitle="Draft or failed re-calibration"
          icon={AlertCircle}
          accentColor="bg-rose-50 text-rose-600"
        />
      </div>

      {/* Action Bar & Filters */}
      <div className="p-4 bg-white rounded-3xl border border-slate-200/80 shadow-xs space-y-3">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3">
          
          {/* Search Input */}
          <div className="relative flex-1">
            <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 w-4 h-4 text-slate-400" />
            <input
              type="text"
              value={searchQuery}
              onChange={e => setSearchQuery(e.target.value)}
              placeholder="Search instruments by name, ID, serial number, make..."
              className="w-full pl-10 pr-4 py-2.5 bg-slate-50 border border-slate-200 rounded-2xl text-xs sm:text-sm font-medium focus:bg-white focus:outline-hidden focus:ring-2 focus:ring-cyan-500 focus:border-transparent transition-all"
            />
          </div>

          {/* Register CTA Button */}
          <button
            onClick={() => setShowRegisterModal(true)}
            className="inline-flex items-center justify-center gap-2 px-5 py-2.5 bg-cyan-600 hover:bg-cyan-700 text-white rounded-2xl text-xs font-extrabold shadow-sm transition-all active:scale-95 shrink-0"
          >
            <Plus className="w-4 h-4 stroke-[2.5]" />
            Register Measuring Device
          </button>
        </div>

        {/* Filter Pills */}
        <div className="flex flex-wrap items-center justify-between gap-2 pt-2 border-t border-slate-100 text-xs">
          
          {/* Categories */}
          <div className="flex items-center gap-1.5 overflow-x-auto pb-1 sm:pb-0">
            <span className="text-slate-600 font-bold flex items-center gap-1 text-[11px] uppercase mr-1">
              <Filter className="w-3 h-3 text-slate-600" />
              Category:
            </span>
            {categories.map(cat => (
              <button
                key={cat}
                onClick={() => setCategoryFilter(cat)}
                className={`px-2.5 py-1 rounded-lg font-medium transition-all ${
                  categoryFilter === cat
                    ? 'bg-slate-900 text-white font-bold shadow-xs'
                    : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                }`}
              >
                {cat}
              </button>
            ))}
          </div>

          {/* Status Filter */}
          <div className="flex items-center gap-1.5 overflow-x-auto">
            <span className="text-slate-600 font-bold text-[11px] uppercase mr-1">
              Status:
            </span>
            {statusOptions.map(st => (
              <button
                key={st}
                onClick={() => setStatusFilter(st)}
                className={`px-2.5 py-1 rounded-lg font-medium text-[11px] transition-all ${
                  statusFilter === st
                    ? 'bg-cyan-600 text-white font-bold shadow-xs'
                    : 'bg-slate-100 text-slate-700 hover:bg-slate-200'
                }`}
              >
                {st === 'ALL' ? 'All Status' : st === 'CERTIFICATE_GENERATED' ? 'Certified' : st.replace('_', ' ')}
              </button>
            ))}
          </div>

        </div>
      </div>

      {/* Instruments Grid */}
      {filteredInstruments.length === 0 ? (
        <div className="p-12 text-center bg-white rounded-3xl border border-slate-200 shadow-xs space-y-3">
          <div className="w-12 h-12 rounded-full bg-slate-100 text-slate-400 mx-auto flex items-center justify-center">
            <Scale className="w-6 h-6" />
          </div>
          <h3 className="text-base font-bold text-slate-800">No measuring instruments found</h3>
          <p className="text-xs text-slate-500 max-w-sm mx-auto">
            Try adjusting your search criteria or register a new device using the button above.
          </p>
          <button
            onClick={() => { setSearchQuery(''); setStatusFilter('ALL'); setCategoryFilter('ALL'); }}
            className="inline-flex items-center gap-1 text-xs font-bold text-cyan-600 hover:underline"
          >
            Clear all filters
          </button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
          {filteredInstruments.map(inst => {
            const cert = certificates.find(c => c.instrumentId === inst.instrumentId || c.certificateNumber === inst.certificateId);

            return (
              <div
                key={inst.id}
                className="bg-white rounded-3xl border border-slate-200/90 shadow-xs hover:border-cyan-400 hover:shadow-md transition-all flex flex-col justify-between overflow-hidden group"
              >
                {/* Card Header */}
                <div className="p-5 pb-3 space-y-3">
                  
                  <div className="flex items-start justify-between gap-2">
                    <span className="font-mono font-bold text-[11px] text-cyan-700 bg-cyan-50 px-2.5 py-1 rounded-md border border-cyan-100">
                      {inst.instrumentId}
                    </span>
                    <RiskScoreBadge riskLevel={inst.riskScore} />
                  </div>

                  <div>
                    <h3 className="text-sm font-extrabold text-slate-900 group-hover:text-cyan-700 transition-colors line-clamp-1">
                      {inst.name}
                    </h3>
                    <p className="text-xs text-slate-600 mt-0.5">
                      {inst.manufacturer} • {inst.modelNumber}
                    </p>
                  </div>

                  {/* Status & Expiry */}
                  <div className="flex flex-wrap items-center gap-2 pt-1">
                    <InstrumentStatusBadge status={inst.status} />
                    <ExpiryStatusBadge validUntilDate={inst.nextVerificationDate} />
                  </div>

                  {/* Details Spec Box */}
                  <div className="p-3 bg-slate-50 rounded-2xl border border-slate-100 text-xs space-y-1.5">
                    <div className="flex justify-between">
                      <span className="text-slate-600">Capacity:</span>
                      <span className="font-bold text-slate-900">{inst.capacity} {inst.unitOfMeasurement}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-slate-600">Serial No:</span>
                      <span className="font-mono text-slate-700">{inst.serialNumber}</span>
                    </div>
                    <div className="flex justify-between">
                      <span className="text-slate-600">Tolerance:</span>
                      <span className="font-mono text-slate-700">±{(inst.permissibleTolerance * 100).toFixed(2)}%</span>
                    </div>
                    <div className="flex items-center gap-1 text-[11px] text-slate-600 pt-1 border-t border-slate-200/50">
                      <MapPin className="w-3 h-3 text-slate-600 shrink-0" />
                      <span className="truncate">{inst.location}</span>
                    </div>
                  </div>

                </div>

                {/* Card Actions Footer */}
                <div className="px-5 py-3.5 bg-slate-50/70 border-t border-slate-100 flex items-center justify-between gap-2">
                  <button
                    onClick={() => setSelectedInstrument(inst)}
                    className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-white text-slate-700 border border-slate-200 hover:bg-slate-100 rounded-xl text-xs font-bold transition-colors"
                  >
                    <Eye className="w-3.5 h-3.5" />
                    Details
                  </button>

                  <div className="flex items-center gap-1.5">
                    {cert && (
                      <button
                        onClick={() => {
                          setSelectedCertificate(cert);
                          setShowCertificateModal(true);
                        }}
                        className="inline-flex items-center gap-1 px-2.5 py-1.5 bg-emerald-50 text-emerald-700 border border-emerald-200 hover:bg-emerald-100 rounded-xl text-xs font-bold transition-colors"
                      >
                        <Award className="w-3.5 h-3.5" />
                        Certificate
                      </button>
                    )}

                    {(inst.status === 'DRAFT' || inst.status === 'FAILED') && (
                      <button
                        onClick={() => requestVerification(inst)}
                        className="inline-flex items-center gap-1 px-3 py-1.5 bg-cyan-600 hover:bg-cyan-700 text-white rounded-xl text-xs font-bold shadow-xs transition-all active:scale-95"
                      >
                        <Send className="w-3.5 h-3.5" />
                        Request Verify
                      </button>
                    )}
                  </div>
                </div>

              </div>
            );
          })}
        </div>
      )}

    </div>
  );
};
