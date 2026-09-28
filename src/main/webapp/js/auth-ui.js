const authShellPages = document.querySelector('body:not(.dashboard-app):not(.auth-page)');

const authStyle = document.createElement('style');
authStyle.textContent = `.app-user-identity{display:flex;align-items:center;gap:9px;color:#697181;font-size:13px;font-weight:600}.app-user-avatar{display:grid;place-items:center;width:32px;height:32px;border-radius:50%;background:#e8eaff;color:#4350cf;font-weight:700}`;
document.head.append(authStyle);

function authEscape(value) { return String(value ?? '').replace(/[&<>'"]/g, (character) => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[character])); }

async function loadAuthShell() {
    if (!authShellPages) return;
    document.body.classList.add('has-app-sidebar');
    const response = await fetch('currentUser');
    if (!response.ok) return;
    const user = await response.json();
    const existingTopbar = document.querySelector('.topbar');
    if (existingTopbar) existingTopbar.classList.add('auth-topbar');
    const drawer = document.createElement('aside');
    drawer.className = 'app-sidebar';
    drawer.innerHTML = `<button class="app-sidebar-toggle" type="button" aria-label="Collapse sidebar">×</button><a class="app-sidebar-brand" href="index.html"><span class="brand-mark">E</span><span>EMS</span></a><nav class="app-sidebar-nav"><a href="index.html">Dashboard</a><a href="employees.html">Employees</a><a href="projects.html">Projects</a><a href="assign-project.html">Assignments</a></nav><div class="app-sidebar-divider"></div><span class="app-sidebar-label">Workspace</span><nav class="app-sidebar-nav"><a href="assign-project.html">Reports</a><a href="index.html">Settings</a></nav><button class="app-sidebar-logout" id="app-logout">Log out</button>`;
    const overlay = document.createElement('div');
    overlay.className = 'app-sidebar-overlay';
    const toggle = () => { drawer.classList.toggle('open'); overlay.classList.toggle('open'); };
    const collapse = () => { drawer.classList.toggle('collapsed'); document.body.classList.toggle('sidebar-collapsed'); };
    drawer.querySelector('.app-sidebar-toggle').addEventListener('click', collapse);
    const identity = document.createElement('div');
    identity.className = 'app-user-identity';
    identity.innerHTML = `<span class="app-user-avatar">${authEscape(user.username).charAt(0).toUpperCase()}</span><span>${authEscape(user.username)}</span>`;
    if (existingTopbar) existingTopbar.append(identity);
    overlay.addEventListener('click', toggle);
    drawer.querySelectorAll('a').forEach((link) => link.addEventListener('click', () => { drawer.classList.remove('open'); overlay.classList.remove('open'); }));
    drawer.querySelector('#app-logout').addEventListener('click', async () => { await fetch('logout', {method:'POST'}); window.location.href = 'login.html'; });
}

loadAuthShell().catch(() => {});
