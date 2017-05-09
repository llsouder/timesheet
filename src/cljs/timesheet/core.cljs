(ns timesheet.core
  (:require-macros [hiccups.core :as hiccups :refer [html]]))
  
defn get-app []
   (or (.-app js/window)
       (set! (.-app js/window) #js {})))

(defn init! []
  (let [app (get-app)
        c (.Component (.-core js/ng)
                      #js {:selector "my-app"
                           :template (html [:div
                                            [:h1 "My first Angular 2 app"]
                                            [:div [:h2 "test"]]
                                            [:div [:h3 "test2"]]])})
        c (.Class c #js {:constructor (fn [])})]
    (set! (.-AppComponent app) c)))

