package co.edu.udistrital.service;

import co.edu.udistrital.dto.LoginRequest;
import co.edu.udistrital.dto.LoginResponse;
import co.edu.udistrital.exception.CuentaInactivaException;
import co.edu.udistrital.exception.CredencialesInvalidasException;
import co.edu.udistrital.exception.DatabaseException;
import co.edu.udistrital.model.Cuenta;
import co.edu.udistrital.model.Usuario;
import co.edu.udistrital.model.Cuenta.TipoRol;
import co.edu.udistrital.repository.CuentaRepository;
import co.edu.udistrital.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticacionService {

    @Autowired
    private CuentaRepository cuentaRepository;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Transactional(readOnly = true)
    public LoginResponse iniciarSesion(LoginRequest loginRequest) {
        try {
            // 1. Consultar cuenta en la base de datos
            Cuenta cuenta = cuentaRepository.findByNombreUsuario(loginRequest.getUsername())
                    .orElseThrow(() -> new CredencialesInvalidasException("El usuario no existe"));

            // 2. Validar credenciales
            if (!cuenta.getContrasena().equals(loginRequest.getPassword())) {
                throw new CredencialesInvalidasException("Usuario o contraseña incorrectos");
            }

            // 3. Verificar si la cuenta está activa
            if (!cuenta.isActivo()) {
                throw new CuentaInactivaException("La cuenta está inactiva. Contacte al administrador.");
            }

            // 4. Buscar el usuario asociado a la cuenta
            Usuario usuario = usuariosRepository.findByCuenta(cuenta)
                    .orElseThrow(() -> new RuntimeException("No se encontró un usuario asociado a esta cuenta"));

            // 5. Determinar la URL de redirección según el rol
            String redirectUrl = determinarRedireccion(cuenta.getRol());

            // 6. Retornar respuesta exitosa con el ID del usuario (profesor/acudiente)
            return new LoginResponse(
                    true,
                    "Inicio de sesión exitoso",
                    usuario.getId(),
                    cuenta.getNombreUsuario(),
                    cuenta.getRol(),
                    redirectUrl
            );

        } catch (CredencialesInvalidasException | CuentaInactivaException e) {
            throw e;
        } catch (DataAccessException e) {
            throw new DatabaseException("Error al consultar la base de datos", e);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            System.out.println(e.getMessage());
            throw new RuntimeException("Error inesperado durante la autenticación", e);
        }
    }

    private String determinarRedireccion(TipoRol rol) {
        switch (rol) {
            case admin:
                return "/admin/dashboard";
            case acudiente:
                return "/acudiente/menu";
            case profesor:
                return "/docente/menu";
            default:
                return "/error";
        }
    }

    public boolean validarCredenciales(String username, String password) {
        try {
            Cuenta cuenta = cuentaRepository.findByNombreUsuario(username)
                    .orElse(null);

            return cuenta != null && cuenta.getContrasena().equals(password) && cuenta.isActivo();

        } catch (DataAccessException e) {
            throw new DatabaseException("Error al validar credenciales", e);
        }
    }
}
