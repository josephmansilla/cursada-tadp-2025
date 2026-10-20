import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._

import calabozo._
import CalabozoFixtures._

class PersonajesTest extends AnyFreeSpec {

  "Stats y héroes" - {
    "cambiarVida nunca baja de cero" in {
      val stats = Stats(10, 5, 5, 1)

      stats.cambiarVida(15).vida shouldBe 0
      stats.cambiarVida(5).vida shouldBe 5
    }

    "un héroe introvertido solo acepta grupos menores a tres integrantes" in {
      val duo = grupo(ladron(1), guerrero(1))
      val trio = grupo(ladron(1), guerrero(1), mago(1, Map.empty))

      ladron(1).leAgradaElGrupo(duo) shouldBe true
      ladron(1).leAgradaElGrupo(trio) shouldBe false
    }

    "el criterio de puerta ordenado selecciona la primera puerta disponible" in {
      val puertaPrioritaria = Puerta(List(Escondida), salida = true)
      val puertaSecundaria = Puerta(List(Cerrada(10)), salida = true)
      val grupoConPuertas = grupo(ladron(5, criterioPuerta = Ordenado))
        .agregarPuertasCerradas(List(puertaPrioritaria, puertaSecundaria))

      grupoConPuertas.heroes.head.elegirProximaPuerta(grupoConPuertas) shouldBe puertaPrioritaria
    }
  }
}
