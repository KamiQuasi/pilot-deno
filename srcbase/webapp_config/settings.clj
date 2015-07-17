(ns webapp-config.settings)






(defonce ^:dynamic *mandrill-api-key* "enter_api_key_for_mandrill_here")

(defonce ^:dynamic *record-pointer-locally* false)

(defonce ^:dynamic *record-ui* false)

(defonce ^:dynamic *email-debug-mode* true)

(defonce ^:dynamic *environment* "base")

(defonce ^:dynamic *web-server* "127.0.0.1:3449")


(defonce ^:dynamic *database-type* "postgres")
(defonce ^:dynamic *database-server* "127.0.0.1")
(defonce ^:dynamic *database-user* "postgres")
(defonce ^:dynamic *database-password* "manager")
(defonce ^:dynamic *database-name* "coils")


;(defonce ^:dynamic *database-type* "oracle")
;(defonce ^:dynamic *database-server* "localhost")
;(defonce ^:dynamic *database-name* "ORCL")
;(defonce ^:dynamic *database-user* "system")
;(defonce ^:dynamic *database-password* "Manager2")



(defonce ^:dynamic *sql-encryption-password* "animal")

(defonce ^:dynamic *show-code* true)

(defonce ^:dynamic *main-page* "main.html")

(defonce ^:dynamic *neo4j-server* "localhost")

(defonce ^:dynamic *neo4j-port* "7474")

(defmacro setup-fn [] (quote webapp.framework.client.init/setup-properties))
;(defmacro setup-fn [] (quote webapp.client.demoapp/setup-properties))

