# ARQUITECTURA DEL SISTEMA

```
┌─────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                 │
│                                                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │    View     │  │   Routing   │  │    Guard    │    │
│  │  Templates  │  │  Navigation │  │    Auth     │    │
│  └─────────────┘  └─────────────┘  └─────────────┘    │
│                                                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │   Service   │  │ Interceptor │  │   Module    │    │
│  │    HTTP     │  │    OAuth2   │  │   System    │    │
│  └─────────────┘  └─────────────┘  └─────────────┘    │
│                                                         │
└─────────────────────────┬───────────────────────────────┘
                          │ HTTP/HTTPS (REST API)
                          │ JSON Request/Response
                          │ OAuth2 Bearer Token (JWT)
                          │ CORS Headers
                          ▼
┌─────────────────────────────────────────────────────────┐
│                    CAPA DE SERVICIOS                    │
│                                                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │ Controller  │  │   Config    │  │  Exception  │    │
│  │  REST API   │  │ OAuth2/JWT  │  │   Handler   │    │
│  └──────┬──────┘  └─────────────┘  └─────────────┘    │
│         │                              ▲              │
│         │ @Autowired                   │              │
│  ┌──────▼──────┐  ┌─────────────┐     │              │
│  │   Service   │◄─┤     DTO     │     │ throw        │
│  │   Business  │  │ Data Transfer│     │ Exception    │
│  │    Logic    │  │   Objects   │  ┌──┴───────────┐  │
│  └──────┬──────┘  └─────────────┘  │  Constant    │  │
│         │                          │   Values     │  │
│         │ @Autowired               └──────────────┘  │
│  ┌──────▼──────┐  ┌─────────────┐                     │
│  │     DAO     │◄─┤    Model    │                     │
│  │ Data Access │  │   Entity    │                     │
│  │   Object    │  │    JPA      │                     │
│  └─────┬───────┘  └─────────────┘                     │
│        │                                              │
│        │ @Query/@Repository                           │
└────────┼──────────────────────────────────────────────┘
         │
                          │ JPA/Hibernate ORM
                          │ JDBC Connection Pool (HikariCP)
                          │ SQL Queries (JPQL/Native)
                          │ Database Transactions
                          ▼
┌─────────────────────────────────────────────────────────┐
│                  CAPA DE PERSISTENCIA                   │
│                                                         │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐    │
│  │ Database    │  │   Tables    │  │   Indexes   │    │
│  │  Schema     │  │ Relationships│ │ Constraints │    │
│  └─────────────┘  └─────────────┘  └─────────────┘    │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## PROTOCOLOS Y TECNOLOGÍAS DE COMUNICACIÓN

### Frontend ↔ Backend
- **Protocolo**: HTTP/HTTPS
- **Formato**: JSON (Request/Response)
- **Autenticación**: OAuth2 + JWT Bearer Token
- **Métodos REST**: GET, POST, PUT, DELETE, PATCH
- **Headers**: Authorization, Content-Type, Accept
- **CORS**: Cross-Origin Resource Sharing
- **Interceptors**: Request/Response manipulation

### Backend ↔ Base de Datos
- **ORM**: JPA/Hibernate
- **Driver**: JDBC
- **Connection Pool**: HikariCP
- **Queries**: JPQL, Native SQL, Criteria API
- **Transactions**: @Transactional (ACID)
- **Caching**: First/Second Level Cache

## RELACIONES Y FUNCIONAMIENTO

### 1. FLUJO DE PETICIÓN (Request)
```
Angular Component
       ↓ (HTTP Service)
REST Controller (@RestController)
       ↓ (@Autowired)
Service Layer (@Service)
       ↓ (@Autowired)
DAO Layer (@Repository)
       ↓ (JPA/Hibernate)
Database (Entity → Table)
```

### 2. FLUJO DE RESPUESTA (Response)
```
Database (SELECT)
       ↓ (ResultSet)
Entity (JPA Mapping)
       ↓ (DAO Processing)
DTO (Data Transfer Object)
       ↓ (Service Logic)
JSON Response
       ↓ (HTTP Response)
Angular Component
```

### 3. MANEJO DE ERRORES
```
Exception en cualquier capa
       ↓
@ControllerAdvice (Exception Handler)
       ↓
Error Response JSON
       ↓
Angular Error Interceptor
```

### 4. SEGURIDAD OAUTH2
```
Angular → Token Request → OAuth2 Server
       ↓
JWT Token ← OAuth2 Server
       ↓
HTTP Request + Bearer Token → Spring Security
       ↓
@PreAuthorize → Controller
```

## ESTRUCTURA ARQUITECTÓNICA FRONTEND

### Capa de Presentación (View Layer)
- **Templates** - HTML con Angular binding
- **Styling** - SCSS + PrimeFlex CSS Framework
- **UI Components** - PrimeNG component library

### Capa de Navegación (Routing Layer)
- **Router** - Angular Router para SPA
- **Guards** - Protección de rutas (AuthGuard)
- **Lazy Loading** - Carga bajo demanda de módulos

### Capa de Servicios (Service Layer)
- **HTTP Client** - Comunicación con backend
- **Business Logic** - Lógica específica del frontend
- **State Management** - Manejo de estado de la aplicación

### Capa de Seguridad (Security Layer)
- **Interceptors** - Manejo automático de tokens
- **OAuth2 Service** - Autenticación y autorización
- **Token Management** - Almacenamiento y renovación de JWT

### Capa de Configuración (Config Layer)
- **Environment** - Configuraciones por ambiente
- **Constants** - Valores constantes de la aplicación
- **Module System** - Organización modular de funcionalidades
