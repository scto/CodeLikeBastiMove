; C++ locals

; Scopes
[
  (translation_unit)
  (function_definition)
  (compound_statement)
  (class_specifier)
  (struct_specifier)
  (namespace_definition)
  (for_statement)
  (if_statement)
  (while_statement)
  (do_statement)
  (switch_statement)
  (try_statement)
  (catch_clause)
  (lambda_expression)
] @scope

; Definitions
(declaration
  declarator: (identifier) @definition.var)

(parameter_declaration
  declarator: (identifier) @definition.parameter)

(function_declarator
  declarator: (identifier) @definition.function)

(field_declaration
  declarator: (field_identifier) @definition.field)

(type_definition
  declarator: (type_identifier) @definition.type)

; References
(identifier) @reference
