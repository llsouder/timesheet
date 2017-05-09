(ns ^:figwheel-no-load timesheet.app
  (:require [timesheet.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
