document.addEventListener("DOMContentLoaded", () => {
    const menuButton = document.getElementById("dashboard-menu");
    const sidebar = document.getElementById("dashboard-sidebar");
    const overlay = document.getElementById("dashboard-overlay");
    const closeButton = document.getElementById("sidebar-close");

    if (!menuButton || !sidebar || !overlay) {
        return;
    }

    function openSidebar() {
        sidebar.classList.add("open");
        overlay.classList.add("open");
        menuButton.setAttribute("aria-expanded", "true");
        document.body.style.overflow = "hidden";
    }

    function closeSidebar() {
        sidebar.classList.remove("open");
        overlay.classList.remove("open");
        menuButton.setAttribute("aria-expanded", "false");
        document.body.style.overflow = "";
    }

    menuButton.addEventListener("click", () => {
        if (sidebar.classList.contains("open")) {
            closeSidebar();
        } else {
            openSidebar();
        }
    });

    closeButton?.addEventListener("click", closeSidebar);
    overlay.addEventListener("click", closeSidebar);

    /* =========================
       ACTIVE SIDEBAR LINK
    ========================= */

    const currentPage =
        window.location.pathname.split("/").pop() || "index.html";

    let activePage = currentPage;

    if (
        currentPage === "add-employee.html" ||
        currentPage === "edit-employee.html"
    ) {
        activePage = "employees.html";
    }

    if (
        currentPage === "add-project.html" ||
        currentPage === "edit-project.html"
    ) {
        activePage = "projects.html";
    }

    document.querySelectorAll(".sidebar-link").forEach((link) => {
        const href = link.getAttribute("href");

        if (href === activePage) {
            link.classList.add("active");
        } else {
            link.classList.remove("active");
        }

        link.addEventListener("click", () => {
            closeSidebar();
        });
    });

    /* =========================
       ESCAPE KEY
    ========================= */

    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape") {
            closeSidebar();
        }
    });

    /* =========================
       LOGOUT
    ========================= */

    const logoutButton = document.getElementById("logout-button");

    logoutButton?.addEventListener("click", async () => {
        try {
            await fetch("logout", {
                method: "POST"
            });
        } catch (error) {
            console.error("Logout request failed:", error);
        } finally {
            window.location.href = "login.html";
        }
    });
});