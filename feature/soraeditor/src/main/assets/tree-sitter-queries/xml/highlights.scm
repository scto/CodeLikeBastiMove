; XML Syntax Highlighting

(tag_name) @tag
(erroneous_end_tag_name) @error
(attribute_name) @attribute
(attribute_value) @string
(text) @none
(comment) @comment
(cdata_section) @string.special
(processing_instruction) @keyword.directive

"<" @punctuation.bracket
">" @punctuation.bracket
"</" @punctuation.bracket
"/>" @punctuation.bracket
"<?" @punctuation.bracket
"?>" @punctuation.bracket
"=" @operator

(entity_reference) @constant
(char_reference) @constant

(doctype) @keyword.directive
(start_tag) @none
(end_tag) @none
(self_closing_tag) @none
