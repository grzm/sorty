(ns com.grzm.sorty.devcards
  (:require-macros
   [devcards.core :refer [defcard]])
  (:require
   [com.grzm.sorty.devcards.slides]
   [sablono.core :as sab]))

(defcard my-first-card
  (sab/html [:h1 "Devcards is freaking awesome!!!"]))
