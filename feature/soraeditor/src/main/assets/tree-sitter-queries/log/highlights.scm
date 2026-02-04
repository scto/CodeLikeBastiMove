; Log Syntax Highlighting (AndroidIDE log format)

; Log levels
((identifier) @keyword.error
  (#match? @keyword.error "^(ERROR|FATAL|E|F)$"))

((identifier) @keyword.warning
  (#match? @keyword.warning "^(WARN|WARNING|W)$"))

((identifier) @keyword.info
  (#match? @keyword.info "^(INFO|I)$"))

((identifier) @keyword.debug
  (#match? @keyword.debug "^(DEBUG|D|VERBOSE|V)$"))

; Timestamps
(time) @number
(date) @number

; Process/Thread IDs
(pid) @constant
(tid) @constant

; Tags
(tag) @type

; Messages
(message) @string

; Punctuation
[
  "["
  "]"
  ":"
  "/"
] @punctuation.delimiter
