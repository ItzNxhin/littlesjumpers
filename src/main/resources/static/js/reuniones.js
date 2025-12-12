// Gestión de Reuniones / Citaciones
class GestionReuniones {
    constructor() {
        this.usuarios = [];
        this.usuariosSeleccionados = [];
        this.tipoSeleccionado = null;
    }

    async init() {
        await this.cargarUsuarios();
        await this.cargarHistorialCitaciones();
    }

    // ========== CARGAR DATOS ==========

    async cargarUsuarios() {
        try {
            const response = await fetch('/api/admin/citaciones/usuarios');
            if (!response.ok) {
                throw new Error('Error al cargar usuarios');
            }
            this.usuarios = await response.json();
        } catch (error) {
            console.error('Error:', error);
            alert('Error al cargar usuarios: ' + error.message);
        }
    }

    async cargarHistorialCitaciones() {
        try {
            const response = await fetch('/api/admin/citaciones');
            if (!response.ok) {
                throw new Error('Error al cargar citaciones');
            }
            const citaciones = await response.json();
            this.renderizarHistorial(citaciones);
        } catch (error) {
            console.error('Error:', error);
            const container = document.getElementById('citacionesTableContainer');
            container.innerHTML = '<p style="color: #e74c3c;">Error al cargar historial de citaciones</p>';
        }
    }

    renderizarHistorial(citaciones) {
        const container = document.getElementById('citacionesTableContainer');

        if (citaciones.length === 0) {
            container.innerHTML = `
                <div class="empty-state-card">
                    <div class="card-icon">📭</div>
                    <p>No hay citaciones registradas</p>
                </div>
            `;
            return;
        }

        let html = `
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tipo</th>
                        <th>Asunto</th>
                        <th>Fecha Reunión</th>
                        <th>Fecha Envío</th>
                        <th>Destinatarios</th>
                    </tr>
                </thead>
                <tbody>
        `;

        citaciones.forEach(citacion => {
            const fechaReunion = new Date(citacion.fechaReunion).toLocaleString('es-ES');
            const fechaEnvio = new Date(citacion.fechaEnvio).toLocaleString('es-ES');
            const tipoBadge = citacion.tipo === 'masiva'
                ? '<span class="status-badge status-success">Masiva</span>'
                : '<span class="status-badge status-info">Selectiva</span>';

            html += `
                <tr>
                    <td>${citacion.id}</td>
                    <td>${tipoBadge}</td>
                    <td>${citacion.asunto}</td>
                    <td>${fechaReunion}</td>
                    <td>${fechaEnvio}</td>
                    <td>${citacion.cantidadDestinatarios} usuarios</td>
                </tr>
            `;
        });

        html += '</tbody></table>';
        container.innerHTML = html;
    }

    // ========== MODAL Y FORMULARIO ==========

    seleccionarTipo(tipo) {
        this.tipoSeleccionado = tipo;
        document.getElementById('citacionTipo').value = tipo;
        this.abrirModalCrearCitacion();
        this.cambiarTipoCitacion();
    }

    abrirModalCrearCitacion() {
        const modal = document.getElementById('modalCrearCitacion');
        modal.style.display = 'flex';

        // Limpiar formulario
        document.getElementById('formCrearCitacion').reset();
        this.usuariosSeleccionados = [];
        document.getElementById('grupoDestinatarios').style.display = 'none';
    }

    cerrarModalCrearCitacion() {
        document.getElementById('modalCrearCitacion').style.display = 'none';
        this.tipoSeleccionado = null;
    }

    cambiarTipoCitacion() {
        const tipo = document.getElementById('citacionTipo').value;
        const grupoDestinatarios = document.getElementById('grupoDestinatarios');

        if (tipo === 'selectiva') {
            grupoDestinatarios.style.display = 'block';
            this.renderizarListaUsuarios();
        } else {
            grupoDestinatarios.style.display = 'none';
            this.usuariosSeleccionados = [];
        }
    }

    renderizarListaUsuarios() {
        const listaUsuarios = document.getElementById('listaUsuarios');
        const searchInput = document.getElementById('searchUsuarios');

        // Filtrar usuarios según búsqueda
        searchInput.oninput = () => {
            const busqueda = searchInput.value.toLowerCase();
            const usuariosFiltrados = this.usuarios.filter(u =>
                u.nombreCompleto.toLowerCase().includes(busqueda) ||
                u.correo.toLowerCase().includes(busqueda)
            );
            this.renderizarUsuarios(usuariosFiltrados);
        };

        // Renderizar todos los usuarios inicialmente
        this.renderizarUsuarios(this.usuarios);
    }

    renderizarUsuarios(usuarios) {
        const listaUsuarios = document.getElementById('listaUsuarios');

        if (usuarios.length === 0) {
            listaUsuarios.innerHTML = '<p style="color: #666; text-align: center;">No se encontraron usuarios</p>';
            return;
        }

        let html = '';
        usuarios.forEach(usuario => {
            const isSelected = this.usuariosSeleccionados.includes(usuario.id);
            html += `
                <div class="usuario-checkbox" style="padding: 8px; border-bottom: 1px solid #eee;">
                    <label style="display: flex; align-items: center; cursor: pointer;">
                        <input type="checkbox"
                               value="${usuario.id}"
                               ${isSelected ? 'checked' : ''}
                               onchange="reuniones.toggleUsuario(${usuario.id})"
                               style="margin-right: 10px;">
                        <div>
                            <strong>${usuario.nombreCompleto}</strong><br>
                            <small style="color: #666;">${usuario.correo}</small>
                        </div>
                    </label>
                </div>
            `;
        });

        listaUsuarios.innerHTML = html;
    }

    toggleUsuario(usuarioId) {
        const index = this.usuariosSeleccionados.indexOf(usuarioId);
        if (index > -1) {
            this.usuariosSeleccionados.splice(index, 1);
        } else {
            this.usuariosSeleccionados.push(usuarioId);
        }
    }

    // ========== ENVIAR CITACIÓN ==========

    async enviarCitacion() {
        const tipo = document.getElementById('citacionTipo').value;
        const asunto = document.getElementById('citacionAsunto').value;
        const cuerpo = document.getElementById('citacionCuerpo').value;
        const fechaReunion = document.getElementById('citacionFecha').value;

        // Validaciones
        if (!tipo || !asunto || !cuerpo || !fechaReunion) {
            alert('Por favor, complete todos los campos obligatorios');
            return;
        }

        if (tipo === 'selectiva' && this.usuariosSeleccionados.length === 0) {
            alert('Por favor, seleccione al menos un destinatario');
            return;
        }

        // Preparar datos
        const citacionData = {
            tipo: tipo,
            asunto: asunto,
            cuerpo: cuerpo,
            fechaReunion: fechaReunion,
            destinatariosIds: tipo === 'selectiva' ? this.usuariosSeleccionados : []
        };

        try {
            const response = await fetch('/api/admin/citaciones', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(citacionData)
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Error al enviar citación');
            }

            const result = await response.json();

            alert(`Citación enviada exitosamente a ${result.cantidadDestinatarios} usuarios.\n\nLos correos se están enviando en segundo plano.`);

            this.cerrarModalCrearCitacion();
            await this.cargarHistorialCitaciones();

        } catch (error) {
            console.error('Error:', error);
            alert('Error al enviar citación: ' + error.message);
        }
    }
}

// Instancia global
const reuniones = new GestionReuniones();

// Inicializar cuando se muestra la sección
document.addEventListener('DOMContentLoaded', () => {
    const reunionesSection = document.getElementById('reuniones');
    if (reunionesSection) {
        // Observar cuando se activa la sección
        const observer = new MutationObserver((mutations) => {
            mutations.forEach((mutation) => {
                if (mutation.type === 'attributes' && mutation.attributeName === 'class') {
                    if (reunionesSection.classList.contains('active')) {
                        reuniones.init();
                    }
                }
            });
        });

        observer.observe(reunionesSection, { attributes: true });

        // Si la sección ya está activa, inicializar
        if (reunionesSection.classList.contains('active')) {
            reuniones.init();
        }
    }
});
