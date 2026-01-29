(comment) @comment

[
  "if" "then" "else" "elif" "fi"
  "for" "do" "done"
  "while" "until"
  "case" "in" "esac"
  "function"
  "select"
] @keyword

(function_definition name: (word) @function)
(command name: (word) @function.call)

(variable_assignment name: (variable_name) @variable)
(variable_name) @variable
(simple_expansion) @variable
(special_variable_name) @variable.builtin

(string) @string
(heredoc_body) @string

((command name: (word) @keyword)
 (#match? @keyword "^(return|exit|declare|export|local|source|alias|eval)$"))