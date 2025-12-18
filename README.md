

# epersgeist-distributed-persistence

Sistema distribuido orientado a eventos con Apache Kafka y persistencia poliglota.

Este proyecto modela un dominio compuesto por **espíritus, médiums y ubicaciones**,
evolucionando progresivamente desde un enfoque monolítico hacia una arquitectura
distribuida basada en eventos. El objetivo principal es explorar distintas estrategias
de persistencia y comunicación entre componentes, priorizando claridad conceptual,
diseño y desacoplamiento.

---

## Descripción general

Epersgeist es un sistema que representa entidades del mundo espiritual y sus interacciones,
permitiendo modelar comportamientos complejos a través de distintos mecanismos de
persistencia y mensajería.

A lo largo de su evolución, el proyecto incorpora múltiples tecnologías de bases de datos
y finalmente adopta una arquitectura **event-driven**, donde distintos servicios se
comunican de manera asincrónica utilizando **Apache Kafka**.

---

## Arquitectura

En su etapa final, el sistema se organiza como un **sistema distribuido simple**, compuesto
por múltiples servicios que se ejecutan de forma independiente y se comunican exclusivamente
mediante eventos.

Los servicios no se invocan entre sí de manera directa ni comparten código, lo que reduce
el acoplamiento y permite una evolución más flexible del sistema.

### Servicios

- **Epersgeist (core)**  
  Servicio principal del dominio. Modela espíritus, médiums y ubicaciones, y actúa como
  orquestador del comportamiento central del sistema.

- **Servicio de mensajería**  
  Encargado de la emisión y consumo de eventos relacionados con la comunicación entre
  entidades del dominio.

- **Servicio de temperatura**  
  Publica y procesa eventos asociados a cambios de temperatura en ubicaciones.

- **Servicio de probabilidad**  
  Consume eventos del sistema y realiza cálculos probabilísticos derivados de los mismos.

### Comunicación

- Comunicación asincrónica mediante **Apache Kafka**
- Interacción basada en eventos

---

## Persistencia

El proyecto utiliza un enfoque de **persistencia poliglota**, seleccionando distintas
tecnologías según la naturaleza de los datos:

- **PostgreSQL**: dominio principal
- **MongoDB**: coordenadas
- **Neo4j**: relaciones entre ubicaciones

---

## Tecnologías utilizadas

- Java
- Spring Boot
- Apache Kafka
- PostgreSQL
- MongoDB
- Neo4j
- Docker
- Docker Compose

---

## Ejecución del proyecto

### Prerrequisitos

- Docker
- Docker Compose

No es necesario instalar Java, Maven, Kafka ni bases de datos de forma local.
Todo el entorno se ejecuta mediante contenedores.

---

### Perfiles disponibles

El proyecto utiliza **Docker Compose profiles** para permitir distintos escenarios
de ejecución:

- `kafka` → Kafka, Zookeeper y Kafka UI
- `sql` → PostgreSQL
- `nosql` → MongoDB
- `graph` → Neo4j
- `app` → Servicio principal Epersgeist (monolito del dominio)
- `micro` → Microservicios (mensajería, temperatura y probabilidad)

---

### Ejecución completa (recomendada)

Para levantar todo el sistema (bases de datos, Kafka, servicio principal y microservicios):

```bash
docker compose --profile kafka --profile sql --profile nosql --profile graph --profile app --profile micro up



