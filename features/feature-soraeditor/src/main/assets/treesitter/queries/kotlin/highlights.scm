; Keywords
[
  "abstract"
  "actual"
  "annotation"
  "as"
  "break"
  "by"
  "catch"
  "class"
  "companion"
  "const"
  "constructor"
  "continue"
  "crossinline"
  "data"
  "do"
  "else"
  "enum"
  "expect"
  "external"
  "final"
  "finally"
  "for"
  "fun"
  "get"
  "if"
  "import"
  "in"
  "infix"
  "init"
  "inline"
  "inner"
  "interface"
  "internal"
  "is"
  "lateinit"
  "noinline"
  "object"
  "open"
  "operator"
  "out"
  "override"
  "package"
  "private"
  "protected"
  "public"
  "reified"
  "return"
  "sealed"
  "set"
  "suspend"
  "tailrec"
  "this"
  "throw"
  "try"
  "typealias"
  "val"
  "var"
  "vararg"
  "when"
  "where"
  "while"
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
  "&&"
  "||"
  "!"
  "=="
  "!="
  "==="
  "!=="
  "<"
  "<="
  ">"
  ">="
  "="
  "+="
  "-="
  "*="
  "/="
  "%="
  "->"
  "?:"
  "?."
  "::"
  ".."
  "!!"
] @operator

; Literals
(integer_literal) @number
(long_literal) @number
(hex_literal) @number
(bin_literal) @number
(real_literal) @number

(string_literal) @string
(character_literal) @string
(multiline_string_literal) @string

[
  "true"
  "false"
] @constant.builtin

(null_literal) @constant.builtin

; Types
(user_type
  (type_identifier) @type)

; Functions
(function_declaration
  (simple_identifier) @function)

(call_expression
  (simple_identifier) @function.call)

(constructor_invocation
  (user_type
    (type_identifier) @constructor))

; Variables
(property_declaration
  (variable_declaration
    (simple_identifier) @variable))

(parameter
  (simple_identifier) @variable.parameter)

; Annotations
(annotation
  (user_type) @attribute)

; Comments
(line_comment) @comment
(multiline_comment) @comment

; Punctuation
["(" ")" "[" "]" "{" "}"] @punctuation.bracket
[";" "," "." ":"] @punctuation.delimiter

; Class definitions
(class_declaration
  (type_identifier) @type.class)

(object_declaration
  (type_identifier) @type.class)

; this/super
(this_expression) @variable.builtin
(super_expression) @variable.builtin

; Labels
(label) @label
