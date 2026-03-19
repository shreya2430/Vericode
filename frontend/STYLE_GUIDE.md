# Vericode Frontend Style Guide

Reference for all frontend contributors. When building new pages or components,
follow these conventions so the UI stays consistent.

---

## Design Principles

- Dark mode only. No light mode variant.
- Sophisticated over flashy. Colors are muted, not electric.
- Minimal surface area. Only add what the page needs.
- All CSS tokens live in `src/index.css`. Never hardcode colors or radii.

---

## Color Tokens

Defined in `:root` inside `src/index.css`. Always use these variables.

### Backgrounds

| Token          | Value     | Use                                      |
|----------------|-----------|------------------------------------------|
| `--bg-base`    | `#111318` | Page background                          |
| `--bg-surface` | `#1c1f26` | Cards, panels, elevated containers      |
| `--bg-raised`  | `#252830` | Dropdowns, tooltips, modals             |

### Text

| Token            | Value     | Use                              |
|------------------|-----------|----------------------------------|
| `--text-primary` | `#ffffff` | Headings, body copy, labels      |
| `--text-muted`   | `#8b929e` | Subtitles, hints, secondary info |

### Accent Colors

Used for tags, highlights, and interactive elements. Pick one per semantic purpose
and stay consistent across pages.

| Token           | Value     | Suggested use              |
|-----------------|-----------|----------------------------|
| `--neon-green`  | `#6ee7b7` | Primary CTA, brand accent  |
| `--neon-cyan`   | `#7dd3fc` | Java / info tags           |
| `--neon-purple` | `#c4b5fd` | Lint / style tags          |
| `--neon-orange` | `#fdba74` | Warnings, JS tags          |

### Borders and Radius

| Token      | Value     | Use                            |
|------------|-----------|--------------------------------|
| `--border` | `#2e3138` | All borders and dividers       |
| `--radius` | `6px`     | All rounded corners            |

---

## Typography

Font stack (set on `body`, inherited everywhere):

```
-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, sans-serif
```

Always use `font-family: inherit` on `button` and `input` elements - browsers
override these by default.

### Scale in use

| Role            | Size       | Weight |
|-----------------|------------|--------|
| Hero title      | `4rem`     | 800    |
| Section heading | `1rem`     | 700    |
| Subtitle        | `1.15rem`  | 400    |
| Body / card     | `0.88rem`  | 400    |
| Button          | `0.875rem` | 500    |
| Tag label       | `0.78rem`  | 600    |
| Navbar brand    | `1.1rem`   | 700    |

Line height for body text: `1.6`.

---

## Background

The grid and glow are global - do not re-implement them per page.

### Grid

Applied to `body` in `src/index.css`:

```css
background-image:
  linear-gradient(rgba(255, 255, 255, 0.035) 1px, transparent 1px),
  linear-gradient(90deg, rgba(255, 255, 255, 0.035) 1px, transparent 1px);
background-size: 48px 48px;
```

### Radial glow (landing page)

A `::before` pseudo-element on `.landing` casts a soft green-to-cyan ellipse
behind the hero. Reuse this pattern on other hero sections if needed, adjusting
the color stops to match the page accent.

---

## Components

### Navbar (`src/components/Navbar.jsx`)

- `height: 56px`, fully transparent background, sticky at top.
- Brand: white text + `--neon-green` accent on the suffix.
- Links slot into `.navbar-links` (flex row, `gap: 28px`).
- Buttons in the navbar use `cursor: none` to preserve the custom cursor.

### Buttons

Two variants, both use `--radius` and `transition: opacity 0.15s`:

| Class          | Style                                          |
|----------------|------------------------------------------------|
| `.btn--primary`| Outlined, `--neon-green` border and text       |
| `.btn--ghost`  | Outlined, `--border` border, white text        |

Filled variant (e.g. Sign In button): solid `--neon-green` background,
`#0a0f0a` text, `transition: opacity 0.2s ease`, `opacity: 0.85` on hover.

### Tags

Pill shape (`border-radius: 999px`), uppercase, `letter-spacing: 0.04em`.
Background is the accent color at `0.1` opacity. Border uses `currentColor`.

```css
.tag--neon-green  { color: var(--neon-green);  background: rgba(110, 231, 183, 0.1); }
.tag--neon-cyan   { color: var(--neon-cyan);   background: rgba(125, 211, 252, 0.1); }
.tag--neon-purple { color: var(--neon-purple); background: rgba(196, 181, 253, 0.1); }
.tag--neon-orange { color: var(--neon-orange); background: rgba(253, 186, 116, 0.1); }
```

### Cards

```css
background: var(--bg-surface);
border: 1px solid var(--border);
border-radius: var(--radius);
padding: 28px 24px;
```

### Custom Cursor (`src/components/Cursor.jsx`)

Rendered once in `App.jsx`, applies globally. Two layers:

- **Dot** - 5px, `--neon-green` fill, glow via `box-shadow`. Snaps instantly.
- **Ring** - 32px hollow circle, `rgba(110, 231, 183, 0.5)` border, trails the
  dot with a lerp factor of `0.12`.

Because `cursor: none` is set on `body`, every interactive element must NOT
set `cursor: pointer`. Use `cursor: none` on buttons and links instead.

---

## File Structure

```
src/
  index.css                  Global tokens, reset, body background
  App.jsx                    Routes + Cursor + Navbar
  components/
    Cursor.jsx / Cursor.css  Custom cursor (global)
    Navbar.jsx / Navbar.css  Top navigation
    PRForm.jsx               PR submission form
    PRList.jsx               List of PR cards
    PRCard.jsx               Single PR summary
    CheckResults.jsx         Automated check output
    ReviewActions.jsx        Approve / Reject / Comment buttons
    CommentThread.jsx        Line-level comments
    NotificationBanner.jsx   In-app alerts
  pages/
    LandingPage.jsx / .css   Home - style reference page
    PRListPage.jsx           /prs
    PRSubmitPage.jsx         /submit
    PRDetailPage.jsx         /pr/:id
  services/
    api.js                   Axios base instance (proxied to port 8080)
    prService.js             All REST endpoint calls
  context/
    NotificationContext.jsx  In-app notification state (Observer pattern)
```

---

## Rules

1. All colors via CSS variables. No raw hex values in component CSS files.
2. All new interactive elements: set `cursor: none` to preserve the custom cursor.
3. Per-component CSS files live next to their component (`Navbar.css` beside `Navbar.jsx`).
4. Global styles only in `src/index.css`. Do not add page-level rules there.
5. No non-ASCII characters anywhere in source files. The autograder flags them.
