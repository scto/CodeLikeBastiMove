; Code block patterns for Kotlin editor

(class_declaration
  (class_body) @scope.marked)

(object_declaration
  (class_body) @scope.marked)

(function_declaration
  (function_body) @scope.marked)

(lambda_literal) @scope.marked

(if_expression
  (control_structure_body) @scope.marked)

(when_expression) @scope.marked

(for_statement
  (control_structure_body) @scope.marked)

(while_statement
  (control_structure_body) @scope.marked)

(do_while_statement
  (control_structure_body) @scope.marked)

(try_expression) @scope.marked
