const page = document.body.dataset.page;
const form = document.querySelector("#employee-form");
const feedback = document.querySelector("#feedback");

/* =========================
   API REQUEST
========================= */

async function request(url, options = {}) {
    const response = await fetch(url, options);

    const contentType = response.headers.get("content-type") || "";

    let data;

    if (contentType.includes("application/json")) {
        data = await response.json();
    } else {
        const text = await response.text();
        data = {
            message: text || "Request failed."
        };
    }

    if (!response.ok) {
        throw new Error(data.message || "Request failed.");
    }

    return data;
}

/* =========================
   FEEDBACK
========================= */

function showFeedback(message, error = false) {
    if (!feedback) {
        return;
    }

    feedback.hidden = false;
    feedback.textContent = message;
    feedback.classList.toggle("error", error);
}

/* =========================
   FORM VALIDATION
========================= */

function validateForm() {
    if (!form) {
        return true;
    }

    let valid = true;

    form.querySelectorAll("[required]").forEach((input) => {
        const errorElement = input.parentElement.querySelector(".field-error");

        if (!input.value.trim()) {
            valid = false;

            if (errorElement) {
                errorElement.textContent = "This field is required.";
            }
        } else if (errorElement) {
            errorElement.textContent = "";
        }
    });

    return valid;
}

/* =========================
   LOAD DEPARTMENTS
========================= */

async function loadDepartments(selects) {
    const data = await request("listEmployees");

    selects.forEach((select) => {
        if (!select) {
            return;
        }

        select.innerHTML = '<option value="">Select Department</option>';

        (data.departments || []).forEach((department) => {
            select.add(
                new Option(
                    department.name,
                    department.id
                )
            );
        });
    });

    return data;
}

/* =========================
   LOAD EMPLOYEES
========================= */

async function loadEmployees(url = "listEmployees") {
    const data = await request(url);

    const rows = document.querySelector("#employee-rows");

    if (!rows) {
        return data;
    }

    if (!data.employees || data.employees.length === 0) {
        rows.innerHTML = `
            <tr>
                <td colspan="6" class="empty">
                    No employees found.
                </td>
            </tr>
        `;

        return data;
    }

    rows.innerHTML = data.employees
        .map(
            (employee) => `
            <tr>
                <td>${employee.id}</td>

                <td>
                    <div class="employee-name">
                        ${employee.name || ""}
                    </div>
                </td>

                <td>
                    ${employee.email || "-"}
                </td>

                <td>
                    ${employee.department || "-"}
                </td>

                <td class="salary">
                    ₹${Number(employee.salary || 0).toLocaleString("en-IN")}
                </td>

                <td>
                    <div class="actions">
                        <a
                            class="action"
                            href="edit-employee.html?id=${employee.id}"
                        >
                            Edit
                        </a>

                        <button
                            class="action danger"
                            type="button"
                            onclick="deleteEmployee(${employee.id})"
                        >
                            Delete
                        </button>
                    </div>
                </td>
            </tr>
        `
        )
        .join("");

    return data;
}

/* =========================
   DELETE EMPLOYEE
========================= */

async function deleteEmployee(id) {
    const confirmed = confirm(
        "Are you sure you want to delete this employee?"
    );

    if (!confirmed) {
        return;
    }

    try {
        await request(`deleteEmployee?id=${encodeURIComponent(id)}`, {
            method: "POST"
        });

        await loadEmployees();

        if (page === "dashboard") {
            await loadDashboard();
        }
    } catch (error) {
        alert(error.message);
    }
}

/* =========================
   DASHBOARD EMPLOYEES
========================= */

function renderRecentEmployees(employees) {
    const rows = document.querySelector("#recent-employees");

    if (!rows) {
        return;
    }

    if (!employees || employees.length === 0) {
        rows.innerHTML = `
            <tr>
                <td colspan="6" class="empty">
                    No employees found.
                </td>
            </tr>
        `;

        return;
    }

    rows.innerHTML = employees
        .slice(0, 5)
        .map(
            (employee) => `
            <tr>
                <td>${employee.id}</td>

                <td>
                    <div class="employee-name">
                        ${employee.name || ""}
                    </div>
                </td>

                <td>${employee.email || "-"}</td>

                <td>${employee.department || "-"}</td>

                <td class="salary">
                    ₹${Number(employee.salary || 0).toLocaleString("en-IN")}
                </td>

                <td>
                    <a
                        class="action"
                        href="edit-employee.html?id=${employee.id}"
                    >
                        Edit
                    </a>
                </td>
            </tr>
        `
        )
        .join("");
}

/* =========================
   DASHBOARD PROJECTS
========================= */

function formatDate(date) {
    if (!date) {
        return "-";
    }

    return date;
}

function getStatusClass(status) {
    if (!status) {
        return "";
    }

    return `status-${status
        .toLowerCase()
        .replace(/\s+/g, "-")}`;
}

function renderProjectOverview(projects) {
    const rows = document.querySelector("#project-overview");

    if (!rows) {
        return;
    }

    if (!projects || projects.length === 0) {
        rows.innerHTML = `
            <tr>
                <td colspan="6" class="empty">
                    No projects found.
                </td>
            </tr>
        `;

        return;
    }

    rows.innerHTML = projects
        .slice(0, 5)
        .map(
            (project) => `
            <tr>
                <td>${project.id}</td>

                <td>
                    <strong>${project.name || ""}</strong>
                </td>

                <td>
                    ${project.description || "-"}
                </td>

                <td>
                    ${formatDate(project.startDate)}
                </td>

                <td>
                    ${formatDate(project.endDate)}
                </td>

                <td>
                    <span class="status-badge ${getStatusClass(
                        project.status
                    )}">
                        ${project.status || "-"}
                    </span>
                </td>
            </tr>
        `
        )
        .join("");
}

/* =========================
   DASHBOARD
========================= */

async function loadDashboard() {
    try {
        const employeeData = await request("listEmployees");

        const employeeCount = document.querySelector("#employee-count");
        const departmentCount = document.querySelector("#department-count");

        if (employeeCount) {
            employeeCount.textContent =
                employeeData.employees?.length || 0;
        }

        if (departmentCount) {
            departmentCount.textContent =
                employeeData.departments?.length || 0;
        }

        renderRecentEmployees(employeeData.employees || []);

        /* Projects */

        try {
            const projectData = await request("listProjects");

            const projectCount = document.querySelector("#project-count");

            if (projectCount) {
                projectCount.textContent =
                    projectData.projects?.length || 0;
            }

            renderProjectOverview(projectData.projects || []);
        } catch (error) {
            console.error("Project loading failed:", error);

            const projectCount =
                document.querySelector("#project-count");

            if (projectCount) {
                projectCount.textContent = "0";
            }
        }

        /* Assignments */

        try {
            const assignmentData =
                await request("employeeProjects");

            const assignmentCount =
                document.querySelector("#assignment-count");

            if (assignmentCount) {
                const assignments =
                    assignmentData.assignments ||
                    assignmentData.employeeProjects ||
                    [];

                assignmentCount.textContent =
                    assignments.length;
            }
        } catch (error) {
            console.error(
                "Assignment loading failed:",
                error
            );

            const assignmentCount =
                document.querySelector("#assignment-count");

            if (assignmentCount) {
                assignmentCount.textContent = "0";
            }
        }
    } catch (error) {
        console.error("Dashboard loading failed:", error);
        showFeedback(error.message, true);
    }
}

/* =========================
   DEPARTMENTS PAGE
========================= */

async function loadDepartmentsPage() {
    const rows = document.querySelector("#department-rows");

    if (!rows) {
        return;
    }

    try {
        const data = await request("listEmployees");

        const departments = data.departments || [];

        if (departments.length === 0) {
            rows.innerHTML = `
                <tr>
                    <td colspan="2" class="empty">
                        No departments found.
                    </td>
                </tr>
            `;

            return;
        }

        rows.innerHTML = departments
            .map(
                (department) => `
                <tr>
                    <td>${department.id}</td>
                    <td>
                        <strong>${department.name}</strong>
                    </td>
                </tr>
            `
            )
            .join("");
    } catch (error) {
        rows.innerHTML = `
            <tr>
                <td colspan="2" class="empty">
                    Unable to load departments.
                </td>
            </tr>
        `;

        console.error(error);
    }
}

/* =========================
   EMPLOYEE FORM
========================= */

async function loadEmployeeForEdit() {
    if (!form) {
        return;
    }

    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (!id) {
        return;
    }

    try {
        const employee = await request(
            `editEmployee?id=${encodeURIComponent(id)}`
        );

        Object.entries(employee).forEach(([key, value]) => {
            const input = form.elements[key];

            if (input) {
                input.value = value ?? "";
            }
        });
    } catch (error) {
        showFeedback(error.message, true);
    }
}

/* =========================
   EMPLOYEE FORM SUBMIT
========================= */

async function submitEmployeeForm(event) {
    event.preventDefault();

    if (!validateForm()) {
        showFeedback(
            "Please fill in all required fields.",
            true
        );
        return;
    }

    const formData = new FormData(form);

    const id = formData.get("id");

    const endpoint = id
        ? "updateEmployee"
        : "addEmployee";

    try {
        const data = await request(endpoint, {
            method: "POST",
            body: new URLSearchParams(formData)
        });

        showFeedback(
            data.message ||
                (id
                    ? "Employee updated successfully."
                    : "Employee added successfully.")
        );

        setTimeout(() => {
            window.location.href = "employees.html";
        }, 800);
    } catch (error) {
        showFeedback(error.message, true);
    }
}

/* =========================
   PAGE INITIALIZATION
========================= */

document.addEventListener("DOMContentLoaded", async () => {
    if (page === "dashboard") {
        await loadDashboard();
    }

    if (page === "employees") {
        const departmentSelect =
            document.querySelector("#department-filter");

        try {
            const data = await loadEmployees();

            if (departmentSelect) {
                departmentSelect.innerHTML =
                    '<option value="">All departments</option>';

                data.departments?.forEach((department) => {
                    departmentSelect.add(
                        new Option(
                            department.name,
                            department.name
                        )
                    );
                });
            }
        } catch (error) {
            console.error(error);
        }

        const searchInput =
            document.querySelector("#employee-search");

        searchInput?.addEventListener("input", async () => {
            const keyword = searchInput.value.trim();

            try {
                if (keyword) {
                    await loadEmployees(
                        `searchEmployee?keyword=${encodeURIComponent(
                            keyword
                        )}`
                    );
                } else {
                    await loadEmployees();
                }
            } catch (error) {
                console.error(error);
            }
        });

        departmentSelect?.addEventListener("change", async () => {
            const selected =
                departmentSelect.value.trim();

            try {
                const data = await request("listEmployees");

                const filtered =
                    selected === ""
                        ? data.employees
                        : data.employees.filter(
                              (employee) =>
                                  employee.department ===
                                  selected
                          );

                renderEmployeeRows(filtered);
            } catch (error) {
                console.error(error);
            }
        });
    }

    if (page === "departments") {
        await loadDepartmentsPage();
    }

    if (page === "form") {
        const departmentSelect =
            form?.querySelector('[name="departmentId"]');

        if (departmentSelect) {
            try {
                await loadDepartments([departmentSelect]);
            } catch (error) {
                showFeedback(error.message, true);
            }
        }

        await loadEmployeeForEdit();

        form?.addEventListener(
            "submit",
            submitEmployeeForm
        );
    }
});

/* =========================
   EMPLOYEE FILTER RENDER
========================= */

function renderEmployeeRows(employees) {
    const rows = document.querySelector("#employee-rows");

    if (!rows) {
        return;
    }

    if (!employees || employees.length === 0) {
        rows.innerHTML = `
            <tr>
                <td colspan="6" class="empty">
                    No employees found.
                </td>
            </tr>
        `;

        return;
    }

    rows.innerHTML = employees
        .map(
            (employee) => `
            <tr>
                <td>${employee.id}</td>

                <td>
                    <div class="employee-name">
                        ${employee.name || ""}
                    </div>
                </td>

                <td>${employee.email || "-"}</td>

                <td>${employee.department || "-"}</td>

                <td class="salary">
                    ₹${Number(employee.salary || 0).toLocaleString(
                        "en-IN"
                    )}
                </td>

                <td>
                    <div class="actions">
                        <a
                            class="action"
                            href="edit-employee.html?id=${employee.id}"
                        >
                            Edit
                        </a>

                        <button
                            class="action danger"
                            type="button"
                            onclick="deleteEmployee(${employee.id})"
                        >
                            Delete
                        </button>
                    </div>
                </td>
            </tr>
        `
        )
        .join("");
}

/* =========================
   AUTH UI
========================= */

const authUiScript = document.createElement("script");
authUiScript.src = "js/auth-ui.js";
document.body.appendChild(authUiScript);