require_relative './Annotations.rb'
require_relative './Tag'


class Serializer
  include ValueTypeChecker

  def self.serialize(an_object)
    Serializer.new.serialize(an_object)
  end

  def serialize(object_to_serialize)
    an_object = object_to_serialize.apply_class_annotations
    if an_object.nil?
      return
    end

    tag_name = an_object.get_class_name

    tag = Tag.with_label(tag_name)

    if is_primitive?(an_object)
      tag.with_child(an_object)
      return tag
    end

    attribute_keys = []
    an_object.get_properties.each do |k, val|
      attribute = an_object.apply_method_annotations(k, val)
      next if attribute.nil?

      key = attribute[:key]
      value = attribute[:value]

      raise(LabelRepeatedError, key) if attribute_keys.include?(key)

      attribute_keys << key

      if is_primitive?(value)
        tag.with_attribute(key, value)
      elsif value.is_a?(Array)
        value.each do |v|
          arr_tag = Tag.with_label(v.get_class_name)
          arr_tag.with_child(v)
          tag.with_child(arr_tag)
        end
      else
        tag.with_child(Serializer.serialize(value))
      end
    end
    tag
  end
end