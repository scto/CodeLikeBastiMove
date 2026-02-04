; JSON Syntax Highlighting

(pair
  key: (string) @property)

(string) @string

(number) @number

[
  (true)
  (false)
] @boolean

(null) @constant.builtin

(comment) @comment

[
  "{"
  "}"
] @punctuation.bracket

[
  "["
  "]"
] @punctuation.bracket

":" @punctuation.delimiter
"," @punctuation.delimiter

(escape_sequence) @string.escape
