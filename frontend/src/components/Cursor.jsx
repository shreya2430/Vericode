import { useEffect, useRef } from 'react';
import './Cursor.css';

const TRAIL_COUNT = 6;

function Cursor() {
  const dotRef   = useRef(null);
  const ringRef  = useRef(null);
  const trailRef = useRef([]);

  useEffect(() => {
    const dot   = dotRef.current;
    const ring  = ringRef.current;
    const trail = trailRef.current;

    let mouseX = 0, mouseY = 0;
    let ringX  = 0, ringY  = 0;
    let rotation = 0;
    let rafId;

    const points = Array.from({ length: TRAIL_COUNT }, () => ({ x: 0, y: 0 }));

    const lerp = (a, b, t) => a + (b - a) * t;

    const onMove = (e) => {
      mouseX = e.clientX;
      mouseY = e.clientY;
      dot.style.transform = `translate(${mouseX}px, ${mouseY}px)`;
    };

    const tick = () => {
      rotation += 0.5;

      ringX = lerp(ringX, mouseX, 0.12);
      ringY = lerp(ringY, mouseY, 0.12);
      ring.style.transform = `translate(${ringX}px, ${ringY}px) rotate(${rotation}deg)`;

      let prevX = mouseX, prevY = mouseY;
      for (let i = 0; i < TRAIL_COUNT; i++) {
        points[i].x = lerp(points[i].x, prevX, 0.12);
        points[i].y = lerp(points[i].y, prevY, 0.12);
        if (trail[i]) {
          trail[i].style.transform = `translate(${points[i].x}px, ${points[i].y}px)`;
        }
        prevX = points[i].x;
        prevY = points[i].y;
      }

      rafId = requestAnimationFrame(tick);
    };

    const setVisible = (v) => {
      const vis = v ? '' : 'hidden';
      dot.style.visibility  = vis;
      ring.style.visibility = vis;
      trail.forEach((t) => { if (t) t.style.visibility = vis; });
    };

    document.addEventListener('mousemove', onMove);
    document.addEventListener('mouseenter', () => setVisible(true));
    document.addEventListener('mouseleave', () => setVisible(false));
    rafId = requestAnimationFrame(tick);

    return () => {
      document.removeEventListener('mousemove', onMove);
      cancelAnimationFrame(rafId);
    };
  }, []);

  return (
    <>
      <div ref={dotRef}  className="cursor-dot" />
      <div ref={ringRef} className="cursor-ring" />
      {Array.from({ length: TRAIL_COUNT }, (_, i) => {
        const scale = 1 - i / TRAIL_COUNT;
        const size  = Math.max(2, 4 * scale);
        return (
          <div
            key={i}
            ref={(el) => (trailRef.current[i] = el)}
            className="cursor-trail"
            style={{
              width:   `${size}px`,
              height:  `${size}px`,
              opacity: scale * 0.6,
              marginLeft: `-${size / 2}px`,
              marginTop:  `-${size / 2}px`,
            }}
          />
        );
      })}
    </>
  );
}

export default Cursor;
