(ns ^:figwheel-no-load timesheet.app
  (:require [timesheet.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(defn reload []
  (core/init!)
  ((.. js/ng -platform -browser -bootstrap)
   (.-AppComponent (.-app js/window))))

;;(devtools/install!)

(figwheel/watch-and-reload :websocket-url "ws://localhost:3449/figwheel-ws"
                           :on-jsload reload)

(core/init!)
(defonce only-attach-listener-once
  (.addEventListener js/document "DOMContentLoaded"
                     (fn []
                       ((.. js/ng -platform -browser -bootstrap)
                        (.-AppComponent (core/get-app))))))
