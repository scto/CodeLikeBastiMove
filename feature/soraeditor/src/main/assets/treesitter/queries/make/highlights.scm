(comment) @comment

(rule targets: (targets) @function)
(variable_assignment name: (word) @variable)

(variable_reference) @variable
(substitution_reference) @variable
(automatic_variable) @variable.builtin

(function_call function: (word) @function.builtin)

[ "include" "export" "override" "vpath" ] @keyword
[ "if" "ifeq" "ifneq" "else" "endif" "define" "endef" ] @keyword.control

(recipe) @string