(comment) @comment

[
  "package"
  "import"
  "interface"
  "parcelable"
  "oneway"
  "in"
  "out"
  "inout"
  "const"
  "enum"
  "union"
] @keyword

(primitive_type) @type.builtin
(void_type) @type.builtin
(user_type) @type
(array_type) @type

(interface_declaration name: (identifier) @type.definition)
(parcelable_declaration name: (identifier) @type.definition)
(method_declaration name: (identifier) @function.method)

(argument name: (identifier) @variable.parameter)
(field_declaration name: (identifier) @variable.field)
(const_declaration name: (identifier) @constant)

(integer_value) @number
(float_value) @number
(boolean_value) @boolean
(string_value) @string

(annotation name: (identifier) @attribute)