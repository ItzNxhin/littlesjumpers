// Elementos del DOM
const btnIniciarSesion = document.getElementById('btnIniciarSesion');
const loginModal = document.getElementById('loginModal');
const closeModal = document.getElementById('closeModal');
const loginForm = document.getElementById('loginForm');
const btnLogin = document.getElementById('btnLogin');
const errorMessage = document.getElementById('errorMessage');
const successMessage = document.getElementById('successMessage');

// Abrir modal
btnIniciarSesion.addEventListener('click', () => {
    loginModal.classList.add('active');
    document.getElementById('username').focus();
});

// Cerrar modal
closeModal.addEventListener('click', () => {
    loginModal.classList.remove('active');
    limpiarFormulario();
});

// Cerrar modal al hacer click fuera
loginModal.addEventListener('click', (e) => {
    if (e.target === loginModal) {
        loginModal.classList.remove('active');
        limpiarFormulario();
    }
});

// Cerrar modal con tecla ESC
document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && loginModal.classList.contains('active')) {
        loginModal.classList.remove('active');
        limpiarFormulario();
    }
});

// Manejar el envío del formulario
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const username = document.getElementById('username').value.trim();
    const password = document.getElementById('password').value;

    // Validaciones básicas
    if (!username || !password) {
        mostrarError('Por favor, complete todos los campos');
        return;
    }

    // Deshabilitar botón y mostrar loading
    btnLogin.disabled = true;
    btnLogin.innerHTML = 'Ingresando<span class="spinner"></span>';
    ocultarMensajes();

    try {
        // Llamada al endpoint de autenticación
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        const data = await response.json();

        if (response.ok && data.success) {
            // Login exitoso
            mostrarExito('¡Inicio de sesión exitoso! Redirigiendo...');

            // Guardar información del usuario en sessionStorage
            sessionStorage.setItem('userId', data.userId);
            sessionStorage.setItem('username', data.username);
            sessionStorage.setItem('rol', data.rol);

            // Redirigir según el rol después de 1 segundo
            setTimeout(() => {
                window.location.href = data.redirectUrl;
            }, 1000);

        } else {
            // Error de autenticación
            mostrarError(data.message || 'Error al iniciar sesión');
            btnLogin.disabled = false;
            btnLogin.innerHTML = 'Ingresar';
        }

    } catch (error) {
        console.error('Error:', error);
        mostrarError('Error de conexión. Verifique su conexión a internet.');
        btnLogin.disabled = false;
        btnLogin.innerHTML = 'Ingresar';
    }
});

// Funciones auxiliares
function mostrarError(mensaje) {
    errorMessage.textContent = mensaje;
    errorMessage.classList.add('active');
    successMessage.classList.remove('active');
}

function mostrarExito(mensaje) {
    successMessage.textContent = mensaje;
    successMessage.classList.add('active');
    errorMessage.classList.remove('active');
}

function ocultarMensajes() {
    errorMessage.classList.remove('active');
    successMessage.classList.remove('active');
}

function limpiarFormulario() {
    loginForm.reset();
    ocultarMensajes();
    btnLogin.disabled = false;
    btnLogin.innerHTML = 'Ingresar';
}

// Permitir submit con Enter
document.getElementById('password').addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        loginForm.dispatchEvent(new Event('submit'));
    }
});
