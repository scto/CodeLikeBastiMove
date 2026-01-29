(line_comment) @comment
(bracket_comment) @comment

(normal_command (identifier) @function)

(variable_ref (variable) @variable) @embedded
(env_var) @variable.builtin

(normal_command
  (identifier) @function
  (argument_list (argument) @variable)
  (#match? @function "^(?i)set$"))

(quoted_argument) @string
(bracket_argument) @string

((argument) @keyword
 (#match? @keyword "^(PUBLIC|PRIVATE|INTERFACE|SHARED|STATIC|MODULE|CACHE|FORCE|PARENT_SCOPE)$"))

((identifier) @keyword.control
 (#match? @keyword.control "^(?i)(if|elseif|else|endif|foreach|endforeach|while|endwhile|function|endfunction|macro|endmacro)$"))