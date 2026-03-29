import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../context/UserContext';
import { updateUser } from '../services/userService';
import './ProfilePage.css';

function formatDate(dateStr) {
  if (!dateStr) return '-';
  const d = new Date(dateStr);
  return d.toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' });
}

function getInitials(name) {
  if (!name) return '?';
  return name
    .split(' ')
    .map((w) => w[0])
    .slice(0, 2)
    .join('')
    .toUpperCase();
}

function IconPencil() {
  return (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7" />
      <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z" />
    </svg>
  );
}

function IconCheck() {
  return (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="20 6 9 17 4 12" />
    </svg>
  );
}

function IconX() {
  return (
    <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
      <line x1="18" y1="6" x2="6" y2="18" />
      <line x1="6" y1="6" x2="18" y2="18" />
    </svg>
  );
}

function ProfilePage() {
  const { user, login, logout } = useUser();
  const navigate = useNavigate();

  const [editingField, setEditingField] = useState(null);
  const [editValue, setEditValue] = useState('');
  const [fieldError, setFieldError] = useState('');
  const [saving, setSaving] = useState(false);

  if (!user) {
    navigate('/auth');
    return null;
  }

  function startEdit(field) {
    setEditingField(field);
    setEditValue(user[field]);
    setFieldError('');
  }

  function cancelEdit() {
    setEditingField(null);
    setEditValue('');
    setFieldError('');
  }

  async function saveEdit() {
    if (!editValue.trim()) {
      setFieldError(`${editingField === 'name' ? 'Name' : 'Username'} cannot be blank.`);
      return;
    }
    if (editValue.trim() === user[editingField]) {
      cancelEdit();
      return;
    }
    setSaving(true);
    setFieldError('');
    try {
      const newName = editingField === 'name' ? editValue.trim() : user.name;
      const newUsername = editingField === 'username' ? editValue.trim() : user.username;
      const updated = await updateUser(user.id, newName, newUsername);
      login({ ...user, name: updated.name, username: updated.username });
      setEditingField(null);
    } catch (err) {
      const msg = err?.response?.data?.error || 'Failed to save.';
      setFieldError(msg);
    } finally {
      setSaving(false);
    }
  }

  function handleKeyDown(e) {
    if (e.key === 'Enter') saveEdit();
    if (e.key === 'Escape') cancelEdit();
  }

  function handleSignOut() {
    logout();
    navigate('/');
  }

  const readOnlyFields = [
    { label: 'Email',        value: user.email,              mono: false },
    { label: 'User ID',      value: `#${user.id}`,           mono: true  },
    { label: 'Member since', value: formatDate(user.createdAt), mono: false },
  ];

  return (
    <div className="profile-page">
      <div className="profile-card">
        <div className="profile-avatar">{getInitials(user.name)}</div>
        <div className="profile-name">{user.name}</div>
        <div className="profile-username">@{user.username}</div>

        <div className="profile-divider" />

        <dl className="profile-fields">

          {/* Name - editable */}
          <div className="profile-field">
            <dt className="profile-field__label">Full name</dt>
            <dd className="profile-field__value">
              {editingField === 'name' ? (
                <div className="profile-inline-edit">
                  <div className="profile-input-wrap">
                    <input
                      className={`profile-input${fieldError ? ' profile-input--error' : ''}`}
                      value={editValue}
                      onChange={(e) => { setEditValue(e.target.value); setFieldError(''); }}
                      onKeyDown={handleKeyDown}
                      autoFocus
                      disabled={saving}
                    />
                    {fieldError && <span className="profile-input-error">{fieldError}</span>}
                  </div>
                  <div className="profile-inline-actions">
                    <button className="profile-icon-btn profile-icon-btn--confirm" onClick={saveEdit} disabled={saving} title="Save">
                      <IconCheck />
                    </button>
                    <button className="profile-icon-btn profile-icon-btn--cancel" onClick={cancelEdit} disabled={saving} title="Cancel">
                      <IconX />
                    </button>
                  </div>
                </div>
              ) : (
                <div className="profile-value-row">
                  <span>{user.name || '-'}</span>
                  <button className="profile-icon-btn profile-icon-btn--edit" onClick={() => startEdit('name')} title="Edit name">
                    <IconPencil />
                  </button>
                </div>
              )}
            </dd>
          </div>

          {/* Username - editable */}
          <div className="profile-field">
            <dt className="profile-field__label">Username</dt>
            <dd className="profile-field__value">
              {editingField === 'username' ? (
                <div className="profile-inline-edit">
                  <div className="profile-input-wrap">
                    <input
                      className={`profile-input profile-input--mono${fieldError ? ' profile-input--error' : ''}`}
                      value={editValue}
                      onChange={(e) => { setEditValue(e.target.value); setFieldError(''); }}
                      onKeyDown={handleKeyDown}
                      autoFocus
                      disabled={saving}
                    />
                    {fieldError && <span className="profile-input-error">{fieldError}</span>}
                  </div>
                  <div className="profile-inline-actions">
                    <button className="profile-icon-btn profile-icon-btn--confirm" onClick={saveEdit} disabled={saving} title="Save">
                      <IconCheck />
                    </button>
                    <button className="profile-icon-btn profile-icon-btn--cancel" onClick={cancelEdit} disabled={saving} title="Cancel">
                      <IconX />
                    </button>
                  </div>
                </div>
              ) : (
                <div className="profile-value-row">
                  <span className="profile-field__value--mono">{user.username || '-'}</span>
                  <button className="profile-icon-btn profile-icon-btn--edit" onClick={() => startEdit('username')} title="Edit username">
                    <IconPencil />
                  </button>
                </div>
              )}
            </dd>
          </div>

          {/* Read-only fields */}
          {readOnlyFields.map((f) => (
            <div className="profile-field" key={f.label}>
              <dt className="profile-field__label">{f.label}</dt>
              <dd className={`profile-field__value${f.mono ? ' profile-field__value--mono' : ''}`}>
                {f.value || '-'}
              </dd>
            </div>
          ))}

        </dl>

        <div className="profile-divider" />

        <button className="profile-signout" onClick={handleSignOut}>
          Sign out
        </button>
      </div>
    </div>
  );
}

export default ProfilePage;
