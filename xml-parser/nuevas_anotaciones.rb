class Validate < Annotation
  def initialize(&block)
    @block = block
  end

  def apply(_, clave, valor)
    raise(InvalidAnnotation, "Validate debe tener bloque") unless @block

    begin return nil unless @block.call(valor)
    rescue StandardError => e
      raise(InvalidCondition, e.message)
    end

    {key: clave, value: valor}
  end
end
class InvalidCondition < StandardError; end

class Default < Annotation
  def initialize(default_value = nil, &block)
    @default_value = default_value
    @block = block
  end

  def apply(_, clave, valor)
    return {key: clave, value: valor} if valor

    nuevo_valor = begin
                    if @block; @block.call else @default_value end
                  rescue StandardError => e
                    raise(InvalidAnnotation, e.message)
                  end

    return {key: clave, value: nuevo_valor}
  end
end


class Mask < Annotation
  def initialize(visible: 0, &block)
    @visible = visible
    @block = block
  end
  def apply(_, clave, valor)
    return {key: clave, value: valor} if valor

    masked_valor = begin
                     if @block; @block.call(valor) else masked_valor(valor) end
                   rescue StandardError => e
                     raise(InvalidAnnotation, e.message)
                   end

    {key: clave, value: masked_valor}
  end

  private def masked_valor(valor)
    raise(InvalidAnnotation, valor) unless valor.is_a?(Numeric) || valor.is_a?(String)

    s = valor.to_s
    mantener = [[@visible, 0].max, s.length].min
    ocultar = s.length - mantener

    ("*" * ocultar) + s[-mantener, mantener]
  end
end