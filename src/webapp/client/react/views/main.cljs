(ns webapp.client.react.components.main
  (:require
   [webapp.framework.client.coreclient   :as c :include-macros true]))

(c/ns-coils 'webapp.framework.client.components.main)





(c/defn-ui-component     main-view   [app]
  {:absolute-path []}

  (c/div nil
       (c/h2 nil "Yazz")
       "Yazz.com"))
