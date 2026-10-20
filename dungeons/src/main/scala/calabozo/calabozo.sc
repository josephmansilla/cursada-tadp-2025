import calabozo.*

val lider = Heroe(
  stats = Stats(20, 15, 24, 100),
  trabajo = Ladron(7),
  criterioDeGrupo = List(Introvertido),
  criterioDePuerta = Heroico
)
val mago = Heroe(
  stats = Stats(12, 10, 16, 100),
  trabajo = Mago(Map(Hechizo("vislumbrar") -> 15, Hechizo("alojomora") -> 16)),
  criterioDeGrupo = List(Bigote),
  criterioDePuerta = Vidente
)
val heroePerdido = Heroe(
  stats = Stats(17, 16, 13, 80),
  trabajo = Ladron(21),
  criterioDeGrupo = List(Introvertido),
  criterioDePuerta = Ordenado
)

val grupo = Grupo(List(lider, mago))

val habitacion2 = Habitacion(situacion = List(Encuentro(heroePerdido)))
val habitacion3 = Habitacion(
  puertas = List(Puerta(List(Cerrada(20), Encantada(Hechizo("alojomora"))), salida = true)),
  situacion = List(Dardos)
)

val habitacion = Habitacion(
  puertas = List(
    Puerta(List(Cerrada(7)), habitacion2),
    Puerta(List.empty, habitacion3)
  ),
  situacion = List(TesoroPerdido(Item("ganzua")))
)
val puertaEntrada = Puerta(List(Escondida, Encantada(Hechizo("alojomora"))), habitacion)

/** 1. */
println("1. Puede abrir puerta de entrada? - "+puertaEntrada.puedeSuperarObstaculos(grupo))

/** 2. */
println("2. Estado de grupo luego de visitar habitacion2? - " + habitacion2.ejecutarSituation(grupo))

/** 3. */
val calabozoRecorrido = grupo.recorrerCalabozo(puertaEntrada)
println("3.  Exito/Fallo: " + (if calabozoRecorrido.resultado.isSuccess then "Success" else "Failure"))
println("    Estado del grupo: " + calabozoRecorrido.grupoModificado)
println("    Puntaje del grupo: " + calabozoRecorrido.grupoModificado.puntaje())

/** 4 */
println("4. Que grupo es mejor para calabozo? - "+ puertaEntrada.queGrupoEsMejor(List(grupo)))

/** 5 */
val liderManco = Heroe(Stats(20, 15, 2, 100), Ladron(3), List(Introvertido), Heroico)
val magoManco = Heroe(Stats(12, 10, 2, 100), Mago(Map(Hechizo("vislumbrar") -> 15, Hechizo("alojomora") -> 16)), List(Bigote), Vidente)
val grupoManco = Grupo(List(liderManco, magoManco))
println("5. Cuantos niveles tiene que subir? - "+ grupoManco.cuantosNivelesTieneQueSubir(puertaEntrada))
