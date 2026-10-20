# Object-Functional Dungeons 🏰

[![Scala Version](https://img.shields.io/badge/scala-3.3.1-red.svg?logo=scala)](https://www.scala-lang.org/)
[![Build Tool](https://img.shields.io/badge/build-sbt-CC342D.svg?logo=sbt)](https://www.scala-sbt.org/)
[![Testing](https://img.shields.io/badge/tested%20with-ScalaTest%203.2-orange.svg)](https://www.scalatest.org/)
[![Paradigm](https://img.shields.io/badge/paradigm-Object--Functional-blue.svg)](#principios-del-paradigma-objeto-funcional)
[![Course](https://img.shields.io/badge/TADP-2C%202025-brightgreen.svg)](#sobre-el-proyecto)

> **Sugerencia de denominación:** Este proyecto fue denominado inicialmente `Dungeons`. Nombres alternativos sugeridos para reflejar mejor su arquitectura y propuesta lúdica: **Object-Functional Dungeons**, **Functional Dungeons Simulator** o **Calabozos & Mónadas**.

---

## 📖 Descripción General

**Object-Functional Dungeons** es un simulador de exploración de mazmorras por turnos modelado bajo el **paradigma híbrido objeto-funcional** en Scala 3. 

Un grupo jerárquico de aventureros (guerreros, ladrones y magos) emprende expediciones a través de un calabozo modelado como un grafo de habitaciones interconectadas por puertas con múltiples cerrojos, trampas y encantamientos. La toma de decisiones está delegada en el criterio particular del líder del grupo, mientras que la resolución de conflictos (combates, emboscadas y cofres) transforma el estado de la party mediante transiciones puras e inmutables.

El proyecto fue desarrollado de forma grupal como parte de la cursada de **Técnicas Avanzadas de Programación (TADP)** en el lenguaje **Scala**.

---

## 🧩 Principios del Paradigma Objeto-Funcional

El diseño evita la mutación de estado descontrolada y combina las ventajas del modelado de dominio orientado a objetos con la expresividad y solidez del paradigma funcional:

1. **Inmutabilidad estricta**:
   - Todas las entidades del dominio (`Heroe`, `Grupo`, `Stats`, `Puerta`, `Habitacion`) están modeladas con `case class`.
   - Ningún método modifica destructivamente atributos; en su lugar, se utilizan métodos de copia (`copy`) devolviendo nuevas instancias con el nuevo estado del juego (`abrePuerta`, `subirNivel`, `cambiarVida`).

2. **Polimorfismo Paramétrico & Pattern Matching**:
   - Clasificación y resolución de obstáculos y profesiones usando `sealed trait` y pattern matching exhaustivo en tiempo de compilación.
   - Las profesiones (`Guerrero`, `Ladron`, `Mago`) y las dificultades de acceso (`Cerrada`, `Escondida`, `Encantada`) desacoplan la validación de requerimientos.

3. **Funciones de Orden Superior & Composición**:
   - Métodos funcionales sobre colecciones (`forall`, `exists`, `map`, `filterNot`, `foldLeft`, `maxBy`, `minBy`).
   - El paso de una habitación aplica un pipeline de situaciones sobre el grupo mediante `situacion.foldLeft(grupo)((g, sit) => sit(g))`.

4. **Tratamiento Funcional de Errores y Resultados**:
   - La simulación del recorrido utiliza mónadas de efecto (`Try[Grupo]`) encapsulando tanto las victorias como las muertes del grupo o callejones sin salida en un ADT `CalabozoRecorrido`.

---

## 🗺️ Modelo de Dominio

### 1. Héroes y Trabajos
Cada aventurero posee estadísticas base (`vida`, `fuerza`, `velocidad`, `nivel`), un trabajo asignado y criterios de toma de decisiones:
- **Guerrero**: Destaca en combate físico; su fuerza efectiva escala un 20% adicional por nivel del héroe.
- **Ladrón**: Especialista en forzar cerraduras; su destreza depende de la habilidad de manos multiplicada por su nivel.
- **Mago**: Domina un repertorio de hechizos y el nivel mínimo necesario para conjurarlos (ej. *vislumbrar*, *alojomora*).

### 2. Organización del Grupo (Party)
- El grupo mantiene un orden jerárquico. El **líder** es siempre el primer héroe con vida disponible.
- Todos los integrantes comparten un **cofre común** de ítems (llaves, ganzúas, tesoros).
- Si la salud de un aventurero llega a cero, deja de considerarse activo para superar obstáculos.

### 3. Puertas y Dificultades
Cada puerta conecta habitaciones y puede contener una combinación de obstáculos:
- **Cerrada**: Requiere el ítem `llave`, o bien un ladrón con ganzúas o destreza suficiente.
- **Escondida**: Exige un mago que conozca el conjuro *vislumbrar* o un ladrón con habilidad perceptiva $\ge 6$.
- **Encantada**: Demanda que un mago conozca exactamente el hechizo con el que fue sellada.
- **Maestría del ladrón**: Un ladrón con habilidad $\ge 20$ es capaz de abrir cualquier puerta sin importar sus dificultades.

### 4. Habitaciones y Situaciones
Al franquear una puerta, el grupo enfrenta una situación antes de descubrir las siguientes rutas:
- **Tesoro Perdido**: Añade ítems al cofre compartido.
- **Muchos Dardos**: Inflige daño directo a la salud de todos los integrantes.
- **Trampa de Leones**: Devora instantáneamente al aventurero con menor velocidad.
- **Encuentro con Héroe**: Situación social y táctica. Si el extraño y el líder se agradan mutuamente (según criterios: *Introvertido*, *Bigote*, *Interesado*, *Loquito*), se une al equipo; en caso contrario, se desata un combate a muerte donde el grupo puede subir de nivel o recibir daño masivo distribuido.

### 5. Algoritmo de Exploración y Criterios del Líder
El líder elige la siguiente puerta entre todas las descubiertas que aún no fueron abiertas:
- **Heroico**: Selecciona de manera impulsiva la última puerta añadida.
- **Ordenado**: Prefiere explorar sistemáticamente las puertas más antiguas de salas previas.
- **Vidente**: Simula el futuro calculando la rama que maximiza la fórmula de puntuación del grupo:
  $$\text{Puntaje} = (\text{vivos} \times 10) - (\text{muertos} \times 5) + |\text{cofre}| + \max(\text{nivel})$$

---

## 🎯 Requerimientos del Sistema

El simulador da respuesta a las 5 consultas requeridas por la especificación:

1. **Capacidad de apertura (`puedeSuperarObstaculos`)**: Valida si los integrantes y el inventario del grupo satisfacen cada obstáculo de una puerta.
2. **Impacto de sala (`ejecutarSituation`)**: Evalúa el desenlace de los eventos de una habitación sobre los recursos del grupo.
3. **Simulación de expedición (`recorrerCalabozo`)**: Ejecuta el recorrido del calabozo hasta encontrar una puerta marcada como salida o extinguirse todas las alternativas viables, retornando un `CalabozoRecorrido`.
4. **Optimización de grupo (`queGrupoEsMejor`)**: Dado un conjunto de grupos candidatos, determina cuál obtendrá el puntaje más alto al completar la travesía.
5. **Niveles requeridos (`cuantosNivelesTieneQueSubir`)**: Calcula iterativamente el incremento mínimo de nivel global (con un límite de 20) necesario para que un grupo inicialmente incapaz logre conquistar la mazmorra.

---

## 📁 Estructura del Código

```text
dungeons/
├── build.sbt                               # Configuración de Scala 3 y dependencias
├── src/
│   ├── main/scala/calabozo/
│   │   ├── Grupo.scala                     # Agregado Grupo, lógica de recorrido y simulación
│   │   ├── Habitacion.scala                # Habitaciones y catálogo de Situaciones (Dardos, Leones, etc.)
│   │   ├── Heroe.scala                     # Héroes, Stats, Trabajos, Criterios de afinidad y navegación
│   │   ├── Puerta.scala                    # Puertas y catálogo de Dificultades (Cerrada, Escondida, etc.)
│   │   └── calabozo.sc                     # Script interactivo de ejecución y demostración
│   └── test/scala/
│       ├── CalabozoFixtures.scala          # Helpers y builders para escenarios de prueba
│       ├── CalabozoTest.scala              # Pruebas integrales de recorrido y requerimientos 4 y 5
│       ├── HabitacionTest.scala            # Tests unitarios de situaciones y peligros
│       ├── PersonajesTest.scala            # Tests de profesiones, stats y criterios
│       └── PuertaTest.scala                # Tests unitarios de superación de cerrojos
```

---

## 🚀 Ejecución y Tests

### Requisitos previos
- JDK 17 o superior.
- [sbt](https://www.scala-sbt.org/) (Scala Build Tool).

### Comandos principales

Compilar el proyecto:
```bash
sbt compile
```

Ejecutar la suite completa de pruebas unitarias y de integración:
```bash
sbt test
```

Ejecutar el script de prueba interactivo:
```bash
sbt "Test / console"
```
o abrir `calabozo.sc` desde IntelliJ IDEA / VS Code con Metals.
