# Técnicas Avanzadas de Programación (TADP) - 2C 2025 🎓

[![UTN FRBA](https://img.shields.io/badge/UTN-FRBA-005BBB.svg)](https://www.frba.utn.edu.ar/)
[![Course Period](https://img.shields.io/badge/cursada-2C%202025-blue.svg)](#sobre-la-cursada)
[![Scala](https://img.shields.io/badge/scala-3.3.1-red.svg?logo=scala)](https://www.scala-lang.org/)
[![Ruby](https://img.shields.io/badge/ruby-3.x-CC342D.svg?logo=ruby)](https://www.ruby-lang.org/)
[![Paradigms](https://img.shields.io/badge/paradigms-Object--Functional%20%7C%20Metaprogramming-purple.svg)](#ejes-temáticos)

Este repositorio almacena todo el código, ejercitación práctica de clases y proyectos integradores desarrollados durante la cursada cuatrimestral de **Técnicas Avanzadas de Programación (TADP)**, dictada en el **segundo cuatrimestre de 2025** en la Universidad Tecnológica Nacional - Facultad Regional Buenos Aires (UTN-FRBA).

---

## 📚 Ejes Temáticos de la Materia

La materia profundiza en conceptos de diseño avanzado y paradigmas de desarrollo que van más allá del enfoque clásico orientado a objetos:

1. **Metaprogramación & Lenguajes Dinámicos (Ruby)**:
   - Metamodelo, introspección, reflexión computacional y despacho dinámico (`method_missing`, `send`, `instance_eval`, `instance_exec`).
   - Ciclo de vida y hooks (`inherited`, `method_added`).
   - Construcción de Lenguajes de Dominio Específico (DSLs) internos.
   - Sistemas de tipos dinámicos, multimétodos (multiple dispatch) y modelos basados en prototipos.

2. **Paradigma Híbrido Objeto-Funcional (Scala)**:
   - Inmutabilidad estricta y modelado con tipos algebraicos de datos (ADTs / `case class` / `sealed trait`).
   - Pattern Matching y polimorfismo paramétrico vs. polimorfismo ad-hoc.
   - Funciones de primera clase, orden superior, currying y composición.
   - Manejo funcional de efectos y errores con mónadas (`Option`, `Try`, `Either`).

---

## 🏆 Proyectos Integradores Grupales & Evaluaciones

A lo largo del cuatrimestre se desarrollaron dos trabajos prácticos grupales troncales. Sobre la base arquitectónica de estos mismos proyectos se incorporaron requerimientos adicionales que conformaron las instancias de **evaluación parcial**:

### 1. [Object-Functional Dungeons (`dungeons/`)](./dungeons/)
- **Lenguaje / Herramientas**: Scala 3, sbt, ScalaTest.
- **Enfoque**: Paradigma Híbrido Objeto-Funcional.
- **Descripción**: Simulador de exploración de mazmorras por turnos. Modela un grafo de habitaciones conectadas por puertas con múltiples cerrojos (cerradas, encantadas, escondidas) que son franqueadas por una party cooperativa de héroes (guerreros, ladrones, magos). Toda la simulación y resolución de eventos (trampas, dardos, leones, encuentros con otros aventureros) se resuelve de manera pura e inmutable con mónadas de resultado.
- **Examen / Requerimientos Adicionales**: Algoritmos de simulación de expediciones (`recorrerCalabozo`), optimización y selección del mejor grupo mediante scoring, y cálculo de incremento de nivel mínimo requerido por el equipo para superar la mazmorra.
- 👉 [Consultar el README detallado de Dungeons](./dungeons/README.md)

### 2. [TadPrinter - XML Parser & Serializer (`xml-parser/`)](./xml-parser/)
- **Lenguaje / Herramientas**: Ruby 3, Bundler, RSpec.
- **Enfoque**: Metaprogramación, DSLs internos y Metadata.
- **Descripción**: Framework declarativo de generación y serialización de objetos a XML. Provee un DSL nativo en Ruby para describir documentos anidados mediante bloques y un motor de introspección automática basado en variables de instancia y getters.
- **Examen / Requerimientos Adicionales**: Sistema de anotaciones sintácticas con chispitas (`✨Annotation✨`) integradas al metamodelo de clases y métodos (`✨Label✨`, `✨Ignore✨`, `✨Inline✨`, `✨Custom✨`, `✨Extra✨`), junto con el diseño y testeo de extensiones avanzadas (`✨Validate✨`, `✨Default✨`, `✨Mask✨`).
- 👉 [Consultar el README detallado de TadPrinter](./xml-parser/README.md)

---

## 🗂️ Estructura del Repositorio

```text
cursada-tadp-2025/
├── dungeons/                       # TP Integrador en Scala 3: Minijuego / simulador de calabozos
│   ├── src/main/scala/calabozo/    # Modelo de héroes, grupos, puertas, habitaciones y juego
│   ├── src/test/scala/             # Batería de pruebas unitarias y de integración (ScalaTest)
│   └── README.md                   # Documentación técnica del proyecto Dungeons
│
├── xml-parser/                     # TP Integrador en Ruby: Framework TadPrinter (DSL + Serializador XML)
│   ├── lib/                        # Core de anotaciones, serialización recursiva y DSL
│   ├── nuevas_anotaciones.rb       # Extensiones adicionales (Validate, Default, Mask)
│   └── README.md                   # Documentación técnica del proyecto TadPrinter
│
├── ruby/                           # Prácticas y ejercitación de clase en Ruby
│   └── Clases/
│       ├── src/                    # Prototipos, multimétodos (multiple dispatch), tutoriales
│       └── spec/                   # Pruebas automatizadas en RSpec
│
├── scala/                          # Prácticas y ejercitación de clase en Scala
│   ├── clase1/                     # Introducción a la sintaxis y tipos
│   ├── clase2/                     # Herencia, traits y modelado (Granja, Animales)
│   ├── clase3/                     # Funciones de orden superior y colecciones
│   ├── clase4/                     # Pattern matching exhaustivo y recursión
│   ├── clase5a/                    # Modelado funcional aplicado
│   ├── clase5B/                    # Caso de estudio: Sistema de Pociones
│   └── pokemon-version/            # Modelado de combates Pokémon objeto-funcional
│
└── README.md                       # Índice y presentación general del repositorio
```

---

## 🛠️ Tecnologías y Entorno de Desarrollo

- **Ruby**: Versión 3.x con Bundler y RSpec.
- **Scala**: Versión 3.3.1 con sbt (Scala Build Tool) y ScalaTest 3.2.
- **JDK**: Java 17 LTS o superior.
- **IDEs recomendados**: IntelliJ IDEA (con plugins de Scala y Ruby) o VS Code (con Metals y Ruby LSP).
