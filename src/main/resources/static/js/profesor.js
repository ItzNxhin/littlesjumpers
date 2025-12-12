class GestionAcademicaProfesor {

    constructor() {
        this.estudiantes = [];
        this.logros = [];
        this.selectedEstudianteId = null;
        this.profesorId = null; // Se debe obtener de la sesión
        this.init();
    }

    init() {
        // Obtener el ID del profesor de la sesión (puedes ajustar esto según tu implementación)
        this.obtenerProfesorId();

        // Cargar datos iniciales
        this.cargarLogros();
        this.cargarEstudiantes();

        // Configurar event listeners
        this.setupEventListeners();
    }

    setupEventListeners() {
        // Event listeners para el menú
        document.querySelectorAll('.menu-item').forEach(item => {
            item.addEventListener('click', (e) => {
                e.preventDefault();
                const section = item.dataset.section;
                this.cambiarSeccion(section);
            });
        });

        // Event listener para cerrar sesión
        const btnCerrarSesion = document.getElementById('btnCerrarSesion');
        if (btnCerrarSesion) {
            btnCerrarSesion.addEventListener('click', () => this.cerrarSesion());
        }
    }

    obtenerProfesorId() {
        // Obtener el ID del profesor desde sessionStorage
        const profesorId = sessionStorage.getItem('profesorId');

        if (!profesorId) {
            this.mostrarError('No se encontró el ID del profesor en la sesión. Por favor inicie sesión nuevamente.');
            setTimeout(() => {
                window.location.href = '/';
            }, 2000);
            return;
        }

        this.profesorId = parseInt(profesorId);
    }

    cambiarSeccion(seccionId) {
        // Ocultar todas las secciones
        document.querySelectorAll('.section').forEach(sec => {
            sec.classList.remove('active');
        });

        // Mostrar la sección seleccionada
        const seccion = document.getElementById(seccionId);
        if (seccion) {
            seccion.classList.add('active');
        }

        // Actualizar menú activo
        document.querySelectorAll('.menu-item').forEach(item => {
            item.classList.remove('active');
        });
        document.querySelector(`[data-section="${seccionId}"]`)?.classList.add('active');

        // Cargar datos según la sección
        if (seccionId === 'estudiantes' && this.selectedEstudianteId) {
            this.mostrarDetalleEstudiante(this.selectedEstudianteId);
        } else if (seccionId === 'calificaciones') {
            this.cargarSeccionCalificaciones();
        } else if (seccionId === 'mis-cursos') {
            this.cargarSeccionBoletines();
        } else if (seccionId === 'perfil') {
            this.cargarSeccionObservador();
        }
    }

    // ========== OBTENER LISTA DE ESTUDIANTES ==========
    async cargarEstudiantes() {
        try {
            const url = `/api/profesor/${this.profesorId}/estudiantes`;

            const response = await fetch(url);
            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Error al cargar estudiantes');
            }

            this.estudiantes = await response.json();
            this.renderizarListaEstudiantes();
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar los estudiantes: ' + error.message);
        }
    }

    renderizarListaEstudiantes() {
        const tbody = document.querySelector('#estudiantes tbody');
        if (!tbody) return;

        if (this.estudiantes.length === 0) {
            tbody.innerHTML = '<tr><td colspan="5" class="empty-state">No hay estudiantes en tu grupo</td></tr>';
            return;
        }

        tbody.innerHTML = this.estudiantes.map(est => `
            <tr class="estudiante-row" data-estudiante-id="${est.id}">
                <td>${est.tarjeta_identidad || 'N/A'}</td>
                <td>${est.nombre} ${est.apellido}</td>
                <td>${est.grado_aplicado || 'N/A'}</td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="gestionProfesor.verCalificacionesEstudiante(${est.id})">
                        Ver
                    </button>
                </td>
                <td>
                    <button class="btn btn-sm btn-primary" onclick="gestionProfesor.seleccionarEstudiante(${est.id})">
                        Seleccionar
                    </button>
                </td>
            </tr>
        `).join('');
    }

    verListaEstudiantesFormal() {
        // Redirigir a la página de lista formal con parámetros
        const url = `/lista-estudiantes?tipo=profesor&usuarioId=${this.profesorId}`;
        window.open(url, '_blank');
    }

    seleccionarEstudiante(estudianteId) {
        this.selectedEstudianteId = estudianteId;

        // Marcar como seleccionado visualmente
        document.querySelectorAll('.estudiante-row').forEach(row => {
            row.classList.remove('selected');
        });
        document.querySelector(`[data-estudiante-id="${estudianteId}"]`)?.classList.add('selected');

        const estudiante = this.estudiantes.find(e => e.id === estudianteId);
        if (estudiante) {
            this.mostrarNotificacion(`Estudiante seleccionado: ${estudiante.nombre} ${estudiante.apellido}`);
        }
    }

    // ========== CARGAR LOGROS ==========
    async cargarLogros() {
        try {
            const response = await fetch('/api/logros');
            if (!response.ok) throw new Error('Error al cargar logros');

            this.logros = await response.json();
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar los logros');
        }
    }

    // ========== CALIFICAR ESTUDIANTE ==========
    async cargarSeccionCalificaciones() {
        if (!this.selectedEstudianteId) {
            this.mostrarError('Primero selecciona un estudiante');
            return;
        }

        const estudiante = this.estudiantes.find(e => e.id === this.selectedEstudianteId);
        if (!estudiante) return;

        // Crear el formulario de calificación
        const seccion = document.getElementById('calificaciones');
        seccion.innerHTML = `
            <h2>Calificar Estudiante</h2>
            <div class="profile-card">
                <div class="profile-header">
                    <div class="profile-avatar">👨‍🎓</div>
                    <div class="profile-info">
                        <h3>${estudiante.nombre} ${estudiante.apellido}</h3>
                        <p>Grado: ${estudiante.grado_aplicado}</p>
                    </div>
                </div>
            </div>

            <div class="card" style="margin-top: 20px;">
                <h3>Nueva Calificación</h3>
                <form id="formCalificar" style="max-width: 600px;">
                    <div class="form-group">
                        <label for="selectLogro">Logro *</label>
                        <select id="selectLogro" class="form-control" required>
                            <option value="">Seleccione un logro...</option>
                            ${this.logros.map(logro => `
                                <option value="${logro.id}">
                                    ${logro.nombre} (${logro.categoria})
                                </option>
                            `).join('')}
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="selectPeriodo">Periodo *</label>
                        <select id="selectPeriodo" class="form-control" required>
                            <option value="">Seleccione el periodo...</option>
                            <option value="1">Periodo 1</option>
                            <option value="2">Periodo 2</option>
                            <option value="3">Periodo 3</option>
                            <option value="4">Periodo 4</option>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="inputValor">Calificación (0.0 - 5.0) *</label>
                        <input type="number" id="inputValor" class="form-control"
                               min="0" max="5" step="0.1" required>
                        <small>Ingrese un valor entre 0.0 y 5.0</small>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">Guardar Calificación</button>
                        <button type="button" class="btn btn-secondary" onclick="gestionProfesor.cambiarSeccion('estudiantes')">
                            Cancelar
                        </button>
                    </div>
                </form>
            </div>

            <div id="historialCalificaciones" style="margin-top: 30px;">
                <h3>Historial de Calificaciones</h3>
                <div id="listaCalificaciones"></div>
            </div>
        `;

        // Configurar event listener del formulario
        document.getElementById('formCalificar').addEventListener('submit', (e) => {
            e.preventDefault();
            this.guardarCalificacion();
        });

        // Cargar historial de calificaciones
        this.cargarHistorialCalificaciones(this.selectedEstudianteId);
    }

    async guardarCalificacion() {
        try {
            const logroId = parseInt(document.getElementById('selectLogro').value);
            const periodo = parseInt(document.getElementById('selectPeriodo').value);
            const valor = parseFloat(document.getElementById('inputValor').value);

            if (!logroId || !periodo || isNaN(valor)) {
                this.mostrarError('Complete todos los campos obligatorios');
                return;
            }

            if (valor < 0 || valor > 5) {
                this.mostrarError('La calificación debe estar entre 0 y 5');
                return;
            }

            const data = {
                estudianteId: this.selectedEstudianteId,
                logroId: logroId,
                periodo: periodo,
                valor: valor
            };

            const response = await fetch(`/api/profesor/${this.profesorId}/calificar`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Error al guardar calificación');
            }

            const calificacion = await response.json();

            this.mostrarExito('Calificación guardada correctamente');

            // Limpiar formulario
            document.getElementById('formCalificar').reset();

            // Recargar historial
            this.cargarHistorialCalificaciones(this.selectedEstudianteId);

        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al guardar calificación: ' + error.message);
        }
    }

    // ========== VER CALIFICACIONES DE UN ESTUDIANTE ==========
    async verCalificacionesEstudiante(estudianteId) {
        this.selectedEstudianteId = estudianteId;
        this.cambiarSeccion('calificaciones');
    }

    async cargarHistorialCalificaciones(estudianteId) {
        try {
            const response = await fetch(`/api/profesor/estudiantes/${estudianteId}/calificaciones`);
            if (!response.ok) throw new Error('Error al cargar calificaciones');

            const calificaciones = await response.json();
            this.renderizarHistorialCalificaciones(calificaciones);

        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar el historial de calificaciones');
        }
    }

    renderizarHistorialCalificaciones(calificaciones) {
        const container = document.getElementById('listaCalificaciones');
        if (!container) return;

        if (calificaciones.length === 0) {
            container.innerHTML = '<p class="empty-state">No hay calificaciones registradas</p>';
            return;
        }

        container.innerHTML = `
            <table class="data-table">
                <thead>
                    <tr>
                        <th>Periodo</th>
                        <th>Logro</th>
                        <th>Categoría</th>
                        <th>Calificación</th>
                        <th>Profesor</th>
                    </tr>
                </thead>
                <tbody>
                    ${calificaciones.map(cal => `
                        <tr>
                            <td>Periodo ${cal.periodo}</td>
                            <td>${cal.logroNombre}</td>
                            <td>${cal.logroCategoria}</td>
                            <td><strong>${cal.valor}</strong></td>
                            <td>${cal.profesorNombre}</td>
                        </tr>
                    `).join('')}
                </tbody>
            </table>
        `;
    }

    // ========== VER BOLETINES (HISTÓRICO DE CALIFICACIONES EN UN PERIODO) ==========
    async cargarSeccionBoletines() {
        const seccion = document.getElementById('mis-cursos');

        seccion.innerHTML = `
            <h2>Boletines - Histórico de Calificaciones</h2>
            <p class="section-description">Consulta las calificaciones de todos los estudiantes del grupo</p>

            <div class="actions-bar">
                <select id="filtroPeriodo" class="filter-select">
                    <option value="">Todos los periodos</option>
                    <option value="1">Periodo 1</option>
                    <option value="2">Periodo 2</option>
                    <option value="3">Periodo 3</option>
                    <option value="4">Periodo 4</option>
                </select>
                <button class="btn btn-primary" onclick="gestionProfesor.filtrarBoletines()">
                    Filtrar
                </button>
                <button class="btn btn-secondary" onclick="gestionProfesor.exportarBoletines()">
                    📥 Exportar CSV
                </button>
            </div>

            <div id="contenedorBoletines"></div>
        `;

        this.cargarTodasCalificaciones();
    }

    async cargarTodasCalificaciones() {
        try {
            const response = await fetch(`/api/profesor/${this.profesorId}/calificaciones`);
            if (!response.ok) throw new Error('Error al cargar calificaciones');

            const calificaciones = await response.json();
            this.renderizarBoletines(calificaciones);

        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar los boletines');
        }
    }

    renderizarBoletines(calificaciones) {
        const container = document.getElementById('contenedorBoletines');
        if (!container) return;

        if (calificaciones.length === 0) {
            container.innerHTML = '<p class="empty-state">No hay calificaciones registradas</p>';
            return;
        }

        // Agrupar por estudiante
        const porEstudiante = {};
        calificaciones.forEach(cal => {
            if (!porEstudiante[cal.estudianteId]) {
                porEstudiante[cal.estudianteId] = {
                    nombre: cal.estudianteNombre,
                    calificaciones: [],
                    periodos: new Set()
                };
            }
            porEstudiante[cal.estudianteId].calificaciones.push(cal);
            porEstudiante[cal.estudianteId].periodos.add(cal.periodo);
        });

        // Crear botones de boletín formal por estudiante y periodo
        let botonesBoletinHTML = '';
        Object.keys(porEstudiante).forEach(estudianteId => {
            const estudiante = porEstudiante[estudianteId];
            const periodosArray = Array.from(estudiante.periodos).sort();

            botonesBoletinHTML += `
                <div class="student-bulletin-section" style="margin-bottom: 20px; padding: 15px; background: #f8f9fa; border-radius: 8px;">
                    <h4 style="margin: 0 0 10px 0; color: #2c3e50;">${estudiante.nombre}</h4>
                    <div style="display: flex; gap: 10px; flex-wrap: wrap;">
                        ${periodosArray.map(periodo => `
                            <button class="btn btn-primary"
                                    onclick="gestionProfesor.verBoletinFormal(${estudianteId}, ${periodo})"
                                    style="padding: 8px 16px;">
                                📄 Ver Boletín Periodo ${periodo}
                            </button>
                        `).join('')}
                    </div>
                </div>
            `;
        });

        container.innerHTML = `
            <h3 style="margin-bottom: 15px;">Boletines Formales por Estudiante</h3>
            ${botonesBoletinHTML}

            <h3 style="margin: 30px 0 15px 0;">Detalle de Calificaciones</h3>
            <div class="table-container">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Estudiante</th>
                            <th>Periodo</th>
                            <th>Logro</th>
                            <th>Categoría</th>
                            <th>Calificación</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${calificaciones.map(cal => `
                            <tr>
                                <td>${cal.estudianteNombre}</td>
                                <td>Periodo ${cal.periodo}</td>
                                <td>${cal.logroNombre}</td>
                                <td>${cal.logroCategoria}</td>
                                <td><strong>${cal.valor}</strong></td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;
    }

    verBoletinFormal(estudianteId, periodo) {
        // Redirigir a la página del boletín formal con parámetros
        const url = `/boletin?estudianteId=${estudianteId}&periodo=${periodo}&tipo=profesor&usuarioId=${this.profesorId}`;
        window.open(url, '_blank');
    }

    filtrarBoletines() {
        const periodo = document.getElementById('filtroPeriodo').value;

        // Si no hay filtro, cargar todo
        if (!periodo) {
            this.cargarTodasCalificaciones();
            return;
        }

        // Filtrar por periodo (implementación simple del lado del cliente)
        fetch(`/api/profesor/${this.profesorId}/calificaciones`)
            .then(response => response.json())
            .then(calificaciones => {
                const filtradas = calificaciones.filter(cal => cal.periodo === parseInt(periodo));
                this.renderizarBoletines(filtradas);
            })
            .catch(error => {
                console.error('Error:', error);
                this.mostrarError('Error al filtrar boletines');
            });
    }

    exportarBoletines() {
        fetch(`/api/profesor/${this.profesorId}/calificaciones`)
            .then(response => response.json())
            .then(calificaciones => {
                let csv = 'Estudiante,Periodo,Logro,Categoría,Calificación\n';

                calificaciones.forEach(cal => {
                    csv += `"${cal.estudianteNombre}",${cal.periodo},"${cal.logroNombre}","${cal.logroCategoria}",${cal.valor}\n`;
                });

                const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
                const link = document.createElement('a');
                link.href = URL.createObjectURL(blob);
                link.download = `Boletines_${new Date().toISOString().split('T')[0]}.csv`;
                link.click();

                this.mostrarExito('Boletines exportados correctamente');
            })
            .catch(error => {
                console.error('Error:', error);
                this.mostrarError('Error al exportar boletines');
            });
    }

    // ========== OBSERVADOR ==========
    cargarSeccionObservador() {
        if (!this.selectedEstudianteId) {
            this.mostrarError('Primero selecciona un estudiante');
            this.cambiarSeccion('estudiantes');
            return;
        }

        const estudiante = this.estudiantes.find(e => e.id === this.selectedEstudianteId);
        if (!estudiante) return;

        const seccion = document.getElementById('perfil');

        seccion.innerHTML = `
            <div class="observador-container">
                <div class="observador-header">
                    <h2>📋 Observador del Estudiante</h2>
                    <p class="observador-subtitle">Registra y gestiona observaciones sobre el comportamiento</p>
                </div>

                <!-- Tarjeta de Perfil del Estudiante -->
                <div class="estudiante-card">
                    <div class="estudiante-card-header">
                        <div class="estudiante-avatar">👨‍🎓</div>
                        <div class="estudiante-card-info">
                            <h3>${estudiante.nombre} ${estudiante.apellido}</h3>
                            <div class="estudiante-details">
                                <span class="detail-badge">
                                    <strong>Código:</strong> ${estudiante.tarjeta_identidad || 'N/A'}
                                </span>
                                <span class="detail-badge">
                                    <strong>Grado:</strong> ${estudiante.grado_aplicado || 'N/A'}
                                </span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Formulario Nueva Observación -->
                <div class="observador-form-section">
                    <div class="section-header">
                        <h3>✍️ Nueva Observación</h3>
                        <p>Añade una nueva observación sobre el comportamiento del estudiante</p>
                    </div>
                    <form id="formObservacion" class="observador-form">
                        <div class="form-group">
                            <label for="textoObservacion">Observación sobre el comportamiento *</label>
                            <textarea id="textoObservacion" class="form-control form-textarea-lg"
                                      placeholder="Escriba aquí sus observaciones... (máximo 500 caracteres)"
                                      maxlength="500"
                                      required></textarea>
                            <small class="char-counter"><span id="charCount">0</span>/500</small>
                        </div>

                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary btn-lg">
                                ✓ Guardar Observación
                            </button>
                            <button type="button" class="btn btn-secondary btn-lg" onclick="gestionProfesor.cambiarSeccion('estudiantes')">
                                ✕ Volver
                            </button>
                        </div>
                    </form>
                </div>

                <!-- Historial de Observaciones -->
                <div class="observador-history-section">
                    <div class="section-header">
                        <h3>📚 Historial de Observaciones</h3>
                        <p>Todas las observaciones registradas para este estudiante</p>
                    </div>
                    <div id="listaObservaciones" class="observations-container"></div>
                </div>
            </div>
        `;

        // Contador de caracteres
        const textarea = document.getElementById('textoObservacion');
        textarea.addEventListener('input', () => {
            document.getElementById('charCount').textContent = textarea.value.length;
        });

        // Configurar event listener del formulario
        document.getElementById('formObservacion').addEventListener('submit', (e) => {
            e.preventDefault();
            this.guardarObservacion();
        });

        // Cargar historial de observaciones
        this.cargarObservaciones(this.selectedEstudianteId);
    }

    async guardarObservacion() {
        try {
            const texto = document.getElementById('textoObservacion').value.trim();

            if (!texto) {
                this.mostrarError('Debe escribir una observación');
                return;
            }

            const data = {
                estudianteId: this.selectedEstudianteId,
                profesorId: this.profesorId,
                texto: texto
            };

            const response = await fetch(`/api/profesor/${this.profesorId}/observacion`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Error al guardar observación');
            }

            this.mostrarExito('Observación guardada correctamente');

            // Limpiar formulario
            document.getElementById('formObservacion').reset();

            // Recargar historial
            this.cargarObservaciones(this.selectedEstudianteId);

        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al guardar observación: ' + error.message);
        }
    }

    async cargarObservaciones(estudianteId) {
        try {
            const response = await fetch(`/api/profesor/estudiantes/${estudianteId}/observaciones`);
            if (!response.ok) throw new Error('Error al cargar observaciones');

            const observaciones = await response.json();
            this.renderizarObservaciones(observaciones);

        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar las observaciones');
        }
    }

    renderizarObservaciones(observaciones) {
        const container = document.getElementById('listaObservaciones');
        if (!container) return;

        if (observaciones.length === 0) {
            container.innerHTML = `
                <div class="empty-observations">
                    <div class="empty-icon">📝</div>
                    <p>No hay observaciones registradas</p>
                    <small>Las observaciones que registres aparecerán aquí</small>
                </div>
            `;
            return;
        }

        container.innerHTML = `
            <div class="observations-list">
                ${observaciones.map((obs, index) => `
                    <div class="observation-item" data-observacion-id="${obs.id}">
                        <div class="observation-header">
                            <div class="observation-number">
                                <span class="obs-badge">#${observaciones.length - index}</span>
                            </div>
                            <div class="observation-meta">
                                <span class="obs-date">📅 ${new Date(obs.fecha).toLocaleDateString('es-CO', {
                                    year: 'numeric',
                                    month: 'long',
                                    day: 'numeric'
                                })}</span>
                                <span class="obs-time">⏰ ${new Date(obs.fecha).toLocaleTimeString('es-CO', {
                                    hour: '2-digit',
                                    minute: '2-digit'
                                })}</span>
                                <span class="obs-profesor">👨‍🏫 ${obs.profesorNombre}</span>
                            </div>
                            <div class="observation-actions">
                                <button class="btn-action btn-edit" onclick="gestionProfesor.editarObservacion(${obs.id}, '${obs.texto.replace(/'/g, "\\'")}')">
                                    ✏️
                                </button>
                                <button class="btn-action btn-delete" onclick="gestionProfesor.eliminarObservacion(${obs.id})">
                                    🗑️
                                </button>
                            </div>
                        </div>
                        <div class="observation-body">
                            <p class="obs-texto">${obs.texto}</p>
                        </div>
                    </div>
                `).join('')}
            </div>
        `;
    }

    editarObservacion(observacionId, textoActual) {
        // Crear un modal o formulario para editar
        const formularioEdicion = `
            <div class="modal-overlay" id="modalEdicion" onclick="if(event.target === this) gestionProfesor.cerrarModalEdicion()">
                <div class="modal-content">
                    <div class="modal-header">
                        <h3>Editar Observación</h3>
                        <button class="btn-close" onclick="gestionProfesor.cerrarModalEdicion()">×</button>
                    </div>
                    <form id="formEditarObservacion" class="form-edicion">
                        <div class="form-group">
                            <label for="textoEdicion">Observación *</label>
                            <textarea id="textoEdicion" class="form-control" rows="4" required>${textoActual}</textarea>
                        </div>
                        <div class="form-actions">
                            <button type="submit" class="btn btn-primary">Guardar Cambios</button>
                            <button type="button" class="btn btn-secondary" onclick="gestionProfesor.cerrarModalEdicion()">
                                Cancelar
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        `;

        // Insertar el modal en el DOM
        document.body.insertAdjacentHTML('beforeend', formularioEdicion);

        // Configurar el event listener del formulario
        document.getElementById('formEditarObservacion').addEventListener('submit', async (e) => {
            e.preventDefault();
            const nuevoTexto = document.getElementById('textoEdicion').value;
            await this.guardarCambiosObservacion(observacionId, nuevoTexto);
        });

        // Almacenar el ID de la observación siendo editada
        this.observacionEditando = observacionId;
    }

    cerrarModalEdicion() {
        const modal = document.getElementById('modalEdicion');
        if (modal) {
            modal.remove();
        }
        this.observacionEditando = null;
    }

    async guardarCambiosObservacion(observacionId, nuevoTexto) {
        try {
            const response = await fetch(
                `/api/profesor/${this.profesorId}/observacion/${observacionId}`,
                {
                    method: 'PUT',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        estudianteId: this.selectedEstudianteId,
                        texto: nuevoTexto
                    })
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Error al actualizar la observación');
            }

            this.mostrarExito('Observación actualizada exitosamente');
            this.cerrarModalEdicion();

            // Recargar las observaciones
            this.cargarObservaciones(this.selectedEstudianteId);
        } catch (error) {
            this.mostrarError(error.message);
        }
    }

    async eliminarObservacion(observacionId) {
        if (!confirm('¿Está seguro de que desea eliminar esta observación? Esta acción no se puede deshacer.')) {
            return;
        }

        try {
            const response = await fetch(
                `/api/profesor/${this.profesorId}/observacion/${observacionId}`,
                {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                    }
                }
            );

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Error al eliminar la observación');
            }

            this.mostrarExito('Observación eliminada exitosamente');

            // Recargar las observaciones
            this.cargarObservaciones(this.selectedEstudianteId);
        } catch (error) {
            this.mostrarError(error.message);
        }
    }

    // ========== UTILIDADES ==========
    mostrarError(mensaje) {
        alert('Error: ' + mensaje);
    }

    mostrarExito(mensaje) {
        alert(mensaje);
    }

    mostrarNotificacion(mensaje) {
        console.log('Notificación:', mensaje);
        // Puedes implementar un sistema de notificaciones más sofisticado
    }

    cerrarSesion() {
        if (confirm('¿Estás seguro de que deseas cerrar sesión?')) {
            // Limpiar sessionStorage
            sessionStorage.removeItem('userId');
            sessionStorage.removeItem('username');
            sessionStorage.removeItem('rol');
            sessionStorage.removeItem('profesorId');
            sessionStorage.removeItem('acudienteId');

            // Redirigir a la página principal
            window.location.href = '/';
        }
    }
}

// Instancia global
let gestionProfesor;

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    gestionProfesor = new GestionAcademicaProfesor();
});