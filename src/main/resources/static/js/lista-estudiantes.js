// Gestión de la lista de estudiantes
class GestionListaEstudiantes {
    constructor() {
        this.usuarioId = null;
        this.grupoId = null;
        this.tipoUsuario = null; // 'acudiente', 'profesor', o 'admin'
        this.estudiantes = [];
    }

    async init() {
        // Obtener parámetros de la URL
        const params = new URLSearchParams(window.location.search);
        this.tipoUsuario = params.get('tipo');
        this.usuarioId = params.get('usuarioId');
        this.grupoId = params.get('grupoId');

        if (!this.tipoUsuario) {
            alert('Parámetros inválidos');
            window.history.back();
            return;
        }

        // Validar parámetros según tipo
        if (this.tipoUsuario === 'admin' && !this.grupoId) {
            alert('Se requiere el ID del grupo');
            window.history.back();
            return;
        } else if ((this.tipoUsuario === 'acudiente' || this.tipoUsuario === 'profesor') && !this.usuarioId) {
            alert('Se requiere el ID del usuario');
            window.history.back();
            return;
        }

        await this.cargarDatos();
    }

    async cargarDatos() {
        try {
            // Determinar el endpoint según el tipo de usuario
            let endpoint;
            if (this.tipoUsuario === 'acudiente') {
                endpoint = `/api/acudiente/${this.usuarioId}/hijos`;
            } else if (this.tipoUsuario === 'profesor') {
                endpoint = `/api/profesor/${this.usuarioId}/estudiantes`;
            } else if(this.tipoUsuario === 'admin' && this.grupoId === "sin-grupo") {
                endpoint = `/api/gestion/estudiantes/sin-grupo`;
            } else if (this.tipoUsuario === 'admin') {
                endpoint = `/api/gestion/estudiantes/grupo/${this.grupoId}`;
            }  else {
                throw new Error('Tipo de usuario no válido');
            }

            // Cargar estudiantes
            const response = await fetch(endpoint);

            if (!response.ok) {
                throw new Error('Error al cargar estudiantes');
            }

            this.estudiantes = await response.json();

            this.renderizarLista();

            // Ocultar indicador de carga y mostrar contenido
            document.getElementById('loadingIndicator').style.display = 'none';
            document.getElementById('actionButtons').style.display = 'flex';
            document.getElementById('listaContainer').style.display = 'block';

        } catch (error) {
            console.error('Error:', error);
            document.getElementById('loadingIndicator').innerHTML = `
                <div style="color: #e74c3c; font-size: 48px;">❌</div>
                <p style="color: #e74c3c;">Error al cargar datos de la lista: ${error.message}</p>
                <button class="btn btn-secondary" onclick="window.history.back()" style="margin-top: 20px;">← Volver</button>
            `;
        }
    }

    renderizarLista() {
        // Establecer título según tipo de usuario
        if (this.tipoUsuario === 'acudiente') {
            document.getElementById('responsibleTitle').textContent = 'Información del Acudiente';
            document.getElementById('responsibleLabel').textContent = 'Acudiente:';
            document.getElementById('responsibleName').textContent = 'ID: ' + this.usuarioId;
        } else if (this.tipoUsuario === 'profesor') {
            document.getElementById('responsibleTitle').textContent = 'Información del Profesor';
            document.getElementById('responsibleLabel').textContent = 'Profesor:';
            document.getElementById('responsibleName').textContent = 'ID: ' + this.usuarioId;
        } else if (this.tipoUsuario === 'admin') {
            document.getElementById('responsibleTitle').textContent = 'Información del Grupo';
            document.getElementById('responsibleLabel').textContent = 'Grupo:';
            document.getElementById('responsibleName').textContent = 'ID: ' + this.grupoId;
        }

        // Establecer fecha de generación
        const fechaActual = new Date().toLocaleDateString('es-ES', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
        document.getElementById('fechaGeneracion').textContent = fechaActual;
        document.getElementById('fechaGeneracionFooter').textContent = fechaActual;

        // Establecer total de estudiantes
        document.getElementById('totalEstudiantes').textContent = this.estudiantes.length;

        // Renderizar tabla de estudiantes
        this.renderizarTabla();
    }

    renderizarTabla() {
        const container = document.getElementById('studentsTableContainer');

        if (this.estudiantes.length === 0) {
            container.innerHTML = `
                <div style="text-align: center; padding: 40px; color: #7f8c8d;">
                    <p>No hay estudiantes registrados</p>
                </div>
            `;
            return;
        }

        let html = `
            <table class="students-table">
                <thead>
                    <tr>
                        <th style="width: 15%;">Tarjeta de Identidad</th>
                        <th style="width: 25%;">Nombre</th>
                        <th style="width: 25%;">Apellido</th>
                        <th style="width: 20%;">Grado</th>
                        <th style="width: 15%;">Estado</th>
                    </tr>
                </thead>
                <tbody>
                    ${this.estudiantes.map(est => `
                        <tr>
                            <td>${est.tarjeta_identidad || 'N/A'}</td>
                            <td>${est.nombre}</td>
                            <td>${est.apellido}</td>
                            <td>${this.formatearGrado(est.grado_aplicado)}</td>
                            <td>${this.formatearEstado(est.estado)}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;

        container.innerHTML = html;
    }

    formatearGrado(grado) {
        if (!grado) return 'Sin grado';

        const grados = {
            'parvulos': 'Párvulos',
            'caminadores': 'Caminadores',
            'pre_jardin': 'Pre-jardín'
        };
        return grados[grado] || grado;
    }

    formatearEstado(estado) {
        if (!estado) return 'N/A';

        const estados = {
            'activo': 'Activo',
            'inactivo': 'Inactivo',
            'aspirante': 'Aspirante',
            'graduado': 'Graduado'
        };
        return estados[estado] || estado;
    }
}

// Instancia global
const listaEstudiantes = new GestionListaEstudiantes();

// Función para volver
function volver() {
    // Intentar cerrar la ventana si fue abierta con window.open
    if (window.opener && !window.opener.closed) {
        window.close();
    } else if (window.history.length > 1) {
        // Si hay historial, volver atrás
        window.history.back();
    } else {
        // Si no hay historial, cerrar la ventana o ir a la página principal
        window.close();
    }
}

// Función para descargar PDF
function descargarPDF() {
    window.print();
}

// Inicializar cuando se carga la página
document.addEventListener('DOMContentLoaded', () => {
    listaEstudiantes.init();
});
