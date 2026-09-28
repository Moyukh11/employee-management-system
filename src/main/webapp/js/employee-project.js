const assignmentFeedback = document.querySelector('#assignment-feedback');
const assignmentForm = document.querySelector('#assignment-form');

async function assignmentRequest(url, options) {
    const response = await fetch(url, options);
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || 'Request failed.');
    return data;
}

function showAssignmentFeedback(message, error = false) {
    assignmentFeedback.textContent = message;
    assignmentFeedback.hidden = false;
    assignmentFeedback.classList.toggle('error', error);
}

function escapeAssignment(value) {
    return String(value ?? '').replace(/[&<>'"]/g, (character) => ({'&':'&amp;','<':'&lt;','>':'&gt;',"'":'&#39;','"':'&quot;'}[character]));
}

async function loadAssignmentOptions() {
    const employees = await assignmentRequest('listEmployees');
    const projects = await assignmentRequest('listProjects');
    const employeeSelects = [document.querySelector('#assignment-employee'), document.querySelector('#assignment-employee-filter')];
    const projectSelects = [document.querySelector('#assignment-project'), document.querySelector('#assignment-project-filter')];
    employeeSelects.forEach((select) => employees.employees.forEach((employee) => select.add(new Option(employee.name, employee.id))));
    projectSelects.forEach((select) => projects.projects.forEach((project) => select.add(new Option(project.name, project.id))));
    const params = new URLSearchParams(window.location.search);
    if (params.get('employeeId')) document.querySelector('#assignment-employee-filter').value = params.get('employeeId');
    if (params.get('projectId')) document.querySelector('#assignment-project-filter').value = params.get('projectId');
}

async function loadAssignments() {
    const rows = document.querySelector('#assignment-rows');
    if (!rows) return;
    rows.innerHTML = '<tr><td colspan="6" class="empty">Loading assignments...</td></tr>';
    const search = document.querySelector('#assignment-search').value.trim();
    const employeeId = document.querySelector('#assignment-employee-filter').value;
    const projectId = document.querySelector('#assignment-project-filter').value;
    const params = new URLSearchParams();
    if (search) params.set('keyword', search);
    else if (employeeId) params.set('employeeId', employeeId);
    else if (projectId) params.set('projectId', projectId);
    try {
        const data = await assignmentRequest(`employeeProjects${params.toString() ? `?${params}` : ''}`);
        rows.innerHTML = data.assignments.length ? data.assignments.map((assignment) => `<tr><td class="employee-name">${escapeAssignment(assignment.employeeName)}</td><td>${escapeAssignment(assignment.employeeDepartment || '—')}</td><td>${escapeAssignment(assignment.projectName)}</td><td>${escapeAssignment(assignment.role)}</td><td>${escapeAssignment(assignment.assignedDate || '—')}</td><td><button class="action delete remove-assignment" data-id="${assignment.id}" data-employee="${escapeAssignment(assignment.employeeName)}" data-project="${escapeAssignment(assignment.projectName)}">Remove</button></td></tr>`).join('') : '<tr><td colspan="6" class="empty">No assignments found.</td></tr>';
        rows.querySelectorAll('.remove-assignment').forEach((button) => button.addEventListener('click', () => removeAssignment(button)));
    } catch (error) {
        rows.innerHTML = '<tr><td colspan="6" class="empty">Unable to load assignments.</td></tr>';
        showAssignmentFeedback(error.message, true);
    }
}

async function removeAssignment(button) {
    if (!window.confirm(`Are you sure you want to remove ${button.dataset.employee} from ${button.dataset.project}?`)) return;
    try {
        const data = await assignmentRequest(`removeEmployeeProject?id=${encodeURIComponent(button.dataset.id)}`, {method:'POST'});
        showAssignmentFeedback(data.message);
        await loadAssignments();
    } catch (error) { showAssignmentFeedback(error.message, true); }
}

function validateAssignment() {
    let valid = true;
    assignmentForm.querySelectorAll('.field-error').forEach((item) => item.textContent = '');
    const values = Object.fromEntries(new FormData(assignmentForm).entries());
    const error = (name, message) => { assignmentForm.elements[name].nextElementSibling.textContent = message; valid = false; };
    if (!values.employeeId) error('employeeId', 'Select an employee.');
    if (!values.projectId) error('projectId', 'Select a project.');
    if (!values.role.trim()) error('role', 'Role is required.');
    if (!values.assignedDate) error('assignedDate', 'Assigned date is required.');
    return valid;
}

assignmentForm.elements.assignedDate.value = new Date().toISOString().slice(0, 10);
assignmentForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    if (!validateAssignment()) return;
    try {
        const data = await assignmentRequest('assignEmployeeProject', {method:'POST', headers:{'Content-Type':'application/x-www-form-urlencoded'}, body:new URLSearchParams(new FormData(assignmentForm))});
        showAssignmentFeedback(data.message);
        assignmentForm.elements.role.value = '';
        await loadAssignments();
    } catch (error) { showAssignmentFeedback(error.message, true); }
});

document.querySelector('#assignment-search').addEventListener('input', (() => { let timer; return () => { clearTimeout(timer); timer = setTimeout(loadAssignments, 250); }; })());
document.querySelector('#assignment-project-filter').addEventListener('change', loadAssignments);
document.querySelector('#assignment-employee-filter').addEventListener('change', loadAssignments);
loadAssignmentOptions().then(loadAssignments).catch((error) => showAssignmentFeedback(error.message, true));

const authUiScript = document.createElement('script');
authUiScript.src = 'js/auth-ui.js';
document.body.append(authUiScript);
