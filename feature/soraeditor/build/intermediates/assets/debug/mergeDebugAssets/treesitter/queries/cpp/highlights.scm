; Keywords
[
  "alignas"
  "alignof"
  "break"
  "case"
  "catch"
  "class"
  "co_await"
  "co_return"
  "co_yield"
  "concept"
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
  "export"
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
  "requires"
  "return"
  "sizeof"
  "static"
  "static_assert"
  "static_cast"
  "struct"
  "switch"
  "template"
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

; Types
(primitive_type) @type.builtin

(type_identifier) @type

(auto) @type.builtin

; Literals
(number_literal) @number
(string_literal) @string
(raw_string_literal) @string
(char_literal) @string
(true) @constant.builtin
(false) @constant.builtin
(null) @constant.builtin
(nullptr) @constant.builtin

; Functions
(function_declarator
  declarator: (identifier) @function)

(call_expression
  function: (identifier) @function.call)

(call_expression
  function: (field_expression
    field: (field_identifier) @function.call))

; Variables
(declaration
  declarator: (identifier) @variable)

(parameter_declaration
  declarator: (identifier) @variable.parameter)

(field_declaration
  declarator: (field_identifier) @property)

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
  "&&"
  "||"
  "!"
  "=="
  "!="
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
  "&="
  "|="
  "^="
  "<<="
  ">>="
  "->"
  "."
  "::"
  "?"
  ":"
] @operator

; Comments
(comment) @comment

; Preprocessor
(preproc_include) @keyword.directive
(preproc_def) @keyword.directive
(preproc_ifdef) @keyword.directive
(preproc_else) @keyword.directive
(preproc_endif) @keyword.directive

; Punctuation
["(" ")" "[" "]" "{" "}"] @punctuation.bracket
[";" "," "."] @punctuation.delimiter

; Namespaces
(namespace_identifier) @namespace

; this
(this) @variable.builtin

; Labels
(statement_identifier) @label
