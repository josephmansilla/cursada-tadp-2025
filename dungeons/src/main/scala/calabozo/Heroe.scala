package calabozo

case class Hechizo(nombre: String)
case class Item(nombre: String)
case class Stats (vida: Double, fuerza: Double, velocidad: Double, nivel: Int) {

  def subirNivel(cantidad:Int):Stats = this.copy(nivel = nivel + cantidad)

  def cambiarVida(cantidad:Double):Stats = this.copy(vida = (vida - cantidad).max(0))
  def matar():Stats = this.copy(vida = 0)
  def estaVivo():Boolean = vida > 0
}

case class Heroe(
                  stats: Stats,
                  trabajo: Trabajo,
                  criterioDeGrupo: List[CriterioDeGrupo],
                  criterioDePuerta: CriterioDePuerta
) {
  lazy val vida: Double = this.stats.vida

  def cambiarDeTrabajo(t: Trabajo): Heroe = { copy(trabajo = t) }
  def leAgradaElGrupo(grupo: Grupo): Boolean = criterioDeGrupo.forall(criterio => criterio(grupo))
  def elegirProximaPuerta(grupo: Grupo): Puerta = criterioDePuerta(grupo.puertasCerradas, grupo)
}


sealed trait Trabajo
case class Guerrero() extends Trabajo {
  def fuerza(h: Heroe): Double = h.stats.fuerza + h.stats.fuerza*1.2*h.stats.nivel
}
case class Ladron(habilidadManos: Int) extends Trabajo {
  def habilidadAbrirPuertas(h:Heroe):Int = {
    habilidadManos * h.stats.nivel
  }
}
case class Mago(hechizos: Map[Hechizo, Int]) extends Trabajo {
  def puedeUsarHechizo(hechizo: Hechizo, h:Heroe):Boolean = {
    hechizos.get(hechizo).exists(_ <= h.stats.nivel)
  }
}

sealed trait CriterioDeGrupo {
  def apply(grupo: Grupo): Boolean
}
case object Bigote extends CriterioDeGrupo{
  def apply(g:Grupo): Boolean = {
    g.heroes.map(_.trabajo).exists match {
      case ladron:Ladron => false
      case _ => true
    }
  }
}
case class Interesado(item: Item) extends CriterioDeGrupo {
  def apply(g:Grupo): Boolean = g.cofre.contains(item)
}
case object Introvertido extends CriterioDeGrupo {
  def apply(g: Grupo): Boolean = g.heroes.size < 3
}
case object Loquito extends CriterioDeGrupo {
  def apply(g: Grupo): Boolean = false
}

sealed trait CriterioDePuerta {
  def apply(p: List[Puerta], g:Grupo): Puerta
}
case object Heroico extends CriterioDePuerta {
  override def apply(p: List[Puerta], g: Grupo): Puerta = p.last
}
case object Ordenado extends CriterioDePuerta {
  override def apply(p: List[Puerta], g: Grupo): Puerta = p.head
}
case object Vidente extends CriterioDePuerta {
  override def apply(p: List[Puerta], g: Grupo): Puerta = {
    p.maxBy(g.recorrerCalabozo(_).grupoModificado.puntaje())
  }
}

