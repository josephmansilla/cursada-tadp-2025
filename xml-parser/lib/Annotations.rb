module ValueTypeChecker
  @primitives = [String, TrueClass, FalseClass, Numeric, NilClass]
  @complex_values = [Array]

  class << self
    attr_reader :primitives, :complex_values
  end

  def is_primitive?(value)
    ValueTypeChecker.primitives.any?{ |type| value.is_a?(type) }
  end
  def is_a_complex_value?(value)
    ValueTypeChecker.complex_values.any?{ |type| value.is_a?(type) }
  end
end


class AnnotationManager
  @pending_annotations = []

  class << self
    attr_accessor :pending_annotations
  end

  def self.add_pending(annotation)
    self.pending_annotations << annotation
  end

  def self.take_pending_and_clear
    result = self.pending_annotations.dup
    self.pending_annotations.clear
    result
  end

  def self.has_pending?
    !self.pending_annotations.empty?
  end
end

class Object
  include ValueTypeChecker
  def get_properties
    self.instance_variables.each_with_object({}) do |var, hash|
      getter = var.to_s.delete("@").to_sym
      hash[getter] = self.send(getter) if self.respond_to?(getter)
    end
  end

  def method_missing(name, *args, &block)
    es_annotation?(name) ? procesar_annotation(name, *args, &block) : super
  end

  def has_class_annotations
    !!(self.class.respond_to?(:has_class_annotations) && self.class.has_class_annotations)
  end
  def has_method_annotations
    !!(self.class.respond_to?(:has_method_annotations) && self.class.has_method_annotations)
  end

  def apply_class_annotations
    an_object = self.dup

    an_object.class.class_annotations&.each do |annotation|
      an_object = annotation.apply(an_object, nil, nil)
    end if an_object.class.respond_to?(:has_class_annotations) && self.class.has_class_annotations

    an_object
  end

  def apply_method_annotations(key, value)
    attribute = {:key => key, :value => value}

    attribute = self.class.method_annotations[key].reduce(attribute) do |attr, annotation|
      if is_primitive? value
        attr = annotation.apply(self, attr[:key], attr[:value])
        unless attr
          attribute = nil
          break
        end
      else
        if value.has_class_annotations
          value.class.class_annotations << annotation
        end
      end
      attr
    end if self.has_method_annotations && self.class.method_annotations[key]

    attribute
  end

  def get_class_name
    self.respond_to?(:__custom_label__) ? self.__custom_label__ : self.class.to_s.downcase
  end

  private
  def es_annotation?(name)
    name.to_s.start_with?('✨') && name.to_s.end_with?('✨')
  end
  def procesar_annotation(name, *args, &block)
    posible_annotation = name.to_s[1..-2]
    annotation_instance = find_subclass_by_name(posible_annotation).new(*args, &block)

    AnnotationManager.add_pending(annotation_instance)
  end

  def find_subclass_by_name(name)
    Annotation.subclasses.find { |a_class| a_class.name.downcase == name.to_s.downcase }  || validate_annotation(name)
  end

  def validate_annotation(name)
    raise(SubclassNotFoundError, "No se encontró clase con nombre '#{name}'")
  end

  def self.inherited(subclass)
    super
    subclass.class_annotations = AnnotationManager.take_pending_and_clear if AnnotationManager.has_pending?
  end

  def self.method_added(method_name)
    super
    if AnnotationManager.has_pending?
      self.method_annotations ||= {}
      self.method_annotations[method_name] = AnnotationManager.take_pending_and_clear
    end
  end

  def self.attr_accessor(*names)
    add_annotations_to_attribute(names)
    super
  end

  def self.attr_reader(*names)
    add_annotations_to_attribute(names)
    super
  end

  def self.add_annotations_to_attribute(names)
    if AnnotationManager.has_pending?
      self.method_annotations ||= {}
      pending = AnnotationManager.take_pending_and_clear
      names.each do |method_name|
        self.method_annotations[method_name] = pending
      end
    end
  end
end

class SubclassNotFoundError < StandardError; end
class LabelRepeatedError < StandardError; end

class Class
  attr_accessor :class_annotations, :method_annotations

  def has_class_annotations
    self.class_annotations && !self.class_annotations.empty?
  end

  def has_method_annotations
    self.method_annotations && !self.method_annotations.empty?
  end
end

class Annotation
end

class Label < Annotation
  attr_accessor :new_label
  def initialize(new_label)
    @new_label = new_label
  end

  def apply(clase, metodo, valor)
    new_label = @new_label
    if metodo.nil?
      clase.define_singleton_method("__custom_label__") do new_label end
      clase
    else
      {:key=>new_label, :value=>valor}
    end
  end
end

class Ignore < Annotation
  def apply(_, _, _)
  end
end

class Inline < Annotation
  @block
  def initialize(&block)
    @block = block
  end

  def apply(_, metodo, valor)
    {:key=>metodo, :value=>@block.call(valor)}
  end
end

class Custom < Annotation
  attr_accessor :block, :original_instance, :custom_instance
  def initialize(&block)
    @block = block
  end
  def apply(clase, _, _)
    @original_instance = clase
    self.instance_exec(clase, &@block)
    @custom_instance
  end

  def method_missing(name, *args, &block)
    @custom_instance = Class.new if @custom_instance.nil?
    class_name = @original_instance.get_class_name
    @custom_instance.define_singleton_method(:__custom_label__) do class_name end

    if block_given?
      child_class = Class.new
      child_class.define_singleton_method(:__custom_label__) do name end
      child_class.define_singleton_method(:to_s) do "#{block.call}" end

      @custom_instance.instance_variable_set("@#{name}", nil)
      @custom_instance.define_singleton_method(name) do [child_class] end
    end

  end
end

class MethodExists < StandardError; end
class InvalidAnnotation < StandardError; end

class Extra < Annotation
  @extra_method
  @block

  def initialize(sym, &block)
    @block = block
    @extra_method = sym
  end

  def apply(clase, metodo, _)
    raise(InvalidAnnotation, metodo) if metodo
    raise(MethodExists, @extra_method) if clase.respond_to?(@extra_method.to_sym)
    bloque = @block
    clase.instance_variable_set("@#{@extra_method}", nil)
    clase.define_singleton_method(@extra_method) do
      begin
        self.instance_eval(&bloque)
      rescue
        raise(InvalidAnnotation, bloque)
      end
    end

    clase
  end
end

class Expand < Annotation
  attr_accessor :block
  def initialize(&block)
    @block = block
  end

  def apply(_, clave, valor)
    raise(InvalidAnnotation, "No puede ser aplicada a una clase") if clave.nil? || valor.nil?
    raise(InvalidAnnotation, "Bloque vacío") if @block.nil?

    expandido = instance_exec(valor, &@block)

    {key: clave, value: expandido}

  end

end