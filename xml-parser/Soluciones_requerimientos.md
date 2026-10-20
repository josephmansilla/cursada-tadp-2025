# Soluciones propuestas para las nuevas anotaciones

## ?Validate?
- Proposito: validar el valor de un atributo/metodo; si la condicion falla, aborta el atributo o dispara `InvalidAnnotation` con mensaje.
- Uso ejemplo:
  ```ruby
  class Alumno
    ?Validate? { |v| v.is_a?(String) && !v.empty? }
    attr_accessor :nombre
  end
  ```
- Comportamiento:
  - Evalua el bloque con el valor; si retorna `true`, deja pasar `{ key, value }`.
  - Si retorna `false` o levanta error, retorna `nil` para omitir el atributo o levanta `InvalidAnnotation` con detalle.
  - Se puede pasar un mensaje opcional: `?Validate?("mensaje") { |v| ... }`.
- Casos borde: valores `nil`, tipos no esperados, bloques que explotan.
- Pruebas sugeridas: valida true/false, omision de atributo, error custom, combinacion con `Label` y `Inline`.

## ?Default?
- Proposito: sustituir `nil` o ausencia por un valor por defecto, eager o lazy.
- Uso ejemplo:
  ```ruby
  class Alumno
    ?Default? "N/D"
    attr_accessor :telefono

    ?Default? { Time.now.year }
    attr_accessor :ingreso
  end
  ```
- Comportamiento:
  - Si el valor es `nil`, reemplaza por literal o `block.call`.
  - Si hay valor no-nil, lo deja intacto.
  - Puede aplicarse a clase para setear defaults compartidos.
- Casos borde: bloque que falla, valores falsy (false) no deben ser reemplazados, valores complejos.
- Pruebas sugeridas: nil -> default, false/no cambio, bloque lazy ejecutado una sola vez por atributo.

## ?Mask?
- Proposito: enmascarar datos sensibles dejando visibles ultimos N caracteres o usando bloque custom.
- Uso ejemplo:
  ```ruby
  class Alumno
    ?Mask?(visible: 4)
    attr_accessor :dni

    ?Mask? { |v| "****-#{v.to_s[-2,2]}" }
    attr_accessor :token
  end
  ```
- Comportamiento:
  - Si `visible: N`, para strings muestra `*` en todos salvo los ultimos N; para numeros castea a string.
  - Si se pasa bloque, usa el resultado del bloque.
  - Para valores cortos, deja solo el ultimo caracter si N>=1; si N=0, todo enmascarado.
- Casos borde: valores no-string, arrays (deberian serializarse sin mask), nil (retorna nil), longitud menor a N.
- Pruebas sugeridas: string largo, corto, numerico, bloque custom, combinacion con `Label` y `Ignore`.
