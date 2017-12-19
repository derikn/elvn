(ns elvn.lib.http)

(defmulti process
  (fn [{:keys [msg-type]}] msg-type))
