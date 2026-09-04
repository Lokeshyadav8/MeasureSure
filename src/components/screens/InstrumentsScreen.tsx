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
import { formatCapacity, formatTolerance } from '../../utils/formatters';

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
    <div className="space-y-5">
      
      {/* Top Metrics Row */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-3.5">
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
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2.5">
          
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
            className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-cyan-600 hover:bg-cyan-700 text-white rounded-2xl text-xs font-extrabold shadow-sm transition-all active:scale-95 shrink-0 whitespace-nowrap"
          >
            <Plus className="w-4 h-4 stroke-[2.5]" />
            Register Measuring Device
          </button>
        </div>

        {/* Filter Pills */}
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-2.5 pt-2 border-t border-slate-100 text-xs">
          
          {/* Categories */}
          <div className="flex items-center gap-1.5 overflow-x-auto no-scrollbar pb-1 md:pb-0">
            <span className="text-slate-600 font-bold flex items-center gap-1 text-[11px] uppercase mr-1 whitespace-nowrap">
              <Filter className="w-3 h-3 text-slate-500 shrink-0" />
              Category:
            </span>
            {categories.map(cat => (
              <button
                key={cat}
                onClick={() => setCategoryFilter(cat)}
                className={`px-2.5 py-1 rounded-lg font-medium transition-all whitespace-nowrap text-xs ${
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
          <div className="flex items-center gap-1.5 overflow-x-auto no-scrollbar pb-1 md:pb-0">
            <span className="text-slate-600 font-bold text-[11px] uppercase mr-1 whitespace-nowrap">
              Status:
            </span>
            {statusOptions.map(st => (
              <button
                key={st}
                onClick={() => setStatusFilter(st)}
                className={`px-2.5 py-1 rounded-lg font-medium text-[11px] transition-all whitespace-nowrap ${
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
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 sm:gap-5">
          {filteredInstruments.map(inst => {
            const cert = certificates.find(c => c.instrumentId === inst.instrumentId || c.certificateNumber === inst.certificateId);

            return (
              <div
                key={inst.id}
                className="bg-white rounded-3xl border border-slate-300 shadow-sm hover:border-cyan-500 hover:shadow-md transition-all flex flex-col justify-between overflow-hidden group"
              >
                {/* Card Header */}
                <div className="p-4 sm:p-5 pb-3 space-y-3">
                  
                  {/* Top ID & Risk Badge in clean horizontal bar */}
                  <div className="flex items-center justify-between gap-2">
                    <span className="font-mono font-black text-xs text-cyan-950 bg-cyan-100 px-3 py-1 rounded-lg border border-cyan-300 whitespace-nowrap shrink-0">
                      {inst.instrumentId}
                    </span>
                    <RiskScoreBadge riskLevel={inst.riskScore} className="shrink-0" />
                  </div>

                  <div>
                    <h3 className="text-sm sm:text-base font-black text-slate-950 group-hover:text-cyan-800 transition-colors line-clamp-1">
                      {inst.name}
                    </h3>
                    <p className="text-xs text-slate-700 font-bold mt-0.5 truncate">
                      {inst.manufacturer} • {inst.modelNumber}
                    </p>
                  </div>

                  {/* Status & Expiry */}
                  <div className="flex flex-wrap items-center gap-2 pt-0.5">
                    <InstrumentStatusBadge status={inst.status} />
                    <ExpiryStatusBadge validUntilDate={inst.nextVerificationDate} />
                  </div>

                  {/* Details Spec Box */}
                  <div className="p-3.5 bg-slate-50 rounded-2xl border border-slate-200 text-xs space-y-2">
                    <div className="flex justify-between items-center">
                      <span className="text-slate-800 font-bold">Capacity:</span>
                      <span className="font-black text-slate-950 text-xs sm:text-sm">{formatCapacity(inst.capacity, inst.unitOfMeasurement)}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-slate-800 font-bold">Serial No:</span>
                      <span className="font-mono font-bold text-slate-900 truncate max-w-[170px]">{inst.serialNumber}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-slate-800 font-bold">Tolerance:</span>
                      <span className="font-mono font-black text-slate-950">{formatTolerance(inst.permissibleTolerance)}</span>
                    </div>
                    <div className="flex items-center gap-1.5 text-xs text-slate-800 font-semibold pt-1.5 border-t border-slate-200">
                      <MapPin className="w-3.5 h-3.5 text-slate-700 shrink-0" />
                      <span className="truncate">{inst.location}</span>
                    </div>
                  </div>

                </div>

                {/* Card Actions Footer */}
                <div className="px-4 sm:px-5 py-3 bg-slate-100/90 border-t border-slate-200 flex items-center justify-between gap-2">
                  <button
                    onClick={() => setSelectedInstrument(inst)}
                    className="inline-flex items-center gap-1.5 px-3.5 py-2 bg-white text-slate-900 border border-slate-300 hover:bg-slate-50 rounded-xl text-xs font-bold transition-colors whitespace-nowrap shadow-xs"
                  >
                    <Eye className="w-3.5 h-3.5 text-slate-700" />
                    Details
                  </button>

                  <div className="flex items-center gap-2">
                    {cert && (
                      <button
                        onClick={() => {
                          setSelectedCertificate(cert);
                          setShowCertificateModal(true);
                        }}
                        className="inline-flex items-center gap-1.5 px-3 py-2 bg-emerald-100 text-emerald-950 border border-emerald-300 hover:bg-emerald-200 rounded-xl text-xs font-black transition-colors whitespace-nowrap"
                      >
                        <Award className="w-3.5 h-3.5 text-emerald-700" />
                        Certificate
                      </button>
                    )}

                    {(inst.status === 'DRAFT' || inst.status === 'FAILED') && (
                      <button
                        onClick={() => requestVerification(inst)}
                        className="inline-flex items-center gap-1.5 px-3.5 py-2 bg-cyan-600 hover:bg-cyan-700 text-white rounded-xl text-xs font-black shadow-xs transition-all active:scale-95 whitespace-nowrap"
                      >
                        <Send className="w-3.5 h-3.5" />
                        Verify
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
