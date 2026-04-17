import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const LANGUAGES = ['JAVA', 'PYTHON', 'JAVASCRIPT'];

const LANGUAGE_LABEL = {
  JAVA: 'Java',
  PYTHON: 'Python',
  JAVASCRIPT: 'JavaScript',
};

const PLACEHOLDERS = {
  JAVA: `public class Main {\n    public static void main(String[] args) {\n        System.out.println("Hello, world!");\n    }\n}`,
  PYTHON: `def greet(name):\n    print(f"Hello, {name}!")`,
  JAVASCRIPT: `function greet(name) {\n    console.log(\`Hello, \${name}!\`);\n}`,
};

function validate(form) {
  const errors = {};
  if (!form.title.trim()) errors.title = 'Title is required.';
  if (!form.codeSnippet.trim()) errors.codeSnippet = 'Code snippet is required.';
  return errors;
}

// PRForm: controlled form for submitting a new PR
// Props:
//   onSubmit(formData) — called with { title, description, language, codeSnippet }
//   loading            — disables the submit button while the request is in flight
const EMPTY_FORM = { title: '', description: '', language: 'JAVA', codeSnippet: '' };

function PRForm({ onSubmit, loading, prId, onCreateAnother }) {
  const navigate = useNavigate();
  const [form, setForm] = useState(EMPTY_FORM);
  const [fieldErrors, setFieldErrors] = useState({});
  const [touched, setTouched] = useState({});

  function handleChange(e) {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
    if (touched[name]) {
      setFieldErrors(prev => ({ ...prev, [name]: validate({ ...form, [name]: value })[name] || '' }));
    }
  }

  function handleBlur(e) {
    const { name } = e.target;
    setTouched(prev => ({ ...prev, [name]: true }));
    setFieldErrors(prev => ({ ...prev, [name]: validate(form)[name] || '' }));
  }

  function handleSubmit(e) {
    e.preventDefault();
    const errors = validate(form);
    if (Object.keys(errors).filter(k => errors[k]).length > 0) {
      setFieldErrors(errors);
      setTouched({ title: true, codeSnippet: true });
      return;
    }
    onSubmit(form);
  }

  return (
    <form className="pr-form" onSubmit={handleSubmit} noValidate>
      {/* Title */}
      <div className={`pr-field${fieldErrors.title ? ' pr-field--error' : ''}`}>
        <label htmlFor="title">Title</label>
        <input
          id="title"
          name="title"
          type="text"
          placeholder="Fix null pointer in AuthService"
          value={form.title}
          onChange={handleChange}
          onBlur={handleBlur}
          disabled={loading}
        />
        {fieldErrors.title && <span className="pr-field-error">{fieldErrors.title}</span>}
      </div>

      {/* Description */}
      <div className="pr-field">
        <label htmlFor="description">Description <span className="pr-field-optional">(optional)</span></label>
        <textarea
          id="description"
          name="description"
          placeholder="Briefly describe what this PR does and why..."
          value={form.description}
          onChange={handleChange}
          rows={3}
          disabled={loading}
          className="pr-textarea"
        />
      </div>

      {/* Language */}
      <div className="pr-field">
        <label>Language</label>
        <div className="pr-lang-group">
          {LANGUAGES.map(lang => (
            <button
              key={lang}
              type="button"
              className={`pr-lang-btn${form.language === lang ? ' pr-lang-btn--active' : ''}`}
              onClick={() => setForm(prev => ({ ...prev, language: lang }))}
              disabled={loading}
            >
              {LANGUAGE_LABEL[lang]}
            </button>
          ))}
        </div>
      </div>

      {/* Code snippet */}
      <div className={`pr-field${fieldErrors.codeSnippet ? ' pr-field--error' : ''}`}>
        <label htmlFor="codeSnippet">Code Snippet</label>
        <textarea
          id="codeSnippet"
          name="codeSnippet"
          placeholder={PLACEHOLDERS[form.language]}
          value={form.codeSnippet}
          onChange={handleChange}
          onBlur={handleBlur}
          rows={14}
          disabled={loading}
          className="pr-textarea pr-textarea--code"
          spellCheck={false}
        />
        {fieldErrors.codeSnippet && (
          <span className="pr-field-error">{fieldErrors.codeSnippet}</span>
        )}
      </div>

      {prId ? (
        <div className="pr-submit-row">
          <button className="pr-submit-btn" type="button" onClick={() => navigate(`/pr/${prId}`)}>
            PR Review
          </button>
          <button
            className="pr-submit-btn pr-submit-btn--secondary"
            type="button"
            onClick={() => {
              setForm(EMPTY_FORM);
              setFieldErrors({});
              setTouched({});
              onCreateAnother();
            }}
          >
            Create another
          </button>
        </div>
      ) : (
        <button className="pr-submit-btn" type="submit" disabled={loading}>
          {loading ? 'Running checks...' : 'Submit pull request'}
        </button>
      )}
    </form>
  );
}

export default PRForm;
