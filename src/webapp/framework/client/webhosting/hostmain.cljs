(ns webapp.framework.client.webhosting.hostmain
  (:require
   [webapp.framework.client.coreclient   :as c ]
   [om.core :as om :include-macros true]
   [cljs.core.async  :refer [put! chan <! pub timeout]])

  (:use-macros
   [webapp.framework.client.coreclient  :only [ns-coils defn-ui-component def-coils-app
                                               container  map-many  inline  text log sql textarea a
                                               div img pre component h2 input section header button label form iframe
                                               write-ui read-ui container input component <-- data-view-result-set
                                               h1 h2 h3 h4 h5 h6 span  data-view-v2 select dselect realtime drealtime
                                               input-field remote
                                               ]])
  (:use
   [webapp.framework.client.system-globals :only  [appshare-dev-server appshare-dev-port]])

  (:require-macros
   [cljs.core.async.macros :refer [go alt!]]))
(ns-coils 'webapp.framework.client.webhosting.hostmain)






(def start "(ns webapp.framework.client.fns
  (:require-macros
    [webapp.framework.client.macros :refer [ refresh  ns-coils   div button input span defn-ui-component component]]))
(ns-coils 'webapp.framework.client.fns)")
(def end "(webapp.framework.client.system-globals.touch [:ui])")






(defn reeval [app-id]
  (go
    (let [code (js/getCodeMirrorValue)]
      (remote !savecode {:id app-id :code code})
      (js/sendcode (str start code end ))
      )))







(defn-ui-component     editor-component   [app]
  {:on-mount
   (do  (go (if  (read-ui app [:app-id])
              (let [x (remote  !getfilecontents  {:id (read-ui app [:app-id]) })]

                (js/createEditor)
                (js/populateEditor (get x :value))
                (reeval (read-ui app [:app-id]))
                ;(js/populateEditor (str "Loaded: " (read-ui app [:app-id])))
                ;(js/alert (str "loaded" (read-ui app [:app-id])))
                ))))}


  (div nil
       (textarea {:id "cm" :style {:display "inline-block" :width "1200" :height "800"}} "")))












(defn-ui-component     browser-component   [app]  {}


  (div {:style {:margin "30px"}}
       (realtime select id, application_name from coils_applications order by id {}
               (div nil


                    (button {:style {:marginRight "30px" :marginBottom "10px"}
                             :className "btn btn-small"
                             :onClick     #(go (sql "delete from coils_applications
                                              where id = ?"
                                              [(<-- :id) ]))
                             } "X")




                    (cond
                      (and (= (read-ui app [:submode]) "editappname") (= (<-- :id ) (read-ui app [:app-id])))
                      (input-field {:style {:marginBottom "20px" :color "black"} :placeholder  "Enter name"}
                                   app
                                   (fn [new-name]
                                     (let [id (read-ui app [:app-id])]
                                       (go
                                         (sql "update  coils_applications
                                              set application_name = ?
                                              where id = ?"
                                              [new-name id ]  )
                                         ;(js/alert (str new-name ":" id))
                                         (write-ui app [:submode] nil)
                                         ))))


                      ; if we select a different app
                      :else
                      (do
                        (div {:onClick     #(go  (write-ui app [:submode] "editappname")
                                                 (write-ui app [:app-id] (<-- :id)))

                              :style {:display "inline-block" :fontFamily "Ubuntu" :fontWeight "700" :fontSize "1.3em" ::marginTop "0.7em"}}
                             (str (<-- :application_name)))))






                    (button {:style {:marginBottom "10px" :marginLeft "30px"} :className "btn btn-default"
                             :onClick     #(go (write-ui app [:mode] "edit")
                                             (write-ui app [:app-id] (<-- :id))
                                             (remote !loadapp {:id (<-- :id)})
                                             )
                             } "Edit")))))












(defn-ui-component     main-hosting-component   [app]
  {}


  (div nil
       (div {:style     {:border "0px" :backgroundColor "black" :width "100%" :height "3em" :verticalAlign "top"}}
            (img {:style {:display "inline-block" :marginTop "-0.0em"} :src "appshare_logo_dark_background.png"})

            (button {:className    "btn btn-default"
                     :style       {:display "inline-block" :marginLeft "30px" :fontFamily "Ubuntu" :fontSize "1em" :marginTop "-0.3em"}
                     :onClick     #(write-ui app [:mode] "browse")} "Browse")

            (button {:className    "btn btn-default"
                     :style       {:display "inline-block" :marginLeft "30px" :fontFamily "Ubuntu" :fontSize "1em" :marginTop "-0.3em"}
                     :onClick     #(go
                                      (remote !newapp {}))   } "New")



            (cond
             (= (read-ui app [:mode]) "edit")
             (div {:style       {:display "inline-block"}}
               (comment a {:className    "btn btn-default"
                   :target       "new"
                   :style       {:display "inline-block" :marginLeft "30px" :fontFamily "Ubuntu" :fontSize "1em"  :marginTop "-0.3em" }
                   :href         (str "http://" @appshare-dev-server ":3450")}
                  "Run in own window")

               (button {:className    "btn btn-default"
                        :style       {:display "inline-block" :marginLeft "30px" :fontFamily "Ubuntu" :fontSize "1em" :marginTop "-0.3em"  }
                        :onClick     #(reeval  (read-ui app [:app-id]))}
                       "Save")))

            (a {:target       "appshare.co"
                :style       {:textDecoration "underline" :float "right"  :display "inline-block" :marginRight "30px" :fontFamily "Ubuntu" :fontSize "2em" :marginTop "0.3em"}
                :href         (str "http://canlabs.com")}
               "AppShare.co"))




       (div {}
            (div {:style {:display "inline-block" :width "1200" :height "800" :verticalAlign "top"}}
                 (cond
                   (or (= (read-ui app [:mode]) nil) (= (read-ui app [:mode]) "browse"))
                   (component browser-component app [])

                   (= (read-ui app [:mode]) "edit")
                   (component editor-component app [])

                   :else
                   (div {} "Nothing selected")))



       (iframe {:id "appframe" :style {:display "inline-block"} :src
                (str (cond
                      @c/debug-mode
                      "http://127.0.0.1:3449"

                      :else
                      "http://appshare.co/appshare")
                     "/devclient.html") :width "600" :height "800"}))
       ))







(def-coils-app     host-app   main-hosting-component)

