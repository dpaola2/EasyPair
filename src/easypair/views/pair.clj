(ns easypair.views.pair
  (:use [noir.core :only [defpage]]
        [noir.request]
        [clj-time.core :exclude [extend]])
  (:require [noir.request]
            [noir.response :as response]))

; from https://gist.github.com/1302024
(defn md5
  "Generate a md5 checksum for the given string"
  [token]
  (let [hash-bytes
         (doto (java.security.MessageDigest/getInstance "MD5")
               (.reset)
               (.update (.getBytes token)))]
       (.toString
         (new java.math.BigInteger 1 (.digest hash-bytes)) ; Positive and the size of the number
         16))) ; Use base16 i.e. hex


(def sessions (ref (hash-map)))

(defn new-session [ipaddr]
  "Generate a new session, returning a map with the session hash and session map"
  (let [
        new-key (md5 (str ipaddr "-" (now)))
        new-session (hash-map :ipaddr ipaddr :contents "" :cursor-x 0 :cursor-y 0)]
    (dosync 
     (ref-set sessions
              (assoc (deref sessions) new-key new-session))
     (hash-map :session-hash new-key :session new-session)))) ; return the new session

(defn list-sessions [] 
  (deref sessions))

(defn update-session [session-hash contents]
  (let [
        old-session (get (deref sessions) session-hash)
        new-session (assoc old-session :contents contents)]
    (dosync
     (ref-set sessions
              (assoc (deref sessions) session-hash new-session))
     new-session)))

(defn generate-session-data []
  (do
    (new-session "192.168.1.1")
    (new-session "10.5.5.5")))

(defn test-update []
  (let [session-hash (first (keys (list-sessions)))]
    (update-session session-hash "foo")))

(defpage "/new-session" []
  (response/json
   (new-session
    (:remote-addr (ring-request)))))

(defpage "/session/:id" {session-hash :id}
  (response/json
   (get (list-sessions) session-hash)))

(defpage "/update-session/:id" {:keys [id contents]}
  (response/json (update-session id contents))) 