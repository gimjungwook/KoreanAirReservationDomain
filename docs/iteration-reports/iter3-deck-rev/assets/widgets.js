/* ============================================================
   OODP Iter3 Revision Deck — interactive widgets
   전역 THREE(r128 classic build) 사용 → file:// 더블클릭 호환.
   namespace: window.OODP
   ============================================================ */
(function () {
  'use strict';
  const OODP = (window.OODP = window.OODP || {});

  const css = getComputedStyle(document.documentElement);
  const C = {
    subject:  css.getPropertyValue('--subject').trim()  || '#1E6FB8',
    listener: css.getPropertyValue('--listener').trim() || '#C0492E',
    event:    css.getPropertyValue('--event').trim()    || '#7928CA',
    nw:       css.getPropertyValue('--new').trim()       || '#FF0080',
  };
  OODP.colors = C;

  const clamp = (x, a, b) => Math.min(b, Math.max(a, x));
  const lerp = (a, b, t) => a + (b - a) * t;
  const easeInOut = (t) => (t < .5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2);
  OODP.clamp = clamp;

  OODP.onVisible = function (el, fn) {
    if (!('IntersectionObserver' in window)) { fn(); return; }
    const io = new IntersectionObserver((ents) => {
      ents.forEach(e => { if (e.isIntersecting) { io.disconnect(); fn(); } });
    }, { rootMargin: '120px' });
    io.observe(el);
  };

  /* ============================================================
     OBSERVER 3D — broadcast + instanceof 선택 처리 시각화
     하나의 Subject가 publish → 모든 Listener에 broadcast.
     매칭 Listener만 onEvent에서 실제 처리(녹색 점화), 나머지는 instanceof false로 무시.
     ============================================================ */
  // 4 subjects (각 이벤트의 발행자) — 실제 코드와 1:1
  const SUBJECTS = [
    { id: 'hold',   subj: 'SeatHoldMonitor',  ev: 'SeatHoldExpiredEvent',     match: 'ReservationHoldListener',       act: 'seat.release() + Reservation.cancel()' },
    { id: 'pay',    subj: 'PaymentProcessor', ev: 'PaymentFailedEvent',       match: 'ReservationAutoCancelListener', act: 'Reservation.handlePaymentFailure()' },
    { id: 'flight', subj: 'FlightSchedule',   ev: 'FlightStatusChangedEvent', match: 'AffectedReservationListener',   act: 'registry.all() → notify N건' },
    { id: 'ticket', subj: 'TicketPublisher',  ev: 'TicketIssuedEvent',        match: 'BusTicketPurchaseListener',     act: 'busTicketingService.issuePremiumTicket()' },
  ];
  // 4 listeners (항상 전원 구독 중 — broadcast 대상)
  const LISTENERS = [
    { id: 'L_hold',   name: 'ReservationHoldListener',       handles: 'SeatHoldExpiredEvent' },
    { id: 'L_pay',    name: 'ReservationAutoCancelListener', handles: 'PaymentFailedEvent' },
    { id: 'L_flight', name: 'AffectedReservationListener',   handles: 'FlightStatusChangedEvent' },
    { id: 'L_ticket', name: 'BusTicketPurchaseListener',     handles: 'TicketIssuedEvent' },
  ];

  OODP.observer3D = function (root) {
    if (!window.THREE) { root.innerHTML = '<p style="color:#fff;padding:40px">THREE 로드 실패</p>'; return; }
    const THREE = window.THREE;

    const wrap = document.createElement('div'); wrap.className = 'obs3d-wrap';
    const overlay = document.createElement('div'); overlay.className = 'obs3d-overlay';
    const readout = document.createElement('div'); readout.className = 'obs3d-readout';
    readout.innerHTML = '<b>Observer broadcast</b><br>publish 버튼을 눌러 이벤트 발행';
    const controls = document.createElement('div'); controls.className = 'obs3d-controls';
    wrap.appendChild(overlay); wrap.appendChild(readout); wrap.appendChild(controls);
    root.appendChild(wrap);

    const W = wrap.clientWidth || 1500, H = wrap.clientHeight || 760;
    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(42, W / H, 0.1, 100);
    camera.position.set(0, 0.5, 15.5);
    const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    renderer.setSize(W, H);
    wrap.appendChild(renderer.domElement);

    scene.add(new THREE.AmbientLight(0xffffff, 0.7));
    const key = new THREE.PointLight(0xffffff, 0.8); key.position.set(6, 8, 12); scene.add(key);

    const toCol = (h) => new THREE.Color(h);
    // Subject node (left)
    const subjPos = new THREE.Vector3(-6.2, 0, 0);
    const subjMat = new THREE.MeshStandardMaterial({ color: toCol(C.subject), emissive: toCol(C.subject), emissiveIntensity: 0.35, metalness: 0.3, roughness: 0.4 });
    const subj = new THREE.Mesh(new THREE.IcosahedronGeometry(1.15, 1), subjMat);
    subj.position.copy(subjPos); scene.add(subj);
    const subjRing = new THREE.Mesh(new THREE.TorusGeometry(1.7, 0.03, 8, 64), new THREE.MeshBasicMaterial({ color: toCol(C.subject), transparent: true, opacity: 0.5 }));
    subjRing.position.copy(subjPos); scene.add(subjRing);

    // hub (broadcast split point)
    const hubPos = new THREE.Vector3(-1.6, 0, 0);

    // Listener nodes (right, vertical arc)
    const listenerMeshes = LISTENERS.map((L, i) => {
      const y = 3.6 - i * 2.4;
      const pos = new THREE.Vector3(6.0, y, 0);
      const mat = new THREE.MeshStandardMaterial({ color: toCol('#5b5560'), emissive: toCol('#000'), emissiveIntensity: 0, metalness: 0.2, roughness: 0.6 });
      const mesh = new THREE.Mesh(new THREE.BoxGeometry(1.5, 1.5, 1.5), mat);
      mesh.position.copy(pos); scene.add(mesh);
      return { L, mesh, pos, baseScale: 1, glow: 0 };
    });

    // static connecting lines subject→hub→listeners
    function lineBetween(a, b, color, op) {
      const g = new THREE.BufferGeometry().setFromPoints([a, b]);
      const m = new THREE.LineBasicMaterial({ color: toCol(color), transparent: true, opacity: op });
      const l = new THREE.Line(g, m); scene.add(l); return l;
    }
    lineBetween(subjPos, hubPos, C.subject, 0.5);
    listenerMeshes.forEach(lm => lineBetween(hubPos, lm.pos, '#3a4055', 0.35));

    // event particle factory
    function spawnEvent() {
      const geo = new THREE.SphereGeometry(0.22, 12, 12);
      const mat = new THREE.MeshBasicMaterial({ color: toCol(C.event) });
      const p = new THREE.Mesh(geo, mat); p.position.copy(subjPos); scene.add(p);
      return p;
    }

    // HTML overlay labels
    function mkTag(cls, text) { const d = document.createElement('div'); d.className = 'obs3d-tag ' + cls; d.textContent = text; overlay.appendChild(d); return d; }
    const subjTag = mkTag('subject', 'Subject');
    const listenerTags = listenerMeshes.map(lm => mkTag('listener', lm.L.name));
    function projTag(tag, pos, dy) {
      const v = pos.clone().project(camera);
      const x = (v.x * 0.5 + 0.5) * W, y = (-v.y * 0.5 + 0.5) * H + (dy || 0);
      tag.style.left = x + 'px'; tag.style.top = y + 'px';
      tag.style.display = (v.z < 1) ? 'block' : 'none';
    }

    // animation state
    let current = SUBJECTS[1]; // default PaymentFailedEvent
    let anim = null; // {phase, t, parts:[], targets:[]}

    function setSubject(s) {
      current = s;
      subjTag.textContent = s.subj;
      readout.innerHTML = `<b>${s.subj}</b><br>발행 예정: <span style="color:#c79bf0">${s.ev}</span>`;
      // reset listener glow
      listenerMeshes.forEach(lm => { lm.glow = 0; lm.mesh.material.emissiveIntensity = 0; lm.mesh.material.color.set('#5b5560'); });
      buttons.forEach(b => b.classList.toggle('on', b.dataset.id === s.id));
    }

    function publish() {
      if (anim) return;
      const parts = listenerMeshes.map(() => spawnEvent());
      anim = { t: 0, parts, fired: false };
      readout.innerHTML = `<b>publish(${current.ev})</b><br>→ broadcast to ${LISTENERS.length} listeners`;
      // reset glows
      listenerMeshes.forEach(lm => { lm.glow = 0; lm.mesh.material.emissiveIntensity = 0; lm.mesh.material.color.set('#5b5560'); });
    }

    function tickAnim(dt) {
      if (!anim) return;
      anim.t += dt * 0.9;
      const t = clamp(anim.t, 0, 1);
      anim.parts.forEach((p, i) => {
        const lm = listenerMeshes[i];
        // path: subj → hub (first 35%) → listener
        if (t < 0.35) {
          const u = easeInOut(t / 0.35);
          p.position.lerpVectors(subjPos, hubPos, u);
        } else {
          const u = easeInOut((t - 0.35) / 0.65);
          p.position.lerpVectors(hubPos, lm.pos, u);
        }
      });
      if (t >= 1 && !anim.fired) {
        anim.fired = true;
        // each listener receives; only matching one acts
        let actedName = '', actedAct = '';
        listenerMeshes.forEach((lm) => {
          const matches = lm.L.handles === current.ev;
          if (matches) {
            lm.mesh.material.color.set('#28c840');
            lm.mesh.material.emissive.set('#28c840');
            lm.mesh.material.emissiveIntensity = 0.7;
            lm.glow = 1;
            actedName = lm.L.name; actedAct = current.act;
          } else {
            lm.mesh.material.color.set('#3a3a42');
            lm.mesh.material.emissiveIntensity = 0;
          }
        });
        readout.innerHTML = `<b>onEvent() — instanceof 분기</b><br>`
          + `<span style="color:#28c840">✓ ${actedName}</span> 처리<br>`
          + `<span style="color:#b9e08a">${actedAct}</span><br>`
          + `<span style="color:#8b8b94">나머지 3개: instanceof false → 무시</span>`;
        // remove particles shortly
        setTimeout(() => { anim && anim.parts.forEach(p => scene.remove(p)); anim = null; }, 350);
      }
    }

    // controls
    const buttons = SUBJECTS.map(s => {
      const b = document.createElement('button');
      b.className = 'obs3d-btn ghost'; b.dataset.id = s.id; b.textContent = s.ev.replace('Event', '');
      b.onclick = () => setSubject(s);
      controls.appendChild(b); return b;
    });
    const pub = document.createElement('button');
    pub.className = 'obs3d-btn'; pub.textContent = '▶ publish';
    pub.onclick = publish; controls.appendChild(pub);

    setSubject(current);

    let last = performance.now();
    function loop(now) {
      const dt = Math.min((now - last) / 1000, 0.05); last = now;
      subj.rotation.y += dt * 0.5; subj.rotation.x += dt * 0.18;
      subjRing.rotation.z += dt * 0.6;
      const pulse = 1 + Math.sin(now * 0.004) * 0.04;
      subj.scale.setScalar(pulse);
      listenerMeshes.forEach(lm => {
        lm.mesh.rotation.y += dt * 0.3;
        const s = 1 + lm.glow * (1 + Math.sin(now * 0.012)) * 0.12;
        lm.mesh.scale.setScalar(s);
      });
      tickAnim(dt);
      // overlay tags
      projTag(subjTag, subjPos, -70);
      listenerMeshes.forEach((lm, i) => projTag(listenerTags[i], lm.pos, -58));
      renderer.render(scene, camera);
      requestAnimationFrame(loop);
    }
    requestAnimationFrame(loop);

    window.addEventListener('resize', () => {
      const w = wrap.clientWidth, h = wrap.clientHeight;
      camera.aspect = w / h; camera.updateProjectionMatrix(); renderer.setSize(w, h);
    });
  };

  /* ============================================================
     COVER HERO — subject 중심에서 listener로 퍼지는 입자 (장식)
     ============================================================ */
  OODP.cover = function (canvas) {
    if (!window.THREE) return;
    const THREE = window.THREE;
    const W = canvas.clientWidth || 1920, H = canvas.clientHeight || 1080;
    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(50, W / H, 0.1, 100); camera.position.z = 14;
    const renderer = new THREE.WebGLRenderer({ canvas, antialias: true, alpha: true });
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2)); renderer.setSize(W, H);

    const N = 1400;
    const pos = new Float32Array(N * 3), col = new Float32Array(N * 3);
    const home = [], targetIdx = [];
    const subjC = new THREE.Color(C.subject), evC = new THREE.Color(C.event);
    const hubs = [new THREE.Vector3(7, 4, 0), new THREE.Vector3(8, 0, 0), new THREE.Vector3(7, -4, 0)];
    for (let i = 0; i < N; i++) {
      const a = Math.random() * Math.PI * 2, r = Math.random() * 1.3;
      const sx = -7 + Math.cos(a) * r, sy = Math.sin(a) * r, sz = (Math.random() - .5) * 1.3;
      home.push(new THREE.Vector3(sx, sy, sz));
      pos[i*3]=sx; pos[i*3+1]=sy; pos[i*3+2]=sz;
      const c = subjC.clone().lerp(evC, Math.random()*0.6);
      col[i*3]=c.r; col[i*3+1]=c.g; col[i*3+2]=c.b;
      targetIdx.push(i % hubs.length);
    }
    const geo = new THREE.BufferGeometry();
    geo.setAttribute('position', new THREE.BufferAttribute(pos, 3));
    geo.setAttribute('color', new THREE.BufferAttribute(col, 3));
    const mat = new THREE.PointsMaterial({ size: 0.085, vertexColors: true, transparent: true, opacity: 0.92, depthWrite: false, blending: THREE.AdditiveBlending });
    const pts = new THREE.Points(geo, mat); scene.add(pts);

    let t = 0;
    function loop() {
      t += 0.006;
      const flow = (Math.sin(t) * 0.5 + 0.5); // 0..1 broadcast pulse
      const p = geo.attributes.position.array;
      for (let i = 0; i < N; i++) {
        const h = home[i], tgt = hubs[targetIdx[i]];
        const ph = (flow + (i % 7) / 7) % 1;
        const x = lerp(h.x, tgt.x, ph), y = lerp(h.y, tgt.y, ph), z = lerp(h.z, tgt.z, ph);
        p[i*3]=x; p[i*3+1]=y + Math.sin(t*2 + i)*0.02; p[i*3+2]=z;
      }
      geo.attributes.position.needsUpdate = true;
      pts.rotation.y = Math.sin(t * 0.3) * 0.12;
      renderer.render(scene, camera);
      requestAnimationFrame(loop);
    }
    loop();
  };

  /* ============================================================
     TITLE SCENE — Subject 코어 + 궤도 listener + publish 펄스 링
     (cover의 particle morph와 다른 비주얼)
     ============================================================ */
  OODP.titleScene = function (canvas) {
    if (!window.THREE) return;
    const THREE = window.THREE;
    const W = canvas.clientWidth || 1200, H = canvas.clientHeight || 1000;
    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(46, W / H, 0.1, 100);
    camera.position.set(0, 1.6, 13);
    const renderer = new THREE.WebGLRenderer({ canvas, antialias: true, alpha: true });
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2)); renderer.setSize(W, H);
    scene.add(new THREE.AmbientLight(0xffffff, 0.85));
    const key = new THREE.PointLight(0xffffff, 0.7); key.position.set(5, 6, 10); scene.add(key);

    const col = (h) => new THREE.Color(h);
    // subject core
    const core = new THREE.Mesh(
      new THREE.IcosahedronGeometry(1.5, 1),
      new THREE.MeshStandardMaterial({ color: col(C.subject), emissive: col(C.subject), emissiveIntensity: 0.4, metalness: 0.35, roughness: 0.35, flatShading: true })
    );
    scene.add(core);
    const halo = new THREE.Mesh(new THREE.IcosahedronGeometry(1.62, 1), new THREE.MeshBasicMaterial({ color: col(C.subject), wireframe: true, transparent: true, opacity: 0.25 }));
    scene.add(halo);

    // orbiting listeners
    const ORB = 6, listeners = [];
    const listColors = [C.listener, C.event, '#A47B2E', C.listener, C.event, '#A47B2E'];
    for (let i = 0; i < ORB; i++) {
      const m = new THREE.Mesh(
        new THREE.BoxGeometry(0.72, 0.72, 0.72),
        new THREE.MeshStandardMaterial({ color: col(listColors[i]), emissive: col(listColors[i]), emissiveIntensity: 0.18, metalness: 0.2, roughness: 0.5 })
      );
      scene.add(m);
      const line = new THREE.Line(new THREE.BufferGeometry().setFromPoints([new THREE.Vector3(), new THREE.Vector3()]),
        new THREE.LineBasicMaterial({ color: col(listColors[i]), transparent: true, opacity: 0.28 }));
      scene.add(line);
      listeners.push({ m, line, ang: (i / ORB) * Math.PI * 2, rad: 5.4, tilt: (i % 2 ? 0.5 : -0.4), spd: 0.18 + i * 0.012 });
    }

    // publish pulse rings (expanding torus)
    const rings = [];
    function spawnRing() {
      const r = new THREE.Mesh(new THREE.TorusGeometry(1.6, 0.05, 10, 80),
        new THREE.MeshBasicMaterial({ color: col(C.event), transparent: true, opacity: 0.6 }));
      r.rotation.x = Math.PI / 2.2;
      scene.add(r); rings.push({ mesh: r, t: 0 });
    }
    let ringTimer = 0;

    let t = 0, last = performance.now();
    function loop(now) {
      const dt = Math.min((now - last) / 1000, 0.05); last = now; t += dt;
      core.rotation.y += dt * 0.5; core.rotation.x += dt * 0.16;
      halo.rotation.y -= dt * 0.32; halo.rotation.z += dt * 0.1;
      const cp = 1 + Math.sin(t * 2) * 0.04; core.scale.setScalar(cp);
      listeners.forEach(L => {
        L.ang += dt * L.spd;
        const x = Math.cos(L.ang) * L.rad;
        const z = Math.sin(L.ang) * L.rad;
        const y = Math.sin(L.ang * 1.3) * L.rad * L.tilt * 0.4 + Math.sin(t + L.ang) * 0.2;
        L.m.position.set(x, y, z);
        L.m.rotation.x += dt * 0.6; L.m.rotation.y += dt * 0.4;
        const p = L.line.geometry.attributes.position;
        p.setXYZ(0, 0, 0, 0); p.setXYZ(1, x, y, z); p.needsUpdate = true;
      });
      ringTimer += dt;
      if (ringTimer > 1.7) { ringTimer = 0; spawnRing(); }
      for (let i = rings.length - 1; i >= 0; i--) {
        const R = rings[i]; R.t += dt;
        const s = 1 + R.t * 3.2; R.mesh.scale.set(s, s, s);
        R.mesh.material.opacity = Math.max(0, 0.6 - R.t * 0.32);
        if (R.t > 2) { scene.remove(R.mesh); rings.splice(i, 1); }
      }
      camera.position.x = Math.sin(t * 0.25) * 1.2;
      camera.position.y = 1.6 + Math.sin(t * 0.4) * 0.4;
      camera.lookAt(0, 0, 0);
      renderer.render(scene, camera);
      requestAnimationFrame(loop);
    }
    requestAnimationFrame(loop);
    window.addEventListener('resize', () => {
      const w = canvas.clientWidth, h = canvas.clientHeight;
      camera.aspect = w / h; camera.updateProjectionMatrix(); renderer.setSize(w, h);
    });
  };

  /* ============================================================
     CODE TOGGLE — 다이어그램 ↔ 실제 코드 세그먼트 스위치
     <div data-toggle> 안에 [data-panel="diagram"], [data-panel="code"]
     ============================================================ */
  OODP.codeToggle = function (root) {
    const seg = root.querySelector('.seg');
    const panels = root.querySelectorAll('[data-panel]');
    if (!seg) return;
    seg.querySelectorAll('button').forEach(btn => {
      btn.onclick = () => {
        seg.querySelectorAll('button').forEach(b => b.classList.toggle('on', b === btn));
        const want = btn.dataset.show;
        panels.forEach(p => { p.style.display = (p.dataset.panel === want) ? '' : 'none'; });
      };
    });
  };

  /* ============================================================
     ECB FILTER — 클래스 다이어그램 위 ECB 계층 강조 토글
     root 안의 .ecb-toggle button[data-layer] + [data-ecb] 칩
     ============================================================ */
  OODP.ecbFilter = function (root) {
    const btns = root.querySelectorAll('.ecb-toggle button');
    const items = root.querySelectorAll('[data-ecb]');
    let active = 'all';
    function apply() {
      items.forEach(it => {
        const on = active === 'all' || it.dataset.ecb === active;
        it.style.opacity = on ? '1' : '0.18';
        it.style.filter = on ? 'none' : 'grayscale(1)';
      });
    }
    btns.forEach(b => b.onclick = () => {
      active = b.dataset.layer;
      btns.forEach(x => x.classList.toggle('on', x === b));
      apply();
    });
    apply();
  };

  /* ============================================================
     DEMO CONSOLE — 실제 데모 출력 타이프라이터 재생
     scenarios = [{id,label,lines:[{t,cls}]}]
     ============================================================ */
  OODP.demoConsole = function (root, scenarios) {
    const log = root.querySelector('.clog');
    const tabs = root.querySelector('.demo-tabs');
    let timer = null;
    function clsOf(line) {
      if (/^\[STATE\]/.test(line)) return 'l-state';
      if (/^\[(HOLD-EXPIRY|AUTO-CANCEL|FLIGHT-CANCEL|EVENT|SWEEP|BUS|MILEAGE|GATEWAY)\]/.test(line)) return 'l-event';
      if (/^\[RESULT\]/.test(line)) return 'l-result';
      if (/^---|^====|^\[BOOT\]/.test(line)) return 'l-sect';
      if (/^\s*(→|\.|SeatHold|Payment|Ticket)/.test(line)) return 'l-dim';
      return '';
    }
    function play(sc) {
      if (timer) { clearInterval(timer); timer = null; }
      log.innerHTML = '';
      const lines = sc.lines;
      let i = 0;
      const caret = document.createElement('span'); caret.className = 'caret'; caret.textContent = '▌';
      timer = setInterval(() => {
        if (i >= lines.length) { clearInterval(timer); timer = null; return; }
        const line = lines[i++];
        const span = document.createElement('div');
        span.className = clsOf(line);
        span.textContent = line;
        log.insertBefore(span, caret.parentNode === log ? caret : null);
        log.appendChild(caret);
        log.scrollTop = log.scrollHeight;
      }, 90);
    }
    if (tabs) {
      scenarios.forEach((sc) => {
        const b = document.createElement('button');
        b.className = 'obs3d-btn ghost'; b.style.fontSize = '17px'; b.style.padding = '10px 18px';
        b.textContent = sc.label;
        b.onclick = () => { tabs.querySelectorAll('button').forEach(x => x.classList.toggle('on', x === b)); play(sc); };
        tabs.appendChild(b);
      });
    }
    // autoplay first when visible
    OODP.onVisible(root, () => { const first = tabs && tabs.querySelector('button'); if (first) first.click(); });
  };

})();
