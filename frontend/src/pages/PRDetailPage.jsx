import { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { useUser } from '../context/UserContext';
import { prService } from '../services/prService';
import ReviewActions from '../components/ReviewActions';
import CommentThread from '../components/CommentThread';
import CheckResults from '../components/CheckResults';
import './PRDetailPage.css';

const STATUS_LABEL = {
  DRAFT:             'Draft',
  IN_REVIEW:         'In Review',
  CHANGES_REQUESTED: 'Changes Requested',
  APPROVED:          'Approved',
  MERGED:            'Merged',
};

const STATUS_CLASS = {
  DRAFT:             'status-tag--purple',
  IN_REVIEW:         'status-tag--cyan',
  CHANGES_REQUESTED: 'status-tag--orange',
  APPROVED:          'status-tag--green',
  MERGED:            'status-tag--green',
};

const LANGUAGE_CLASS = {
  JAVA:       'lang-tag--cyan',
  JAVASCRIPT: 'lang-tag--orange',
  PYTHON:     'lang-tag--green',
};

function formatDate(dateStr) {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric',
  });
}

function PRDetailPage() {
  const { id }     = useParams();
  const { user }   = useUser();
  const navigate   = useNavigate();

  const [pr, setPr]               = useState(null);
  const [status, setStatus]       = useState(null); // kept separate so actions can update it
  const [loading, setLoading]     = useState(true);
  const [error, setError]         = useState(null);
  const [comments, setComments]   = useState([]);   // local — not persisted by backend
  const [history, setHistory]     = useState([]);
  const [selectedLine, setSelectedLine] = useState(null);
  const [checkData, setCheckData] = useState(null);

  useEffect(() => {
    Promise.all([
      prService.getById(id),
      prService.getHistory(),
      prService.check(id),
    ])
      .then(([prRes, histRes, checkRes]) => {
        setPr(prRes.data);
        setStatus(prRes.data.status);
        setHistory(histRes.data);
        setCheckData(checkRes.data);
      })
      .catch(() => setError('Failed to load pull request.'))
      .finally(() => setLoading(false));
  }, [id]);

  function handleStatusChange(newStatus) {
    setStatus(newStatus);
    // Refresh history after every state transition
    prService.getHistory().then(res => setHistory(res.data)).catch(() => {});
  }

  function handleUndo() {
    // After undo we don't know what the new status is — refetch the PR
    prService.getById(id).then(res => {
      setStatus(res.data.status);
    }).catch(() => {});
    prService.getHistory().then(res => setHistory(res.data)).catch(() => {});
  }

  async function handleAddComment(lineNumber, content) {
    await prService.comment(id, user.username, lineNumber, content);
    setComments(prev => [...prev, { lineNumber, author: user.username, content }]);
    setSelectedLine(null);
    prService.getHistory().then(res => setHistory(res.data)).catch(() => {});
  }

  if (loading) return <div className="pr-detail-page"><p className="pr-detail__state">Loading...</p></div>;
  if (error)   return <div className="pr-detail-page"><p className="pr-detail__state pr-detail__state--error">{error}</p></div>;
  if (!pr)     return null;

  const codeLines = pr.codeSnippet ? pr.codeSnippet.split('\n') : [];
  const commentedLines = new Set(comments.map(c => c.lineNumber));

  return (
    <div className="pr-detail-page">

      {/* Back link */}
      <Link className="pr-detail__back" to="/prs">← All pull requests</Link>

      {/* Header */}
      <div className="pr-detail__header">
        <div className="pr-detail__title-row">
          <h1 className="pr-detail__title">{pr.title}</h1>
          <span className={`status-tag ${STATUS_CLASS[status]}`}>
            {STATUS_LABEL[status] || status}
          </span>
        </div>
        <div className="pr-detail__meta">
          <span className="pr-detail__author">
            {pr.author?.username || pr.author}
          </span>
          <span className={`lang-tag ${LANGUAGE_CLASS[pr.language]}`}>{pr.language}</span>
          <span className="pr-detail__date">{formatDate(pr.createdAt)}</span>
        </div>
        {pr.description && (
          <p className="pr-detail__description">{pr.description}</p>
        )}
      </div>

      {/* Body */}
      <div className="pr-detail__body">

        {/* Left: code */}
        <div className="pr-detail__code-panel">
          <h3 className="pr-detail__code-label">
            Code Snippet
            <span className="pr-detail__code-hint">click a line to annotate</span>
          </h3>
          <div className="pr-detail__code-block">
            {codeLines.map((line, i) => {
              const lineNum = i + 1;
              const hasComment = commentedLines.has(lineNum);
              const isSelected = selectedLine === lineNum;
              return (
                <div
                  key={i}
                  className={`code-line${hasComment ? ' code-line--commented' : ''}${isSelected ? ' code-line--selected' : ''}`}
                  onClick={() => setSelectedLine(isSelected ? null : lineNum)}
                >
                  <span className="code-line__num">{lineNum}</span>
                  {hasComment && <span className="code-line__dot" title="Has comment" />}
                  <span className="code-line__text">{line || ' '}</span>
                </div>
              );
            })}
          </div>

          {/* Check results below code */}
          {checkData && (
            <div className="pr-detail__checks">
              <CheckResults
                checkResult={checkData.checkResult}
                checklist={checkData.reviewChecklist}
              />
            </div>
          )}
        </div>

        {/* Right: actions + comments */}
        <div className="pr-detail__sidebar">
          <ReviewActions
            pr={{ ...pr, status }}
            reviewer={user.username}
            onStatusChange={handleStatusChange}
            history={history}
            onUndo={handleUndo}
            onDelete={() => navigate('/prs')}
          />

          <div className="pr-detail__divider" />

          <CommentThread
            comments={comments}
            lineCount={codeLines.length}
            selectedLine={selectedLine}
            onAddComment={handleAddComment}
          />
        </div>

      </div>
    </div>
  );
}

export default PRDetailPage;
