(ns webapp.framework.client.system-globals
  (:require
   [goog.net.cookies :as cookie]
   [om.core          :as om :include-macros true]
   [om.dom           :as dom :include-macros true]
   [clojure.data     :as data]
   [clojure.string   :as string]
   )
)

(def record-pointer-locally (atom true))


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

(defn  data-tree! [path value]
   (reset! data-state (assoc-in @data-state path value)))


(defn  data-tree [path]
  (get-in @data-state path))


(defn  -->data [path value]
   (reset! data-state (assoc-in @data-state path value)))


(defn  <--data [path]
  (get-in @data-state path))



(defn update-ui [app path value]
  (om/update! app path value))

(defn add-init-state-fn [nm init-state-fn]
  (do
    ;(.log js/console (str "Init function added: " nm))
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



;-----------------------------------------------------
;-----------------------------------------------------
(add-watch debug-event-timeline
           :change

           (fn [_ _ old-val new-val]
             ;(. js/console log (str "***** " new-val))
             nil
             )
           )




(def debugger-ui
  (atom {
         :mode                     "browse"
         :react-components         []
         :react-components-code    {}
         :watchers-code            {}
         :pos 1
         :total-events-count 0
         }))


(def debug-count (atom 0))
(defn add-debug-event [& {:keys [
                                 event-type
                                 old
                                 new
                                 error
                                 name-space
                                 tree-name
                                 code
                                 ] :or {
                                        event-type     "UI"
                                        error          "Error in field"
                                        }}]

  (if

    (or
      @record-pointer-locally
      (not (and (= event-type     "UI") (get (first (data/diff old new)) :pointer))))

    (cond



     (or (first (data/diff old new)) (second (data/diff old new)))

      (let [debug-id (swap! debug-count inc)]
        (swap! debug-event-timeline assoc
               debug-id  {
                          :id          debug-id
                          :event-type  event-type
                          :old-value   old
                          :value       new})

        (reset! debugger-ui
                (assoc @debugger-ui
                  :total-events-count (count @debug-event-timeline)))

        (if (> (+ (:pos @debugger-ui) 5) (:total-events-count @debugger-ui))
          (reset! debugger-ui
                  (assoc @debugger-ui
                    :pos (:total-events-count @debugger-ui))))

        )

     (and (= event-type     "event"))
     (let [debug-id (swap! debug-count inc)]

       (do
         (swap! debug-event-timeline assoc
                debug-id  {
                           :id          debug-id
                           :event-type  event-type
                           :name-space  name-space
                           :tree-name   tree-name
                           :code        code
                           })
         )
       )


)))



;-----------------------------------------------------
; This is when the user moves the timeline slider
; left and right. If the slider is currently being moves
; (ie: not at the right of the slider - meaning the
; latest position) then turn off the events capture
; from the application
;
; We do this because otherwise the application keeps
; receiving events otherwise
;-----------------------------------------------------
(def data-and-ui-events-on? (atom true))
(add-watch debugger-ui
           :change-debugger-ui

           (fn [_ _ old-val new-val]

             (cond
               (or
                (= js/debug_live false)
                (= (new-val :pos) (new-val :total-events-count))
                )
               (reset! data-and-ui-events-on? true)

              :else
               (reset! data-and-ui-events-on? false)
             )
             ;(. js/console log (pr-str new-val))

             )
           )


;-----------------------------------------------------
;  This is the application watch space. So whenever
; the application changes then we record the event
;-----------------------------------------------------
(def app-watch-on? (atom true))
(add-watch app-state
           :change
           (fn [_ _ old-val new-val]
             (if @app-watch-on?
               (add-debug-event
                :event-type  "UI"
                :old         old-val
                :new         new-val))))

(add-watch data-state
           :change
           (fn [_ _ old-val new-val]
             (if @app-watch-on?
               (add-debug-event
                :event-type  "DATA"
                :old         old-val
                :new         new-val))))


;(+ (:pos @debugger-ui ) 5)
;(:total-events-count @debugger-ui )
;(get @debug-event-timeline 20)

(add-debug-event
                :event-type  "event"
                :name-space  "dummy namespace"
                :tree-name   "UI"
                :code        "(def zoo)"
                )

 (:watchers-code @debugger-ui)
