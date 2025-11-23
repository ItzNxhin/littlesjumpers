// Verificar autenticación al cargar
document.addEventListener('DOMContentLoaded', () => {
    verificarAutenticacion();
    inicializarDashboard();
    configurarCerrarSesion();
});

// Verificar si el usuario está autenticado
function verificarAutenticacion() {
    const userId = sessionStorage.getItem('userId');
    const username = sessionStorage.getItem('username');
    const rol = sessionStorage.getItem('rol');

    if (!userId || !username || !rol) {
        // No hay sesión activa, redirigir al inicio
        window.location.href = '/';
        return;
    }

    // Actualizar información del usuario en la interfaz
    const userInfo = document.getElementById('userInfo');
    if (userInfo) {
        userInfo.textContent = `Bienvenido, ${username}`;
    }

    // Actualizar perfil si existe
    const profileUsername = document.getElementById('profileUsername');
    if (profileUsername) {
        profileUsername.textContent = username;
    }
}

// Inicializar funcionalidad del dashboard
function inicializarDashboard() {
    const menuItems = document.querySelectorAll('.menu-item');
    const sections = document.querySelectorAll('.section');

    menuItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();

            // Remover active de todos los items y secciones
            menuItems.forEach(mi => mi.classList.remove('active'));
            sections.forEach(sec => sec.classList.remove('active'));

            // Agregar active al item clickeado
            item.classList.add('active');

            // Mostrar la sección correspondiente
            const sectionId = item.getAttribute('data-section');
            const section = document.getElementById(sectionId);
            if (section) {
                section.classList.add('active');
            }
        });
    });
}

// Configurar botón de cerrar sesión
function configurarCerrarSesion() {
    const btnCerrarSesion = document.getElementById('btnCerrarSesion');
    if (btnCerrarSesion) {
        btnCerrarSesion.addEventListener('click', () => {
            if (confirm('¿Está seguro que desea cerrar sesión?')) {
                // Limpiar sessionStorage
                sessionStorage.removeItem('userId');
                sessionStorage.removeItem('username');
                sessionStorage.removeItem('rol');

                // Redirigir al inicio
                window.location.href = '/';
            }
        });
    }
}

// Función para seleccionar un estudiante (para acudientes)
function seleccionarEstudiante(estudianteId, estudianteNombre) {
    const studentOptions = document.getElementById('studentOptions');
    const selectedStudentName = document.getElementById('selectedStudentName');

    if (studentOptions && selectedStudentName) {
        selectedStudentName.textContent = estudianteNombre;
        studentOptions.style.display = 'block';

        // Scroll suave a las opciones
        studentOptions.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
}

// Función para seleccionar un curso (para docentes)
function seleccionarCurso(cursoId, cursoNombre) {
    const courseOptions = document.getElementById('courseOptions');
    const selectedCourseName = document.getElementById('selectedCourseName');

    if (courseOptions && selectedCourseName) {
        selectedCourseName.textContent = cursoNombre;
        courseOptions.style.display = 'block';

        // Scroll suave a las opciones
        courseOptions.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
}

// Prevenir navegación hacia atrás después del login
window.addEventListener('pageshow', (event) => {
    if (event.persisted) {
        verificarAutenticacion();
    }
});
