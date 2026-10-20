import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers._

import calabozo._
import CalabozoFixtures._

class CalabozoTest extends AnyFreeSpec {

  "recorrerCalabozo" - {
    "devuelve Success y el grupo modificado cuando puede salir" in {
      val habitacion = habitacionConTesoroYPuertaSalida(llave)
      val puertaEntrada = puertaConHabitacion(List(Escondida), habitacion)
      val grupoInicial = grupo(ladron(nivel = 3, habilidad = 10))

      val recorrido = grupoInicial.recorrerCalabozo(puertaEntrada)

      recorrido.resultado.isSuccess shouldBe true
      recorrido.grupoModificado.cofre should contain(llave)
      recorrido.grupoOriginal.heroes shouldBe grupoInicial.heroes
    }

    "retorna Failure y conserva el estado cuando no hay puertas superables" in {
      val puerta = puertaSalida(List(Encantada(alojo)))
      val grupoSinRecursos = grupo(guerrero(1))

      val recorrido = grupoSinRecursos.recorrerCalabozo(puerta)

      recorrido.resultado.isFailure shouldBe true
      recorrido.grupoModificado shouldBe grupoSinRecursos
    }
  }

  "mejor grupo para un calabozo (req 4)" - {
    "elige al grupo que sale con mayor puntaje cuando ambos completan el recorrido" in {
      val puerta = puertaConHabitacion(List(Cerrada(10)), habitacionConTesoroYPuertaSalida(llave))
      val grupoSuperior = grupo(ladron(nivel = 5, habilidad = 5), guerrero(3))
      val grupoInferior = grupo(ladron(nivel = 2, habilidad = 10), guerrero(1))

      puerta.queGrupoEsMejor(List(grupoInferior, grupoSuperior)) shouldBe grupoSuperior
    }

    "prefiere un grupo que puede abrir la puerta frente a uno que no logra avanzar" in {
      val puerta = puertaSalida(List(Encantada(alojo)))
      val grupoExitoso = grupo(mago(nivel = 20, hechizos = Map(alojo -> 10)))
      val grupoEstancado = grupo(guerrero(4), guerrero(3))

      puerta.queGrupoEsMejor(List(grupoEstancado, grupoExitoso)) shouldBe grupoExitoso
    }

    "considera el puntaje final incluso si un integrante cae en la sala" in {
      val habitacionConTrampa = Habitacion(
        puertas = List(Puerta(List.empty, salida = true)),
        situacion = List(TrampaLeon)
      )
      val puerta = puertaConHabitacion(List(Escondida), habitacionConTrampa)
      val grupoRapido = grupo(ladron(nivel = 5, habilidad = 5, velocidad = 15), guerrero(nivel = 4, velocidad = 14))
      val grupoLento = grupo(ladron(nivel = 3, habilidad = 5, velocidad = 5), guerrero(nivel = 2, velocidad = 6))

      puerta.queGrupoEsMejor(List(grupoLento, grupoRapido)) shouldBe grupoRapido
    }
  }

  "niveles necesarios para salir (req 5)" - {
    "cuando ya puede abrir devuelve 0" in {
      val puerta = puertaSalida(List(Cerrada(10)))
      val grupoCapaz = grupo(ladron(nivel = 5, habilidad = 5))

      grupoCapaz.cuantosNivelesTieneQueSubir(puerta) shouldBe 0
    }

    "calcula los niveles mínimos para que un ladrón alcance el umbral" in {
      val puerta = puertaSalida(List(Cerrada(10)))
      val grupoNovato = grupo(ladron(nivel = 1, habilidad = 5))

      grupoNovato.cuantosNivelesTieneQueSubir(puerta) shouldBe 3
    }

    "si el grupo nunca podría abrir devuelve el máximo configurado (20)" in {
      val puerta = puertaSalida(List(Encantada(alojo)))
      val grupoSinOpciones = grupo(guerrero(1), guerrero(2))

      grupoSinOpciones.cuantosNivelesTieneQueSubir(puerta) shouldBe 20
    }

    "un mago que necesita subir de nivel para aprender el hechizo requerido" in {
      val puerta = puertaSalida(List(Encantada(alojo)))
      val grupoConMago = grupo(mago(nivel = 1, hechizos = Map(alojo -> 5)))

      grupoConMago.cuantosNivelesTieneQueSubir(puerta) shouldBe 4
    }
  }
}
