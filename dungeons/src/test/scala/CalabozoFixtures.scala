import calabozo._

object CalabozoFixtures {
  val alojo: Hechizo = Hechizo("alojomora")
  val vislumbrar: Hechizo = Hechizo("vislumbrar")
  val llave: Item = Item("llave")
  val ganzua: Item = Item("ganzua")

  def ladron(
      nivel: Int,
      habilidad: Int = 5,
      vida: Double = 100,
      fuerza: Double = 10,
      velocidad: Double = 10,
      criterioPuerta: CriterioDePuerta = Ordenado
  ): Heroe =
    Heroe(Stats(vida, fuerza, velocidad, nivel), Ladron(habilidad), List(Introvertido), criterioPuerta)

  def mago(
      nivel: Int,
      hechizos: Map[Hechizo, Int],
      vida: Double = 80,
      fuerza: Double = 6,
      velocidad: Double = 8,
      criterioPuerta: CriterioDePuerta = Ordenado
  ): Heroe =
    Heroe(Stats(vida, fuerza, velocidad, nivel), Mago(hechizos), List(Introvertido), criterioPuerta)

  def guerrero(
      nivel: Int,
      vida: Double = 110,
      fuerza: Double = 15,
      velocidad: Double = 8,
      criterioPuerta: CriterioDePuerta = Ordenado
  ): Heroe =
    Heroe(Stats(vida, fuerza, velocidad, nivel), Guerrero(), List(Introvertido), criterioPuerta)

  def grupo(heroes: Heroe*): Grupo = Grupo(heroes.toList)

  def puertaSalida(dificultades: List[Dificultad]): Puerta = Puerta(dificultades, salida = true)

  def puertaConHabitacion(dificultades: List[Dificultad], habitacion: Habitacion): Puerta =
    Puerta(dificultades, habitacion)

  def habitacionConTesoroYPuertaSalida(item: Item): Habitacion = {
    val puertaFinal = Puerta(List.empty, salida = true)
    Habitacion(puertas = List(puertaFinal), situacion = List(TesoroPerdido(item)))
  }
}
