import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import './PRCard.css';

// Maps each PRStatus to a tag color class from the global style guide.
const STATUS_COLOR = {
  DRAFT:              'tag--neon-purple',
  IN_REVIEW:          'tag--neon-cyan',
  CHANGES_REQUESTED:  'tag--neon-orange',
  APPROVED:           'tag--neon-green',
  MERGED:             'tag--neon-green',
};

// Human-readable labels for each status value.
const STATUS_LABEL = {
  DRAFT:              'Draft',
  IN_REVIEW:          'In Review',
  CHANGES_REQUESTED:  'Changes Requested',
  APPROVED:           'Approved',
  MERGED:             'Merged',
};

// Maps each Language to a tag color class per style guide suggested use:
// Java -> cyan, JavaScript -> orange, Python -> green.
const LANGUAGE_COLOR = {
  JAVA:       'tag--neon-cyan',
  JAVASCRIPT: 'tag--neon-orange',
  PYTHON:     'tag--neon-green',
};

function formatDate(dateStr) {
  if (!dateStr) return '';
  const date = new Date(dateStr);
  return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}

// index drives the staggered animation delay so cards cascade in one by one.
function PRCard({ pr, index = 0 }) {
  return (
    <Link
      to={`/pr/${pr.id}`}
      className="pr-card"
      style={{ animationDelay: `${index * 0.05}s` }}
    >
      <div className="pr-card__top">
        <span className="pr-card__title">{pr.title}</span>
        <span className={`tag ${STATUS_COLOR[pr.status]}`}>
          {STATUS_LABEL[pr.status] || pr.status}
        </span>
      </div>

      <div className="pr-card__meta">
        <span className="pr-card__author">
          {typeof pr.author === 'object' ? pr.author?.username : pr.author}
        </span>
        <span className={`tag ${LANGUAGE_COLOR[pr.language]}`}>{pr.language}</span>
        <span className="pr-card__date">{formatDate(pr.createdAt)}</span>
      </div>
    </Link>
  );
}

PRCard.propTypes = {
  pr: PropTypes.shape({
    id:        PropTypes.number.isRequired,
    title:     PropTypes.string.isRequired,
    author:    PropTypes.oneOfType([PropTypes.string, PropTypes.object]).isRequired,
    language:  PropTypes.string.isRequired,
    status:    PropTypes.string.isRequired,
    createdAt: PropTypes.string,
  }).isRequired,
  index: PropTypes.number,
};

export default PRCard;
