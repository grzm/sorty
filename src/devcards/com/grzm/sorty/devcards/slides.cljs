(ns com.grzm.sorty.devcards.slides
  (:require
   [com.grzm.sorty.client.ui.slides :as slides]
   [devcards.core :refer-macros [defcard]]))

(defcard display-slide
  (slides/ui-slide {:id 3 :text "Here's some text"}))
