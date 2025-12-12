// Gestión del boletín de calificaciones
class GestionBoletin {
    constructor() {
        this.estudianteId = null;
        this.periodo = null;
        this.tipoUsuario = null; // 'acudiente', 'profesor', 'admin'
        this.usuarioId = null;
        this.calificaciones = [];
        this.estudiante = null;
    }

    async init() {
        // Obtener parámetros de la URL
        const params = new URLSearchParams(window.location.search);
        this.estudianteId = params.get('estudianteId');
        this.periodo = parseInt(params.get('periodo'));
        this.tipoUsuario = params.get('tipo');
        this.usuarioId = params.get('usuarioId');

        if (!this.estudianteId || !this.periodo || !this.tipoUsuario) {
            alert('Parámetros inválidos');
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
                endpoint = `/api/acudiente/${this.usuarioId}/estudiantes/${this.estudianteId}/calificaciones`;
            } else if (this.tipoUsuario === 'profesor') {
                endpoint = `/api/profesor/estudiantes/${this.estudianteId}/calificaciones`;
            } else {
                // Admin u otro tipo
                endpoint = `/api/gestion/estudiantes/${this.estudianteId}`;
            }

            // Cargar calificaciones
            const responseCalif = await fetch(
                this.tipoUsuario === 'acudiente' || this.tipoUsuario === 'profesor'
                    ? endpoint
                    : `/api/calificaciones/estudiante/${this.estudianteId}`
            );

            if (!responseCalif.ok) {
                throw new Error('Error al cargar calificaciones');
            }

            this.calificaciones = await responseCalif.json();

            // Cargar información del estudiante
            const responseEst = await fetch(`/api/gestion/estudiantes/${this.estudianteId}`);
            if (responseEst.ok) {
                this.estudiante = await responseEst.json();
            }

            this.renderizarBoletin();

            // Ocultar indicador de carga y mostrar contenido
            document.getElementById('loadingIndicator').style.display = 'none';
            document.getElementById('actionButtons').style.display = 'flex';
            document.getElementById('boletinContainer').style.display = 'block';

        } catch (error) {
            console.error('Error:', error);
            document.getElementById('loadingIndicator').innerHTML = `
                <div style="color: #e74c3c; font-size: 48px;">❌</div>
                <p style="color: #e74c3c;">Error al cargar datos del boletín: ${error.message}</p>
                <button class="btn btn-secondary" onclick="window.history.back()" style="margin-top: 20px;">← Volver</button>
            `;
        }
    }

    renderizarBoletin() {
        // Filtrar calificaciones por periodo
        const calificacionesPeriodo = this.calificaciones.filter(c => c.periodo === this.periodo);

        if (calificacionesPeriodo.length === 0) {
            alert('No hay calificaciones para este periodo');
            return;
        }

        // Establecer información del estudiante
        document.getElementById('estudianteNombre').textContent =
            this.estudiante ? `${this.estudiante.nombre} ${this.estudiante.apellido}` :
            calificacionesPeriodo[0].estudianteNombre;

        document.getElementById('estudianteGrado').textContent =
            this.estudiante ? this.formatearGrado(this.estudiante.grado_aplicado) : '-';

        document.getElementById('boletinPeriodo').textContent = this.periodo;
        document.getElementById('boletinAnio').textContent = new Date().getFullYear();

        // Calcular promedio general
        const promedioGeneral = calificacionesPeriodo.reduce((sum, cal) =>
            sum + parseFloat(cal.valor), 0) / calificacionesPeriodo.length;

        document.getElementById('promedioGeneral').textContent = promedioGeneral.toFixed(2);
        document.getElementById('descripcionPromedio').textContent =
            this.getDescripcionPromedio(promedioGeneral);

        // Agrupar por categoría
        const porCategoria = {};
        calificacionesPeriodo.forEach(cal => {
            if (!porCategoria[cal.logroCategoria]) {
                porCategoria[cal.logroCategoria] = [];
            }
            porCategoria[cal.logroCategoria].push(cal);
        });

        // Renderizar calificaciones por categoría
        this.renderizarCalificaciones(porCategoria);

        // Establecer fecha de generación
        document.getElementById('fechaGeneracion').textContent =
            new Date().toLocaleDateString('es-ES', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });
    }

    renderizarCalificaciones(porCategoria) {
        const gradesSection = document.getElementById('gradesSection');
        let html = '';

        Object.keys(porCategoria).sort().forEach(categoria => {
            const calificaciones = porCategoria[categoria];
            const promedioCat = calificaciones.reduce((sum, cal) =>
                sum + parseFloat(cal.valor), 0) / calificaciones.length;

            html += `
                <div class="category-section">
                    <div class="category-header">
                        <h3>${this.formatearCategoria(categoria)}</h3>
                        <span class="category-average">Promedio: ${promedioCat.toFixed(2)}</span>
                    </div>
                    <table class="grades-table">
                        <thead>
                            <tr>
                                <th style="width: 50%;">Logro</th>
                                <th style="width: 30%;">Docente</th>
                                <th style="width: 20%;">Calificación</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${calificaciones.map(cal => `
                                <tr>
                                    <td>${cal.logroNombre}</td>
                                    <td>${cal.profesorNombre}</td>
                                    <td class="grade-value ${this.getGradeClass(cal.valor)}">
                                        ${parseFloat(cal.valor).toFixed(1)}
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `;
        });

        gradesSection.innerHTML = html;
    }

    formatearGrado(grado) {
        const grados = {
            'parvulos': 'Párvulos',
            'caminadores': 'Caminadores',
            'pre_jardin': 'Pre-jardín'
        };
        return grados[grado] || grado;
    }

    formatearCategoria(categoria) {
        const categorias = {
            'COGNITIVO': 'Desarrollo Cognitivo',
            'MOTRIZ': 'Desarrollo Motriz',
            'SOCIOAFECTIVO': 'Desarrollo Socioafectivo',
            'LENGUAJE': 'Lenguaje y Comunicación',
            'ARTISTICO': 'Desarrollo Artístico'
        };
        return categorias[categoria] || categoria;
    }

    getDescripcionPromedio(promedio) {
        if (promedio >= 4.5) return 'Desempeño Excelente';
        if (promedio >= 4.0) return 'Desempeño Muy Bueno';
        if (promedio >= 3.5) return 'Desempeño Bueno';
        if (promedio >= 3.0) return 'Desempeño Aceptable';
        return 'Requiere Refuerzo';
    }

    getGradeClass(valor) {
        const val = parseFloat(valor);
        if (val >= 4.5) return 'grade-excellent';
        if (val >= 4.0) return 'grade-good';
        if (val >= 3.5) return 'grade-fair';
        if (val >= 3.0) return 'grade-acceptable';
        return 'grade-low';
    }
}

// Instancia global
const boletin = new GestionBoletin();

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
    boletin.init();
});
