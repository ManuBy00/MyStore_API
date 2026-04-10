# 💈 MyStore API

![Spring Boot](https://img.shields.io/badge/Backend-Spring_Boot_3-green?style=for-the-badge&logo=springboot)
![Android](https://img.shields.io/badge/Mobile-Android_Native-brightgreen?style=for-the-badge&logo=android)
![Angular](https://img.shields.io/badge/Web-Angular_Dashboard-red?style=for-the-badge&logo=angular)
![MySQL](https://img.shields.io/badge/DB-MySQL_8-blue?style=for-the-badge&logo=mysql)

Esta API es el motor central de un ecosistema diseñado para digitalizar por completo cualquier necogio que requiera gestión de citas. Permite la convivencia de dos perfiles de usuario: el **cliente final**, que busca agilidad en sus reservas y compras desde su móvil, y el **administrador**, que necesita control total sobre su negocio desde un panel web.

---

## 🌟 Funcionalidades Principales

El sistema se divide en tres pilares funcionales que cubren todas las necesidades del negocio:

### 1. Gestión de Citas (Booking)
* **Reserva en Tiempo Real:** Los clientes pueden consultar la disponibilidad real de cada barbero según su especialidad.
* **Algoritmo de No-Solapamiento:** El sistema valida automáticamente que no existan conflictos horarios, calculando la duración exacta de cada servicio antes de confirmar.
* **Consulta de Disponibilidad:** Endpoint especializado que calcula los tramos horarios libres filtrando por barbero y fecha.
* **Control de Agenda:** El administrador dispone de una visión global de las citas para optimizar los tiempos de sus empleados.

### 2. Módulo de Venta y Retail (E-commerce)
* **Catálogo de Productos:** Gestión completa de productos (ceras, aceites, herramientas) con control de stock en tiempo real.
* **Carrito de Compra:** Flujo de compra integrado que permite al cliente adquirir productos junto a su cita o de forma independiente.
* **Pasarela de Pagos Segura:** Integración con la API de **Stripe** para procesar cobros con tarjeta mediante *Payment Intents*, garantizando seguridad PCI DSS.

### 3. Administración y Control de Negocio
* **Gestión de Personal:** CRUD completo de barberos, incluyendo su estado (activo/inactivo) y especialidades.
* **Control de Inventario:** Alertas de stock y actualización de precios centralizada desde el panel administrativo.
* **Reportes de Facturación:** Visualización de ingresos totales derivados tanto de servicios (citas) como de ventas directas de productos.

---

## 🛠️ Stack Tecnológico

* **Backend:** Java 17 con **Spring Boot 3** (Spring Security, Spring Data JPA).
* **Base de Datos:** **MySQL 8** con persistencia relacional normalizada.
* **Seguridad:** Autenticación **JWT (JSON Web Token)** para comunicaciones *stateless*.
* **Cifrado:** Implementación de **BCrypt** para la protección de credenciales de usuario.
* **Documentación:** **Swagger UI / OpenAPI 3.0** para pruebas e integración de clientes.

---

## ⚙️ Arquitectura del Software

Para este proyecto se ha implementado una **Arquitectura en Capas**, garantizando que la lógica de negocio esté aislada de la infraestructura:

1.  **Controladores (API REST):** Gestionan las peticiones mediante **DTOs** (Data Transfer Objects) para evitar la recursividad infinita y proteger datos sensibles.
2.  **Servicios (Business Logic):** Capa donde residen los algoritmos de validación de citas y la orquestación de pagos.
3.  **Repositorios (Persistence):** Acceso a datos mediante interfaces JPA y consultas personalizadas mediante **JPQL**.

---

## 🚀 Instalación y Uso

1.  **Clonar repositorio:** `git clone https://github.com/tu-usuario/mibarberia-api.git`
2.  **Configurar DB:** Ajustar credenciales en `src/main/resources/application.properties`.
3.  **Ejecutar:** `./mvnw spring-boot:run` o ejecutar la clase `MyStoreApplication.java` desde tu IDE.
4.  **Swagger:** Acceder a `http://localhost:8080/swagger-ui.html` para probar la API.

---

> **Nota:** Este proyecto ha sido desarrollado como Proyecto Final para el Ciclo de Grado Superior en **Desarrollo de Aplicaciones Multiplataforma (DAM)**.