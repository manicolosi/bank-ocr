(ns bank-ocr.core
  (:require [clojure.java.io :as io])
  (:gen-class :main true))

(defn partition-string [n s]
  (map (partial apply str) (partition n s)))

(defn separate-numbers
  "Takes a sequence of strings representing lines of a character set and
  re-arranges them into individual characters."
  [char-width char-height font-def]
  (->> font-def
       (map (partial partition-string char-width))
       (apply interleave)
       (partition char-height)))

(defn create-character-set [char-width char-height font-def]
  {:width char-width
   :height char-height
   :characters (separate-numbers char-width char-height font-def)})

(def character-set
  (create-character-set
    3 3
    [" _     _  _     _  _  _  _  _ "
     "| |  | _| _||_||_ |_   ||_||_|"
     "|_|  ||_  _|  | _||_|  ||_| _|"]))

(defn hash-map-with-index-values
  [a-seq]
  (reduce (fn [m [v k]] (assoc m k v)) {}
          (map-indexed vector a-seq)))

(defn parse [input]
  (let [{:keys [width height characters]} character-set
        char-indices (hash-map-with-index-values characters)
        parsed-input (separate-numbers width height (take height input))]
    (apply str (map #(get char-indices %1 "?") parsed-input))))

(defn int-seq
  [n]
  (map #(Integer/parseInt (str %1)) (str n)))

(defn valid?
  "Checksums a parsed bank account number returns a truthy value if it is
  valid."
  [number]
  (let [checksum (->> (int-seq number)
                      reverse
                      (map-indexed (fn [i n] (* (inc i) n)))
                      (reduce +))]
    (zero? (mod checksum 11))))

(defn legible?
  "A parsed bank account number is legible if it doesn't contain any question
  marks."
  [number]
  (not (some #{\?} number)))

(defn write-account-number [writer account]
  (.write writer account)
  (.write writer
    (cond
      (not (legible? account)) " ILL"
      (not (valid? account)) " ERR"
      :else ""))
  (.write writer "\n"))

(defn process [filename]
  (with-open [reader (io/reader filename)
              writer (io/writer (str filename ".out"))]
    (doseq [lines (partition (inc (:height character-set)) (line-seq reader))
            :let [account (parse lines)]]
      (write-account-number writer account))))

(defn -main [& args]
  (doseq [filename args]
    (process filename)))
