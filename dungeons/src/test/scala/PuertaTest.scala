import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._

import calabozo._
import CalabozoFixtures._

class PuertaTest extends AnyFreeSpec {

  "puedeSuperarObstaculos" - {
    "un ladrón con habilidad suficiente abre una puerta cerrada" in {
      val puerta = puertaSalida(List(Cerrada(50)))
      val grupoFuerte = grupo(ladron(nivel = 6, habilidad = 10))

      puerta.puedeSuperarObstaculos(grupoFuerte) shouldBe true
    }

    "un mago con el hechizo correcto abre una puerta encantada y sin él falla" in {
      val puerta = puertaSalida(List(Encantada(alojo)))
      val grupoConMago = grupo(mago(nivel = 16, hechizos = Map(alojo -> 10)))
      val grupoSinHechizo = grupo(ladron(nivel = 2, habilidad = 5))

      puerta.puedeSuperarObstaculos(grupoConMago) shouldBe true
      puerta.puedeSuperarObstaculos(grupoSinHechizo) shouldBe false
    }

    "una puerta escondida puede abrirse con un ladrón veloz" in {
      val puerta = puertaSalida(List(Escondida))
      val grupoVeloz = grupo(ladron(nivel = 2, habilidad = 3, velocidad = 15))
      val grupoSinAyuda = grupo(guerrero(1), mago(3, Map.empty))

      puerta.puedeSuperarObstaculos(grupoVeloz) shouldBe true
      puerta.puedeSuperarObstaculos(grupoSinAyuda) shouldBe false
    }
  }
}
