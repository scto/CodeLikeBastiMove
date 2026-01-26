; Keywords
[
  "abstract"
  "assert"
  "break"
  "case"
  "catch"
  "class"
  "continue"
  "default"
  "do"
  "else"
  "enum"
  "exports"
  "extends"
  "final"
  "finally"
  "for"
  "if"
  "implements"
  "import"
  "instanceof"
  "interface"
  "module"
  "native"
  "new"
  "open"
  "opens"
  "package"
  "private"
  "protected"
  "provides"
  "public"
  "record"
  "requires"
  "return"
  "sealed"
  "static"
  "strictfp"
  "switch"
  "synchronized"
  "throw"
  "throws"
  "to"
  "transient"
  "transitive"
  "try"
  "uses"
  "var"
  "volatile"
  "while"
  "with"
  "yield"
] @keyword

; Operators
[
  "+"
  "-"
  "*"
  "/"
  "%"
  "++"
  "--"
  "&"
  "|"
  "^"
  "~"
  "<<"
  ">>"
  ">>>"
  "=="
  "!="
  "<"
  "<="
  ">"
  ">="
  "&&"
  "||"
  "!"
  "?"
  ":"
  "="
  "+="
  "-="
  "*="
  "/="
  "%="
  "&="
  "|="
  "^="
  "<<="
  ">>="
  ">>>="
  "->"
  "::"
] @operator

; Literals
(decimal_integer_literal) @number
(hex_integer_literal) @number
(octal_integer_literal) @number
(binary_integer_literal) @number
(decimal_floating_point_literal) @number
(hex_floating_point_literal) @number

(string_literal) @string
(character_literal) @string

[
  (true)
  (false)
  (null_literal)
] @constant

; Types
(type_identifier) @type
(void_type) @type

(primitive_type) @type.builtin

; Functions/Methods
(method_declaration
  name: (identifier) @function)

(method_invocation
  name: (identifier) @function.call)

(constructor_declaration
  name: (identifier) @constructor)

; Variables
(variable_declarator
  name: (identifier) @variable)

(formal_parameter
  name: (identifier) @variable.parameter)

(field_declaration
  declarator: (variable_declarator
    name: (identifier) @property))

; Annotations
(annotation
  name: (identifier) @attribute)
(marker_annotation
  name: (identifier) @attribute)

; Comments
(line_comment) @comment
(block_comment) @comment

; Punctuation
["(" ")" "[" "]" "{" "}"] @punctuation.bracket
[";" "," "."] @punctuation.delimiter

; Class/Interface names
(class_declaration
  name: (identifier) @type.class)
(interface_declaration
  name: (identifier) @type.interface)
(enum_declaration
  name: (identifier) @type.enum)
(record_declaration
  name: (identifier) @type.class)

; Package/Import
(package_declaration
  (scoped_identifier) @namespace)
(import_declaration
  (scoped_identifier) @namespace)

; this/super
(this) @variable.builtin
(super) @variable.builtin
