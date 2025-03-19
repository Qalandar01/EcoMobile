
closeConfirmationModal()
closeResetPasswordModal()
closeModal()
closeVerificationModal()
function handleLogin(event) {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    request.post("/login", { email, password })
        .then(response => {
            alert("Login successful!");

            // Token va foydalanuvchi ID sini localStorage ga saqlash
            localStorage.setItem("token", response.data.token);
            localStorage.setItem("userId", response.data.userId);

            console.log("User ID:", localStorage.getItem("userId"));
            window.location.href = "product.html"
        })
        .catch(error => {
            console.error("Login failed", error);
            alert("Login failed! Check your credentials.");
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
            method: "post",
            data: {
                email,
                password: newPassword
            }
        }
    ).then(res => {
        closeModal();
        closeConfirmationModal();
        closeResetPasswordModal();
        alert("Password Changed Successfully")
    }).catch(ex => {
        alert("Cannot change password? Please contact admin")
    })
}



let firstName
let lastName
let email
let password
let confirmPassword
let age
let phone
let gender

function handleRegister(event) {
    event.preventDefault();

    firstName = document.getElementById('register-first-name').value;
    lastName = document.getElementById('register-last-name').value
    email = document.getElementById('register-email').value
    password = document.getElementById('register-password').value
    confirmPassword = document.getElementById('register-confirm-password').value
    age = document.getElementById('register-age').value
    phone = document.getElementById('register-phone').value
    gender = document.getElementById('register-gender').value



    if (password !== confirmPassword) {
        document.getElementById('register-error').textContent = "Passwords do not match!";
        return;
    }
    if (validatePassword(password)) {
        request.post('/register-mail', {email})
            .then(res => {
                if (res.status === 200) {
                    alert("✅ Confirmation code sent!");
                    openVerificationModal()
                    clearInputs()
                } else {
                    document.getElementById('register-error').textContent = res.data.message || "❌ Email already exists or an error occurred!";
                }
            })
            .catch(err => {
                console.error('Error:', err);
                alert("❌ Email already exists or an error occurred!")
            });
    }
}


function closeVerificationModal() {
    document.getElementById('verification-modal').style.display = 'none';
}

function handleVerification() {
    const code = document.getElementById('verification-code').value;

    if (!code) {
       document.getElementById('verification-error').textContent = "Please enter the code!";
        return;
    }

    request({
        url: `/verification`,
        method: "post",
        data: {
            email,
            code,
            firstName,
            lastName,
            phone,
            gender,
            age,
            password

        }
    })
        .then(res => {
            if (res.status === 200) {
                alert("✅ Verification successful!");
                closeVerificationModal();
                closeResetPasswordModal()
                closeModal()
                toggleForm()

                // Redirect or proceed with registration
            } else {
                document.getElementById('verification-error').textContent = "❌ Incorrect code. Please try again!";

            }
        })
        .catch(err => {
            console.error('Error:', err);
            document.getElementById('verification-error').textContent = "⚠️ Verification failed!";
        });
}


function togglePasswordVisibility(fieldId, iconElement) {
    const field = document.getElementById(fieldId);
    if (field.type === "password") {
        field.type = "text";
        iconElement.textContent = "🙈";
    } else {
        field.type = "password";
        iconElement.textContent = "👁️";
    }
}
function toggleForm() {
    const container = document.getElementById('container');
    container.classList.toggle('active');
}

function openVerificationModal(){
    document.getElementById('verification-modal').style.display = 'block';
}
function validatePassword(password) {
    const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    const errorMessage = document.getElementById("register-error");

    if (!regex.test(password)) {
        alert( "Parol kamida 8 ta belgidan iborat, bitta katta harf, bitta kichik harf, bitta raqam va bitta maxsus belgi (@$!%*?&) bo'lishi kerak!");
        return false;
    }

    errorMessage.innerText = "";
    return true;
}

function clearInputs() {
    document.getElementById('register-first-name').value = "";
    document.getElementById('register-last-name').value = "";
    document.getElementById('register-email').value = "";
    document.getElementById('register-password').value = "";
    document.getElementById('register-confirm-password').value = "";
    document.getElementById('register-age').value = "";
    document.getElementById('register-phone').value = "";
    document.getElementById('register-gender').selectedIndex = 0;
}


