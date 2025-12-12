// gestion-usuarios.js - Versión inline (integrada en dashboard)
class GestionUsuarios {
    constructor() {
        this.currentTab = 'estudiantes';
        this.selectedGrupoId = null;
        this.selectedUsuarioId = null;
        this.selectedUsuarioType = null;
        this.grupos = [];
        this.estudiantes = [];
        this.profesores = [];
        this.acudientes = [];

        this.init();
    }

    init() {
        this.setupTabs();
    }

    setupTabs() {
        // Esperar a que el DOM esté listo
        setTimeout(() => {
            const tabButtons = document.querySelectorAll('.tab-btn-inline');
            if (tabButtons.length > 0) {
                tabButtons.forEach(btn => {
                    btn.addEventListener('click', (e) => {
                        const tab = e.target.dataset.tab;
                        this.cambiarTab(tab);
                    });
                });
            }
        }, 100);
    }

    // Método para inicializar cuando se activa la sección
    async activarSeccion() {
        await this.cargarDatos();
    }

    cambiarTab(tab) {
        // Actualizar botones
        document.querySelectorAll('.tab-btn-inline').forEach(btn => {
            btn.classList.remove('active');
            if (btn.dataset.tab === tab) {
                btn.classList.add('active');
            }
        });

        // Actualizar contenido
        document.querySelectorAll('.tab-content-inline').forEach(content => {
            content.classList.remove('active');
        });

        const tabContent = document.getElementById(`tab-${tab}-inline`);
        if (tabContent) {
            tabContent.classList.add('active');
        }

        this.currentTab = tab;
        this.limpiarSeleccion();
        this.cargarDatosTab(tab);

        const botones = [
        'btnConsultarEstudiante', 'btnModificarEstudiante', 'btnCambiarEstado',
        'btnConsultarProfesor', 'btnModificarProfesor', 'btnAsignarCuentaProfesor',
        'btnConsultarAcudiente', 'btnModificarAcudiente', 'btnAsignarCuentaAcudiente'
        ];
        botones.forEach(id => {
            const btn = document.getElementById(id);
            if (btn) btn.disabled = true;
        });
    }

    async cargarDatos() {
        await this.cargarDatosTab(this.currentTab);
    }

    async cargarDatosTab(tab) {
        try {
            switch(tab) {
                case 'estudiantes':
                    await this.cargarGrupos();
                    break;
                case 'profesores':
                    await this.cargarProfesores();
                    break;
                case 'acudientes':
                    await this.cargarAcudientes();
                    break;
            }
        } catch (error) {
            console.error('Error al cargar datos:', error);
            this.mostrarError('Error al cargar los datos');
        }
    }

    async cargarGrupos() {
        try {
            const response = await fetch('/api/gestion/grupos');
            if (!response.ok) throw new Error('Error al cargar grupos');

            this.grupos = await response.json();
            this.renderizarGrupos();
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar los grupos');
        }
    }

    renderizarGrupos() {
        const gruposList = document.getElementById('gruposList');
        if (!gruposList) return;

        const gruposHTML = `
            <div class="grupo-item sin-grupo" data-grupo-id="sin-grupo" onclick="gestionUsuarios.seleccionarGrupo('sin-grupo')">
                <div class="grupo-nombre">Sin Grupo</div>
                <div class="grupo-info">Estudiantes sin asignar</div>
            </div>
            ${this.grupos.map(grupo => `
                <div class="grupo-item" data-grupo-id="${grupo.id}" onclick="gestionUsuarios.seleccionarGrupo(${grupo.id})">
                    <div class="grupo-nombre">${grupo.identificador}</div>
                    <div class="grupo-info">Grado: ${this.formatearGrado(grupo.grado)}</div>
                    <div class="grupo-count">${grupo.estudiantes_count || 0} estudiantes</div>
                </div>
            `).join('')}
        `;

        gruposList.innerHTML = gruposHTML;
    }

    async seleccionarGrupo(grupoId) {
        // Actualizar UI
        document.querySelectorAll('.grupo-item').forEach(item => {
            item.classList.remove('active');
        });

        const grupoElement = document.querySelector(`[data-grupo-id="${grupoId}"]`);
        if (grupoElement) {
            grupoElement.classList.add('active');
        }

        this.selectedGrupoId = grupoId;
        this.limpiarSeleccionEstudiante();

        // Actualizar nombre del grupo
        const nombreGrupo = grupoId === 'sin-grupo'
            ? 'Sin Grupo'
            : this.grupos.find(g => g.id === grupoId)?.identificador || 'Grupo';

        const nombreElement = document.getElementById('grupoSeleccionadoNombre');
        if (nombreElement) {
            nombreElement.textContent = nombreGrupo;
        }

        // Cargar estudiantes del grupo
        await this.cargarEstudiantesGrupo(grupoId);
    }

    async cargarEstudiantesGrupo(grupoId) {
        try {
            let url;
            if (grupoId === 'sin-grupo') {
                url = '/api/gestion/estudiantes/sin-grupo';
            } else {
                url = '/api/gestion/estudiantes';
            }

            const response = await fetch(url);
            if (!response.ok) throw new Error('Error al cargar estudiantes');

            let estudiantes = await response.json();

            // Si no es "sin-grupo", filtrar por grupoId
            if (grupoId !== 'sin-grupo') {
                estudiantes = estudiantes.filter(e => e.grupo_id === grupoId);
            }

            this.estudiantes = estudiantes;
            this.renderizarEstudiantes();
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar los estudiantes');
        }
    }

    renderizarEstudiantes() {
        const estudiantesList = document.getElementById('estudiantesList');
        if (!estudiantesList) return;

        if (this.estudiantes.length === 0) {
            estudiantesList.innerHTML = `
                <div class="empty-state-inline">
                    <div class="empty-state-icon">📚</div>
                    <div class="empty-state-text">No hay estudiantes en este grupo</div>
                </div>
            `;
            return;
        }

        const estudiantesHTML = this.estudiantes.map(estudiante => `
            <div class="usuario-card" data-usuario-id="${estudiante.id}" onclick="gestionUsuarios.seleccionarEstudiante(${estudiante.id})">
                <div class="usuario-nombre">${estudiante.nombre} ${estudiante.apellido}</div>
                <div class="usuario-info">
                    <div class="usuario-info-item">📋 ${estudiante.tarjeta_identidad}</div>
                    <div class="usuario-info-item">📅 ${this.formatearFecha(estudiante.fecha_nacimiento)}</div>
                    <div class="usuario-info-item">🎓 Grado: ${this.formatearGrado(estudiante.grado_aplicado)}</div>
                </div>
                <span class="usuario-estado estado-${estudiante.estado}">${this.formatearEstado(estudiante.estado)}</span>
            </div>
        `).join('');

        estudiantesList.innerHTML = estudiantesHTML;
    }

    seleccionarEstudiante(estudianteId) {
        // Actualizar UI
        document.querySelectorAll('#estudiantesList .usuario-card').forEach(card => {
            card.classList.remove('selected');
        });

        const estudianteCard = document.querySelector(`#estudiantesList [data-usuario-id="${estudianteId}"]`);
        if (estudianteCard) {
            estudianteCard.classList.add('selected');
        }

        this.selectedUsuarioId = estudianteId;
        this.selectedUsuarioType = 'estudiante';

        // Habilitar botones de acción
        const btnConsultar = document.getElementById('btnConsultarEstudiante');
        const btnModificar = document.getElementById('btnModificarEstudiante');
        const btnCambiarEstado = document.getElementById('btnCambiarEstado');
        const btnDescargar = document.getElementById('btnDescargarLista');

        if (btnConsultar) btnConsultar.disabled = false;
        if (btnModificar) btnModificar.disabled = false;
        if (btnCambiarEstado) btnCambiarEstado.disabled = false;
        if (btnDescargar) btnDescargar.disabled = false;
    }

    actualizarEstudiantes(grupo){
        this.cargarEstudiantesGrupo(grupo)
        this.renderizarEstudiantes()
    }

    limpiarSeleccionEstudiante() {
        this.selectedUsuarioId = null;
        this.selectedUsuarioType = null;

        document.querySelectorAll('#estudiantesList .usuario-card').forEach(card => {
            card.classList.remove('selected');
        });

        const btnConsultar = document.getElementById('btnConsultarEstudiante');
        const btnModificar = document.getElementById('btnModificarEstudiante');
        const btnCambiarEstado = document.getElementById('btnCambiarEstado');
        const btnDescargar = document.getElementById('btnDescargarLista');

        if (btnConsultar) btnConsultar.disabled = true;
        if (btnModificar) btnModificar.disabled = true;
        if (btnCambiarEstado) btnCambiarEstado.disabled = true;
    }

    async cargarProfesores() {
        try {
            const response = await fetch('/api/gestion/profesores');
            if (!response.ok) throw new Error('Error al cargar profesores');

            this.profesores = await response.json();
            this.renderizarProfesores();
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar los profesores');
        }
    }

    renderizarProfesores() {
        const profesoresList = document.getElementById('profesoresList');
        if (!profesoresList) return;

        if (this.profesores.length === 0) {
            profesoresList.innerHTML = `
                <div class="empty-state-inline">
                    <div class="empty-state-icon">👨‍🏫</div>
                    <div class="empty-state-text">No hay profesores registrados</div>
                </div>
            `;
            return;
        }

        const profesoresHTML = this.profesores.map(profesor => `
            <div class="usuario-card" data-usuario-id="${profesor.id}">
                <div class="usuario-nombre">${profesor.nombre} ${profesor.apellido}</div>
                <div class="usuario-info">
                    <div class="usuario-info-item">📋 ${profesor.cedula}</div>
                    <div class="usuario-info-item">📧 ${profesor.correo || 'Sin correo'}</div>
                    <div class="usuario-info-item">🎓 TP: ${profesor.tarjeta_profesional || 'N/A'}</div>
                </div>
            </div>
        `).join('');

        profesoresList.innerHTML = profesoresHTML;

        // Agregar event listeners después de renderizar
        document.querySelectorAll('#profesoresList .usuario-card').forEach(card => {
            // Read attribute directly (more robust than dataset in some environments)
            const idAttr = card.getAttribute('data-usuario-id');
            const profesorId = parseInt(idAttr, 10);
            card.addEventListener('click', (e) => {
                this.seleccionarProfesor(profesorId, e);
            });
            // Accessibility: allow selecting by keyboard (Enter)
            card.setAttribute('tabindex', '0');
            card.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    this.seleccionarProfesor(profesorId, e);
                }
            });
        });
    }

    seleccionarProfesor(profesorId, event) {
        // Prevenir propagación de eventos
        if (event) event.stopPropagation();

        document.querySelectorAll('#profesoresList .usuario-card').forEach(card => {
            card.classList.remove('selected');
        });

        const profesorCard = document.querySelector(`#profesoresList [data-usuario-id="${profesorId}"]`);
        if (profesorCard) {
            profesorCard.classList.add('selected');
        }

        this.selectedUsuarioId = profesorId;
        this.selectedUsuarioType = 'profesor';

        const btnConsultar = document.getElementById('btnConsultarProfesor');
        const btnModificar = document.getElementById('btnModificarProfesor');
        const btnAsignarCuenta = document.getElementById('btnAsignarCuentaProfesor');

        if (btnConsultar) btnConsultar.disabled = false;
        if (btnModificar) btnModificar.disabled = false;
        if (btnAsignarCuenta) btnAsignarCuenta.disabled = false;
    }

    limpiarSeleccionProfesor() {
        this.selectedUsuarioId = null;
        this.selectedUsuarioType = null;

        document.querySelectorAll('#profesoresList .usuario-card').forEach(card => {
            card.classList.remove('selected');
        });

        const btnConsultar = document.getElementById('btnConsultarProfesor');
        const btnModificar = document.getElementById('btnModificarProfesor');
        const btnAsignarCuenta = document.getElementById('btnAsignarCuentaProfesor');

        if (btnConsultar) btnConsultar.disabled = true;
        if (btnModificar) btnModificar.disabled = true;
        if (btnAsignarCuenta) btnAsignarCuenta.disabled = true;
    }

    async cargarAcudientes() {
        try {
            const response = await fetch('/api/gestion/acudientes');
            if (!response.ok) throw new Error('Error al cargar acudientes');

            this.acudientes = await response.json();
            this.renderizarAcudientes();
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar los acudientes');
        }
    }

    renderizarAcudientes() {
        const acudientesList = document.getElementById('acudientesList');
        if (!acudientesList) return;

        if (this.acudientes.length === 0) {
            acudientesList.innerHTML = `
                <div class="empty-state-inline">
                    <div class="empty-state-icon">👪</div>
                    <div class="empty-state-text">No hay acudientes registrados</div>
                </div>
            `;
            return;
        }

        const acudientesHTML = this.acudientes.map(acudiente => `
            <div class="usuario-card" data-usuario-id="${acudiente.id}">
                <div class="usuario-nombre">${acudiente.nombre} ${acudiente.apellido}</div>
                <div class="usuario-info">
                    <div class="usuario-info-item">📋 ${acudiente.cedula}</div>
                    <div class="usuario-info-item">📧 ${acudiente.correo || 'Sin correo'}</div>
                    <div class="usuario-info-item">📱 ${acudiente.contacto_extra || 'Sin contacto'}</div>
                </div>
            </div>
        `).join('');

        acudientesList.innerHTML = acudientesHTML;

        // Agregar event listeners después de renderizar
        document.querySelectorAll('#acudientesList .usuario-card').forEach(card => {
            // Read attribute directly for reliability
            const idAttr = card.getAttribute('data-usuario-id');
            const acudienteId = parseInt(idAttr, 10);
            card.addEventListener('click', (e) => {
                this.seleccionarAcudiente(acudienteId, e);
            });
            // Accessibility: support Enter / Space selection
            card.setAttribute('tabindex', '0');
            card.addEventListener('keydown', (e) => {
                if (e.key === 'Enter' || e.key === ' ') {
                    e.preventDefault();
                    this.seleccionarAcudiente(acudienteId, e);
                }
            });
        });
    }

    seleccionarAcudiente(acudienteId, event) {
        // Prevenir propagación de eventos
        if (event) event.stopPropagation();
        //this.limpiarSeleccionAcudiente();
        document.querySelectorAll('#acudientesList .usuario-card').forEach(card => {
            card.classList.remove('selected');
        });

        const acudienteCard = document.querySelector(`#acudientesList [data-usuario-id="${acudienteId}"]`);
        if (acudienteCard) {
            acudienteCard.classList.add('selected');
        }

        this.selectedUsuarioId = acudienteId;
        this.selectedUsuarioType = 'acudiente';

        const btnConsultar = document.getElementById('btnConsultarAcudiente');
        const btnModificar = document.getElementById('btnModificarAcudiente');
        const btnAsignarCuenta = document.getElementById('btnAsignarCuentaAcudiente');

        if (btnConsultar) btnConsultar.disabled = false;
        if (btnModificar) btnModificar.disabled = false;
        if (btnAsignarCuenta) btnAsignarCuenta.disabled = false;
    }

    limpiarSeleccionAcudiente() {
        this.selectedUsuarioId = null;
        this.selectedUsuarioType = null;

        document.querySelectorAll('#acudientesList .usuario-card').forEach(card => {
            card.classList.remove('selected');
        });

        const btnConsultar = document.getElementById('btnConsultarAcudiente');
        const btnModificar = document.getElementById('btnModificarAcudiente');
        const btnAsignarCuenta = document.getElementById('btnAsignarCuentaAcudiente');

        if (btnConsultar) btnConsultar.disabled = true;
        if (btnModificar) btnModificar.disabled = true;
        if (btnAsignarCuenta) btnAsignarCuenta.disabled = true;
    }

    limpiarSeleccion() {
        this.selectedGrupoId = null;
        this.selectedUsuarioId = null;
        this.selectedUsuarioType = null;
    }

    // Métodos de utilidad
    formatearGrado(grado) {
        const grados = {
            'parvulos': 'Párvulos',
            'caminadores': 'Caminadores',
            'pre_jardin': 'Pre-jardín'
        };
        return grados[grado] || grado;
    }

    formatearEstado(estado) {
        const estados = {
            'aspirante': 'Aspirante',
            'aceptado': 'Aceptado',
            'rechazado': 'Rechazado'
        };
        return estados[estado] || estado;
    }

    formatearFecha(fecha) {
        if (!fecha) return 'N/A';
        const date = new Date(fecha);
        return date.toLocaleDateString('es-ES');
    }

    mostrarError(mensaje) {
        alert(mensaje);
    }

    mostrarExito(mensaje) {
        alert(mensaje);
    }

    // ========== MÉTODOS PARA CONSULTAR ==========
    async consultarEstudiante() {
        if (!this.selectedUsuarioId) return;

        try {
            const response = await fetch(`/api/gestion/estudiantes/${this.selectedUsuarioId}`);
            if (!response.ok) throw new Error('Error al obtener estudiante');

            const estudiante = await response.json();

            document.getElementById('modalConsultarTitulo').textContent = 'Detalles del Estudiante';
            document.getElementById('modalConsultarBody').innerHTML = `
                <div class="info-grid">
                    <div class="info-item">
                        <div class="info-item-label">Nombre Completo</div>
                        <div class="info-item-value">${estudiante.nombre} ${estudiante.apellido}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-item-label">Tarjeta de Identidad</div>
                        <div class="info-item-value">${estudiante.tarjeta_identidad}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-item-label">Fecha de Nacimiento</div>
                        <div class="info-item-value">${this.formatearFecha(estudiante.fecha_nacimiento)}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-item-label">Grado Aplicado</div>
                        <div class="info-item-value">${this.formatearGrado(estudiante.grado_aplicado)}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-item-label">Estado</div>
                        <div class="info-item-value">${this.formatearEstado(estudiante.estado)}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-item-label">Grupo</div>
                        <div class="info-item-value">${estudiante.grupo_nombre || 'Sin grupo asignado'}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-item-label">Acudiente</div>
                        <div class="info-item-value">${estudiante.acudiente_nombre || 'N/A'}</div>
                    </div>
                </div>
                <div id="hv_container" style="margin-top:18px;"></div>
                <div style="margin-top:12px; display:flex; gap:8px;">
                    <button class="btn btn-secondary btn-sm" onclick="gestionUsuarios.cerrarModalConsultar()">Cerrar</button>
                </div>
            `;

            document.getElementById('modalConsultar').classList.add('active');

            // Cargar e incrustar la Hoja de Vida directamente en el modal
            (async () => {
                try {
                    const r = await fetch(`/api/gestion/estudiantes/${estudiante.id}/hoja-vida`);
                    if (!r.ok) return; // no mostrar si falla
                    const hv = await r.json();
                    const hvHtml = `
                        <div class="section-header" style="margin-top:8px;"><h4>Hoja de Vida</h4></div>
                        <div class="info-grid">
                            <div class="info-item">
                                <div class="info-item-label">Estado de Salud</div>
                                <div class="info-item-value">${hv.estadoSalud || 'No registrado'}</div>
                            </div>
                            <div class="info-item">
                                <div class="info-item-label">Alergias</div>
                                <div class="info-item-value">${hv.alergias || 'No registra'}</div>
                            </div>
                            <div class="info-item" style="grid-column: 1 / -1;">
                                <div class="info-item-label">Notas de Aprendizaje</div>
                                <div class="info-item-value">${hv.notasAprendizaje || 'Sin notas'}</div>
                            </div>
                        </div>
                    `;
                    const container = document.getElementById('hv_container');
                    if (container) container.innerHTML = hvHtml;
                } catch (err) {
                    console.warn('No se pudo cargar Hoja de Vida inline', err);
                }
            })();
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar los datos del estudiante');
        }
    }

    async consultarProfesor() {
        if (!this.selectedUsuarioId) return;

        try {
            const response = await fetch(`/api/gestion/profesores/${this.selectedUsuarioId}`);
            if (!response.ok) throw new Error('Error al obtener profesor');

            const profesor = await response.json();

            document.getElementById('modalConsultarTitulo').textContent = 'Detalles del Profesor';
            document.getElementById('modalConsultarBody').innerHTML = `
                <div class="info-grid">
                    <div class="info-item">
                        <div class="info-item-label">Nombre Completo</div>
                        <div class="info-item-value">${profesor.nombre} ${profesor.apellido}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-item-label">Cédula</div>
                        <div class="info-item-value">${profesor.cedula}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-item-label">Correo</div>
                        <div class="info-item-value">${profesor.correo || 'No registrado'}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-item-label">Tarjeta Profesional</div>
                        <div class="info-item-value">${profesor.tarjeta_profesional || 'No registrada'}</div>
                    </div>
                </div>
            `;

            document.getElementById('modalConsultar').classList.add('active');
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar los datos del profesor');
        }
    }

    async consultarAcudiente() {
        if (!this.selectedUsuarioId) return;

        try {
            const response = await fetch(`/api/gestion/acudientes/${this.selectedUsuarioId}`);
            if (!response.ok) throw new Error('Error al obtener acudiente');

            const acudiente = await response.json();

            document.getElementById('modalConsultarTitulo').textContent = 'Detalles del Acudiente';
            document.getElementById('modalConsultarBody').innerHTML = `
                <div class="info-grid">
                    <div class="info-item">
                        <div class="info-item-label">Nombre Completo</div>
                        <div class="info-item-value">${acudiente.nombre} ${acudiente.apellido}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-item-label">Cédula</div>
                        <div class="info-item-value">${acudiente.cedula}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-item-label">Correo</div>
                        <div class="info-item-value">${acudiente.correo || 'No registrado'}</div>
                    </div>
                    <div class="info-item">
                        <div class="info-item-label">Contacto Extra</div>
                        <div class="info-item-value">${acudiente.contacto_extra || 'No registrado'}</div>
                    </div>
                </div>
            `;

            document.getElementById('modalConsultar').classList.add('active');
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar los datos del acudiente');
        }
    }

    

    cerrarModalConsultar() {
        document.getElementById('modalConsultar').classList.remove('active');
    }

    // ========== MÉTODOS PARA MODIFICAR ==========
    async modificarEstudiante() {
        if (!this.selectedUsuarioId) return;

        try {
            const response = await fetch(`/api/gestion/estudiantes/${this.selectedUsuarioId}`);
            if (!response.ok) throw new Error('Error al obtener estudiante');

            const estudiante = await response.json();

            // Cargar grupos para el selector
            const gruposResponse = await fetch('/api/gestion/grupos');
            let grupos = [];
            if (gruposResponse.ok) {
                grupos = await gruposResponse.json();
            }

            // Filtrar grupos por el grado del estudiante
            const gruposDelGrado = grupos.filter(g => g.grado === estudiante.grado_aplicado);

            let gruposHTML = '<option value="">Sin grupo asignado</option>';
            gruposDelGrado.forEach(grupo => {
                const selected = estudiante.grupo_id === grupo.id ? 'selected' : '';
                gruposHTML += `<option value="${grupo.id}" ${selected}>${grupo.identificador} (${grupo.capacidad - grupo.estudiantes_count}/${grupo.capacidad} disponibles)</option>`;
            });

            document.getElementById('modalModificarTitulo').textContent = 'Modificar Estudiante';
            document.getElementById('formModificar').innerHTML = `
                <div class="form-row">
                    <div class="form-group">
                        <label for="modNombre">Nombre *</label>
                        <input type="text" id="modNombre" class="form-control" value="${estudiante.nombre}" required>
                    </div>
                    <div class="form-group">
                        <label for="modApellido">Apellido *</label>
                        <input type="text" id="modApellido" class="form-control" value="${estudiante.apellido}" required>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label for="modTarjetaIdentidad">Tarjeta de Identidad *</label>
                        <input type="text" id="modTarjetaIdentidad" class="form-control" value="${estudiante.tarjeta_identidad}" required>
                    </div>
                    <div class="form-group">
                        <label for="modFechaNacimiento">Fecha de Nacimiento *</label>
                        <input type="date" id="modFechaNacimiento" class="form-control" value="${estudiante.fecha_nacimiento}" required>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label for="modGradoAplicado">Grado Aplicado *</label>
                        <select id="modGradoAplicado" class="form-control" required onchange="gestionUsuarios.actualizarGruposDisponibles(this.value)">
                            <option value="parvulos" ${estudiante.grado_aplicado === 'parvulos' ? 'selected' : ''}>Párvulos</option>
                            <option value="caminadores" ${estudiante.grado_aplicado === 'caminadores' ? 'selected' : ''}>Caminadores</option>
                            <option value="pre_jardin" ${estudiante.grado_aplicado === 'pre_jardin' ? 'selected' : ''}>Pre-jardín</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="modGrupo">Grupo</label>
                        <select id="modGrupo" class="form-control">
                            ${gruposHTML}
                        </select>
                    </div>
                </div>
            `;

            document.getElementById('modalModificar').classList.add('active');
            // Cargar Hoja de Vida y añadir campos al formulario de modificación (inline)
            (async () => {
                try {
                    const r = await fetch(`/api/gestion/estudiantes/${this.selectedUsuarioId}/hoja-vida`);
                    if (!r.ok) return;
                    const hv = await r.json();
                    const hvSection = `
                        <hr style="margin:18px 0; border: 1px solid #e9ecef;">
                        <h4>Hoja de Vida</h4>
                        <div class="form-group">
                            <label for="mod_hv_estadoSalud">Estado de Salud</label>
                            <textarea id="mod_hv_estadoSalud" class="form-control" rows="2">${hv.estadoSalud || ''}</textarea>
                        </div>
                        <div class="form-group">
                            <label for="mod_hv_alergias">Alergias</label>
                            <textarea id="mod_hv_alergias" class="form-control" rows="2">${hv.alergias || ''}</textarea>
                        </div>
                        <div class="form-group">
                            <label for="mod_hv_notas">Notas de Aprendizaje</label>
                            <textarea id="mod_hv_notas" class="form-control" rows="4">${hv.notasAprendizaje || ''}</textarea>
                        </div>
                    `;
                    const form = document.getElementById('formModificar');
                    if (form) form.insertAdjacentHTML('beforeend', hvSection);
                } catch (err) {
                    console.warn('No se pudo cargar Hoja de Vida para edición', err);
                }
            })();
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar los datos del estudiante');
        }
    }

    async actualizarGruposDisponibles(grado) {
        try {
            const gruposResponse = await fetch('/api/gestion/grupos');
            if (!gruposResponse.ok) return;

            const grupos = await gruposResponse.json();
            const gruposDelGrado = grupos.filter(g => g.grado === grado);

            const selectGrupo = document.getElementById('modGrupo');
            if (!selectGrupo) return;

            let gruposHTML = '<option value="">Sin grupo asignado</option>';
            gruposDelGrado.forEach(grupo => {
                gruposHTML += `<option value="${grupo.id}">${grupo.identificador} (${grupo.capacidad - grupo.estudiantes_count}/${grupo.capacidad} disponibles)</option>`;
            });

            selectGrupo.innerHTML = gruposHTML;
        } catch (error) {
            console.error('Error:', error);
        }
    }

    async modificarProfesor() {
        if (!this.selectedUsuarioId) return;

        try {
            const response = await fetch(`/api/gestion/profesores/${this.selectedUsuarioId}`);
            if (!response.ok) throw new Error('Error al obtener profesor');

            const profesor = await response.json();

            document.getElementById('modalModificarTitulo').textContent = 'Modificar Profesor';
            document.getElementById('formModificar').innerHTML = `
                <div class="form-row">
                    <div class="form-group">
                        <label for="modNombre">Nombre *</label>
                        <input type="text" id="modNombre" class="form-control" value="${profesor.nombre}" required>
                    </div>
                    <div class="form-group">
                        <label for="modApellido">Apellido *</label>
                        <input type="text" id="modApellido" class="form-control" value="${profesor.apellido}" required>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label for="modCedula">Cédula *</label>
                        <input type="text" id="modCedula" class="form-control" value="${profesor.cedula}" required>
                    </div>
                    <div class="form-group">
                        <label for="modCorreo">Correo</label>
                        <input type="email" id="modCorreo" class="form-control" value="${profesor.correo || ''}">
                    </div>
                </div>
                <div class="form-group">
                    <label for="modTarjetaProfesional">Tarjeta Profesional</label>
                    <input type="text" id="modTarjetaProfesional" class="form-control" value="${profesor.tarjeta_profesional || ''}">
                </div>
            `;

            document.getElementById('modalModificar').classList.add('active');
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar los datos del profesor');
        }
    }

    async modificarAcudiente() {
        if (!this.selectedUsuarioId) return;

        try {
            const response = await fetch(`/api/gestion/acudientes/${this.selectedUsuarioId}`);
            if (!response.ok) throw new Error('Error al obtener acudiente');

            const acudiente = await response.json();

            document.getElementById('modalModificarTitulo').textContent = 'Modificar Acudiente';
            document.getElementById('formModificar').innerHTML = `
                <div class="form-row">
                    <div class="form-group">
                        <label for="modNombre">Nombre *</label>
                        <input type="text" id="modNombre" class="form-control" value="${acudiente.nombre}" required>
                    </div>
                    <div class="form-group">
                        <label for="modApellido">Apellido *</label>
                        <input type="text" id="modApellido" class="form-control" value="${acudiente.apellido}" required>
                    </div>
                </div>
                <div class="form-row">
                    <div class="form-group">
                        <label for="modCedula">Cédula *</label>
                        <input type="text" id="modCedula" class="form-control" value="${acudiente.cedula}" required>
                    </div>
                    <div class="form-group">
                        <label for="modCorreo">Correo</label>
                        <input type="email" id="modCorreo" class="form-control" value="${acudiente.correo || ''}">
                    </div>
                </div>
                <div class="form-group">
                    <label for="modContactoExtra">Contacto Extra</label>
                    <input type="text" id="modContactoExtra" class="form-control" value="${acudiente.contacto_extra || ''}">
                </div>
            `;

            document.getElementById('modalModificar').classList.add('active');
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar los datos del acudiente');
        }
    }

    async guardarModificacion() {
        if (!this.selectedUsuarioId || !this.selectedUsuarioType) return;

        try {
            let data = {};
            let url = '';

            if (this.selectedUsuarioType === 'estudiante') {
                const grupoId = document.getElementById('modGrupo').value;
                data = {
                    nombre: document.getElementById('modNombre').value,
                    apellido: document.getElementById('modApellido').value,
                    tarjeta_identidad: document.getElementById('modTarjetaIdentidad').value,
                    fecha_nacimiento: document.getElementById('modFechaNacimiento').value,
                    grado_aplicado: document.getElementById('modGradoAplicado').value,
                    grupo_id: grupoId ? parseInt(grupoId) : null
                };
                url = `/api/gestion/estudiantes/${this.selectedUsuarioId}`;
                this.actualizarEstudiantes(grupoId);
            } else if (this.selectedUsuarioType === 'profesor') {
                data = {
                    nombre: document.getElementById('modNombre').value,
                    apellido: document.getElementById('modApellido').value,
                    cedula: document.getElementById('modCedula').value,
                    correo: document.getElementById('modCorreo').value || null,
                    tarjeta_profesional: document.getElementById('modTarjetaProfesional').value || null
                };
                url = `/api/gestion/profesores/${this.selectedUsuarioId}`;
            } else if (this.selectedUsuarioType === 'acudiente') {
                data = {
                    nombre: document.getElementById('modNombre').value,
                    apellido: document.getElementById('modApellido').value,
                    cedula: document.getElementById('modCedula').value,
                    correo: document.getElementById('modCorreo').value || null,
                    contacto_extra: document.getElementById('modContactoExtra').value || null
                };
                url = `/api/gestion/acudientes/${this.selectedUsuarioId}`;
            }

            const response = await fetch(url, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            const data2 = await response.json();
            if (!response.ok) throw new Error('Error al actualizar' + data2.message);

            // Si estaba editando un estudiante, también intentar actualizar la Hoja de Vida si los campos existen
            if (this.selectedUsuarioType === 'estudiante') {
                const hvEstadoEl = document.getElementById('mod_hv_estadoSalud');
                if (hvEstadoEl) {
                    const hvPayload = {
                        estadoSalud: document.getElementById('mod_hv_estadoSalud').value,
                        alergias: document.getElementById('mod_hv_alergias').value,
                        notasAprendizaje: document.getElementById('mod_hv_notas').value
                    };

                    const hvResp = await fetch(`/api/gestion/estudiantes/${this.selectedUsuarioId}/hoja-vida`, {
                        method: 'PUT',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify(hvPayload)
                    });

                    if (!hvResp.ok) {
                        const err = await hvResp.json().catch(() => ({}));
                        throw new Error(err.message || 'Error al actualizar la Hoja de Vida');
                    }
                }
            }

            this.mostrarExito('Usuario actualizado correctamente');
            this.cerrarModalModificar();
            await this.cargarDatosTab(this.currentTab);
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al actualizar el usuario' );
        }
    }

    cerrarModalModificar() {
        document.getElementById('modalModificar').classList.remove('active');
    }

    // ========== MÉTODOS PARA CREAR GRUPO ==========
    async crearGrupo() {
        try {
            // Cargar profesores para el selector
            const response = await fetch('/api/gestion/profesores');
            if (response.ok) {
                this.profesores = await response.json();

                const selectProfesor = document.getElementById('grupoProfesor');
                selectProfesor.innerHTML = '<option value="">Sin profesor asignado</option>';
                this.profesores.forEach(prof => {
                    selectProfesor.innerHTML += `<option value="${prof.id}">${prof.nombre} ${prof.apellido}</option>`;
                });
            }

            // Limpiar formulario
            document.getElementById('formCrearGrupo').reset();
            document.getElementById('modalCrearGrupo').classList.add('active');
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al abrir formulario de grupo');
        }
    }

    async guardarGrupo() {
        try {
            const grado = document.getElementById('grupoGrado').value;
            const identificador = document.getElementById('grupoIdentificador').value;
            const profesorId = document.getElementById('grupoProfesor').value;
            const capacidad = document.getElementById('grupoCapacidad').value;

            if (!grado || !identificador || !capacidad) {
                this.mostrarError('Complete los campos obligatorios');
                return;
            }

            const data = {
                grado,
                identificador,
                profesor_id: profesorId ? parseInt(profesorId) : null,
                capacidad: parseInt(capacidad)
            };

            const response = await fetch('/api/gestion/grupos', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (!response.ok) throw new Error('Error al crear grupo');

            this.mostrarExito('Grupo creado correctamente');
            this.cerrarModalCrearGrupo();
            await this.cargarGrupos();
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al crear el grupo');
        }
    }

    cerrarModalCrearGrupo() {
        document.getElementById('modalCrearGrupo').classList.remove('active');
    }

    // ========== MÉTODOS PARA CREAR CUENTA ==========
    async crearProfesor() {
        document.getElementById('modalCrearCuentaTitulo').textContent = 'Crear Cuenta de Profesor';
        document.getElementById('formCrearCuenta').innerHTML = `
            <div class="form-row">
                <div class="form-group">
                    <label for="crearNombre">Nombre *</label>
                    <input type="text" id="crearNombre" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="crearApellido">Apellido *</label>
                    <input type="text" id="crearApellido" class="form-control" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label for="crearCedula">Cédula *</label>
                    <input type="text" id="crearCedula" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="crearCorreo">Correo</label>
                    <input type="email" id="crearCorreo" class="form-control">
                </div>
            </div>
            <div class="form-group">
                <label for="crearTarjetaProfesional">Tarjeta Profesional</label>
                <input type="text" id="crearTarjetaProfesional" class="form-control">
            </div>

            <hr style="margin: 20px 0; border: 1px solid #e0e0e0;">
            <h4 style="margin-bottom: 15px; color: #333;">Credenciales de Acceso</h4>

            <div class="form-row">
                <div class="form-group">
                    <label for="crearUsername">Usuario *</label>
                    <input type="text" id="crearUsername" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="crearPassword">Contraseña *</label>
                    <input type="password" id="crearPassword" class="form-control" required>
                </div>
            </div>
            <input type="hidden" id="tipoUsuario" value="profesor">
        `;

        document.getElementById('modalCrearCuenta').classList.add('active');
    }

    async crearAcudiente() {
        document.getElementById('modalCrearCuentaTitulo').textContent = 'Crear Cuenta de Acudiente';
        document.getElementById('formCrearCuenta').innerHTML = `
            <div class="form-row">
                <div class="form-group">
                    <label for="crearNombre">Nombre *</label>
                    <input type="text" id="crearNombre" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="crearApellido">Apellido *</label>
                    <input type="text" id="crearApellido" class="form-control" required>
                </div>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label for="crearCedula">Cédula *</label>
                    <input type="text" id="crearCedula" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="crearCorreo">Correo</label>
                    <input type="email" id="crearCorreo" class="form-control">
                </div>
            </div>
            <div class="form-group">
                <label for="crearContactoExtra">Contacto Extra</label>
                <input type="text" id="crearContactoExtra" class="form-control">
            </div>

            <hr style="margin: 20px 0; border: 1px solid #e0e0e0;">
            <h4 style="margin-bottom: 15px; color: #333;">Credenciales de Acceso</h4>

            <div class="form-row">
                <div class="form-group">
                    <label for="crearUsername">Usuario *</label>
                    <input type="text" id="crearUsername" class="form-control" required>
                </div>
                <div class="form-group">
                    <label for="crearPassword">Contraseña *</label>
                    <input type="password" id="crearPassword" class="form-control" required>
                </div>
            </div>
            <input type="hidden" id="tipoUsuario" value="acudiente">
        `;

        document.getElementById('modalCrearCuenta').classList.add('active');
    }

    async guardarCuenta() {
        try {
            const tipo = document.getElementById('tipoUsuario').value;
            const nombre = document.getElementById('crearNombre').value;
            const apellido = document.getElementById('crearApellido').value;
            const cedula = document.getElementById('crearCedula').value;
            const username = document.getElementById('crearUsername').value;
            const password = document.getElementById('crearPassword').value;

            if (!nombre || !apellido || !cedula || !username || !password) {
                this.mostrarError('Complete los campos obligatorios');
                return;
            }

            // 1. Crear la cuenta de autenticación primero
            const cuentaData = {
                nombreUsuario: username,
                contrasena: password,
                tipoRol: tipo
            };

            const cuentaResponse = await fetch('/api/gestion/cuentas', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(cuentaData)
            });

            if (!cuentaResponse.ok) {
                const errorData = await cuentaResponse.json();
                throw new Error(errorData.message || 'Error al crear cuenta de autenticación');
            }

            const cuentaCreada = await cuentaResponse.json();

            // 2. Crear el usuario (profesor o acudiente)
            let userData = { nombre, apellido, cedula };
            let url = '';

            if (tipo === 'profesor') {
                userData.correo = document.getElementById('crearCorreo').value || null;
                userData.tarjeta_profesional = document.getElementById('crearTarjetaProfesional').value || null;
                url = '/api/gestion/profesores';
            } else if (tipo === 'acudiente') {
                userData.correo = document.getElementById('crearCorreo').value || null;
                userData.contacto_extra = document.getElementById('crearContactoExtra').value || null;
                url = '/api/gestion/acudientes';
            }

            const userResponse = await fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(userData)
            });

            if (!userResponse.ok) throw new Error('Error al crear usuario');

            const usuarioCreado = await userResponse.json();

            // 3. Asignar la cuenta al usuario
            const asignarData = {
                cuentaId: cuentaCreada.id,
                usuarioId: usuarioCreado.id,
                tipoUsuario: tipo
            };

            const asignarResponse = await fetch('/api/gestion/cuentas/asignar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(asignarData)
            });

            if (!asignarResponse.ok) {
                console.warn('Error al asignar cuenta, pero el usuario fue creado');
            }

            this.mostrarExito('Cuenta creada correctamente');
            this.cerrarModalCrearCuenta();
            await this.cargarDatosTab(this.currentTab);
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError(error.message || 'Error al crear la cuenta');
        }
    }

    cerrarModalCrearCuenta() {
        document.getElementById('modalCrearCuenta').classList.remove('active');
    }

    // ========== MÉTODOS PARA CAMBIAR ESTADO ==========
    async cambiarEstadoEstudiante() {
        if (!this.selectedUsuarioId) return;

        try {
            const response = await fetch(`/api/gestion/estudiantes/${this.selectedUsuarioId}`);
            if (!response.ok) throw new Error('Error al obtener estudiante');

            const estudiante = await response.json();

            document.getElementById('estudianteNombreEstado').textContent =
                `Cambiar estado de: ${estudiante.nombre} ${estudiante.apellido}`;
            document.getElementById('nuevoEstado').value = estudiante.estado;

            document.getElementById('modalCambiarEstado').classList.add('active');
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cargar el estudiante');
        }
    }

    async guardarCambioEstado() {
        try {
            const nuevoEstado = document.getElementById('nuevoEstado').value;

            if (!nuevoEstado) {
                this.mostrarError('Seleccione un estado');
                return;
            }

            const response = await fetch(`/api/gestion/estudiantes/${this.selectedUsuarioId}/estado`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ estado: nuevoEstado })
            });

            if (!response.ok) throw new Error('Error al cambiar estado');

            this.mostrarExito('Estado actualizado correctamente');
            this.cerrarModalCambiarEstado();
            await this.cargarEstudiantesGrupo(this.selectedGrupoId);
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al cambiar el estado');
        }
    }

    cerrarModalCambiarEstado() {
        document.getElementById('modalCambiarEstado').classList.remove('active');
    }

    // ========== DESCARGAR LISTA ==========
    async descargarLista() {
        if (!this.selectedGrupoId) return;

        // Abrir la página de lista formal con parámetros para admin
        const url = `/lista-estudiantes?tipo=admin&grupoId=${this.selectedGrupoId}`;
        window.open(url, '_blank');
    }

    // ========== ASIGNAR CUENTA A USUARIO EXISTENTE ==========
    async asignarCuenta(tipoUsuario) {
        if (!this.selectedUsuarioId) return;

        try {
            // Obtener datos del usuario
            let usuario;
            if (tipoUsuario === 'profesor') {
                const response = await fetch(`/api/gestion/profesores/${this.selectedUsuarioId}`);
                if (!response.ok) throw new Error('Error al obtener profesor');
                usuario = await response.json();
            } else if (tipoUsuario === 'acudiente') {
                const response = await fetch(`/api/gestion/acudientes/${this.selectedUsuarioId}`);
                if (!response.ok) throw new Error('Error al obtener acudiente');
                usuario = await response.json();
            }

            // Cargar cuentas disponibles del mismo rol
            const cuentasResponse = await fetch('/api/gestion/cuentas');
            if (!cuentasResponse.ok) throw new Error('Error al cargar cuentas');

            const todasCuentas = await cuentasResponse.json();
            const cuentasDisponibles = todasCuentas.filter(c => c.rol === tipoUsuario);

            // Mostrar el modal
            document.getElementById('usuarioNombreAsignar').textContent =
                `Asignar cuenta para: ${usuario.nombre} ${usuario.apellido}`;

            const selectCuenta = document.getElementById('cuentaExistente');
            selectCuenta.innerHTML = '<option value="">Seleccione una cuenta...</option>';
            cuentasDisponibles.forEach(cuenta => {
                selectCuenta.innerHTML += `<option value="${cuenta.id}">${cuenta.nombreUsuario} (${cuenta.rol})</option>`;
            });

            // Limpiar campos de nueva cuenta
            document.getElementById('nuevaCuentaUsername').value = '';
            document.getElementById('nuevaCuentaPassword').value = '';

            // Guardar tipo de usuario en el modal
            this.selectedAsignarTipo = tipoUsuario;

            document.getElementById('modalAsignarCuenta').classList.add('active');
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError('Error al abrir formulario de asignación');
        }
    }

    async guardarAsignacionCuenta() {
        try {
            const cuentaExistenteId = document.getElementById('cuentaExistente').value;
            const nuevoUsername = document.getElementById('nuevaCuentaUsername').value;
            const nuevoPassword = document.getElementById('nuevaCuentaPassword').value;

            let cuentaId;

            if (cuentaExistenteId) {
                // Asignar cuenta existente
                cuentaId = parseInt(cuentaExistenteId);
            } else if (nuevoUsername && nuevoPassword) {
                // Crear nueva cuenta
                const cuentaData = {
                    nombreUsuario: nuevoUsername,
                    contrasena: nuevoPassword,
                    tipoRol: this.selectedAsignarTipo
                };

                const response = await fetch('/api/gestion/cuentas', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(cuentaData)
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || 'Error al crear cuenta');
                }

                const cuentaCreada = await response.json();
                cuentaId = cuentaCreada.id;
            } else {
                this.mostrarError('Seleccione una cuenta existente o cree una nueva');
                return;
            }

            // Asignar la cuenta al usuario
            const asignarData = {
                cuentaId: cuentaId,
                usuarioId: this.selectedUsuarioId,
                tipoUsuario: this.selectedAsignarTipo
            };

            const asignarResponse = await fetch('/api/gestion/cuentas/asignar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(asignarData)
            });

            if (!asignarResponse.ok) {
                const errorData = await asignarResponse.json();
                throw new Error(errorData.message || 'Error al asignar cuenta');
            }

            this.mostrarExito('Cuenta asignada correctamente');
            this.cerrarModalAsignarCuenta();
        } catch (error) {
            console.error('Error:', error);
            this.mostrarError(error.message || 'Error al asignar cuenta');
        }
    }

    cerrarModalAsignarCuenta() {
        document.getElementById('modalAsignarCuenta').classList.remove('active');
    }
}

// Instancia global
let gestionUsuarios;

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    gestionUsuarios = new GestionUsuarios();
});
