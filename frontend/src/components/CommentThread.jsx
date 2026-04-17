import { useState, useEffect } from 'react';

// CommentThread: shows line-level comments on a PR and a form to add new ones.
// Comments are kept in local React state (backend Review/Comment are not persisted to DB).
// Props:
//   comments      — [{ lineNumber, author, content }]
//   lineCount     — total lines in the code snippet, used to clamp/validate input
//   selectedLine  — pre-fills the line number when a code line is clicked
//   onAddComment  — called with (lineNumber: number, content: string)
//   disabled      — disables the form (e.g. while a request is in flight)
function CommentThread({ comments = [], lineCount, selectedLine, onAddComment, disabled }) {
  const [lineInput, setLineInput] = useState(selectedLine != null ? String(selectedLine) : '');
  const [content, setContent]     = useState('');
  const [error, setError]         = useState('');
  const [loading, setLoading]     = useState(false);

  useEffect(() => {
    if (selectedLine != null) setLineInput(String(selectedLine));
  }, [selectedLine]);

  function validate() {
    const n = parseInt(lineInput, 10);
    if (!lineInput.trim() || isNaN(n) || n < 1) return 'Enter a valid line number.';
    if (lineCount && n > lineCount) return `Line number must be between 1 and ${lineCount}.`;
    if (!content.trim()) return 'Comment cannot be empty.';
    return '';
  }

  async function handleSubmit(e) {
    e.preventDefault();
    const err = validate();
    if (err) { setError(err); return; }
    setError('');
    setLoading(true);
    try {
      await onAddComment(parseInt(lineInput, 10), content.trim());
      setLineInput('');
      setContent('');
    } catch {
      setError('Failed to post comment.');
    } finally {
      setLoading(false);
    }
  }

  const sorted = [...comments].sort((a, b) => a.lineNumber - b.lineNumber);

  return (
    <div className="comment-thread">
      <h3 className="comment-thread__title">Comments</h3>

      {sorted.length === 0 && (
        <p className="comment-thread__empty">No comments yet. Click a line in the code to annotate it.</p>
      )}

      {sorted.length > 0 && (
        <ul className="comment-thread__list">
          {sorted.map((c, i) => (
            <li key={i} className="comment-item">
              <div className="comment-item__meta">
                <span className="comment-item__author">{c.author}</span>
                <span className="comment-item__line">line {c.lineNumber}</span>
              </div>
              <p className="comment-item__content">{c.content}</p>
            </li>
          ))}
        </ul>
      )}

      {/* New comment form — key forces re-mount (resets inputs) when selectedLine changes */}
      <form
        key={selectedLine}
        className="comment-form"
        onSubmit={handleSubmit}
        noValidate
      >
        <div className="comment-form__row">
          <div className="comment-form__line-field">
            <label htmlFor="comment-line">Line</label>
            <input
              id="comment-line"
              type="number"
              min={1}
              max={lineCount || undefined}
              value={lineInput}
              onChange={e => setLineInput(e.target.value)}
              placeholder="1"
              disabled={disabled || loading}
            />
          </div>
          <div className="comment-form__content-field">
            <label htmlFor="comment-content">Comment</label>
            <textarea
              id="comment-content"
              rows={3}
              placeholder="Leave a comment on this line..."
              value={content}
              onChange={e => { setContent(e.target.value); setError(''); }}
              disabled={disabled || loading}
            />
          </div>
        </div>
        {error && <p className="comment-form__error">{error}</p>}
        <button
          className="comment-form__submit"
          type="submit"
          disabled={disabled || loading}
        >
          {loading ? 'Posting...' : 'Add comment'}
        </button>
      </form>
    </div>
  );
}

export default CommentThread;
