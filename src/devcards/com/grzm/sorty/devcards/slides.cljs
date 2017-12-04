(ns com.grzm.sorty.devcards.slides
  (:require
   [com.grzm.sorty.client.ui.slides :as slides]
   [devcards.core :refer-macros [defcard]]))

(def slide-data {:id      3
                 :text    "Here's some text"
                 :s-class "spam"})

(defcard display-slide
  (slides/ui-slide slide-data))

(defcard display-slide-with-buttons
  (slides/ui-slide-with-buttons slide-data))
