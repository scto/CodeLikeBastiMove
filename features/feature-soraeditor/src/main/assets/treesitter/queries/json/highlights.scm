; Properties
(pair
  key: (string) @property)

; Values
(string) @string

(number) @number

[
  (true)
  (false)
] @constant.builtin

(null) @constant.builtin

; Punctuation
["[" "]" "{" "}"] @punctuation.bracket
[":" ","] @punctuation.delimiter

; Errors
(ERROR) @error
