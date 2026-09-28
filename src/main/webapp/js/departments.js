document.addEventListener("DOMContentLoaded", () => {

    // =========================
    // ELEMENTS
    // =========================

    const departmentList =
        document.getElementById("department-list");

    const departmentFormContainer =
        document.getElementById(
            "department-form-container"
        );

    const departmentForm =
        document.getElementById("department-form");

    const departmentName =
        document.getElementById("department-name");

    const searchInput =
        document.getElementById("department-search");

    const feedback =
        document.getElementById("department-feedback");

    const addDepartmentNav =
        document.getElementById("add-department-nav");

    const cancelDepartment =
        document.getElementById("cancel-department");

    const cancelDepartment2 =
        document.getElementById("cancel-department-2");


    // =========================
    // DATA
    // =========================

    let departments = [];


    // =========================
    // FEEDBACK
    // =========================

    function showFeedback(
        message,
        type = "success"
    ) {

        feedback.textContent = message;

        feedback.className =
            `feedback ${type}`;

        feedback.hidden = false;


        setTimeout(() => {

            feedback.hidden = true;

        }, 3000);
    }


    // =========================
    // OPEN ADD FORM
    // =========================

    function openAddDepartmentForm() {

        departmentFormContainer.hidden = false;

        departmentName.focus();


        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    }


    // =========================
    // CLOSE ADD FORM
    // =========================

    function closeAddDepartmentForm() {

        departmentFormContainer.hidden = true;

        departmentForm.reset();
    }


    // Navbar Add Department

    addDepartmentNav.addEventListener(
        "click",
        openAddDepartmentForm
    );


    // Cancel buttons

    cancelDepartment.addEventListener(
        "click",
        closeAddDepartmentForm
    );


    cancelDepartment2.addEventListener(
        "click",
        closeAddDepartmentForm
    );


    // =========================
    // LOAD DEPARTMENTS
    // =========================

    async function loadDepartments() {

        try {

            const response =
                await fetch("listDepartments");


            if (!response.ok) {

                throw new Error(
                    `HTTP ${response.status}`
                );
            }


            const data =
                await response.json();


            /*
             * Supports:
             *
             * [
             *   {
             *      id: 1,
             *      name: "IT",
             *      employeeCount: 5
             *   }
             * ]
             *
             * OR:
             *
             * {
             *   departments: [...]
             * }
             */


            departments =
                Array.isArray(data)
                    ? data
                    : (data.departments || []);


            renderDepartments(
                departments
            );


        } catch (error) {

            console.error(
                "Failed to load departments:",
                error
            );


            departmentList.innerHTML = `
                <div class="empty">
                    Unable to load departments.
                </div>
            `;
        }
    }


    // =========================
    // RENDER DEPARTMENTS
    // =========================

    function renderDepartments(list) {

        if (!list.length) {

            departmentList.innerHTML = `
                <div class="empty">
                    No departments found.
                </div>
            `;

            return;
        }


        departmentList.innerHTML =
            list.map(department => {

                const id =
                    department.id;


                const name =
                    department.name ||
                    department.departmentName ||
                    "Unnamed Department";


                const employeeCount =
                    department.employeeCount ??
                    department.employee_count ??
                    0;


                return `
                    <article
                        class="department-card"
                    >

                        <div
                            class="department-card-header"
                        >

                            <div
                                class="department-icon"
                            >
                                ▦
                            </div>


                            <div>

                                <h3>
                                    ${escapeHtml(name)}
                                </h3>

                                <p>
                                    Department #${id}
                                </p>

                            </div>

                        </div>


                        <div
                            class="department-card-body"
                        >

                            <div
                                class="department-stat"
                            >

                                <strong>
                                    ${employeeCount}
                                </strong>

                                <span>
                                    Employees
                                </span>

                            </div>

                        </div>


                        <div
                            class="department-card-actions"
                        >

                            <button
                                type="button"
                                class="button danger delete-department"
                                data-id="${id}"
                                data-name="${escapeHtml(name)}"
                            >
                                Remove
                            </button>

                        </div>

                    </article>
                `;

            }).join("");


        // Delete buttons

        document
            .querySelectorAll(
                ".delete-department"
            )
            .forEach(button => {

                button.addEventListener(
                    "click",
                    () => {

                        deleteDepartment(
                            button.dataset.id,
                            button.dataset.name
                        );
                    }
                );

            });
    }


    // =========================
    // ADD DEPARTMENT
    // =========================

    departmentForm.addEventListener(
        "submit",
        async (event) => {

            event.preventDefault();


            const name =
                departmentName.value.trim();


            if (!name) {

                showFeedback(
                    "Department name is required.",
                    "error"
                );

                departmentName.focus();

                return;
            }


            try {

                const response =
                    await fetch(
                        "addDepartment",
                        {
                            method: "POST",

                            headers: {
                                "Content-Type":
                                    "application/x-www-form-urlencoded"
                            },

                            body:
                                new URLSearchParams({
                                    name: name
                                })
                        }
                    );


                if (!response.ok) {

                    const message =
                        await response.text();

                    throw new Error(
                        message ||
                        `HTTP ${response.status}`
                    );
                }


                closeAddDepartmentForm();


                showFeedback(
                    "Department added successfully."
                );


                await loadDepartments();


            } catch (error) {

                console.error(
                    "Add department error:",
                    error
                );


                showFeedback(
                    "Unable to add department.",
                    "error"
                );
            }
        }
    );


    // =========================
    // DELETE DEPARTMENT
    // =========================

    async function deleteDepartment(
        id,
        name
    ) {

        const confirmed =
            confirm(
                `Are you sure you want to remove "${name}"?`
            );


        if (!confirmed) {
            return;
        }


        try {

            const response =
                await fetch(
                    `deleteDepartment?id=${encodeURIComponent(id)}`,
                    {
                        method: "POST"
                    }
                );


            if (!response.ok) {

                const message =
                    await response.text();

                throw new Error(
                    message ||
                    `HTTP ${response.status}`
                );
            }


            showFeedback(
                "Department removed successfully."
            );


            await loadDepartments();


        } catch (error) {

            console.error(
                "Delete department error:",
                error
            );


            showFeedback(
                "Unable to remove department.",
                "error"
            );
        }
    }


    // =========================
    // SEARCH
    // =========================

    searchInput.addEventListener(
        "input",
        () => {

            const keyword =
                searchInput.value
                    .trim()
                    .toLowerCase();


            if (!keyword) {

                renderDepartments(
                    departments
                );

                return;
            }


            const filtered =
                departments.filter(
                    department => {

                        const name =
                            (
                                department.name ||
                                department.departmentName ||
                                ""
                            ).toLowerCase();


                        return name.includes(
                            keyword
                        );
                    }
                );


            renderDepartments(
                filtered
            );
        }
    );


    // =========================
    // ESCAPE HTML
    // =========================

    function escapeHtml(value) {

        return String(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }


    // =========================
    // INITIAL LOAD
    // =========================

    loadDepartments();

});