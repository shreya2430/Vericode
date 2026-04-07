import { useState } from 'react';
import { prService } from '../services/prService';

// Which buttons to show per status
const ACTIONS = {
  DRAFT:             ['submit'],
  IN_REVIEW:         ['approve', 'requestChanges'],
  CHANGES_REQUESTED: ['submit'],
  APPROVED:          ['merge'],
  MERGED:            [],
};

const BUTTON_LABEL = {
  submit:         'Submit for Review',
  approve:        'Approve',
  requestChanges: 'Request Changes',
  merge:          'Merge',
};

const BUTTON_CLASS = {
  submit:         'review-btn review-btn--submit',
  approve:        'review-btn review-btn--approve',
  requestChanges: 'review-btn review-btn--changes',
  merge:          'review-btn review-btn--merge',
};

// ReviewActions: approve / request-changes / merge / submit action buttons for a PR.
// Props:
//   pr             — current PR object
//   reviewer       — current user's username (sent as reviewer in request body)
//   onStatusChange — called with the new status string after a successful action
//   history        — string[] of command history entries
//   onUndo         — called after a successful undo
function ReviewActions({ pr, reviewer, onStatusChange, history = [], onUndo }) {
  const [loading, setLoading] = useState(null); // tracks which button is in flight
  const [error, setError]     = useState(null);

  const actions = ACTIONS[pr.status] ?? [];

  async function handleAction(action) {
    setError(null);
    setLoading(action);
    try {
      let res;
      if (action === 'submit') {
        res = await prService.submitForReview(pr.id);
        onStatusChange(res.data.status);
      } else if (action === 'approve') {
        res = await prService.approve(pr.id, reviewer);
        onStatusChange(res.data.status);
      } else if (action === 'requestChanges') {
        res = await prService.requestChanges(pr.id, reviewer);
        onStatusChange(res.data.status);
      } else if (action === 'merge') {
        res = await prService.merge(pr.id, reviewer);
        onStatusChange(res.data.status);
      }
    } catch (err) {
      const msg = err?.response?.data?.error || 'Action failed. Please try again.';
      setError(typeof msg === 'string' ? msg : 'Something went wrong.');
    } finally {
      setLoading(null);
    }
  }

  async function handleUndo() {
    setError(null);
    setLoading('undo');
    try {
      await prService.undo();
      onUndo();
    } catch (err) {
      const msg = err?.response?.data?.error || 'Nothing to undo.';
      setError(typeof msg === 'string' ? msg : 'Something went wrong.');
    } finally {
      setLoading(null);
    }
  }

  return (
    <div className="review-actions">
      <h3 className="review-actions__title">Actions</h3>

      {actions.length > 0 ? (
        <div className="review-actions__buttons">
          {actions.map(action => (
            <button
              key={action}
              className={BUTTON_CLASS[action]}
              onClick={() => handleAction(action)}
              disabled={loading !== null}
            >
              {loading === action ? 'Please wait...' : BUTTON_LABEL[action]}
            </button>
          ))}
        </div>
      ) : (
        <p className="review-actions__terminal">This PR has been merged.</p>
      )}

      {error && <p className="review-actions__error">{error}</p>}

      {history.length > 0 && (
        <div className="review-actions__history">
          <div className="review-actions__history-header">
            <h4 className="review-actions__history-title">History</h4>
            <button
              className="review-actions__undo-btn"
              onClick={handleUndo}
              disabled={loading !== null}
            >
              {loading === 'undo' ? '...' : 'Undo last'}
            </button>
          </div>
          <ul className="review-actions__history-list">
            {[...history].reverse().map((entry, i) => (
              <li key={i} className="review-actions__history-entry">{entry}</li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}

export default ReviewActions;
