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
                // Acudiente existe - redirigir o mostrar siguiente paso
                mostrarExito('¡Bienvenido! Tu información fue encontrada. Redirigiendo...');

                setTimeout(() => {
                    // TODO: Redirigir a la página de inscripción de aspirante
                    // window.location.href = `/aspirantes/registro?cedula=${cedula}`;
                    console.log('Redirigir a formulario de inscripción de aspirante');
                    cerrarModal();
                }, 2000);

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
                body: JSON.stringify(datos)
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Error al registrar');
            }

            const resultado = await response.json();

            // Registro exitoso
            mostrarExito('¡Registro exitoso! Redirigiendo al formulario de inscripción...');

            setTimeout(() => {
                // TODO: Redirigir a la página de inscripción de aspirante
                // window.location.href = `/aspirantes/registro?cedula=${datos.cedula}`;
                console.log('Redirigir a formulario de inscripción de aspirante');
                cerrarModal();
            }, 2000);

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

        // Mostrar formulario de verificación
        verificacionFormContainer.classList.remove('hidden');
        registroFormContainer.classList.add('hidden');
        registroFormContainer.classList.remove('active');

        // Resetear indicadores de paso
        step1.classList.add('active');
        step2.classList.remove('active');

        // Ocultar mensajes
        ocultarMensajes();

        // Habilitar botones
        btnVerificar.disabled = false;
        btnRegistrar.disabled = false;
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
});