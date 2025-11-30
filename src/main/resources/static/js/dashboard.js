// dashboard.js completo modificado
document.addEventListener('DOMContentLoaded', () => {
    verificarAutenticacion();
    inicializarDashboard();
    configurarCerrarSesion();
    loadEntrevistas();

    // Cargar entrevistas si la sección está activa al cargar la página
    if (document.querySelector('.section.active')?.id === 'entrevistas') {
        loadEntrevistas();
    }
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
};

    

function configurarCerrarSesion() {
    const btnCerrarSesion = document.getElementById('btnCerrarSesion');
    
    if (btnCerrarSesion) {
        btnCerrarSesion.addEventListener('click', (e) => {
            e.preventDefault(); // por si acaso el botón está dentro de un form

            // Confirmación para evitar cierre accidental (muy recomendado)
            if (!confirm('¿Está seguro que desea cerrar sesión?')) {
                return; // si dice "Cancelar" no hace nada
            }

            // Limpiar
            sessionStorage.removeItem('userId');
            sessionStorage.removeItem('username');
            sessionStorage.removeItem('rol');

            // Recomendado: usar replace en vez de href para que no pueda volver atrás con el botón del navegador
            window.location.replace('/'); 
            // o si tu login está en otra ruta: window.location.replace('/login');
        });
    }
}

let currentPreinscripcionId = null;
const modal = document.getElementById('modal');

function inicializarDashboard() {
    const menuItems = document.querySelectorAll('.menu-item');
    const sections = document.querySelectorAll('.section');

    menuItems.forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();

            menuItems.forEach(mi => mi.classList.remove('active'));
            sections.forEach(sec => sec.classList.remove('active'));

            item.classList.add('active');
            const sectionId = item.getAttribute('data-section');
            const section = document.getElementById(sectionId);
            if (section) {
                section.classList.add('active');
                if (sectionId === 'entrevistas') {
                    loadEntrevistas();
                }
            }
        });
    });
}

function getEstadoText(estado) {
    if (!estado) return 'Sin preinscripción';
    const map = {
        'PENDIENTE': 'Pendiente',
        'PROGRAMADA': 'Programada',
        'REALIZADA': 'Realizada',
        'pendiente': 'Pendiente',
        'programada': 'Programada',
        'realizada': 'Realizada'
    };
    return map[estado] || estado;
}

function formatDateTime(iso) {
    if (!iso) return '-';
    const date = new Date(iso);
    return date.toLocaleString('es-ES', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function loadEntrevistas() {
    const tbody = document.querySelector('#entrevistasTable tbody');
    tbody.innerHTML = '<tr><td colspan="9" class="empty-state">Cargando...</td></tr>';

    Promise.all([
        fetch('/api/admin/entrevistas/aspirantes').then(r => r.json()),
        fetch('/api/admin/entrevistas/preinscripciones').then(r => r.json())
    ])
    .then(([aspirantes, preinscripciones]) => {
        const preMap = new Map(preinscripciones.map(p => [p.estudiante_id, p]));

        tbody.innerHTML = '';

        if (aspirantes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="9" class="empty-state">No hay estudiantes aspirantes</td></tr>';
            return;
        }

        aspirantes.forEach(est => {
            const pre = preMap.get(est.id);

            const row = document.createElement('tr');

            row.innerHTML = `
                <td>${est.id}</td>
                <td>${est.tarjeta_identidad || '-'}</td>
                <td>${est.nombre} ${est.apellido}</td>
                <td>${est.fecha_nacimiento ? new Date(est.fecha_nacimiento).toLocaleDateString('es-ES') : '-'}</td>
                <td>${est.grado_aplicado || '-'}</td>
                <td>${getEstadoText(pre?.estado_entrevista)}</td>
                <td>${pre ? formatDateTime(pre.fecha_solicitud) : '-'}</td>
                <td>${pre && pre.fecha_entrevista ? formatDateTime(pre.fecha_entrevista) : '-'}</td>
                <td id="acciones-${est.id}"></td>
            `;

            tbody.appendChild(row);

            const cell = document.getElementById(`acciones-${est.id}`);

            // Botón Rechazar siempre disponible
            const btnRechazar = document.createElement('button');
            btnRechazar.className = 'btn btn-danger btn-sm';
            btnRechazar.innerHTML = 'Rechazar';
            btnRechazar.onclick = () => rechazarEstudiante(est.id);
            cell.appendChild(btnRechazar);

            if (!pre) {
                // Sin preinscripción → solo crear
                const btnCrear = document.createElement('button');
                btnCrear.className = 'btn btn-primary btn-sm';
                btnCrear.innerHTML = 'Crear Preinscripción';
                btnCrear.onclick = () => crearPreinscripcion(est.id);
                cell.appendChild(document.createElement('br'));
                cell.appendChild(btnCrear);
            } else if (!pre.estado_entrevista || pre.estado_entrevista.toUpperCase() === 'PENDIENTE') {
                const btnProgramar = document.createElement('button');
                btnProgramar.className = 'btn btn-primary btn-sm';
                btnProgramar.innerHTML = 'Programar Entrevista';
                btnProgramar.onclick = () => programarEntrevista(pre.id);
                cell.appendChild(document.createElement('br'));
                cell.appendChild(btnProgramar);
            } else if (pre.estado_entrevista.toUpperCase() === 'PROGRAMADA') {
                const btnRealizada = document.createElement('button');
                btnRealizada.className = 'btn btn-success btn-sm';
                btnRealizada.innerHTML = 'Marcar Realizada';
                btnRealizada.onclick = () => marcarEntrevistaRealizada(pre.id);
                cell.appendChild(document.createElement('br'));
                cell.appendChild(btnRealizada);
            } else if (pre.estado_entrevista.toUpperCase() === 'REALIZADA') {
                const btnAceptar = document.createElement('button');
                btnAceptar.className = 'btn btn-success btn-sm';
                btnAceptar.innerHTML = 'Aceptar Estudiante';
                btnAceptar.onclick = () => aceptarEstudiante(est.id);
                cell.appendChild(btnAceptar);
                cell.appendChild(document.createElement('br'));
            }
            // Siempre tiene el botón rechazar mientras sea aspirante
        });
    })
    .catch(err => {
        tbody.innerHTML = '<tr><td colspan="9" class="empty-state">Error al cargar datos</td></tr>';
        console.error(err);
    });
}

function programarEntrevista(preId) {
    currentPreinscripcionId = preId;
    document.getElementById('fechaEntrevistaInput').value = '';
    modal.classList.add('active');
}

function crearPreinscripcion(estudianteId) {
    if (!confirm('¿Crear preinscripción para este estudiante?')) return;

    fetch('/api/admin/entrevistas/preinscripcion', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ estudiante_id: estudianteId })
    })
    .then(r => {
        if (!r.ok) throw Error();
        return r.json();
    })
    .then(data => {
        alert(data.message || 'Preinscripción creada');
        loadEntrevistas();
    })
    .catch(() => alert('Error al crear preinscripción'));
}

function marcarEntrevistaRealizada(preId) {
    if (!confirm('¿Marcar la entrevista como realizada?')) return;

    fetch(`/api/admin/preinscripciones/${preId}/realizada`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(r => {
        if (!r.ok) throw Error();
        return r.json();
    })
    .then(() => {
        alert('Entrevista marcada como realizada');
        loadEntrevistas();
    })
    .catch(() => alert('Error'));
}

function aceptarEstudiante(estId) {
    if (!confirm('¿Aceptar al estudiante?')) return;

    fetch(`/api/admin/estudiantes/${estId}/aceptar`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(r => {
        if (!r.ok) throw Error();
        return r.json();
    })
    .then(() => {
        alert('Estudiante aceptado');
        loadEntrevistas();
    })
    .catch(() => alert('Error'));
}

function rechazarEstudiante(estId) {
    if (!confirm('¿Rechazar al estudiante?')) return;

    fetch(`/api/admin/estudiantes/${estId}/rechazar`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(r => {
        if (!r.ok) throw Error();
        return r.json();
    })
    .then(() => {
        alert('Estudiante rechazado');
        loadEntrevistas();
    })
    .catch(() => alert('Error'));
}

// Eventos del modal
document.getElementById('cancelarModal').onclick = () => modal.classList.remove('active');

document.getElementById('confirmarModal').onclick = () => {
    const fecha = document.getElementById('fechaEntrevistaInput').value;
    if (!fecha) {
        alert('Seleccione fecha y hora');
        return;
    }

    fetch(`/api/admin/preinscripciones/${currentPreinscripcionId}/programar`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ fecha_entrevista: fecha })
    })
    .then(r => {
        if (!r.ok) throw Error();
        return r.json();
    })
    .then(data => {
        alert(data.message || 'Entrevista programada');
        modal.classList.remove('active');
        loadEntrevistas();
    })
    .catch(() => alert('Error al programar'));
};
