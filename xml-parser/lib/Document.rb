require_relative "./Serializer.rb"

class Document
  attr_accessor :root

  def initialize(*args, &block)
    @root = args.first if !args.empty? and args.first.is_a?(Tag)

    execute(&block)
  end

  def execute(*args, &block)
    value = args.first unless args.empty?

    if block_given?
      if value.nil?
        result = self.instance_exec(&block)
      else
        result = self.instance_exec(value, &block)
      end
    end

    @root.with_child(result) unless result.is_a?(Tag) || result.nil?
    self
  end

  def xml
    root.xml
  end

  def build_tag(label, *attributes)
    tag = Tag.with_label(label)

    attributes.last&.keys&.each { |key|
      tag.with_attribute(key.to_s, attributes.last[key])
    } unless attributes.empty?

    tag
  end

  def process_root_and_children(tag, &children)
    if root.nil?
      @root = tag
      execute(&children)
    else
      child_doc = Document.new(tag, &children)
      @root.with_child(child_doc.root) unless child_doc.nil?
    end
    tag
  end

  def method_missing(method_name, *args, &block)
    tag = build_tag(method_name.to_s, *args)

    process_root_and_children(tag, &block)
    tag
  end

  def respond_to_missing?(label, include_private = false)
    true
  end

  def self.serialize(an_object)
    Serializer.serialize(an_object)
  end
end
