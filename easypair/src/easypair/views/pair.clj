(ns easypair.views.pair)

(def sessions (ref (hash-map)))

(defn join [session ipaddr] 
  (let [my-session (get (deref sessions) session)]
     (let [new-list (cons ipaddr my-session)]
       (dosync
        (ref-set sessions (assoc (deref sessions) session new-list))))))

(defn request-control [session] )

(defn new-session [ipaddr] 
  (dosync 
   (ref-set sessions (assoc (deref sessions) ipaddr []))))

(defn list-sessions [] 
  (keys (deref sessions)))

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

(generate-session-data)