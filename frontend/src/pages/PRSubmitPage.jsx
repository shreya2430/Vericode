import { useState } from 'react';
import { useUser } from '../context/UserContext';
import { useNotifications } from '../context/NotificationContext';
import { prService } from '../services/prService';
import PRForm from '../components/PRForm';
import CheckResults from '../components/CheckResults';
import './PRSubmitPage.css';

function PRSubmitPage() {
  const { user } = useUser();
  const { notify } = useNotifications();
  const [loading, setLoading]   = useState(false);
  const [error, setError]       = useState(null);
  const [result, setResult]     = useState(null); // { pullRequest, checkResult, reviewChecklist }

  async function handleSubmit(formData) {
    setError(null);
    setLoading(true);
    try {
      const res = await prService.submit({ ...formData, authorId: user.id });
      setResult(res.data);
      notify('PR submitted, ready for review!');
      // Scroll results into view on small screens
      setTimeout(() => {
        document.getElementById('check-results-anchor')?.scrollIntoView({ behavior: 'smooth' });
      }, 100);
    } catch (err) {
      const msg =
        err?.response?.data?.error ||
        err?.response?.data ||
        'Submission failed. Please try again.';
      setError(typeof msg === 'string' ? msg : 'Something went wrong.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="submit-page">
      <div className="submit-page__layout">

        {/* Left: form */}
        <div className="submit-page__form-panel">
          <div className="submit-page__header">
            <h1 className="submit-page__title">Submit a Pull Request</h1>
            <p className="submit-page__subtitle">
              Your code will be automatically checked for lint, style, and security issues.
            </p>
          </div>

          <PRForm
            onSubmit={handleSubmit}
            loading={loading}
            prId={result?.pullRequest?.id}
            onCreateAnother={() => setResult(null)}
          />

          {error && (
            <p className="submit-page__error">{error}</p>
          )}
        </div>

        {/* Right: results (only visible after a successful submit) */}
        <div className={`submit-page__results-panel${result ? ' submit-page__results-panel--visible' : ''}`}>
          <div id="check-results-anchor" />
          {result && (
            <CheckResults
              checkResult={result.checkResult}
              checklist={result.reviewChecklist}
              prId={result.pullRequest.id}
            />
          )}
          {!result && (
            <div className="submit-page__results-empty">
              <p>Results will appear here after you submit.</p>
            </div>
          )}
        </div>

      </div>
    </div>
  );
}

export default PRSubmitPage;
