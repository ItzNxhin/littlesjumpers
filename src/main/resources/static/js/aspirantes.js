// ============================================
// MÓDULO DE ASPIRANTES - VERIFICACIÓN Y REGISTRO
// ============================================

document.addEventListener('DOMContentLoaded', function() {
    // Elementos del DOM
    const modal = document.getElementById('aspirantesModal');
    const btnInscripcion = document.querySelector('.btn-primary');
    const btnCerrarModal = document.getElementById('closeAspirantesModal');

    // Formularios y sus contenedores
    const verificacionFormContainer = document.getElementById('verificacionFormContainer');
    const registroFormContainer = document.getElementById('registroFormContainer');
    const verificacionForm = document.getElementById('verificacionForm');
    const registroForm = document.getElementById('registroForm');

    // Campos del formulario de verificación
    const inputNombreVerif = document.getElementById('nombreVerificacion');
    const inputCedulaVerif = document.getElementById('cedulaVerificacion');
    const btnVerificar = document.getElementById('btnVerificar');

    // Campos del formulario de registro
    const inputNombreReg = document.getElementById('nombreRegistro');
    const inputCedulaReg = document.getElementById('cedulaRegistro');
    const inputApellido = document.getElementById('apellido');
    const inputCorreo = document.getElementById('correo');
    const inputContactoExtra = document.getElementById('contactoExtra');
    const btnRegistrar = document.getElementById('btnRegistrar');
    const btnVolver = document.getElementById('btnVolver');

    // Mensajes
    const errorMessage = document.getElementById('errorMessageAspirantes');
    const successMessage = document.getElementById('successMessageAspirantes');
    const infoMessage = document.getElementById('infoMessage');
    const warningMessage = document.getElementById('warningMessage');

    // Indicadores de paso
    const step1 = document.getElementById('step1');
    const step2 = document.getElementById('step2');
    const step3 = document.getElementById('step3');

    // Formulario de estudiante
    const estudianteFormContainer = document.getElementById('estudianteFormContainer');
    const estudianteForm = document.getElementById('estudianteForm');
    const btnRegistrarEstudiante = document.getElementById('btnRegistrarEstudiante');
    const btnVolverDesdeEstudiante = document.getElementById('btnVolverDesdeEstudiante');

    // Campos del formulario de estudiante
    const inputAcudienteId = document.getElementById('acudiente_id');
    const inputNombreEstudiante = document.getElementById('nombreEstudiante');
    const inputApellidoEstudiante = document.getElementById('apellidoEstudiante');
    const inputTarjetaIdentidad = document.getElementById('tarjetaIdentidad');
    const inputFechaNacimiento = document.getElementById('fechaNacimiento');
    const inputGradoAplicado = document.getElementById('gradoAplicado');

    // ============================================
    // ABRIR Y CERRAR MODAL
    // ============================================

    btnInscripcion.addEventListener('click', function() {
        abrirModal();
    });

    btnCerrarModal.addEventListener('click', function() {
        cerrarModal();
    });

    modal.addEventListener('click', function(e) {
        if (e.target === modal) {
            cerrarModal();
        }
    });

    function abrirModal() {
        modal.classList.add('active');
        resetearFormularios();
    }

    function cerrarModal() {
        modal.classList.remove('active');
        resetearFormularios();
    }

    // ============================================
    // FORMULARIO DE VERIFICACIÓN
    // ============================================

    verificacionForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const nombre = inputNombreVerif.value.trim();
        const cedula = inputCedulaVerif.value.trim();

        if (!nombre || !cedula) {
            mostrarError('Por favor, complete todos los campos');
            return;
        }

        await verificarAcudiente(nombre, cedula);
    });

    async function verificarAcudiente(nombre, cedula) {
        try {
            // Deshabilitar botón y mostrar loading
            btnVerificar.disabled = true;
            btnVerificar.innerHTML = 'Verificando... <span class="spinner"></span>';
            ocultarMensajes();

            // Hacer petición al backend
            const response = await fetch('/api/aspirantes/existeAcudiente', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    nombre: nombre,
                    cedula: cedula
                })
            });

            if (!response.ok) {
                throw new Error('Error en la comunicación con el servidor');
            }

            const existe = await response.json();

            if (existe === true) {
                // Acudiente existe - mostrar formulario de estudiante
                mostrarExito('¡Bienvenido! Tu información fue encontrada. Redirigiendo...');

                setTimeout(() => {
                    mostrarFormularioEstudiante(cedula);
                }, 1500);

            } else {
                // Acudiente NO existe - mostrar formulario de registro
                mostrarFormularioRegistro(nombre, cedula);
            }

        } catch (error) {
            console.error('Error al verificar acudiente:', error);
            mostrarError('Error al verificar la información. Por favor, intente nuevamente.');
        } finally {
            btnVerificar.disabled = false;
            btnVerificar.innerHTML = 'Verificar';
        }
    }

    // ============================================
    // FORMULARIO DE REGISTRO
    // ============================================

    function mostrarFormularioRegistro(nombre, cedula) {
        // Ocultar formulario de verificación
        verificacionFormContainer.classList.add('hidden');

        // Mostrar formulario de registro
        registroFormContainer.classList.remove('hidden');
        registroFormContainer.classList.add('active');

        // Pre-llenar campos
        inputNombreReg.value = nombre;
        inputCedulaReg.value = cedula;

        // Actualizar indicadores de paso
        step1.classList.remove('active');
        step2.classList.add('active');

        // Mostrar mensaje informativo
        mostrarInfo('No encontramos tu información. Por favor, completa el registro.');

        // Limpiar campos editables
        inputApellido.value = '';
        inputCorreo.value = '';
        inputContactoExtra.value = '';

        // Enfocar primer campo editable
        inputApellido.focus();
    }

    registroForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const datosRegistro = {
            nombre: inputNombreReg.value.trim(),
            cedula: inputCedulaReg.value.trim(),
            apellido: inputApellido.value.trim(),
            correo: inputCorreo.value.trim(),
            contacto_extra: inputContactoExtra.value.trim()
        };

        // Validar campos
        if (!datosRegistro.apellido || !datosRegistro.correo) {
            mostrarError('Por favor, complete todos los campos obligatorios');
            return;
        }

        // Validar formato de correo
        if (!validarEmail(datosRegistro.correo)) {
            mostrarError('Por favor, ingrese un correo electrónico válido');
            return;
        }

        await registrarAcudiente(datosRegistro);
    });

    async function registrarAcudiente(datos) {
        try {
            // Deshabilitar botón y mostrar loading
            btnRegistrar.disabled = true;
            btnRegistrar.innerHTML = 'Registrando... <span class="spinner"></span>';
            ocultarMensajes();

            // Hacer petición al backend
            const response = await fetch('/api/aspirantes/registrarAcudiente', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    nombre: datos.nombre,
                    cedula: datos.cedula,
                    apellido: datos.apellido,
                    correo: datos.correo,
                    contacto_extra: datos.contacto_extra
                })
            });

            const data = await response.json();
            console.log(data);

            if (!response.ok) {
                throw new Error(data.message || 'Error al registrar');
            }

            // Registro exitoso - mostrar formulario de estudiante
            mostrarExito('¡Acudiente registrado! Ahora registre al estudiante...');

            setTimeout(() => {
                mostrarFormularioEstudiante(datos.cedula);
            }, 1500);

        } catch (error) {
            console.error('Error al registrar acudiente:', error);
            mostrarError(error.message || 'Error al registrar. Por favor, intente nuevamente.');
        } finally {
            btnRegistrar.disabled = false;
            btnRegistrar.innerHTML = 'Registrar';
        }
    }

    // Botón volver
    btnVolver.addEventListener('click', function() {
        volverAVerificacion();
    });

    function volverAVerificacion() {
        // Mostrar formulario de verificación
        verificacionFormContainer.classList.remove('hidden');

        // Ocultar formulario de registro
        registroFormContainer.classList.add('hidden');
        registroFormContainer.classList.remove('active');

        // Actualizar indicadores de paso
        step1.classList.add('active');
        step2.classList.remove('active');

        // Limpiar mensajes
        ocultarMensajes();
    }

    // ============================================
    // FUNCIONES DE UTILIDAD
    // ============================================

    function validarEmail(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    }

    function mostrarError(mensaje) {
        ocultarMensajes();
        errorMessage.textContent = mensaje;
        errorMessage.classList.add('active');
    }

    function mostrarExito(mensaje) {
        ocultarMensajes();
        successMessage.textContent = mensaje;
        successMessage.classList.add('active');
    }

    function mostrarInfo(mensaje) {
        ocultarMensajes();
        infoMessage.textContent = mensaje;
        infoMessage.classList.add('active');
    }

    function mostrarAdvertencia(mensaje) {
        ocultarMensajes();
        warningMessage.textContent = mensaje;
        warningMessage.classList.add('active');
    }

    function ocultarMensajes() {
        errorMessage.classList.remove('active');
        successMessage.classList.remove('active');
        infoMessage.classList.remove('active');
        warningMessage.classList.remove('active');
    }

    function resetearFormularios() {
        // Resetear formularios
        verificacionForm.reset();
        registroForm.reset();
        estudianteForm.reset();

        // Mostrar formulario de verificación
        verificacionFormContainer.classList.remove('hidden');
        registroFormContainer.classList.add('hidden');
        registroFormContainer.classList.remove('active');
        estudianteFormContainer.classList.add('hidden');
        estudianteFormContainer.classList.remove('active');

        // Resetear indicadores de paso
        step1.classList.add('active');
        step2.classList.remove('active');
        step3.classList.remove('active');

        // Ocultar mensajes
        ocultarMensajes();

        // Habilitar botones
        btnVerificar.disabled = false;
        btnRegistrar.disabled = false;
        btnRegistrarEstudiante.disabled = false;
    }

    // ============================================
    // VALIDACIÓN EN TIEMPO REAL
    // ============================================

    // Validar formato de correo en tiempo real
    inputCorreo.addEventListener('blur', function() {
        if (this.value && !validarEmail(this.value)) {
            this.style.borderColor = '#c62828';
        } else if (this.value) {
            this.style.borderColor = '#2e7d32';
        }
    });

    inputCorreo.addEventListener('input', function() {
        if (this.value === '') {
            this.style.borderColor = '#ddd';
        }
    });

    // Solo números en cédula
    inputCedulaVerif.addEventListener('input', function() {
        this.value = this.value.replace(/[^0-9]/g, '');
    });

    // Solo letras en nombre
    inputNombreVerif.addEventListener('input', function() {
        this.value = this.value.replace(/[^a-záéíóúñA-ZÁÉÍÓÚÑ\s]/g, '');
    });

    inputApellido.addEventListener('input', function() {
        this.value = this.value.replace(/[^a-záéíóúñA-ZÁÉÍÓÚÑ\s]/g, '');
    });

    // ============================================
    // FORMULARIO DE ESTUDIANTE
    // ============================================

    function mostrarFormularioEstudiante(cedulaAcudiente) {
        // Ocultar formularios anteriores
        verificacionFormContainer.classList.add('hidden');
        registroFormContainer.classList.add('hidden');
        registroFormContainer.classList.remove('active');

        // Mostrar formulario de estudiante
        estudianteFormContainer.classList.remove('hidden');
        estudianteFormContainer.classList.add('active');

        // Establecer ID del acudiente
        inputAcudienteId.value = cedulaAcudiente;

        // Actualizar indicadores de paso
        step1.classList.remove('active');
        step2.classList.remove('active');
        step3.classList.add('active');

        // Mostrar mensaje informativo
        mostrarInfo('Complete los datos del estudiante aspirante.');

        // Limpiar campos
        estudianteForm.reset();
        inputAcudienteId.value = cedulaAcudiente;

        // Enfocar primer campo
        inputNombreEstudiante.focus();
    }

    estudianteForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const datosEstudiante = {
            nombre: inputNombreEstudiante.value.trim(),
            apellido: inputApellidoEstudiante.value.trim(),
            tarjeta_identidad: inputTarjetaIdentidad.value.trim(),
            fecha_nacimiento: inputFechaNacimiento.value,
            grado_aplicado: inputGradoAplicado.value,
            acudiente_cedula: inputAcudienteId.value.trim() // Enviar la cédula del acudiente
        };

        // Validar campos obligatorios
        if (!datosEstudiante.nombre || !datosEstudiante.apellido || !datosEstudiante.fecha_nacimiento || !datosEstudiante.grado_aplicado) {
            mostrarError('Por favor, complete todos los campos obligatorios');
            return;
        }

        await registrarEstudiante(datosEstudiante);
    });

    async function registrarEstudiante(datos) {
        try {
            // Deshabilitar botón y mostrar loading
            btnRegistrarEstudiante.disabled = true;
            btnRegistrarEstudiante.innerHTML = 'Registrando... <span class="spinner"></span>';
            ocultarMensajes();

            // Hacer petición al backend
            const response = await fetch('/api/aspirantes/registrarEstudiante', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(datos)
            });

            const data = await response.json();
            console.log(data);

            if (!response.ok) {
                throw new Error(data.message || 'Error al registrar estudiante');
            }

            // Registro exitoso
            mostrarExito('¡Estudiante registrado exitosamente! Proceso de inscripción completado.');

            setTimeout(() => {
                cerrarModal();
            }, 2500);

        } catch (error) {
            console.error('Error al registrar estudiante:', error);
            mostrarError(error.message || 'Error al registrar estudiante. Por favor, intente nuevamente.');
        } finally {
            btnRegistrarEstudiante.disabled = false;
            btnRegistrarEstudiante.innerHTML = 'Registrar Estudiante';
        }
    }

    // Botón volver desde formulario de estudiante
    btnVolverDesdeEstudiante.addEventListener('click', function() {
        volverAVerificacion();
    });

    // Solo letras en nombre de estudiante
    inputNombreEstudiante.addEventListener('input', function() {
        this.value = this.value.replace(/[^a-záéíóúñA-ZÁÉÍÓÚÑ\s]/g, '');
    });

    inputApellidoEstudiante.addEventListener('input', function() {
        this.value = this.value.replace(/[^a-záéíóúñA-ZÁÉÍÓÚÑ\s]/g, '');
    });

    // Solo números en tarjeta de identidad
    inputTarjetaIdentidad.addEventListener('input', function() {
        this.value = this.value.replace(/[^0-9]/g, '');
    });

    // Validar fecha de nacimiento (no futura)
    inputFechaNacimiento.addEventListener('change', function() {
        const fechaSeleccionada = new Date(this.value);
        const hoy = new Date();

        if (fechaSeleccionada > hoy) {
            mostrarError('La fecha de nacimiento no puede ser futura');
            this.value = '';
        }
    });
});