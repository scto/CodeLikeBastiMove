; Scopes
(function_definition) @scope
(compound_statement) @scope
(for_statement) @scope
(while_statement) @scope
(do_statement) @scope
(if_statement) @scope
(switch_statement) @scope
(try_statement) @scope
(catch_clause) @scope
(lambda_expression) @scope
(class_specifier) @scope
(struct_specifier) @scope
(namespace_definition) @scope

; Definitions
(declaration
  declarator: (identifier) @definition.var)

(parameter_declaration
  declarator: (identifier) @definition.parameter)

(field_declaration
  declarator: (field_identifier) @definition.field)

(function_declarator
  declarator: (identifier) @definition.function)

(type_definition
  declarator: (type_identifier) @definition.type)

(class_specifier
  name: (type_identifier) @definition.type)

(struct_specifier
  name: (type_identifier) @definition.type)

; References
(identifier) @reference
(field_identifier) @reference
