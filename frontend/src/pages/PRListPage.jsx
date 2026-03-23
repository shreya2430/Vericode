import { useEffect, useRef, useState } from 'react';
import { prService } from '../services/prService';
import PRList from '../components/PRList';
import './PRListPage.css';

const STATUSES = ['DRAFT', 'IN_REVIEW', 'CHANGES_REQUESTED', 'APPROVED', 'MERGED'];

const STATUS_LABEL = {
  DRAFT:             'Draft',
  IN_REVIEW:         'In Review',
  CHANGES_REQUESTED: 'Changes Requested',
  APPROVED:          'Approved',
  MERGED:            'Merged',
};

const STATUS_COLOR = {
  DRAFT:             'tag--neon-purple',
  IN_REVIEW:         'tag--neon-cyan',
  CHANGES_REQUESTED: 'tag--neon-orange',
  APPROVED:          'tag--neon-green',
  MERGED:            'tag--neon-green',
};

function PRListPage() {
  const [prs, setPrs]                       = useState([]);
  const [loading, setLoading]               = useState(true);
  const [error, setError]                   = useState(null);
  const [selectedStatuses, setSelectedStatuses] = useState([]);
  const [sortOrder, setSortOrder]           = useState('desc'); // desc = newest first
  const [dropdownOpen, setDropdownOpen]     = useState(false);
  const dropdownRef                         = useRef(null);

  useEffect(() => {
    prService.getAll()
      .then(res => setPrs(res.data))
      .catch(() => setError('Failed to load pull requests.'))
      .finally(() => setLoading(false));
  }, []);

  // Close dropdown when clicking outside of it
  useEffect(() => {
    function handleClickOutside(e) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setDropdownOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  function toggleStatus(status) {
    setSelectedStatuses(prev =>
      prev.includes(status) ? prev.filter(s => s !== status) : [...prev, status]
    );
  }

  function removeStatus(status) {
    setSelectedStatuses(prev => prev.filter(s => s !== status));
  }

  const displayed = prs
    .filter(pr => selectedStatuses.length === 0 || selectedStatuses.includes(pr.status))
    .sort((a, b) => {
      const dateA = new Date(a.createdAt);
      const dateB = new Date(b.createdAt);
      return sortOrder === 'desc' ? dateB - dateA : dateA - dateB;
    });

  return (
    <div className="pr-list-page">
      <div className="pr-list-page__header">
        <h1 className="pr-list-page__title">Pull Requests</h1>
        <p className="pr-list-page__subtitle">All submitted pull requests</p>
      </div>

      <div className="pr-list-page__controls">

        {/* Multi-select status dropdown */}
        <div className="pr-dropdown" ref={dropdownRef}>
          <button
            className="pr-dropdown__trigger"
            onClick={() => setDropdownOpen(prev => !prev)}
          >
            {selectedStatuses.length === 0
              ? 'Filter by status'
              : `${selectedStatuses.length} status${selectedStatuses.length > 1 ? 'es' : ''} selected`}
            <span className={`pr-dropdown__arrow ${dropdownOpen ? 'pr-dropdown__arrow--open' : ''}`} />
          </button>

          {dropdownOpen && (
            <ul className="pr-dropdown__menu">
              {STATUSES.map(status => (
                <li key={status} className="pr-dropdown__item">
                  <label className="pr-dropdown__label">
                    <input
                      type="checkbox"
                      checked={selectedStatuses.includes(status)}
                      onChange={() => toggleStatus(status)}
                      className="pr-dropdown__checkbox"
                    />
                    <span className={`tag ${STATUS_COLOR[status]}`}>
                      {STATUS_LABEL[status]}
                    </span>
                  </label>
                </li>
              ))}
            </ul>
          )}
        </div>

        {/* Ascending / descending sort toggle */}
        <button
          className="pr-sort-toggle"
          onClick={() => setSortOrder(prev => prev === 'desc' ? 'asc' : 'desc')}
        >
          {sortOrder === 'desc' ? 'Newest first' : 'Oldest first'}
          <span className={`pr-sort-toggle__arrow ${sortOrder === 'asc' ? 'pr-sort-toggle__arrow--up' : ''}`} />
        </button>

      </div>

      {/* Active filter tags - shown only when statuses are selected */}
      {selectedStatuses.length > 0 && (
        <div className="pr-list-page__active-filters">
          {selectedStatuses.map(status => (
            <span key={status} className={`tag ${STATUS_COLOR[status]} pr-filter-tag`}>
              {STATUS_LABEL[status]}
              <button
                className="pr-filter-tag__remove"
                onClick={() => removeStatus(status)}
                aria-label={`Remove ${STATUS_LABEL[status]} filter`}
              >
                x
              </button>
            </span>
          ))}
        </div>
      )}

      {loading && <p className="pr-list-page__state">Loading...</p>}
      {error   && <p className="pr-list-page__state pr-list-page__state--error">{error}</p>}
      {!loading && !error && (
        <PRList
          prs={displayed}
          listKey={JSON.stringify(selectedStatuses) + sortOrder}
        />
      )}
    </div>
  );
}

export default PRListPage;
