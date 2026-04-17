import { useState } from 'react';
import { Link } from 'react-router-dom';

const SEVERITY_CLASS = {
  ERROR:   'badge--error',
  WARNING: 'badge--warning',
  INFO:    'badge--info',
};

const TYPE_CLASS = {
  LINT:     'badge--lint',
  STYLE:    'badge--style',
  SECURITY: 'badge--security',
};

function CheckResults({ checkResult, checklist }) {
  const [checkedItems, setCheckedItems] = useState({});

  function toggleItem(i) {
    setCheckedItems(prev => ({ ...prev, [i]: !prev[i] }));
  }

  const { passed, violations = [], errorCount, warningCount } = checkResult;

  return (
    <div className="check-results">
      {/* Header */}
      <div className={`check-results__banner ${passed ? 'check-results__banner--pass' : 'check-results__banner--fail'}`}>
        <div className="check-results__banner-icon">{passed ? '✓' : '✗'}</div>
        <div>
          <p className="check-results__banner-title">
            {passed ? 'All checks passed' : 'Checks completed with issues'}
          </p>
          <p className="check-results__banner-sub">
            {errorCount} error{errorCount !== 1 ? 's' : ''} · {warningCount} warning{warningCount !== 1 ? 's' : ''}
          </p>
        </div>
      </div>

      {/* Violations */}
      {violations.length > 0 ? (
        <div className="check-results__section">
          <h3 className="check-results__section-title">Violations</h3>
          <ul className="check-results__violations">
            {violations.map((v, i) => (
              <li key={i} className="violation">
                <div className="violation__meta">
                  <span className={`badge ${TYPE_CLASS[v.type] || 'badge--info'}`}>{v.type}</span>
                  <span className={`badge ${SEVERITY_CLASS[v.severity] || 'badge--info'}`}>{v.severity}</span>
                  {v.lineNumber != null && (
                    <span className="violation__line">line {v.lineNumber}</span>
                  )}
                </div>
                <p className="violation__message">{v.message}</p>
              </li>
            ))}
          </ul>
        </div>
      ) : (
        <div className="check-results__section">
          <p className="check-results__empty">No violations found.</p>
        </div>
      )}

      {/* Review checklist */}
      {checklist && checklist.length > 0 && (
        <div className="check-results__section">
          <h3 className="check-results__section-title">Review Checklist</h3>
          <ul className="check-results__checklist">
            {checklist.map((item, i) => (
              <li
                key={i}
                className={`checklist-item${checkedItems[i] ? ' checklist-item--checked' : ''}`}
                onClick={() => toggleItem(i)}
              >
                <span className="checklist-item__box">{checkedItems[i] ? '✓' : ''}</span>
                <span className="checklist-item__label">{item}</span>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* CTA */}
      <div className="check-results__actions">
        <Link className="check-results__list-link" to="/prs">
          Back to all PRs
        </Link>
      </div>
    </div>
  );
}

export default CheckResults;
