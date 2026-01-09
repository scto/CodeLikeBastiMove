; Tags
(tag_name) @tag
(STag
  (Name) @tag)
(ETag
  (Name) @tag)
(EmptyElemTag
  (Name) @tag)

; Attributes
(Attribute
  (Name) @attribute)

; Attribute values
(AttValue) @string

; Comments
(Comment) @comment

; Processing instructions
(PI) @keyword

; CDATA
(CDSect) @string

; Entities
(EntityRef) @constant
(CharRef) @constant

; Punctuation
["<" ">" "</" "/>" "<?" "?>" "<!" ">"] @punctuation.bracket
["=" "\""] @punctuation.delimiter

; Prolog
(XMLDecl) @keyword
(doctypedecl) @keyword

; Namespaces
(STag
  (Name) @namespace
  (#match? @namespace "^\\w+:"))

; Content
(content) @string.special
