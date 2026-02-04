; XML Brackets pattern

(element
  (start_tag
    "<" @editor.brackets.open)
  (end_tag
    "</" @editor.brackets.close
    ">" @editor.brackets.close))

(self_closing_tag
  "<" @editor.brackets.open
  "/>" @editor.brackets.close)
