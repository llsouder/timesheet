(ns timesheet.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [timesheet.core-test]))

(doo-tests 'timesheet.core-test)

