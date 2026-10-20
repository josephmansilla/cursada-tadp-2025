# TadPrinter: Declarative XML DSL & Annotation-Driven Serializer ✨

[![Ruby Version](https://img.shields.io/badge/ruby-3.x-red.svg?logo=ruby)](https://www.ruby-lang.org/)
[![Metaprogramming](https://img.shields.io/badge/metaprogramming-DSL%20%26%20Reflection-blueviolet.svg)](#mecanismos-de-metaprogramación-en-ruby)
[![Course](https://img.shields.io/badge/TADP-2C%202025-brightgreen.svg)](#sobre-el-proyecto)
[![Architecture](https://img.shields.io/badge/architecture-AST%20Builder%20%2B%20Metadata-blue.svg)](#arquitectura-del-sistema)

> **Sugerencia de denominación:** Este proyecto fue identificado en el repositorio como `xml-parser`, mientras que en su especificación original lleva el nombre de **TadPrinter** (*TADP XML Printer & Serializer Framework*).

---

## 📖 Descripción General

**TadPrinter** es un framework de serialización y generación de XML en Ruby basado íntegramente en técnicas avanzadas de **metaprogramación**, **construcción de DSLs internos** e **inyección dinámica de metadatos**.

El sistema resuelve dos desafíos complementarios:
1. **Definición declarativa manual**: Un Lenguaje de Dominio Específico (DSL) limpio que permite describir documentos XML anidados con atributos e hijos mediante bloques de código Ruby nativo sin recurrir a sintaxis externas ni parsers de texto.
2. **Serialización automática extensible**: Un motor de introspección capaz de transformar cualquier objeto en su representación XML correspondiente, parametrizable mediante un sistema de **Annotations dinámicas** delimitadas sintácticamente (`✨Annotation✨`).

El proyecto fue desarrollado de forma grupal como parte de la cursada de **Técnicas Avanzadas de Programación (TADP)** en el lenguaje **Ruby**.

---

## ⚙️ Mecanismos de Metaprogramación en Ruby

Para lograr una sintaxis limpia y desacoplada de dependencias externas, el framework explota al máximo las capacidades del metamodelo de Ruby:

1. **DSLs con Evaluación de Contexto (`instance_exec` & `instance_eval`)**:
   - Los bloques pasados a `Document.new` o `Tag` se ejecutan dentro del ámbito de una instancia del constructor de nodos, logrando que los nombres de los métodos invoquen dinámicamente la creación de etiquetas.
2. **Despacho Dinámico de Mensajes (`method_missing` & `respond_to_missing?`)**:
   - En el DSL, cualquier mensaje desconocido se interpreta como la creación de una nueva etiqueta XML cuyos argumentos con clave-valor pasan a ser sus atributos.
   - En el sistema de anotaciones, mensajes que coinciden con el patrón `✨Nombre✨` activan el ciclo de vida de metadatos.
3. **Hooks del Ciclo de Vida del Metamodelo**:
   - `Module#method_added`: Intercepta la definición de métodos para vincularles las anotaciones pendientes que hayan sido declaradas inmediatamente arriba.
   - `Class#inherited`: Propaga o asocia anotaciones pendientes a clases derivadas.
   - Envoltura de macros (`attr_reader` y `attr_accessor`): Permite etiquetar accesores generados automáticamente sin alterar la API nativa de Ruby.
4. **Introspección y Reflexión de Estado**:
   - Consulta dinámica de variables de instancia (`instance_variables`), resolución de getters existentes con `respond_to?` y envío dinámico de mensajes con `send`.
5. **Generación de Clases Anónimas y Singleton Methods**:
   - Las anotaciones complejas (`✨Custom✨`, `✨Extra✨`) crean clases anónimas o inyectan métodos singleton en caliente para renderizar subárboles ad-hoc.

---

## 🏗️ Requerimientos y Paso a Paso

### Punto 1: DSL Declarativo de Impresión
Permite construir estructuras XML completas con una sintaxis fluida y legible:

```ruby
documento = Document.new do
  alumno nombre: "Matias", legajo: "123456-7" do
    telefono { "1234567890" }
    estado es_regular: true do
      finales_rendidos { 3 }
      materias_aprobadas { 5 }
    end
  end
end

puts documento.xml
```

Genera:
```xml
<alumno nombre="Matias" legajo="123456-7">
	<telefono>
		1234567890
	</telefono>
	<estado es_regular=true>
		<finales_rendidos>
			3
		</finales_rendidos>
		<materias_aprobadas>
			5
		</materias_aprobadas>
	</estado>
</alumno>
```

---

### Punto 2: Generación Automática (`Document.serialize`)
El serializador examina cualquier instancia y deduce su estructura según las reglas:
- El tag raíz adopta el nombre de la clase (en minúsculas).
- Solo se serializan atributos que posean un **getter** definido.
- Tipos primitivos (`String`, `Numeric`, `Boolean`, `nil`) se serializan como **atributos** del tag.
- Colecciones (`Array`) se serializan como etiquetas hijas individuales con el nombre de clase de sus elementos.
- Objetos complejos anidados se serializan recursivamente como etiquetas hijas con el nombre del atributo.

```ruby
unEstado = Estado.new(3, 5, true)
unAlumno = Alumno.new("Matias", "123456-8", "1234567890", unEstado)

doc = Document.serialize(unAlumno)
puts doc.xml
```

---

### Punto 3: Metadatos y Sistema de Anotaciones

Las anotaciones se declaran inmediatamente antes de una clase, método o declaración de atributo utilizando el delimitador de chispitas (`✨`). El framework busca la subclase de `Annotation` correspondiente, la instancia con los argumentos o bloque recibidos y la almacena temporalmente en `AnnotationManager` para enlazarla a la siguiente definición:

```ruby
✨Label✨("estudiante")
class Alumno
  ✨Inline✨ { |c| c.upcase }
  attr_reader :nombre

  ✨Ignore✨
  attr_reader :telefono

  ✨Label✨("situacion_academica")
  attr_reader :estado
end
```

#### Catálogo de anotaciones provistas:
| Anotación | Ámbito | Descripción |
|---|---|---|
| `✨Label✨(nuevo_nombre)` | Clase o Atributo | Sobrescribe el nombre del tag o del atributo en el XML resultante. Detecta colisiones de atributos duplicados. |
| `✨Ignore✨` | Clase o Atributo | Omite el campo de la serialización o produce un string vacío si se aplica a toda la clase. |
| `✨Inline✨ { \|v\| ... }` | Atributo | Aplana un objeto complejo transformándolo en atributo escalar evaluando el bloque provisto. |
| `✨Custom✨ do \|instancia\| ... end` | Clase | Reemplaza la estrategia automática estándar por un sub-DSL definido por el desarrollador. |
| `✨Extra✨(:metodo) { ... }` | Clase | Inyecta un método sintético evaluado en el contexto del objeto para ser serializado como campo adicional. |
| `✨Expand✨ { \|v\| ... }` | Atributo | Transforma y expande colecciones o estructuras antes de ser emitidas. |

---

### Punto 4: Nuevas Anotaciones y Extensibilidad

El diseño modular de `Annotation` permite agregar nuevas directivas sin modificar el núcleo del serializador:

- **`✨Validate✨ { |v| ... }`**: Evalúa una condición sobre el valor antes de serializarlo. Si la validación no se cumple, descarta el atributo o eleva una excepción según la configuración.
- **`✨Default✨(valor_o_bloque)`**: Reemplaza valores nulos (`nil`) o ausentes por un valor constante o calculado de forma perezosa (*lazy*).
- **`✨Mask✨(visible: N)`**: Enmascara datos sensibles (DNI, tarjetas de crédito, contraseñas o tokens), ocultando los primeros caracteres con asteriscos y preservando solo los últimos $N$.

Ejemplo de uso de las nuevas anotaciones:
```ruby
class AlumnoSeguro
  ✨Validate✨ { |v| v.is_a?(String) && !v.strip.empty? }
  attr_accessor :nombre

  ✨Mask✨(visible: 3)
  attr_accessor :legajo

  ✨Default✨("Sin teléfono")
  attr_accessor :telefono

  ✨Mask✨(visible: 4)
  attr_accessor :dni
end
```

---

## 📁 Estructura del Proyecto

```text
xml-parser/
├── Gemfile                                 # Especificación de gemas y dependencias
├── nuevas_anotaciones.rb                   # Implementación de ✨Validate✨, ✨Default✨ y ✨Mask✨
├── Requerimiento_nueva_annotation.md       # Consigna del requerimiento de extensión
├── Soluciones_requerimientos.md            # Análisis de diseño y casos borde de nuevas anotaciones
└── lib/
    ├── Annotations.rb                      # Core de anotaciones, interceptores y lifecycle hooks
    ├── Document.rb                         # Constructor del AST, DSL y facade de serialización
    ├── Main.rb                             # Script de prueba y casos de integración
    ├── Serializer.rb                       # Motor de introspección, jerarquía de tipos y serialización
    └── Tag.rb                              # Representación del nodo XML y generador del String final
```

---

## 🚀 Ejecución y Pruebas

### Requisitos previos
- Ruby 3.0 o superior instalado en el entorno.
- Bundler (`gem install bundler`).

### Instalación de dependencias
```bash
bundle install
```

### Ejecución de demostraciones
Para ejecutar el script de verificación con ejemplos del DSL, serialización automática y anotaciones avanzadas:

```bash
ruby lib/Main.rb
```
