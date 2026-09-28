const loginForm = document.querySelector('#login-form');
const loginFeedback = document.querySelector('#login-feedback');
const signupForm = document.querySelector('#signup-form');
const signupFeedback = document.querySelector('#signup-feedback');

if (loginForm && !document.querySelector('#signup-link')) {
    const signupLink = document.createElement('p');
    signupLink.id = 'signup-link';
    signupLink.className = 'auth-hint';
    signupLink.innerHTML = '<a href="signup.html">Create an HR account</a>';
    loginForm.insertAdjacentElement('afterend', signupLink);
}

function showLoginFeedback(message) { loginFeedback.textContent = message; loginFeedback.hidden = false; loginFeedback.classList.add('error'); }
function showSignupFeedback(message) { if (!signupFeedback) return; signupFeedback.textContent = message; signupFeedback.hidden = false; signupFeedback.classList.add('error'); }

if (loginForm) loginForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    const values = new FormData(loginForm);
    if (!values.get('username') || !values.get('password')) { showLoginFeedback('Username and password are required.'); return; }
    try {
        const response = await fetch('login', {method:'POST', headers:{'Content-Type':'application/x-www-form-urlencoded'}, body:new URLSearchParams(values)});
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || 'Unable to sign in.');
        window.location.href = 'index.html';
    } catch (error) { showLoginFeedback(error.message); }
});

if (signupForm) signupForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    const values = new FormData(signupForm);
    if (String(values.get('password') || '').length < 8 || values.get('password') !== values.get('confirmPassword')) { showSignupFeedback('Passwords must match and contain at least 8 characters.'); return; }
    try {
        const response = await fetch('signup', {method:'POST', headers:{'Content-Type':'application/x-www-form-urlencoded'}, body:new URLSearchParams(values)});
        const data = await response.json();
        if (!response.ok) throw new Error(data.message || 'Unable to create account.');
        window.location.href = 'login.html';
    } catch (error) { showSignupFeedback(error.message); }
});