; Scopes
(class_body) @scope
(method_declaration) @scope
(constructor_body) @scope
(block) @scope
(for_statement) @scope
(enhanced_for_statement) @scope
(while_statement) @scope
(do_statement) @scope
(if_statement) @scope
(try_statement) @scope
(catch_clause) @scope
(lambda_expression) @scope

; Definitions
(variable_declarator
  name: (identifier) @definition.var)

(formal_parameter
  name: (identifier) @definition.parameter)

(field_declaration
  declarator: (variable_declarator
    name: (identifier) @definition.field))

(method_declaration
  name: (identifier) @definition.method)

(class_declaration
  name: (identifier) @definition.type)

(interface_declaration
  name: (identifier) @definition.type)

(enum_declaration
  name: (identifier) @definition.type)

; References
(identifier) @reference
