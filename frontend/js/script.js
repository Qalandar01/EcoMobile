function handleLogin(event) {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    request.post("/login", {email, password})
        .then(response => {
            alert("Login successful!");
            localStorage.setItem("token", response.data.token);
            localStorage.setItem("userId", response.data.userId);
            console.log(localStorage.getItem("userId"));

            window.location.href = "product.html";

        })
        .catch(error => {
            document.getElementById("email-error").textContent = error.response?.data?.message || "Invalid email or password";
            document.getElementById("password-error").textContent = " ";
        });
}

function openModal() {
    document.getElementById("forgot-password-modal").style.display = "block";
}

function closeModal() {
    document.getElementById("forgot-password-modal").style.display = "none";
}

function closeConfirmationModal() {
    document.getElementById("confirmation-modal").style.display = "none";
}

function sendResetCode() {
    const email = document.getElementById("reset-email").value;

    if (!email) {
        document.getElementById("reset-email-error").textContent = "Email is required";
        return;
    }

    request.post("/forgot-password", {email})
        .then(response => {
            alert("Confirmation code sent to email!");
            document.getElementById("forgot-password-modal").style.display = "none";
            document.getElementById("confirmation-modal").style.display = "block";
        })
        .catch(error => {
            document.getElementById("reset-email-error").textContent = error.response?.data?.message || "Error sending reset code";
        });
}

function verifyCode() {
    const code = document.getElementById("confirmation-code").value;
    const email = document.getElementById("reset-email").value;

    if (!code) {
        document.getElementById("confirmation-code-error").textContent = "Code is required";
        return;
    }

    request(
        {
            url: "/verify-code",
            method: "post",
            data: {
                email,
                code
            }
        }
    ).then(response => {
        openResetPasswordModal();
        closeConfirmationModal();
    })
        .catch(error => {
            document.getElementById("confirmation-code-error").textContent = error.response?.data?.message || "Invalid code";
        });
}

function openResetPasswordModal() {
    document.getElementById("reset-password-modal").style.display = "flex";
}

function closeResetPasswordModal() {
    document.getElementById("reset-password-modal").style.display = "none";
}

function resetPassword() {
    let newPassword = document.getElementById("new-password").value;
    let repeatPassword = document.getElementById("repeat-password").value;
    let errorNewPassword = document.getElementById("new-password-error");
    let errorRepeatPassword = document.getElementById("repeat-password-error");

    errorNewPassword.textContent = "";
    errorRepeatPassword.textContent = "";

    if (newPassword.length < 6) {
        errorNewPassword.textContent = "Password must be at least 6 characters.";
        return;
    }

    if (newPassword !== repeatPassword) {
        errorRepeatPassword.textContent = "Passwords do not match.";
        return;
    }

    setNewPassword(newPassword)

    closeResetPasswordModal();
}

function setNewPassword(newPassword) {
    let email = document.getElementById("reset-email").value;
    request(
        {
            url: "/set-new-password",
            method:"post",
            data:{
                email,
                password:newPassword
            }
        }
    ).then(res=>{
        closeModal();
        closeConfirmationModal();
        closeResetPasswordModal();
        alert("Password Changed Successfully")
    }).catch(ex=>{
        alert("Cannot change password? Please contact admin")
    })
}

