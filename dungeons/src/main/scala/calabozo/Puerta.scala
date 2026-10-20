package calabozo

case class Puerta(dificultades: List[Dificultad], habitacion: Habitacion = null, salida: Boolean = false) {
  def puedeSuperarObstaculos(grupo: Grupo): Boolean = dificultades.forall { _.puedeSuperar(grupo) }

  def queGrupoEsMejor(grupos: List[Grupo]): Grupo = grupos.maxBy(_.puntajeAlAbrir(this))
}

sealed trait Dificultad {
  def puedeSuperar(grupo:Grupo): Boolean = reglas.exists(_(grupo))

  def reglas:List[Grupo=>Boolean] = List(algunLadronConNivel(_,20))

  def algunLadronConNivel(grupo: Grupo, n:Int):Boolean = {
    grupo.heroes.exists(heroe => heroe.trabajo match {
      case ladron : Ladron if ladron.habilidadAbrirPuertas(heroe) >= n => true
      case _ => false
    })
  }

  def algunMagoConHechizo(grupo: Grupo, hechizo: Hechizo): Boolean = {
    grupo.heroes.exists(heroe => heroe.trabajo match {
      case m: Mago if m.puedeUsarHechizo(hechizo, heroe) => true
      case _ => false
    })
  }

  def grupoTieneItem(grupo: Grupo, item: Item): Boolean = grupo.cofre.contains(item)
}
case class Cerrada(nivelRequerido: Int) extends Dificultad {
  override def reglas: List[Grupo => Boolean] =
    super.reglas :+ (g => grupoTieneItem(g, Item("llave")) || algunLadronConNivel(g,0) && grupoTieneItem(g, Item("ganzua")))
}
case class Encantada(hechizoUsado: Hechizo) extends Dificultad {
  override def reglas: List[Grupo => Boolean] =
    super.reglas :+ (algunMagoConHechizo(_, hechizoUsado))
}
case object Escondida extends Dificultad {
  override def reglas: List[Grupo => Boolean] =
    super.reglas :+ (g => algunMagoConHechizo(g, Hechizo("vislumbrar")) || algunLadronConNivel(g, 6))
}