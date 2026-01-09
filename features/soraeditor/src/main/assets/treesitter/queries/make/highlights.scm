; Targets
(targets) @function

; Prerequisites
(prerequisites) @variable

; Variables
(variable_assignment
  name: (word) @variable)

(variable_reference
  (word) @variable)

; Automatic variables
[
  "$@"
  "$<"
  "$^"
  "$?"
  "$*"
  "$%"
] @variable.builtin

; Functions
(function_call
  name: (word) @function.builtin)

; Includes
(include_directive) @keyword.directive

; Conditionals
[
  "ifeq"
  "ifneq"
  "ifdef"
  "ifndef"
  "else"
  "endif"
] @keyword.directive

; Shell
(shell_command) @string.special

; Comments
(comment) @comment

; Operators
["=" ":=" "?=" "+=" "::=" ":::="] @operator
[":" "|"] @punctuation.delimiter

; Special targets
(special_target) @constant.builtin

; Phony targets
".PHONY" @constant.builtin
