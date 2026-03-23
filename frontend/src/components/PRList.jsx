import PropTypes from 'prop-types';
import PRCard from './PRCard';
import './PRList.css';

// listKey changes whenever filters or sort order change in PRListPage.
// Applying it as the key on <ul> forces React to remount the list,
// which replays the entry animation on every card.
function PRList({ prs, listKey }) {
  if (prs.length === 0) {
    return <p className="pr-list__empty">No pull requests match the current filters.</p>;
  }

  return (
    <ul className="pr-list" key={listKey}>
      {prs.map((pr, index) => (
        <li key={pr.id}>
          <PRCard pr={pr} index={index} />
        </li>
      ))}
    </ul>
  );
}

PRList.propTypes = {
  prs:     PropTypes.array.isRequired,
  listKey: PropTypes.string,
};

export default PRList;
