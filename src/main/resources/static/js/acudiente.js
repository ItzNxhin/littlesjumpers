class GestionAcudiente {
    constructor() {
        this.acudienteId = null;
        this.hijos = [];
        this.selectedEstudianteId = null;
        this.init();
    }

    init() {
        this.obtenerAcudienteId();
        this.setupEventListeners();
        this.cargarHijos();
    }

    obtenerAcudienteId() {
        const acudienteId = sessionStorage.getItem('acudienteId');

        if (!acudienteId) {
            this.mostrarError('No se encontró el ID del acudiente en la sesión. Por favor inicie sesión nuevamente.');
            setTimeout(() => {
                window.location.href = '/';
            }, 2000);
            return;
        }

        this.acudienteId = parseInt(acudienteId);
    }

    setupEventListeners() {
        document.getElementById('btnCerrarSesion').addEventListener('click', () => this.cerrarSesion());
    }

    async cargarHijos() {
        try {
            const response = await fetch(`/api/acudiente/${this.acudienteId}/hijos`);

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Error al cargar hijos');
            }

            this.hijos = await response.json();
            this.renderizarSidebar();

        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar la lista de hijos: ' + error.message);
        }
    }

    renderizarSidebar() {
        const listaHijos = document.getElementById('listaHijos');

        if (this.hijos.length === 0) {
            listaHijos.innerHTML = `
                <div class="empty-state">
                    <p>No hay hijos registrados</p>
                </div>
            `;
            return;
        }

        listaHijos.innerHTML = this.hijos.map(hijo => `
            <a href="#" class="menu-item" data-estudiante-id="${hijo.id}">
                <span class="icon">👨‍🎓</span>
                <span>${hijo.nombre} ${hijo.apellido}</span>
            </a>
        `).join('');

        // Agregar event listeners a los items del menú
        document.querySelectorAll('.menu-item').forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                const estudianteId = parseInt(item.dataset.estudianteId);
                this.seleccionarHijo(estudianteId);

                // Actualizar clase active
                document.querySelectorAll('.menu-item').forEach(i => i.classList.remove('active'));
                item.classList.add('active');
            });
        });
    }

    seleccionarHijo(estudianteId) {
        this.selectedEstudianteId = estudianteId;
        const hijo = this.hijos.find(h => h.id === estudianteId);

        if (!hijo) return;

        this.mostrarDatosEstudiante(hijo);
    }

    mostrarDatosEstudiante(hijo) {
        const content = document.getElementById('content');

        content.innerHTML = `
            <h2>Información del Estudiante</h2>
            <div class="profile-card">
                <div class="profile-header">
                    <div class="profile-avatar">👨‍🎓</div>
                    <div class="profile-info">
                        <h3>${hijo.nombre} ${hijo.apellido}</h3>
                        <p>Grado: ${hijo.grado_aplicado || 'Sin grado asignado'}</p>
                        <p>Estado: ${hijo.estado}</p>
                    </div>
                </div>
            </div>

            <h3 style="margin-top: 30px;">Opciones</h3>
            <div class="card-grid">
                <div class="card">
                    <div class="card-icon">📊</div>
                    <h3>Observador</h3>
                    <p>Ver el observador del estudiante</p>
                    <button class="btn btn-primary" id="btnVerObservador">Ver Observador</button>
                </div>
                <div class="card">
                    <div class="card-icon">📝</div>
                    <h3>Boletines</h3>
                    <p>Consultar calificaciones y boletines</p>
                    <button class="btn btn-primary" id="btnVerBoletines">Ver Boletines</button>
                </div>
            </div>
        `;
    }

    // ========== OBSERVADOR ==========
    async verObservador() {
        if (!this.selectedEstudianteId) {
            this.mostrarError('Por favor selecciona un estudiante primero');
            return;
        }

        const hijo = this.hijos.find(h => h.id === this.selectedEstudianteId);
        if (!hijo) return;

        const content = document.getElementById('content');

        content.innerHTML = `
            <h2>Observador del Estudiante</h2>
            <div class="profile-card">
                <div class="profile-header">
                    <div class="profile-avatar">👨‍🎓</div>
                    <div class="profile-info">
                        <h3>${hijo.nombre} ${hijo.apellido}</h3>
                        <p>Grado: ${hijo.grado_aplicado || 'Sin grado asignado'}</p>
                    </div>
                </div>
            </div>

            <div class="card" style="margin-top: 20px;">
                <h3>Historial de Observaciones</h3>
                <div id="listaObservaciones">
                    <div class="loading">Cargando observaciones...</div>
                </div>
            </div>

            <div class="form-actions" style="margin-top: 20px;">
                <button class="btn btn-secondary" onclick="gestionAcudiente.volverADatos()">
                    Volver
                </button>
            </div>
        `;

        this.cargarObservaciones(this.selectedEstudianteId);
    }

    async cargarObservaciones(estudianteId) {
        try {
            const response = await fetch(`/api/acudiente/${this.acudienteId}/estudiantes/${estudianteId}/observaciones`);

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Error al cargar observaciones');
            }

            const observaciones = await response.json();
            this.renderizarObservaciones(observaciones);

        } catch (error) {
            console.error('Error:', error);
            document.getElementById('listaObservaciones').innerHTML = `
                <div class="empty-state">
                    <p style="color: #e74c3c;">Error al cargar observaciones: ${error.message}</p>
                </div>
            `;
        }
    }

    renderizarObservaciones(observaciones) {
        const lista = document.getElementById('listaObservaciones');

        if (observaciones.length === 0) {
            lista.innerHTML = `
                <div class="empty-state">
                    <p>No hay observaciones registradas para este estudiante</p>
                </div>
            `;
            return;
        }

        lista.innerHTML = observaciones.map(obs => `
            <div class="observacion-card">
                <div class="observacion-header">
                    <div>
                        <strong>Profesor:</strong> ${obs.profesorNombre}
                    </div>
                    <div class="observacion-fecha">
                        ${this.formatearFecha(obs.fecha)}
                    </div>
                </div>
                <div class="observacion-texto">
                    ${obs.texto}
                </div>
            </div>
        `).join('');
    }

    // ========== BOLETINES ==========
    async verBoletines() {
        if (!this.selectedEstudianteId) {
            this.mostrarError('Por favor selecciona un estudiante primero');
            return;
        }

        const hijo = this.hijos.find(h => h.id === this.selectedEstudianteId);
        if (!hijo) return;

        const content = document.getElementById('content');

        content.innerHTML = `
            <h2>Boletines del Estudiante</h2>
            <div class="profile-card">
                <div class="profile-header">
                    <div class="profile-avatar">👨‍🎓</div>
                    <div class="profile-info">
                        <h3>${hijo.nombre} ${hijo.apellido}</h3>
                        <p>Grado: ${hijo.grado_aplicado || 'Sin grado asignado'}</p>
                    </div>
                </div>
            </div>

            <div class="card" style="margin-top: 20px;">
                <h3>Seleccionar Periodo</h3>
                <div class="periodo-selector" style="display: flex; gap: 10px; flex-wrap: wrap;">
                    <button class="btn btn-periodo" data-periodo="1" style="flex: 1; min-width: 120px;">Periodo 1</button>
                    <button class="btn btn-periodo" data-periodo="2" style="flex: 1; min-width: 120px;">Periodo 2</button>
                    <button class="btn btn-periodo" data-periodo="3" style="flex: 1; min-width: 120px;">Periodo 3</button>
                    <button class="btn btn-periodo" data-periodo="4" style="flex: 1; min-width: 120px;">Periodo 4</button>
                </div>
            </div>

            <div class="card" style="margin-top: 20px;">
                <h3>Boletín</h3>
                <div id="boletinContent">
                    <div class="empty-state">
                        <p>Selecciona un periodo para ver el boletín</p>
                    </div>
                </div>
            </div>

            <div class="form-actions" style="margin-top: 20px;">
                <button class="btn btn-secondary" onclick="gestionAcudiente.volverADatos()">
                    Volver
                </button>
            </div>
        `;

        // Agregar event listeners a los botones de periodo
        document.querySelectorAll('.btn-periodo').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const periodo = parseInt(e.target.dataset.periodo);
                this.cargarBoletin(periodo);

                // Marcar como activo
                document.querySelectorAll('.btn-periodo').forEach(b => b.classList.remove('active'));
                e.target.classList.add('active');
            });
        });
    }

    async cargarBoletin(periodo) {
        try {
            const boletinContent = document.getElementById('boletinContent');
            boletinContent.innerHTML = '<div class="loading">Cargando boletín...</div>';

            const response = await fetch(`/api/acudiente/${this.acudienteId}/estudiantes/${this.selectedEstudianteId}/calificaciones`);

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Error al cargar calificaciones');
            }

            const calificaciones = await response.json();

            // Filtrar por periodo
            const calificacionesPeriodo = calificaciones.filter(c => c.periodo === periodo);

            this.renderizarBoletin(calificacionesPeriodo, periodo);

        } catch (error) {
            console.error('Error:', error);
            document.getElementById('boletinContent').innerHTML = `
                <div class="empty-state">
                    <p style="color: #e74c3c;">Error al cargar boletín: ${error.message}</p>
                </div>
            `;
        }
    }

    renderizarBoletin(calificaciones, periodo) {
        const boletinContent = document.getElementById('boletinContent');

        if (calificaciones.length === 0) {
            boletinContent.innerHTML = `
                <div class="empty-state">
                    <p>No hay calificaciones registradas para el periodo ${periodo}</p>
                </div>
            `;
            return;
        }

        // Agrupar por categoría
        const porCategoria = {};
        calificaciones.forEach(cal => {
            if (!porCategoria[cal.logroCategoria]) {
                porCategoria[cal.logroCategoria] = [];
            }
            porCategoria[cal.logroCategoria].push(cal);
        });

        // Calcular promedio general
        const promedioGeneral = calificaciones.reduce((sum, cal) =>
            sum + parseFloat(cal.valor), 0) / calificaciones.length;

        let html = `
            <div class="boletin-header" style="background: #f8f9fa; padding: 15px; border-radius: 8px; margin-bottom: 20px;">
                <h3 style="margin: 0 0 10px 0;">Periodo ${periodo}</h3>
                <div class="promedio-general" style="font-size: 1.2em; color: #2c3e50;">
                    <strong>Promedio General:</strong> <span style="color: #3498db; font-weight: bold;">${promedioGeneral.toFixed(2)}</span>
                </div>
            </div>
        `;

        // Renderizar por categoría
        Object.keys(porCategoria).forEach(categoria => {
            const cals = porCategoria[categoria];
            const promedioCat = cals.reduce((sum, cal) =>
                sum + parseFloat(cal.valor), 0) / cals.length;

            html += `
                <div class="categoria-boletin" style="margin-bottom: 30px;">
                    <h4 style="color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; margin-bottom: 15px;">
                        ${categoria}
                        <span class="promedio-categoria" style="color: #7f8c8d; font-size: 0.9em; font-weight: normal;">
                            (Promedio: ${promedioCat.toFixed(2)})
                        </span>
                    </h4>
                    <table class="tabla-calificaciones" style="width: 100%; border-collapse: collapse; background: white;">
                        <thead>
                            <tr style="background: #ecf0f1;">
                                <th style="padding: 12px; text-align: left; border: 1px solid #ddd;">Logro</th>
                                <th style="padding: 12px; text-align: left; border: 1px solid #ddd;">Profesor</th>
                                <th style="padding: 12px; text-align: center; border: 1px solid #ddd; width: 120px;">Calificación</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${cals.map(cal => `
                                <tr>
                                    <td style="padding: 10px; border: 1px solid #ddd;">${cal.logroNombre}</td>
                                    <td style="padding: 10px; border: 1px solid #ddd;">${cal.profesorNombre}</td>
                                    <td class="calificacion-valor" style="padding: 10px; border: 1px solid #ddd; text-align: center; font-weight: bold; color: ${this.getColorCalificacion(cal.valor)};">
                                        ${parseFloat(cal.valor).toFixed(1)}
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        });

        boletinContent.innerHTML = html;
    }

    volverADatos() {
        if (this.selectedEstudianteId) {
            const hijo = this.hijos.find(h => h.id === this.selectedEstudianteId);
            if (hijo) {
                this.mostrarDatosEstudiante(hijo);
            }
        }
    }

    // ========== UTILIDADES ==========
    getColorCalificacion(valor) {
        const val = parseFloat(valor);
        if (val >= 4.5) return '#27ae60'; // Verde - Excelente
        if (val >= 4.0) return '#2ecc71'; // Verde claro - Muy bien
        if (val >= 3.5) return '#f39c12'; // Naranja - Bien
        if (val >= 3.0) return '#e67e22'; // Naranja oscuro - Aceptable
        return '#e74c3c'; // Rojo - Bajo
    }

    formatearFecha(fechaStr) {
        const fecha = new Date(fechaStr);
        const opciones = {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        };
        return fecha.toLocaleDateString('es-ES', opciones);
    }

    mostrarExito(mensaje) {
        this.mostrarNotificacion(mensaje, 'success');
    }

    mostrarError(mensaje) {
        this.mostrarNotificacion(mensaje, 'error');
    }

    mostrarNotificacion(mensaje, tipo) {
        const notificacion = document.createElement('div');
        notificacion.className = `notification ${tipo}`;
        notificacion.textContent = mensaje;

        notificacion.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            background-color: ${tipo === 'success' ? '#27ae60' : '#e74c3c'};
            color: white;
            border-radius: 4px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.2);
            z-index: 1000;
            animation: slideIn 0.3s ease-out;
        `;

        document.body.appendChild(notificacion);

        setTimeout(() => {
            notificacion.style.animation = 'slideOut 0.3s ease-out';
            setTimeout(() => notificacion.remove(), 300);
        }, 3000);
    }

    cerrarSesion() {
        if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
            sessionStorage.removeItem('userId');
            sessionStorage.removeItem('username');
            sessionStorage.removeItem('rol');
            sessionStorage.removeItem('profesorId');
            sessionStorage.removeItem('acudienteId');

            window.location.href = '/';
        }
    }
}

// Inicializar cuando el DOM esté listo
let gestionAcudiente;
document.addEventListener('DOMContentLoaded', () => {
    gestionAcudiente = new GestionAcudiente();

    // Event delegation para botones dinámicos
    document.addEventListener('click', (e) => {
        if (e.target.id === 'btnVerObservador') {
            e.preventDefault();
            gestionAcudiente.verObservador();
        } else if (e.target.id === 'btnVerBoletines') {
            e.preventDefault();
            gestionAcudiente.verBoletines();
        }
    });
});
