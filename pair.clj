(def *sessions* (ref (hash-map)))

(defn join [session ipaddr] 
  (let [my-session (get (deref *sessions*) session)]
     (let [new-list (cons ipaddr my-session)]
       (dosync
        (ref-set *sessions* (assoc (deref *sessions*) session new-list))))))

(defn request-control [session] )

(defn new-session [ipaddr] 
  (dosync 
   (ref-set *sessions* (assoc (deref *sessions*) ipaddr []))))

(defn list-sessions [] 
  (println (deref *sessions*)))

(defn keystroke [ipaddr contents]
  (println 
   "sending" 
   contents 
   "to" 
   (apply str 
     (interpose 
      ", " 
      (get (deref *sessions*) ipaddr)))))

(new-session "192.168.1.5")
(join "192.168.1.5" "8.8.8.8")
(join "192.168.1.5" "10.5.3.2")

(list-sessions)
(keystroke "192.168.1.5" "foobar")
