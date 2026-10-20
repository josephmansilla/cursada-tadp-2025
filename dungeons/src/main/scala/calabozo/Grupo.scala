package calabozo

import scala.util.boundary.break
import scala.util.{Failure, Try, boundary}

case class Grupo(heroes:List[Heroe], puertasAbiertas:List[Puerta] = List(), puertasCerradas:List[Puerta] = List(), cofre: List[Item] = List()) {
  def agregarPuertasCerradas(puertas: List[Puerta]): Grupo = {
      this.copy(puertasCerradas = puertasCerradas ::: puertas)
  }
  def lider():Heroe = this.heroes.find(_.stats.estaVivo()).get
  def agregarHeroe(heroe: Heroe): Grupo = copy(heroes = heroes :+ heroe)

  def abrePuerta(puerta: Puerta): Grupo =
    copy(puertasAbiertas = puertasAbiertas :+ puerta, puertasCerradas = puertasCerradas.filterNot(_ == puerta))

  def puntaje():Int =
    heroes.count(h => h.stats.estaVivo()) * 10
      - heroes.count(h => !h.stats.estaVivo()) * 5
      + cofre.size + heroes.maxBy(_.stats.nivel).stats.nivel

  def puedeAbrirAlgunaPuerta(): Boolean = this.puertasCerradas.exists(_.puedeSuperarObstaculos(this))

  def recorrerCalabozo(puerta: Puerta): CalabozoRecorrido = {
    val resultado:Try[Grupo] = Try(abrir(puerta))

    if resultado.isSuccess then
      CalabozoRecorrido(this, resultado.get, resultado)
    else
      resultado match {
        case Failure(CustomException(_,grupoModificado)) => CalabozoRecorrido(this, grupoModificado, resultado)
        case _ => CalabozoRecorrido(this, null, resultado)
      }
  }

  def abrir(puerta: Puerta): Grupo = {
    var grupoMod = this
    if (puerta.puedeSuperarObstaculos(grupoMod)) {
      grupoMod = grupoMod.abrePuerta(puerta)

      if (puerta.salida) {
        return grupoMod
      }

      grupoMod = puerta.habitacion.ejecutarSituation(grupoMod)
    }

    if (grupoMod.heroes.exists(h => h.stats.estaVivo())) {
      if (grupoMod.puedeAbrirAlgunaPuerta()) {
        val proximaPuerta = grupoMod.lider().elegirProximaPuerta(grupoMod)
        grupoMod.abrir(proximaPuerta)
      } else {
        throw CustomException("Se quedaron sin puertas", grupoMod) // cambiar a monadas, referenciado en habitacion.scala
      }
    } else {
      throw CustomException("Todos murieron", grupoMod)
    }
  }

  def puntajeAlAbrir(puerta: Puerta): Int = {
    recorrerCalabozo(puerta).grupoModificado.puntaje()
  }

  def cuantosNivelesTieneQueSubir(puerta: Puerta): Int = {
    var niveles = 0
    boundary {
      for (n <- 0 to 20) {
        niveles = n
        val grupoMod = copy(heroes = this.heroes.map(h => h.copy(stats = h.stats.subirNivel(niveles))))

        if grupoMod.recorrerCalabozo(puerta).resultado.isSuccess then break()
      }
    }
    niveles
  }
}

case class CalabozoRecorrido(grupoOriginal:Grupo, grupoModificado:Grupo, resultado:Try[_]) {}

case class CustomException(message:String, grupo: Grupo) extends Exception{}