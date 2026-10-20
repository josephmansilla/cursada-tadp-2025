package calabozo

case class Habitacion(puertas: List[Puerta] = List(), situacion: List[Situacion]) {
  private var situacionesEjecutadas = false
  def ejecutarSituation(grupo: Grupo):Grupo = {
    if (situacionesEjecutadas) return grupo

    situacionesEjecutadas = true
    situacion.foldLeft(grupo)((g, sit) => sit(g)).agregarPuertasCerradas(puertas)
  }
}

sealed trait Situacion {
  def apply(grupo: Grupo): Grupo = grupo
}

case object Dardos extends Situacion {
  override def apply(grupo: Grupo): Grupo = {
    grupo.copy(heroes = grupo.heroes.map(
      h => h.copy(stats = h.stats.cambiarVida(-10))))
  }
}
case class Encuentro(var heroePerdido: Heroe) extends Situacion {
  override def apply(grupo: Grupo): Grupo = {
    val grupoModificado: Grupo = grupo.copy(heroes = grupo.heroes :+ heroePerdido)

    if heroePerdido.leAgradaElGrupo(grupo) && grupo.lider().leAgradaElGrupo(grupoModificado) then
      grupoModificado
    else
      pelea(grupo)
  }

  def pelea(grupo: Grupo): Grupo = {
    if (grupo.heroes.map(_.stats.fuerza).sum > heroePerdido.stats.fuerza) then
      grupo.copy(heroes = grupo.heroes.map(h => h.copy(stats = h.stats.subirNivel(1))))
    else
      grupo.copy(heroes = grupo.heroes.map(
        h => h.copy(stats = h.stats.cambiarVida(-heroePerdido.stats.fuerza.toInt / grupo.heroes.size)))
      )
  }
}


case object TrampaLeon extends Situacion {
  override def apply(grupo: Grupo): Grupo = {
    grupo.copy(heroes = grupo.heroes.map(
      h => if h == grupo.heroes.minBy(_.stats.velocidad) then h.copy(stats = h.stats.matar()) else h))
  }
}

case class TesoroPerdido(itemRecompensa: Item) extends Situacion {
  override def apply(grupo: Grupo): Grupo = {
    grupo.copy(cofre = grupo.cofre :+ itemRecompensa)
  }
}

//case object Nada extends Situacion {
//  override def apply(grupo:Grupo):Grupo = grupo
//}
