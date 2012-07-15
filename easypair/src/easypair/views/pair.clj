(ns easypair.views.pair
  (:use [noir.core :only [defpage]]
        [noir.request]
        [clj-time.core :exclude [extend]])
  (:require [noir.request]))

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

(defn join [session ipaddr] 
  (let [my-session (get (deref sessions) session)]
    (let [new-list (cons ipaddr (:viewers my-session))]
      (let [new-map (assoc my-session :viewers new-list)]
       (dosync
        (ref-set sessions
                 (assoc (deref sessions)
                   session new-map)))))))

(defn new-session [ipaddr]
  (let [new-key (md5 (str ipaddr "-" (now)))]
    (dosync 
     (ref-set sessions
              (assoc (deref sessions) new-key (hash-map
                                              :ipaddr ipaddr
                                              :viewers [])))
     new-key)))

(defn list-sessions [] 
  (deref sessions))

(defn keystroke [ipaddr contents]
  (println 
   "sending" 
   contents 
   "to" 
   (apply str 
     (interpose 
      ", " 
      (get (deref sessions) ipaddr)))))

(defn generate-session-data []
  (do
    (new-session "192.168.1.1")
    (join "192.168.1.1" "1.1.1.1")
    (join "192.168.1.1" "2.2.2.2")
    (join "192.168.1.1" "3.3.3.3")
    (new-session "10.5.5.5")
    (join "10.5.5.5" "4.4.4.4")
    (join "10.5.5.5" "5.5.5.5")
    (join "10.5.5.5" "6.6.6.6")))

(defpage "/new-session" []
  (str (new-session (:remote-addr (ring-request)))))