# Food Store - Sistema de Gestión de Pedidos de Comida

Trabajo Práctico Integrador - Programación 2  
Tecnicatura Universitaria en Programación a Distancia - UTN FRM

## Descripción

Food Store es una aplicación de consola desarrollada en Java para la gestión de
un negocio de comidas. El sistema permite administrar categorías, productos,
usuarios y pedidos mediante operaciones CRUD, aplicando Programación Orientada a
Objetos, persistencia con JDBC y una arquitectura organizada por capas.

El proyecto se desarrolla siguiendo la consigna del TPI de Programación 2, con
persistencia en una base de datos relacional MySQL y acceso a las funcionalidades
desde un menú de consola.

## Objetivo del proyecto

El objetivo principal es implementar una solución funcional que permita:

- Modelar entidades del dominio respetando el UML provisto.
- Aplicar herencia, encapsulamiento, constructores y métodos sobrescritos.
- Separar responsabilidades mediante capas de entidades, DAO, servicios y menú.
- Persistir datos en MySQL utilizando JDBC, SQL parametrizado y transacciones.
- Gestionar errores y validaciones básicas desde los servicios y la interfaz de consola.
- Implementar baja lógica mediante el campo `eliminado`.

## Equipo de trabajo y distribución de responsabilidades

El proyecto se desarrolla de forma colaborativa. Cada integrante tiene asignado
un conjunto de tareas específicas, cubriendo desde el modelo y la persistencia
hasta los servicios y la integración con el menú de consola.

| Integrante | Módulo asignado | Responsabilidades |
|---|---|---|
| Blangetti, Sofía | Productos y usuarios | Implementación del CRUD de productos y usuarios, clases DAO correspondientes, servicios, opciones de menú, validaciones de entrada y control de mail único para usuarios. |
| Avalos, Pablo | Pedidos, detalles e integración general | Implementación del CRUD de pedidos y detalles, uso de `addDetallePedido`, cálculo de totales, manejo transaccional con `commit` y `rollback`, `GenericService<T>` y menú principal `AppMenu`. |
| Suárez, Ismael | Base del sistema y categorías | Implementación del script `schema.sql`, datos de prueba, configuración de conexión JDBC, `persistence.xml`, driver MySQL en `pom.xml`, clases base del dominio, enums, `IBaseDAO<T>` y CRUD de categorías. |

Además de las tareas individuales, el equipo trabaja de forma conjunta en la
documentación académica, las pruebas de integración, la revisión final del
proyecto y la preparación de la entrega.

## Tecnologías utilizadas

- Java 21
- Maven
- JDBC
- MySQL 8
- SQL

## Requisitos previos

Para ejecutar el proyecto es necesario contar con:

- JDK 21 o superior.
- Maven 3.9 o superior.
- MySQL 8 o superior en ejecución.
- Un usuario de MySQL con permisos para crear bases de datos, tablas e insertar datos.

## Estructura del proyecto

```text
foodStore/
├── pom.xml
├── README.md
└── src/
    ├── docs/
    │   └── Consigna y pautas de corrección
    └── main/
        ├── java/integrador/prog2/
        │   ├── Main.java
        │   ├── config/
        │   ├── dao/
        │   ├── entities/
        │   ├── enums/
        │   ├── exception/
        │   └── service/
        └── resources/
            └── schema.sql
```

## Arquitectura

El sistema se organiza en capas para mantener responsabilidades claras:

- `Main` y `AppMenu`: interacción con el usuario, lectura de datos por consola y navegación de menús.
- `service`: validaciones, reglas de negocio simples y coordinación de operaciones.
- `dao`: acceso a datos mediante JDBC, consultas SQL y mapeo entre tablas y objetos.
- `entities`: modelo de dominio basado en el UML.
- `config`: configuración centralizada de conexión a la base de datos.
- `exception`: excepciones propias para errores del dominio o de persistencia.

## Modelo de dominio

Todas las entidades principales heredan de la clase abstracta `Base`, que contiene:

- `id`
- `eliminado`
- `createdAt`

| Entidad | Atributos principales | Relaciones |
|---|---|---|
| Categoria | nombre, descripcion | Relación 1:N con Producto |
| Producto | nombre, precio, descripcion, stock, imagen, disponible | Relación N:1 con Categoria |
| Usuario | nombre, apellido, mail, celular, contraseña, rol | Relación 1:N con Pedido |
| Pedido | fecha, estado, total, formaPago | Relación N:1 con Usuario y composición 1:N con DetallePedido |
| DetallePedido | cantidad, subtotal | Relación N:1 con Producto |

Enums utilizados:

- `Rol`: ADMIN, USUARIO
- `Estado`: PENDIENTE, CONFIRMADO, TERMINADO, CANCELADO
- `FormaPago`: TARJETA, TRANSFERENCIA, EFECTIVO

La clase `Pedido` implementa la interfaz `Calculable` para calcular el total a
partir de sus detalles.

## Base de datos

El archivo `src/main/resources/schema.sql` contiene el script de creación de la
base de datos, las tablas principales y datos iniciales de prueba.

Tablas incluidas:

- `categoria`
- `producto`
- `usuario`
- `pedido`
- `detalle_pedido`

El esquema utiliza claves primarias, claves foráneas, restricciones de unicidad,
validaciones con `CHECK` y baja lógica mediante el campo `eliminado`.

## Configuración de la base de datos

Ejecutar el script SQL desde la raíz del proyecto:

```bash
mysql -u root -p < src/main/resources/schema.sql
```

El script crea la base de datos `pedidos_db` e inserta registros de prueba.

Luego se deben configurar las credenciales de conexión en la capa de configuración
del proyecto, mediante `config/ConexionDB` y el archivo `persistence.xml` si
corresponde.

## Compilación y ejecución

Compilar el proyecto:

```bash
mvn clean compile
```

Ejecutar la aplicación:

```bash
mvn exec:java -Dexec.mainClass="integrador.prog2.Main"
```

También puede ejecutarse desde un IDE compatible con Java 21, iniciando la clase
`integrador.prog2.Main`.

## Menú principal esperado

Al iniciar la aplicación se debe mostrar un menú similar al siguiente:

```text
=== SISTEMA DE PEDIDOS (FOOD STORE) ===
1. Categorías
2. Productos
3. Usuarios
4. Pedidos
0. Salir
Seleccione:
```

Cada opción debe abrir un submenú CRUD con operaciones para listar, crear,
editar y eliminar registros.

## Funcionalidades requeridas

### Categorías

- Listar categorías no eliminadas.
- Crear categorías con nombre y descripción.
- Validar nombre obligatorio y único.
- Editar categorías existentes.
- Eliminar categorías mediante baja lógica.

### Productos

- Listar productos no eliminados.
- Crear productos asociados a una categoría existente.
- Validar nombre obligatorio, precio >= 0 y stock >= 0.
- Editar datos del producto.
- Eliminar productos mediante baja lógica.

### Usuarios

- Listar usuarios no eliminados.
- Crear usuarios con mail único.
- Editar datos del usuario.
- Eliminar usuarios mediante baja lógica.
- Conservar el historial de pedidos asociados.

### Pedidos

- Listar pedidos no eliminados.
- Crear pedidos asociados a un usuario existente.
- Agregar uno o más detalles al pedido.
- Calcular subtotales y total mediante `addDetallePedido` y `calcularTotal`.
- Actualizar estado y forma de pago.
- Eliminar pedidos mediante baja lógica.
- Utilizar transacciones JDBC con `commit` y `rollback` al crear pedidos con detalles.

## Reglas de negocio principales

- No se permite crear productos con precio negativo.
- No se permite crear productos con stock negativo.
- No se permite crear pedidos sin usuario.
- No se permite crear detalles con cantidad menor o igual a cero.
- El mail de usuario debe ser único.
- Las eliminaciones deben realizarse mediante baja lógica.
- Las operaciones de pedido con detalles deben mantener consistencia transaccional.

