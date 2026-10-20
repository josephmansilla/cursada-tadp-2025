# Requerimiento extra: nueva anotacion

Se debe agregar una anotacion adicional al framework de metaprogramacion. La anotacion debe:
- Integrarse con el mecanismo actual de invocacion (?Nombre?) y poder aplicarse en atributos/metodos o clases segun corresponda.
- Demostrar su comportamiento con pruebas automatizadas nuevas o ampliadas.
- Ser distinta de Label, Ignore, Inline, Custom y Extra.

Ideas sugeridas para elegir una:
1. ?Validate? { |valor| condicion }: Evalua el valor con la condicion dada; si falla, puede levantar InvalidAnnotation o descartar el atributo durante el procesamiento. Permite indicar un mensaje personalizado opcional.
2. ?Default? valor_por_defecto o ?Default? { ... }: Reemplaza nil o valores faltantes por el valor por defecto indicado; acepta bloque para calcularlo de forma lazy y admite valores por clase o por atributo.
3. ?Mask? visible: N, con bloque opcional: Enmascara strings u objetos sensibles dejando visibles solo los ultimos N caracteres (o el resultado del bloque), util para datos como tarjetas o tokens antes de serializar.
