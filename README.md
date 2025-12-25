# LittleJumpers 🏫

> Sistema de Gestión Educativa para Jardín Infantil - Universidad Distrital Francisco José de Caldasa

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.x-blue.svg)](https://www.mysql.com/)
[![Status](https://img.shields.io/badge/Status-En%20Desarrollo-yellow.svg)]()

---

## Tabla de Contenidos

- [Descripción](#descripción)
- [Características Principales](#características-principales)
- [Arquitectura](#arquitectura)
- [Stack Tecnológico](#stack-tecnológico)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Modelos de Dominio](#modelos-de-dominio)
- [API Endpoints](#api-endpoints)
- [Instalación y Configuración](#instalación-y-configuración)
- [Uso del Sistema](#uso-del-sistema)
- [Contribución](#contribución)

---

## Descripción

**LittleJumpers** es un sistema web integral diseñado para gestionar eficientemente un jardín infantil. El sistema automatiza todo el proceso de admisión, desde la inscripción inicial hasta la aceptación final del estudiante, pasando por la programación y gestión de entrevistas.

El proyecto proporciona interfaces diferenciadas para tres tipos de usuarios:
- **Administradores:** Control total del sistema y gestión de admisiones
- **Acudientes:** Inscripción de estudiantes y seguimiento del proceso
- **Docentes:** Gestión de grupos y estudiantes asignados

### Propósito

Facilitar la gestión administrativa de jardines infantiles mediante la digitalización del proceso de admisiones, reduciendo tiempos, mejorando la comunicación con los acudientes y centralizando la información de estudiantes, profesores y grupos académicos.

---

## Características Principales

### Gestión de Admisiones
- **Inscripción Online:** Los acudientes pueden registrarse e inscribir estudiantes desde cualquier lugar
- **Verificación Inteligente:** El sistema detecta si un acudiente ya está registrado para evitar duplicados
- **Proceso Completo:** Desde aspirante hasta estudiante aceptado con seguimiento de estados

### Sistema de Entrevistas
- **Programación Centralizada:** Los administradores pueden programar entrevistas desde un dashboard
- **Estados Trazables:** pendiente → programada → realizada
- **Notificaciones Automáticas:** Envío de emails en cada etapa del proceso

### Multi-Rol y Multi-Usuario
- Autenticación basada en roles (admin, acudiente, docente)
- Dashboards personalizados según el rol del usuario
- Redirección automática después del login

### Gestión Académica
- Organización por **grados:** Párvulos, Caminadores, Pre-jardín
- Asignación de profesores a grupos
- Control de capacidad (20 estudiantes por grupo)

### Comunicación Automatizada
- Envío de emails para confirmación de entrevistas
- Notificaciones de aceptación/rechazo
- Sistema configurable vía SMTP Gmail

### Gestión Académica Avanzada
- **Sistema de Calificaciones:** Registro y seguimiento de notas por estudiante
- **Hojas de Vida:** Documentación completa del desarrollo de cada estudiante
- **Boletines:** Generación y emisión de reportes académicos
- **Registros de Logros:** Seguimiento de hitos y competencias alcanzadas
- **Citaciones:** Notificación a acudientes para reuniones y seguimiento
- **Observaciones:** Notas detalladas del progreso académico

---

## Arquitectura

LittleJumpers implementa una **arquitectura en capas** basada en el patrón **MVC** (Model-View-Controller), proporcionando separación de responsabilidades, mantenibilidad y escalabilidad.

```
┌─────────────────────────────────────────────────────────┐
│                  CAPA DE PRESENTACION                   │
│  ┌──────────────┐              ┌──────────────────┐    │
│  │ Controllers  │              │ REST Controllers │    │
│  │ (Thymeleaf)  │              │   (API JSON)     │    │
│  └──────────────┘              └──────────────────┘    │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│                    CAPA DE SERVICIOS                     │
│  ┌─────────────┐ ┌──────────────┐ ┌─────────────────┐  │
│  │ Autenticacion│ │ Aspirantes   │ │  Entrevistas   │  │
│  │   Service   │ │   Service    │ │    Service     │  │
│  └─────────────┘ └──────────────┘ └─────────────────┘  │
│  ┌─────────────┐ ┌──────────────┐ ┌──────────────┐     │
│  │   Email     │ │   Cuentas    │ │Calificaciones│     │
│  │  Service    │ │   Service    │ │   Service    │     │
│  └─────────────┘ └──────────────┘ └──────────────┘     │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐    │
│  │ HojaVida     │ │  Boletines   │ │ Citaciones   │    │
│  │  Service     │ │   Service    │ │   Service    │    │
│  └──────────────┘ └──────────────┘ └──────────────┘    │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│                 CAPA DE REPOSITORIOS                     │
│         (Spring Data JPA - Abstracción BD)              │
│  CuentaRepo │ UsuariosRepo │ EstudianteRepo │ ...       │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│                   CAPA DE PERSISTENCIA                   │
│                 MySQL Database (Remoto)                  │
│                  Hibernate (ORM Layer)                   │
└─────────────────────────────────────────────────────────┘
```

### Patrones de Diseño Implementados

| Patrón | Descripción | Ubicación |
|--------|-------------|-----------|
| **DTO Pattern** | Transferencia de datos entre capas | `dto/` package |
| **Repository Pattern** | Abstracción de acceso a datos | `repository/` package |
| **Service Pattern** | Encapsulación de lógica de negocio | `service/` package |
| **Mapper Pattern** | Transformación entidades ↔ DTOs | `dto/mapper/` package |
| **Exception Handler** | Manejo centralizado de errores | `exception/GlobalExceptionHandler` |
| **Dependency Injection** | Inyección de dependencias Spring | `@Autowired` |
| **Strategy (Herencia)** | Especialización de entidades Usuario | `model/` con `@Inheritance` |

### Estrategia de Herencia

El sistema utiliza **JOINED inheritance strategy** para la jerarquía de `Usuario`:

```
Usuario (tabla base)
  ├── Acudiente (tabla específica)
  ├── Profesor (tabla específica)
  └── Administrativo (futuro)
```

Esta estrategia permite:
- Normalización de datos
- Flexibilidad para agregar roles
- Consultas optimizadas por tipo de usuario

---

## Stack Tecnológico

### Backend

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Java** | 17 | Lenguaje base |
| **Spring Boot** | 3.5.7 | Framework principal |
| **Spring Data JPA** | - | Persistencia de datos |
| **Spring Web** | - | REST API y Web MVC |
| **Spring Boot Mail** | 3.5.7 | Envío de emails |
| **Spring Validation** | - | Validación de datos |
| **Hibernate** | - | ORM (via JPA) |
| **Lombok** | - | Reducción de boilerplate |
| **MySQL Connector** | 8.x | Driver de base de datos |
| **DotEnv Java** | 3.0.0 | Variables de entorno |

### Frontend

| Tecnología | Descripción |
|------------|-------------|
| **Thymeleaf** | Motor de plantillas server-side |
| **HTML5 + CSS3** | Estructura y estilos |
| **JavaScript (Vanilla)** | Interactividad del cliente |
| **Fetch API** | Comunicación asíncrona con backend |

### Herramientas de Desarrollo

- **Maven:** Gestión de dependencias y build
- **Spring DevTools:** Hot reload en desarrollo
- **Git:** Control de versiones

### Infraestructura

- **Base de Datos:** MySQL 8.x (Servidor remoto)
- **SMTP:** Gmail (puerto 587, TLS)
- **Servidor:** Embebido (Tomcat via Spring Boot)

---

## Estructura del Proyecto

```
littlesjumpers/
│
├── src/
│   ├── main/
│   │   ├── java/co/edu/udistrital/
│   │   │   ├── controller/           # Controladores REST y Web
│   │   │   │   ├── AdminController.java
│   │   │   │   ├── AspirantesController.java
│   │   │   │   ├── AutenticacionController.java
│   │   │   │   └── Home.java         # Controlador de vistas
│   │   │   │
│   │   │   ├── dto/                  # Data Transfer Objects
│   │   │   │   ├── request/          # DTOs de entrada
│   │   │   │   ├── response/         # DTOs de salida
│   │   │   │   └── mapper/           # Conversores entidad ↔ DTO
│   │   │   │
│   │   │   ├── exception/            # Manejo de excepciones
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── EstudianteNotFoundException.java
│   │   │   │   └── ...
│   │   │   │
│   │   │   ├── model/                # Entidades JPA
│   │   │   │   ├── Cuenta.java
│   │   │   │   ├── Usuario.java      # Clase base
│   │   │   │   ├── Acudiente.java
│   │   │   │   ├── Profesor.java
│   │   │   │   ├── Estudiante.java
│   │   │   │   ├── Preinscripcion.java
│   │   │   │   └── Grupo.java
│   │   │   │
│   │   │   ├── repository/           # Repositorios Spring Data
│   │   │   │   ├── CuentaRepository.java
│   │   │   │   ├── EstudianteRepository.java
│   │   │   │   ├── PreinscripcionRepository.java
│   │   │   │   └── ...
│   │   │   │
│   │   │   └── service/              # Lógica de negocio
│   │   │       ├── AutenticacionService.java
│   │   │       ├── AspirantesService.java
│   │   │       ├── EntrevistasService.java
│   │   │       ├── EmailService.java
│   │   │       └── ...
│   │   │
│   │   └── resources/
│   │       ├── application.properties   # Configuración de Spring
│   │       ├── static/                  # Recursos estáticos
│   │       │   ├── css/
│   │       │   ├── js/
│   │       │   │   ├── dashboard.js
│   │       │   │   ├── inscripcion.js
│   │       │   │   └── login.js
│   │       │   └── images/
│   │       │
│   │       └── templates/html/          # Plantillas Thymeleaf
│   │           ├── main.html            # Página principal
│   │           ├── admin/
│   │           │   └── dashboard.html
│   │           ├── acudiente/
│   │           │   └── menu.html
│   │           └── docente/
│   │               └── menu.html
│   │
│   └── test/                            # Tests (WIP)
│
├── .env                                 # Variables de entorno
├── pom.xml                              # Configuración Maven
└── README.md
```

**Estadísticas del Proyecto:**
- **95 archivos Java**
- **~7,507 líneas de código**
- **15 entidades de dominio**
- **27 DTOs** (request/response)
- **14+ repositorios**
- **16 servicios de negocio**
- **6 vistas HTML Thymeleaf**
- **10 scripts JavaScript**

---

## Modelos de Dominio

### Diagrama de Relaciones Principales

```
┌─────────────┐           ┌──────────────┐
│   Cuenta    │ 1       1 │   Usuario    │
│             ├───────────┤  (abstract)  │
│  - username │           │  - nombre    │
│  - password │           │  - cedula    │
│  - rol      │           │  - correo    │
└─────────────┘           └───────┬──────┘
                                  │
                    ┌─────────────┼─────────────┐
                    │             │             │
              ┌─────▼──────┐ ┌───▼────────┐ ┌──▼──────────┐
              │ Acudiente  │ │  Profesor  │ │Administrativo│
              │            │ │            │ │             │
              │ - contacto │ │ - tarjeta  │ │  - cargo    │
              └─────┬──────┘ └─────┬──────┘ └─────────────┘
                    │              │
                    │ 1        N   │
              ┌─────▼──────┐       │
              │ Estudiante │       │
              │            │       │ 1
              │ - nombre   │◄──────┤
              │ - grado    │   N   │
              │ - estado   ├───────┤
              └─────┬──────┘       │
                    │ 1            │
                    │              │
              ┌─────▼──────┐  N  ┌─▼─────┐
              │Preinscripción│────►│ Grupo │
              │              │     │       │
              │ - fecha_sol  │     │-grado │
              │ - fecha_ent  │     │-capac.│
              │ - estado     │     └───────┘
              └──────────────┘
```

### Entidades Principales

#### **Usuario** (Herencia JOINED)
```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {
    @Id
    private Integer id;
    private String nombre;
    private String apellido;
    private String cedula;
    private String correo;

    @OneToOne
    private Cuenta cuenta;
}
```

**Subclases:**
- `Acudiente`: Tutor legal del estudiante
- `Profesor`: Docente encargado de un grupo
- `Administrativo`: Personal administrativo (futuro)

#### **Cuenta**
Sistema de autenticación con roles diferenciados.

```java
@Entity
public class Cuenta {
    @Id
    private Integer id;

    @Column(unique = true)
    private String nombreUsuario;

    private String contrasena;  // TODO: Implementar hash

    @Enumerated(EnumType.STRING)
    private TipoRol rol;        // acudiente, profesor, admin

    private boolean activo;
    private LocalDateTime fechaCreacion;
}
```

#### **Estudiante**
```java
@Entity
public class Estudiante {
    @Id
    private Integer id;
    private String tarjeta_identidad;
    private String nombre;
    private String apellido;
    private LocalDate fecha_nacimiento;

    @Enumerated(EnumType.STRING)
    private Grado grado_aplicado;  // parvulos, caminadores, pre_jardin

    @Enumerated(EnumType.STRING)
    private Estado estado;         // aspirante, aceptado, rechazado

    @ManyToOne(optional = false)
    private Acudiente acudiente;

    @ManyToOne
    private Grupo grupo;
}
```

#### **Preinscripcion**
Controla el proceso de admisión del estudiante.

```java
@Entity
public class Preinscripcion {
    @Id
    private Integer id;

    @OneToOne(unique = true)
    private Estudiante estudiante;

    private LocalDateTime fecha_solicitud;
    private LocalDateTime fecha_entrevista;

    @Enumerated(EnumType.STRING)
    private EstadoEntrevista estado;  // pendiente, programada, realizada
}
```

#### **Grupo**
```java
@Entity
public class Grupo {
    @Id
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Grado grado;

    private String identificador;

    @ManyToOne
    private Profesor profesor;

    private Integer capacidad = 20;  // Capacidad máxima
}
```

#### **Calificación**
```java
@Entity
public class Calificacion {
    @Id
    private Integer id;

    @ManyToOne
    private Estudiante estudiante;

    private String materia;

    private Double nota;

    private LocalDateTime fecha;

    private String observaciones;
}
```

#### **HojaVida**
```java
@Entity
public class HojaVida {
    @Id
    private Integer id;

    @OneToOne
    private Estudiante estudiante;

    private String descripcion;

    private String logros;

    private String competencias;

    private LocalDateTime fechaCreacion;
}
```

#### **Boletín**
```java
@Entity
public class Boletin {
    @Id
    private Integer id;

    @OneToOne
    private Estudiante estudiante;

    private String contenido;

    private LocalDate periodo;

    private LocalDateTime fechaGeneracion;
}
```

#### **Citación**
```java
@Entity
public class Citacion {
    @Id
    private Integer id;

    @ManyToOne
    private Acudiente acudiente;

    private String asunto;

    private LocalDateTime fechaCitacion;

    private String observaciones;

    private boolean asistio;
}
```

### Enumeraciones del Dominio

| Enum | Valores | Descripción |
|------|---------|-------------|
| `TipoRol` | acudiente, profesor, admin | Roles del sistema |
| `Estado` | aspirante, aceptado, rechazado | Estado del estudiante |
| `EstadoEntrevista` | pendiente, programada, realizada | Estado de la entrevista |
| `Grado` | parvulos, caminadores, pre_jardin | Niveles educativos |

---

## API Endpoints

### Autenticación (`/api/auth`)

| Método | Endpoint | Descripción | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| `POST` | `/api/auth/login` | Inicio de sesión | `LoginRequest` | `LoginResponse` |
| `GET` | `/api/auth/health` | Health check | - | `"OK"` |

**Ejemplo Login:**
```json
// Request
{
  "username": "admin",
  "password": "admin123"
}

// Response
{
  "success": true,
  "message": "Inicio de sesión exitoso",
  "userId": 1,
  "username": "admin",
  "rol": "admin",
  "redirectUrl": "/admin/dashboard"
}
```

### Aspirantes (`/api/aspirantes`)

| Método | Endpoint | Descripción | Request Body |
|--------|----------|-------------|--------------|
| `POST` | `/existeAcudiente` | Verificar acudiente existente | `UsuarioRequest` |
| `POST` | `/registrarAcudiente` | Crear nuevo acudiente | `AcudienteRequest` |
| `POST` | `/registrarEstudiante` | Inscribir estudiante aspirante | `EstudianteRequest` |

**Ejemplo Registro de Estudiante:**
```json
{
  "nombre": "Juan",
  "apellido": "Pérez",
  "tarjeta_identidad": "1234567890",
  "fecha_nacimiento": "2020-05-15",
  "grado_aplicado": "pre_jardin",
  "acudienteId": 5
}
```

### Administración (`/api/admin`)

#### Gestión de Entrevistas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/entrevistas/aspirantes` | Listar todos los aspirantes |
| `GET` | `/entrevistas/preinscripciones` | Listar todas las preinscripciones |
| `GET` | `/preinscripciones/estado/{estado}` | Filtrar por estado de entrevista |
| `POST` | `/entrevistas/preinscripcion` | Crear nueva preinscripción |
| `PUT` | `/preinscripciones/{id}/programar` | Programar fecha de entrevista |
| `PUT` | `/preinscripciones/{id}/realizada` | Marcar entrevista como realizada |

#### Decisión de Admisión

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `PUT` | `/estudiantes/{id}/aceptar` | Aceptar estudiante (envía email) |
| `PUT` | `/estudiantes/{id}/rechazar` | Rechazar estudiante (envía email) |

**Ejemplo Programar Entrevista:**
```json
// PUT /api/admin/preinscripciones/3/programar
{
  "fechaEntrevista": "2025-12-15T10:00:00"
}
```

#### Gestión Académica

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/calificaciones` | Obtener todas las calificaciones |
| `POST` | `/api/calificaciones` | Crear nueva calificación |
| `PUT` | `/api/calificaciones/{id}` | Actualizar calificación |
| `GET` | `/api/hojas-vida/{estudianteId}` | Obtener hoja de vida del estudiante |
| `POST` | `/api/hojas-vida` | Crear registro en hoja de vida |
| `GET` | `/api/boletines` | Listar boletines académicos |
| `GET` | `/api/citaciones` | Obtener citaciones |
| `POST` | `/api/citaciones` | Crear nueva citación |

### Vistas Web (Thymeleaf)

| Ruta | Vista | Descripción |
|------|-------|-------------|
| `/` | `main.html` | Página principal pública |
| `/admin/dashboard` | `admin/dashboard.html` | Panel administrativo |
| `/acudiente/menu` | `acudiente/menu.html` | Menú de acudiente |
| `/docente/menu` | `docente/menu.html` | Menú de docente |

---

## Instalación y Configuración

### Prerrequisitos

- **Java JDK 17** o superior
- **Maven 3.8+**
- **MySQL 8.x** (o acceso a la BD remota configurada)
- **Git**
- Cuenta de Gmail con contraseña de aplicación (para envío de emails)

### Pasos de Instalación

#### 1. Clonar el Repositorio
```bash
git clone <repository-url>
cd littlesjumpers
```

#### 2. Configurar Variables de Entorno

Crear archivo `.env` en la raíz del proyecto:

```env
# Base de Datos
DB_HOST=<tu_host_mysql>
DB_PORT=<puerto_mysql>
DB_NAME=<nombre_base_datos>
DB_USERNAME=<tu_usuario>
DB_PASSWORD=<tu_contraseña>

# Email Configuration
EMAIL_USERNAME=<tu_email@gmail.com>
EMAIL_PASSWORD=<contraseña_de_aplicacion>
```

**Nota:** Para obtener una contraseña de aplicación de Gmail:
1. Ir a https://myaccount.google.com/security
2. Activar verificación en 2 pasos
3. Generar contraseña de aplicación en "Contraseñas de aplicaciones"

#### 3. Verificar Configuración de Base de Datos

Editar `src/main/resources/application.properties` si es necesario:

```properties
spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useSSL=false&serverTimezone=UTC
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME}
spring.mail.password=${EMAIL_PASSWORD}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

#### 4. Compilar el Proyecto

```bash
mvn clean install
```

#### 5. Ejecutar la Aplicación

```bash
mvn spring-boot:run
```

O ejecutar directamente desde tu IDE:
```java
// Ejecutar: LittlesjumpersApplication.java
```

#### 6. Acceder a la Aplicación

Abrir en el navegador:
```
http://localhost:8080
```

**Credenciales de prueba** (si ya existen en la BD):
```
Usuario: admin
Contraseña: admin123
```

---

## Uso del Sistema

### Flujo Completo de Admisión

#### 1️⃣ Registro de Acudiente (Primera vez)

1. Acceder a la página principal: `http://localhost:8080`
2. Ir a la sección de inscripción
3. Completar formulario de registro de acudiente:
   - Nombre, apellido
   - Cédula
   - Correo electrónico
   - Contacto extra
4. El sistema valida si el acudiente ya existe
5. Si no existe, crea la cuenta automáticamente

#### 2️⃣ Inscripción de Estudiante

1. Una vez registrado el acudiente, proceder a inscribir al estudiante
2. Completar información del estudiante:
   - Nombre, apellido
   - Tarjeta de identidad
   - Fecha de nacimiento
   - Grado deseado (Párvulos, Caminadores, Pre-jardín)
3. Asociar con el acudiente
4. El sistema automáticamente:
   - Crea el estudiante con estado "aspirante"
   - Genera una preinscripción con estado "pendiente"

#### 3️⃣ Gestión Administrativa

1. Login como administrador: `http://localhost:8080` → "Iniciar Sesión"
2. Acceder al Dashboard Administrativo
3. Ver lista de aspirantes y preinscripciones
4. **Programar entrevista:**
   - Seleccionar aspirante
   - Asignar fecha y hora
   - Sistema envía email automático al acudiente
   - Estado cambia a "programada"

#### 4️⃣ Entrevista Realizada

1. Después de realizar la entrevista presencial
2. Marcar en el sistema como "realizada"
3. Ahora se habilitan las opciones de aceptar/rechazar

#### 5️⃣ Decisión Final

**Si se acepta:**
- Click en "Aceptar"
- Estado del estudiante cambia a "aceptado"
- Se envía email de bienvenida al acudiente
- El estudiante queda registrado en el sistema

**Si se rechaza:**
- Click en "Rechazar"
- Estado del estudiante cambia a "rechazado"
- Se envía email de notificación al acudiente

### Usuarios y Roles

| Rol | Dashboard | Funcionalidades |
|-----|-----------|-----------------|
| **Admin** | `/admin/dashboard` | - Ver todos los aspirantes<br>- Programar entrevistas<br>- Aceptar/rechazar estudiantes<br>- Gestionar todo el sistema |
| **Acudiente** | `/acudiente/menu` | - Inscribir estudiantes<br>- Ver estado de solicitudes<br>- Actualizar información |
| **Docente** | `/docente/menu` | - Ver grupos asignados<br>- Gestionar estudiantes del grupo<br>- Reportes académicos |

---

## Contribución

Este es un proyecto académico de la **Universidad Distrital Francisco José de Caldas**.

### Equipo de Desarrollo

- **Desarrolladores:** Nahin Peñaranda y Anderson Arenas
- **Profesor/Supervisor:** Henry Diosa
- **Materia:** Fundamentos de Ingeniería de Software (FIS)

### Cómo Contribuir

1. **Fork** el repositorio
2. **Crear** una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. **Push** a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un **Pull Request**

### Estándares de Código

- **Java:** Seguir convenciones de Java (camelCase, etc.)
- **Commits:** Mensajes descriptivos en español
- **Documentación:** Comentar código complejo
- **Tests:** Agregar tests para nuevas funcionalidades

---

## Licencia

Este proyecto es de uso **académico** y fue desarrollado como parte de la asignatura de Fundamentos de Ingeniería de Software en la Universidad Distrital Francisco José de Caldas.

---

## Contacto y Soporte

Para preguntas, sugerencias o reporte de bugs:

- **Issues:** [GitHub Issues](https://github.com/ItzNxhin/littlesjumpers)
- **Email:** naidavid978@gmail.com
- **Universidad:** Universidad Distrital Francisco José de Caldas


<div align="center">

*Universidad Distrital Francisco José de Caldas - 2025*

</div>
