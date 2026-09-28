const projectPage = document.body.dataset.projectPage;
const projectFeedback = document.querySelector('#project-feedback');

async function projectRequest(url, options) {
    const response = await fetch(url, options);
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || 'Request failed.');
    return data;
}

function showProjectFeedback(message, error = false) {
    if (!projectFeedback) return;
    projectFeedback.textContent = message;
    projectFeedback.hidden = false;
    projectFeedback.classList.toggle('error', error);
}

function projectEscape(value) {
    return String(value ?? '').replace(/[&<>'"]/g, (character) => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[character]));
}

function projectStatus(status) {
    return `<span class="status-badge status-${status.toLowerCase().replace(/\s+/g, '-')}">${projectEscape(status)}</span>`;
}

async function loadProjects(url = 'listProjects', status = '') {
    const rows = document.querySelector('#project-rows');
    if (!rows) return;
    rows.innerHTML = '<tr><td colspan="7" class="empty">Loading projects...</td></tr>';
    try {
        const data = await projectRequest(url);
        const projects = status ? data.projects.filter((project) => project.status === status) : data.projects;
        rows.innerHTML = projects.length ? projects.map((project) => `<tr><td>${project.id}</td><td class="employee-name">${projectEscape(project.name)}</td><td>${projectEscape(project.description || '—')}</td><td>${projectEscape(project.startDate || '—')}</td><td>${projectEscape(project.endDate || '—')}</td><td>${projectStatus(project.status)}</td><td><div class="actions"><a class="action" href="edit-project.html?id=${project.id}">Edit</a><a class="action" href="assign-project.html?projectId=${project.id}">View employees</a><button class="action delete" data-id="${project.id}">Delete</button></div></td></tr>`).join('') : '<tr><td colspan="7" class="empty">No projects found.</td></tr>';
        rows.querySelectorAll('.delete').forEach((button) => button.addEventListener('click', () => deleteProject(button.dataset.id)));
    } catch (error) {
        rows.innerHTML = '<tr><td colspan="7" class="empty">Unable to load projects.</td></tr>';
        showProjectFeedback(error.message, true);
    }
}

async function deleteProject(id) {
    if (!window.confirm('Are you sure you want to delete this project?')) return;
    try {
        const data = await projectRequest(`deleteProject?id=${encodeURIComponent(id)}`, {method:'POST'});
        showProjectFeedback(data.message);
        await loadProjects();
    } catch (error) { showProjectFeedback(error.message, true); }
}

function validateProjectForm(form) {
    let valid = true;
    form.querySelectorAll('.field-error').forEach((item) => item.textContent = '');
    const values = Object.fromEntries(new FormData(form).entries());
    const error = (name, text) => { form.querySelector(`[name="${name}"]`).nextElementSibling.textContent = text; valid = false; };
    if (!values.name.trim()) error('name', 'Project name is required.');
    else if (values.name.trim().length > 100) error('name', 'Use 100 characters or fewer.');
    if (!values.status) error('status', 'Select a status.');
    if (values.startDate && values.endDate && values.endDate < values.startDate) error('endDate', 'End date cannot be before start date.');
    return valid;
}

async function loadProject(form, id) {
    const project = await projectRequest(`editProject?id=${encodeURIComponent(id)}`);
    Object.entries(project).forEach(([key, value]) => { const field = form.elements[key]; if (field) field.value = value ?? ''; });
    const employeeSelect = form.elements.employeeIds;
    project.employeeIds.forEach((employeeId) => {
        const option = Array.from(employeeSelect.options).find((item) => item.value === String(employeeId));
        if (option) option.selected = true;
    });
}

async function loadProjectEmployees(form) {
    const employeeSelect = form.elements.employeeIds;
    const data = await projectRequest('listEmployees');
    data.employees.forEach((employee) => employeeSelect.add(new Option(`${employee.name} (${employee.email})`, employee.id)));
}

if (projectPage === 'list') {
    const search = document.querySelector('#project-search');
    const filter = document.querySelector('#project-status-filter');
    let timer;
    loadProjects();
    search.addEventListener('input', (event) => { clearTimeout(timer); timer = setTimeout(() => loadProjects(event.target.value.trim() ? `searchProject?keyword=${encodeURIComponent(event.target.value)}` : 'listProjects', filter.value), 250); });
    filter.addEventListener('change', () => {
        const keyword = search.value.trim();
        const url = keyword ? `searchProject?keyword=${encodeURIComponent(keyword)}` : 'listProjects';
        loadProjects(url, filter.value);
    });
}

if (projectPage === 'form') {
    const form = document.querySelector('#project-form');
    const id = new URLSearchParams(window.location.search).get('id');
    loadProjectEmployees(form).then(() => id ? loadProject(form, id) : null).catch((error) => showProjectFeedback(error.message, true));
    form.addEventListener('submit', async (event) => {
        event.preventDefault();
        if (!validateProjectForm(form)) return;
        const payload = new URLSearchParams(new FormData(form));
        payload.delete('employeeIds');
        payload.set('employeeIds', Array.from(form.elements.employeeIds.selectedOptions).map((option) => option.value).join(','));
        const endpoint = id ? 'updateProject' : 'addProject';
        try {
            const data = await projectRequest(endpoint, {method:'POST', headers:{'Content-Type':'application/x-www-form-urlencoded'}, body:payload});
            showProjectFeedback(data.message);
            setTimeout(() => window.location.href = 'projects.html', 500);
        } catch (error) { showProjectFeedback(error.message, true); }
    });
}

const authUiScript = document.createElement('script');
authUiScript.src = 'js/auth-ui.js';
document.body.append(authUiScript);
