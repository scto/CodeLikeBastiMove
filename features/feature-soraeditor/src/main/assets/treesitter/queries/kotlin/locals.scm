; Scopes
(class_body) @scope
(function_body) @scope
(control_structure_body) @scope
(lambda_literal) @scope
(when_expression) @scope
(try_expression) @scope
(catch_block) @scope
(finally_block) @scope

; Definitions
(property_declaration
  (variable_declaration
    (simple_identifier) @definition.var))

(parameter
  (simple_identifier) @definition.parameter)

(function_declaration
  (simple_identifier) @definition.function)

(class_declaration
  (type_identifier) @definition.type)

(object_declaration
  (type_identifier) @definition.type)

; References
(simple_identifier) @reference
