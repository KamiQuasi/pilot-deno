(ns webapp.framework.client.system-globals
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [clojure.data     :as data]
   [clojure.string   :as string]
   )
)


(def start-component (atom nil))


(def playbackmode (atom false))


(def data-state
  (atom
   {}))

(def app-state
  (atom
   {}))




(def ui-watchers (atom []))

(def data-watchers (atom []))

(def ab-tests (atom {}))

(def ab-goals (atom {}))


(def init-state-fns (atom []))



(defn subtree-different? [orig-val new-val path]
  (let [
        orig-subset    (get-in orig-val  path)
        new-subset     (get-in new-val   path)
        ]
      (not (identical?  orig-subset  new-subset))))







;@app-state

(def blank-app-state
  {
   :data {
          :width "50"
          }
   :pointer
   {
    :mouse-x 0
    :mouse-y 0
    }
   :view
   {
    :width   0
    :height  0
    }
   }
  )

;(reset! app-state (assoc-in @app-state [:data :width ] "20"))


(defn reset-app-state []
  (reset!  app-state  blank-app-state))




(def init-fn (atom nil))




(def playback-app-state
  (atom
   {}
   ))




(def playback-controls-state
  (atom
   {:ui
    {
     :current-session   nil
     :current-page      0
     :delete-password {:label "Delete password"
                       :placeholder ""
                       :value ""}

     }
    :data {
           :sessions          []
           }
    }

   ))

(defn reset-playback-app-state []
  (reset!  playback-app-state  blank-app-state))



(defn  update-data [path value]
   (reset! data-state (assoc-in @data-state path value)))

(defn update-ui [app path value]
  (om/update! app path value))

(defn add-init-state-fn [nm init-state-fn]
  (do
    (.log js/console (str "Init function added: " nm))
    (swap!  init-state-fns conj init-state-fn)))


(defn get-in-tree [app path]
  (get-in @app path))


(defn  set-ab-tests [tree]
  (do
    (reset! ab-tests tree)
))



(defn  set-ab-goals [tree]
   (reset! ab-goals tree))




(def touch-id  (atom 0))

(defn touch [path]
  (reset! app-state (assoc-in @app-state path
                     (assoc
                       (get-in @app-state path)
                       :touch-id
                       (swap! touch-id inc)
                       ))))



(def debug-event-timeline (atom {}))

(add-watch debug-event-timeline
           :change

           (fn [_ _ old-val new-val]
             (. js/console log (str "***** " new-val))
             )
           )


(swap! debug-event-timeline assoc :2432 {:a 243})

(def debugger-ui
  (atom {
         :mode                     "browse"
         :react-components         []
         :react-components-code    {}
         :pos 34
         }))


(:mode @debugger-ui )
(:current-component @debugger-ui )


