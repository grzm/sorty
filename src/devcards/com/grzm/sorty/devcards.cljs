(ns com.grzm.sorty.devcards
  (:require-macros
   [devcards.core :refer [defcard-doc]])
  (:require
   [com.grzm.sorty.devcards.classifier]
   [com.grzm.sorty.devcards.queue-list]))

(defcard-doc
  "
# Examples of components

Strive to have each component renderable as a devcard to ensure
component decoupling.
"
  )
