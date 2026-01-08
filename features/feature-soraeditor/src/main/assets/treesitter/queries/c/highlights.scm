; Keywords
[
  "break"
  "case"
  "const"
  "continue"
  "default"
  "do"
  "else"
  "enum"
  "extern"
  "for"
  "goto"
  "if"
  "inline"
  "register"
  "restrict"
  "return"
  "sizeof"
  "static"
  "struct"
  "switch"
  "typedef"
  "union"
  "volatile"
  "while"
] @keyword

; Types
(primitive_type) @type.builtin

(type_identifier) @type

; Literals
(number_literal) @number
(string_literal) @string
(char_literal) @string
(true) @constant.builtin
(false) @constant.builtin
(null) @constant.builtin

; Functions
(function_declarator
  declarator: (identifier) @function)

(call_expression
  function: (identifier) @function.call)

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

; Labels
(statement_identifier) @label
