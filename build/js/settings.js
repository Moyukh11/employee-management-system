document.addEventListener("DOMContentLoaded", () => {

    loadCurrentUser();

    const form =
        document.getElementById("change-password-form");

    if (form) {
        form.addEventListener(
            "submit",
            handlePasswordChange
        );
    }

});


async function loadCurrentUser() {

    try {

        const response = await fetch("currentUser", {
            method: "GET",
            credentials: "same-origin",
            headers: {
                "Accept": "application/json"
            }
        });


        if (response.status === 401) {

            window.location.href = "login.html";

            return;
        }


        if (!response.ok) {

            throw new Error(
                "Unable to load account information."
            );

        }


        const user = await response.json();


        const username =
            user.username || "User";

        const email =
            user.email || "Not available";

        const role =
            user.role || "User";


        const displayRole =
            formatRole(role);


        const initial =
            username.charAt(0).toUpperCase() || "U";


        setText(
            "username",
            username
        );

        setText(
            "email",
            email
        );

        setText(
            "role",
            displayRole
        );

        setText(
            "profile-name",
            username
        );

        setText(
            "profile-role",
            displayRole
        );

        setText(
            "sidebar-user-name",
            username
        );

        setText(
            "sidebar-user-role",
            displayRole
        );

        setText(
            "profile-avatar",
            initial
        );

        setText(
            "sidebar-avatar",
            initial
        );


        /*
         * Add username and avatar
         * to the top navbar.
         */
        const topbar =
            document.querySelector(".topbar");


        if (
            topbar &&
            !document.getElementById(
                "settings-user-identity"
            )
        ) {

            const identity =
                document.createElement("div");


            identity.id =
                "settings-user-identity";


            identity.className =
                "app-user-identity";


            const avatar =
                document.createElement("span");


            avatar.className =
                "app-user-avatar";


            avatar.textContent =
                initial;


            const name =
                document.createElement("span");


            name.textContent =
                username;


            identity.append(
                avatar,
                name
            );


            topbar.append(identity);

        }


    } catch (error) {

        console.error(
            "Loading current user failed:",
            error
        );


        setText(
            "username",
            "Unable to load"
        );

        setText(
            "email",
            "Unable to load"
        );

        setText(
            "role",
            "Unable to load"
        );


        showFeedback(
            "Could not load your account information. Refresh the page and try again.",
            true
        );

    }

}



async function handlePasswordChange(event) {

    event.preventDefault();


    const form =
        event.currentTarget;


    const currentPassword =
        document.getElementById(
            "currentPassword"
        ).value;


    const newPassword =
        document.getElementById(
            "newPassword"
        ).value;


    const confirmPassword =
        document.getElementById(
            "confirmPassword"
        ).value;


    const button =
        document.getElementById(
            "change-password-button"
        );


    if (
        !currentPassword ||
        !newPassword ||
        !confirmPassword
    ) {

        showFeedback(
            "Please fill in all password fields.",
            true
        );

        return;
    }


    if (newPassword.length < 8) {

        showFeedback(
            "Your new password must contain at least 8 characters.",
            true
        );

        return;
    }


    if (newPassword !== confirmPassword) {

        showFeedback(
            "The new password and confirmation do not match.",
            true
        );

        return;
    }


    if (currentPassword === newPassword) {

        showFeedback(
            "Your new password must differ from your current password.",
            true
        );

        return;
    }


    button.disabled = true;

    button.textContent =
        "Changing Password...";


    try {

        const response =
            await fetch("changePassword", {

                method: "POST",

                credentials: "same-origin",

                headers: {
                    "Content-Type":
                        "application/x-www-form-urlencoded;charset=UTF-8",

                    "Accept":
                        "application/json"
                },

                body:
                    new URLSearchParams({
                        currentPassword:
                            currentPassword,

                        newPassword:
                            newPassword
                    })

            });


        const result =
            await response.json();


        if (response.status === 401) {

            window.location.href =
                "login.html";

            return;
        }


        if (
            !response.ok ||
            !result.success
        ) {

            throw new Error(
                result.message ||
                "Password change failed."
            );

        }


        form.reset();


        showFeedback(
            result.message ||
            "Password changed successfully.",
            false
        );


    } catch (error) {

        showFeedback(
            error.message ||
            "An unexpected error occurred.",
            true
        );


    } finally {

        button.disabled = false;

        button.textContent =
            "Change Password";

    }

}



function setText(id, value) {

    const element =
        document.getElementById(id);


    if (element) {

        element.textContent =
            value ?? "";

    }

}



function formatRole(role) {

    const normalized =
        String(role || "")
            .toUpperCase();


    if (normalized === "ADMIN") {

        return "Administrator";

    }


    if (normalized === "HR") {

        return "HR";

    }


    return normalized || "User";

}



function showFeedback(
    message,
    isError
) {

    const feedback =
        document.getElementById(
            "password-feedback"
        );


    if (!feedback) {

        return;

    }


    feedback.textContent =
        message;


    feedback.className =
        "password-feedback " +
        (isError
            ? "error"
            : "success");

}
