(interface_declaration) @scope
(parcelable_declaration) @scope
(method_declaration) @scope

(interface_declaration name: (identifier) @definition.interface)
(parcelable_declaration name: (identifier) @definition.type)
(method_declaration name: (identifier) @definition.method)
(argument name: (identifier) @definition.parameter)
(field_declaration name: (identifier) @definition.field)

(user_type (identifier) @reference.type)