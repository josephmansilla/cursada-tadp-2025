import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._

import calabozo._
import CalabozoFixtures._

class HabitacionTest extends AnyFreeSpec {

  "ejecutarSituation" - {
    "aplica las situaciones en orden y agrega las puertas cerradas al grupo" in {
      val puertaSiguiente = puertaSalida(List.empty)
      val habitacion = Habitacion(
        puertas = List(puertaSiguiente),
        situacion = List(Dardos, TesoroPerdido(llave))
      )
      val grupoInicial = grupo(ladron(1))

      val grupoAlSalir = habitacion.ejecutarSituation(grupoInicial)

      grupoAlSalir.cofre should contain(llave)
      grupoAlSalir.puertasCerradas should contain(puertaSiguiente)
      grupoAlSalir.heroes.head.stats.vida shouldBe (grupoInicial.heroes.head.stats.vida + 10)
    }

    "la trampa del león elimina al héroe con menor velocidad" in {
      val habitacion = Habitacion(
        puertas = List.empty,
        situacion = List(TrampaLeon)
      )
      val lento = ladron(1, velocidad = 5)
      val rapido = guerrero(1, velocidad = 12)
      val grupoInicial = grupo(lento, rapido)

      val grupoAlSalir = habitacion.ejecutarSituation(grupoInicial)

      grupoAlSalir.heroes.find(_.stats.vida == 0).map(_.trabajo) should contain(lento.trabajo)
      grupoAlSalir.heroes.find(_.trabajo == rapido.trabajo).get.stats.vida shouldBe rapido.stats.vida
    }
  }
}
