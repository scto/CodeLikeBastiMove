; C++ Syntax Highlighting

; Types
(type_identifier) @type
(primitive_type) @type.builtin
(sized_type_specifier) @type.builtin

; Identifiers
(identifier) @variable
(field_identifier) @variable.member
(namespace_identifier) @namespace

; Functions
(function_declarator
  declarator: (identifier) @function)
(call_expression
  function: (identifier) @function.call)
(call_expression
  function: (field_expression
    field: (field_identifier) @function.call))

; Constants
((identifier) @constant
  (#match? @constant "^[A-Z][A-Z0-9_]+$"))

; Keywords
[
  "break"
  "case"
  "catch"
  "class"
  "co_await"
  "co_return"
  "co_yield"
  "const"
  "consteval"
  "constexpr"
  "constinit"
  "continue"
  "decltype"
  "default"
  "delete"
  "do"
  "else"
  "enum"
  "explicit"
  "extern"
  "final"
  "for"
  "friend"
  "goto"
  "if"
  "inline"
  "mutable"
  "namespace"
  "new"
  "noexcept"
  "operator"
  "override"
  "private"
  "protected"
  "public"
  "return"
  "sizeof"
  "static"
  "static_assert"
  "static_cast"
  "struct"
  "switch"
  "template"
  "this"
  "throw"
  "try"
  "typedef"
  "typeid"
  "typename"
  "union"
  "using"
  "virtual"
  "volatile"
  "while"
] @keyword

; Preprocessor
[
  "#define"
  "#elif"
  "#else"
  "#endif"
  "#if"
  "#ifdef"
  "#ifndef"
  "#include"
  "#pragma"
] @keyword.directive

(preproc_include
  path: (string_literal) @string)
(preproc_include
  path: (system_lib_string) @string)

; Literals
(string_literal) @string
(system_lib_string) @string
(raw_string_literal) @string
(char_literal) @character
(number_literal) @number

[
  (true)
  (false)
] @boolean

(null) @constant.builtin
(nullptr) @constant.builtin

; Comments
[
  (comment)
] @comment

; Operators
[
  "+"
  "-"
  "*"
  "/"
  "%"
  "^"
  "&"
  "|"
  "~"
  "!"
  "<"
  ">"
  "="
  "<<"
  ">>"
  "=="
  "!="
  "<="
  ">="
  "&&"
  "||"
  "++"
  "--"
  "+="
  "-="
  "*="
  "/="
  "%="
  "^="
  "&="
  "|="
  "<<="
  ">>="
  "->"
  "."
  "::"
  "?"
  ":"
] @operator

; Punctuation
[
  "{"
  "}"
  "("
  ")"
  "["
  "]"
] @punctuation.bracket

[
  ";"
  ","
] @punctuation.delimiter
